package com.invengo.xc2910;

import android.app.Fragment;
import android.app.FragmentManager;
import android.app.Notification;
import android.app.NotificationManager;
import android.content.BroadcastReceiver;
import android.content.ContentValues;
import android.content.Context;
import android.content.ContextWrapper;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.Cursor;
import android.database.DatabaseErrorHandler;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.graphics.Bitmap;
import android.media.AudioManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.view.KeyEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.TextView;
import android.widget.Toast;

import com.invengo.train.tag.BaseTag;
import com.invengo.train.tag.xc2910.InfViewTag;
import com.invengo.xc2910.rfid.DatSender;
import com.invengo.xc2910.rfid.OnDatSendListener;
import com.invengo.xc2910.rfid.OnTagListener;
import com.invengo.xc2910.rfid.XC2910;

import java.io.File;
import java.io.FileOutputStream;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;

public class MainActivity extends AppCompatActivity {
	private static final String xmlPath = "DataDic.xml";	// XML 文件路径
	private static final String iniPath = "conf.xml";	// 配置文件路径
	private static final String timPath = "tim.txt";	// 时间反馈文件路径
	private static final String sdDir = "Invengo/XC2910/";	// SD卡路径
	protected XC2910 demo = null;

	private SimpleDateFormat timFmt = new SimpleDateFormat ("yyyyMMddHHmmss");
	private FrMain frMain = new FrMain();
	private FrScan frScan = new FrScan();
	private FrShow frShow = new FrShow();
	private Fragment curf = null;
	private FragmentManager fm = null;
	protected DatSender ds = new DatSender();
	private FlushUiHandle fh = null;
	private Tim tim = null;
	private TextView vt = null;

	protected DbHandle db = null;
	protected AudioManager am = null;
	private NotificationManager nm = null;
	private Notification ntfc = new Notification();

	// 内存卡监听器
	private BroadcastReceiver sdr;

	// 时间修改监听
	private BroadcastReceiver timr;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
		getWindow().addFlags(WindowManager.LayoutParams.FLAG_DISMISS_KEYGUARD);
		setContentView(R.layout.activity_main);

		try {
			String title = BaseTag.load(this.getAssets().open(xmlPath), new File(Environment.getExternalStorageDirectory(), sdDir + iniPath), this.getAssets().open(iniPath));
			if (title == null) {
				return;
			}
			((TextView) findViewById(R.id.tt)).setText(title);
		} catch (Exception e) {
//			e.printStackTrace();
		}

		if (demo == null) {
			if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
				db = new DbHandle (this);
			} else {
//				Toast.makeText(getApplicationContext(), "找不到 SD 卡！", Toast.LENGTH_LONG).show();
				return;
			}

			vt = (TextView) findViewById(R.id.tim);
			am = (AudioManager) getSystemService(Context.AUDIO_SERVICE);
			nm = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
			fh = new FlushUiHandle();
			tim = new Tim(this);
			fm = getFragmentManager();
			setF(frMain);

			// 创建读出器对象
			demo = new XC2910();
			demo.setOnTagListener(new OnTagListener() {
				@Override
				public void onTag(InfViewTag t) {
					if (curf == frScan) {
						frScan.setTag(t);
						Message msg = new Message();
						Bundle dat = new Bundle();
						dat.putString("stat", "OnTag");
						msg.setData(dat);
						fh.sendMessage(msg);
					}
				}
			});

			// 数据上传回调处理
			ds.setOnDatSendListener(new OnDatSendListener() {
				@Override
				public void onDatSended(boolean b) {
					Message msg = new Message();
					Bundle dat = new Bundle();
					dat.putString("stat", "msg");
					if (b) {
						dat.putString("msg", "OK! 发送成功！");
					} else {
						dat.putString("msg", "Err! 发送失败！");
					}
					msg.setData(dat);
					fh.sendMessage(msg);
				}
			});

			// 监听内存卡
			IntentFilter f = new IntentFilter();
			sdr = new BroadcastReceiver() {
				@Override
				public void onReceive(Context context, Intent intent) {
					finish();
				}
			};
			f.addAction(Intent.ACTION_MEDIA_UNMOUNTED);
			f.addDataScheme("file");
			registerReceiver(sdr, f);

			// 监听时间修改
			timr = new BroadcastReceiver() {
				@Override
				public void onReceive(Context context, Intent intent) {
					String dat = intent.getStringExtra("tim");
					String[] t = dat.split(",");
					tim.setT(
							Integer.parseInt(t[0]),
							Integer.parseInt(t[1]) - 1,
							Integer.parseInt(t[2]),
							Integer.parseInt(t[3]),
							Integer.parseInt(t[4]),
							Integer.parseInt(t[5])
					);

					// 生成反馈文件
					try {
						new File(Environment.getExternalStorageDirectory(), sdDir + timPath).createNewFile();
					} catch (Exception e) {
//					e.printStackTrace();
					}
				}
			};
			f = new IntentFilter();
			f.addAction("com.invengo.xc2910.timr");
			registerReceiver(timr, f);
		}
	}

	@Override
	protected void onResume() {
		if (demo != null) {
			demo.open();
		}
		super.onResume();
	}

	@Override
	protected void onStop() {
		if (demo != null) {
			demo.close();
		}
		super.onStop();
	}

	@Override
	protected void onDestroy() {
		if (demo != null) {
			demo.close();
			unregisterReceiver(sdr);
			unregisterReceiver(timr);
			tim.stop();
		}
		super.onDestroy();
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
//Log.i("-", "" + keyCode);
		if (demo != null) {
			switch (keyCode) {
				case 2:
					if (event.getRepeatCount() == 0) {
						if (curf == frScan) {
							demo.qryTag();
						} else {
							setF(frScan);
						}
					}
					return false;
				case KeyEvent.KEYCODE_BACK:
					if (event.getRepeatCount() == 0) {
						setF(frMain);
					}
					return false;
				case KeyEvent.KEYCODE_HOME:
					if (event.getRepeatCount() == 0) {
						if (curf == frScan) {
							frScan.sav();
						}
					}
					return false;
				case 59:
					if (event.getRepeatCount() == 0) {
//						screenshots(timFmt.format(new Date()));
						if (curf == frScan) {
							frScan.chgXiu(1);
						}
					}
					return false;
				case 60:
					if (event.getRepeatCount() == 0) {
						if (curf == frScan) {
						frScan.chgXiu(-1);
						}
					}
					return false;
				case 84:
					return true;
			}
		}
		return super.onKeyDown(keyCode, event);
	}

	// 截屏
	private void screenshots (String fn) {
		View v = vt.getRootView();
		v.setDrawingCacheEnabled(true);
		v.destroyDrawingCache();
		v.buildDrawingCache();
		Bitmap bp = v.getDrawingCache();
		if(bp != null) {
			try{
				File f = new File(Environment.getExternalStorageDirectory(), sdDir + "img/" + fn + ".png");
				if (!f.getParentFile().exists()) {
					f.getParentFile().mkdirs();
				}
				FileOutputStream out = new FileOutputStream(f);
				bp.compress(Bitmap.CompressFormat.PNG, 100, out);
			}catch(Exception e) {
//				e.printStackTrace();
			}
		}
	}

	// 设置当前页面
	private void setF (Fragment f) {
		if (curf != null) {
			if (curf == f) {
				return;
			} else if (curf == frScan) {
				frScan.clearUi();
			}
			fm.beginTransaction().remove(curf).commit();
		}
		fm.beginTransaction().replace(R.id.frdiv, f).commit();
		curf = f;
	}

	// 播放提示音
	protected void mkNtf (String n) {
		nm.cancel(1);
		ntfc.sound = Uri.withAppendedPath(MediaStore.Audio.Media.INTERNAL_CONTENT_URI, n);
		nm.notify(1, ntfc);
	}

	protected void setF (String strf) {
		Fragment f = null;
		switch (strf) {
			case "m":
				f = frMain;
				break;
			case "s":
				f = frScan;
				break;
			case "w":
				f = frShow;
				break;
		}
		if (f != null) {
			setF(f);
		}
	}

	protected void flushTim () {
		Message msg = new Message();
		Bundle dat = new Bundle();
		dat.putString("stat", "tim");
		msg.setData(dat);
		fh.sendMessage(msg);
	}

	protected String getTim() {
		tim.setT();
		return timFmt.format(tim.getT().getTime());
	}

	private class FlushUiHandle extends Handler {
		@Override
		public void handleMessage(Message msg) {
			super.handleMessage(msg);
			Bundle dat = msg.getData();
			Toast toast;
			switch (dat.getString("stat")) {
				case "tim":
					Calendar c = tim.getT();
					vt.setText(c.get(Calendar.YEAR) + "-" + (c.get(Calendar.MONTH) + 1) + "-" + c.get(Calendar.DATE) + " " + c.get(Calendar.HOUR_OF_DAY) + ":" + c.get(Calendar.MINUTE));
					break;
				case "OnTag":
					frScan.updateUi(true);
					break;
				case "msg":
					toast = Toast.makeText(getApplicationContext(), dat.getString("msg"), Toast.LENGTH_SHORT);
					toast.show();
					break;
			}
		}
	}

	// 数据库
	protected class DbHandle extends SQLiteOpenHelper {
		private final String sqlCrtTab = "create table tbMotor(mid integer primary key autoincrement, readtime text, tagcode text, repairclass text, updated integer)";
		private final String sqlFindAll = "select * from tbMotor";
		private final String sqlDelAll = "delete from tbMotor";

		public DbHandle (Context con) {
			super(new DbContext(con), "motor", null, 1);
		}

		@Override
		public void onCreate(SQLiteDatabase db) {
			db.execSQL(sqlCrtTab);
		}

		@Override
		public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

		}

		public void add (String tim, String cod, String xiu) {
			SQLiteDatabase db = this.getWritableDatabase();
			ContentValues c = new ContentValues();
			c.put ("readtime", tim);
			c.put ("tagcode", cod);
			c.put ("repairclass", xiu);
			c.put ("updated", 0);
			db.insert("tbMotor", null, c);
			db.close();
		}

		public void del () {
			SQLiteDatabase db = this.getWritableDatabase();
			db.execSQL(sqlDelAll);
			db.close();
		}

		public void find (List<HashMap<String, String>> a) {
			SQLiteDatabase db = this.getReadableDatabase();
			Cursor c = db.rawQuery(sqlFindAll, null);
			while (c.moveToNext()) {
				HashMap<String, String> r = new HashMap<>();
				r.put("tim", c.getString(1));
				r.put("cod", c.getString(2));
				r.put("xiu", c.getString(3));
				a.add(r);
			}
			c.close();
			db.close();
		}

		public String getPath () {
			return this.getReadableDatabase().getPath();
		}
	}

	// 数据库位置
	private class DbContext extends ContextWrapper {
		public DbContext (Context c) {
			super(c);
		}

		@Override
		public File getDatabasePath(String name) {
			String p = sdDir + "DB/" + name;
			if (!p.endsWith(".hdf")) {
				p += ".hdf";
			}

			File dbfile = new File(Environment.getExternalStorageDirectory(), p);
			if (!dbfile.getParentFile().exists()) {
				dbfile.getParentFile().mkdirs();
			}

			return dbfile;
		}

		@Override
		public SQLiteDatabase openOrCreateDatabase(String name, int mode, SQLiteDatabase.CursorFactory factory) {
			return SQLiteDatabase.openOrCreateDatabase(getDatabasePath(name), null);
		}

		@Override
		public SQLiteDatabase openOrCreateDatabase(String name, int mode, SQLiteDatabase.CursorFactory factory, DatabaseErrorHandler errorHandler) {
			return openOrCreateDatabase(name, mode, factory);
		}
	}
}
