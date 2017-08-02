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
import android.media.AudioManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.view.KeyEvent;
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
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;

public class MainActivity extends AppCompatActivity {
	private static final String xmlPath = "DataDic.xml";	// XML 文件路径
	private static final String iniPath = "Invengo/XC2910/conf.ini";	// 配置文件路径
	private static final String timPath = "Invengo/XC2910/tim.txt";	// 时间反馈文件路径
	private SimpleDateFormat timFmt = new SimpleDateFormat ("yyyyMMddHHmmss");

	private FrMain frMain = new FrMain();
	private FrScan frScan = new FrScan();
	private FrShow frShow = new FrShow();
	private Fragment curf = null;
	private FragmentManager fm = null;
	protected DatSender ds = new DatSender();
	protected XC2910 demo = null;
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

/*
	// HOME 后重启
	Handler rebootHandler = new Handler();
	Runnable rebootRunnable = new Runnable() {
		@Override
		public void run() {
			reboot();
		}
	};

	// 重启
	public void reboot()
	{
		Intent t = new Intent(this, MainActivity.class);
		t.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
		this.startActivity(t);
	}

	@Override
	protected void onUserLeaveHint() {
		super.onUserLeaveHint();
		rebootHandler.post(rebootRunnable);
	}
*/

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
		setContentView(R.layout.activity_main);

		try {
			BaseTag.load(this.getAssets().open(xmlPath), new File(Environment.getExternalStorageDirectory(), iniPath));
		} catch (Exception e) {
//			e.printStackTrace();
		}

		am = (AudioManager) getSystemService(Context.AUDIO_SERVICE);
		nm = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
		fh = new FlushUiHandle();
		vt = (TextView) findViewById(R.id.tim);
		tim = new Tim(this);

		// 监听内存卡
		sdr = new BroadcastReceiver() {
			@Override
			public void onReceive(Context context, Intent intent) {
				finish();
			}
		};
		IntentFilter f = new IntentFilter();
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
					new File(Environment.getExternalStorageDirectory(), timPath).createNewFile();
				} catch (Exception e) {
//					e.printStackTrace();
				}

//				Log.i("---- timr ----", dat);
//				Toast.makeText(getApplicationContext(), dat, Toast.LENGTH_SHORT).show();
			}
		};
		f = new IntentFilter();
		f.addAction("com.invengo.xc2910.timr");
		registerReceiver(timr, f);
	}

	@Override
	protected void onResume() {
		super.onResume();

		if (demo == null) {
			if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
				db = new DbHandle (this);
				fm = getFragmentManager();
				setF(frMain);

				// 加载 车属性集合的 XML 文档
				try {
					BaseTag.load(this.getAssets().open(xmlPath), new File(Environment.getExternalStorageDirectory(), iniPath));
				} catch (IOException e) {
					e.printStackTrace();
				}
			} else {
				Toast.makeText(getApplicationContext(), "找不到 SD 卡！", Toast.LENGTH_LONG).show();
				return;
			}

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
		}

		demo.open();
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
		}
		unregisterReceiver(sdr);
		unregisterReceiver(timr);
		tim.stop();
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

	// 设置当前页面
	private void setF (Fragment f) {
		if (curf != null) {
			if (curf == f) {
				return;
			}
			fm.beginTransaction().remove(curf).commit();
		}
		fm.beginTransaction().replace(R.id.frdiv, f).commit();
		curf = f;
	}

	// 播放提示音
	protected void mkNtf (String n) {
		nm.cancel(1);
//		ntfc.defaults = Notification.DEFAULT_SOUND;
//		ntfc.sound = Uri.parse("content://media/internal/audio/media/" + n);
		ntfc.sound = Uri.withAppendedPath(MediaStore.Audio.Media.INTERNAL_CONTENT_URI, n);
		nm.notify(1, ntfc);
/* 可用的系统声音：
* 5...
* 7
* 9...
* 18
* 19	v
* 20
* 21
* 22
* 23...
* 24...
* 25...
* 26...
* 27...
* 28...
* 29...
* 30	v
* 31	v
* 32	v
* 33
* 34
* 35
* 36	v
* 37
* 38
* 39
* 40
* 41
* 42
* 43
* 44
* 45
* 46
* 47
* 48
* 49
* 50
* .......
* 80...
* */
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
//					vt.setText(c.get(Calendar.YEAR) + "-" + (c.get(Calendar.MONTH) + 1) + "-" + c.get(Calendar.DATE) + " " + c.get(Calendar.HOUR_OF_DAY) + ":" + c.get(Calendar.MINUTE) + ":" + c.get(Calendar.SECOND));
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
			super(new DbContext(con), "motor", null, 1);	// 使用外部 SD 卡存储数据库
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

	// 修改数据库至 SD 卡
	private class DbContext extends ContextWrapper {
		public DbContext (Context c) {
			super(c);
		}

		@Override
		public File getDatabasePath(String name) {
			String sdPath = "Invengo/XC2910/DB/" + name;
			if (!sdPath.endsWith(".hdf")) {
				sdPath += ".hdf";
			}

			File dbfile = new File(Environment.getExternalStorageDirectory(), sdPath);
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
