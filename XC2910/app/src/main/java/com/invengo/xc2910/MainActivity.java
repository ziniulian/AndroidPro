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
import android.widget.Toast;

import com.invengo.train.tag.BaseTag;
import com.invengo.train.tag.xc2910.InfViewTag;
import com.invengo.xc2910.rfid.DatSender;
import com.invengo.xc2910.rfid.OnDatSendListener;
import com.invengo.xc2910.rfid.OnTagListener;
import com.invengo.xc2910.rfid.XC2910;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.HashMap;
import java.util.List;

public class MainActivity extends AppCompatActivity {
	private static final String xmlPath = "conf.xml";	// XML 文件路径
	protected SimpleDateFormat timFmt = new SimpleDateFormat ("yyyyMMddHHmmss");

	private FrMain frMain = new FrMain();
	private FrScan frScan = new FrScan();
	private FrShow frShow = new FrShow();
	private Fragment curf = null;
	private FragmentManager fm = null;
	protected DatSender ds = new DatSender();
	protected XC2910 demo = null;
	private FlushUiHandle fh = null;

	protected DbHandle db = null;
	protected AudioManager am = null;
	private NotificationManager nm = null;
	private Notification ntfc = new Notification();

	// 内存卡监听器
	private BroadcastReceiver sdr;

	@SuppressWarnings("MissingPermission")
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
		setContentView(R.layout.activity_main);

		am = (AudioManager) getSystemService(Context.AUDIO_SERVICE);
		nm = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

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

//		// GPS监听
//		LocationManager lm = (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);
//		Criteria criteria = new Criteria();criteria.setAccuracy(Criteria.ACCURACY_FINE); // 高精度
//		criteria.setAltitudeRequired(false);
//		criteria.setBearingRequired(false);
//		criteria.setCostAllowed(true);
//		criteria.setPowerRequirement(Criteria.POWER_LOW); // 低功耗
//		String provider = locationManager.getBestProvider(criteria, true);	// 获取GPS信息
//		Log.i("-------- GPS -------", provider);
//		List<String> as = lm.getAllProviders();
//		int i;
//		for (i = 0; i < as.size(); i++) {
//			Log.i("-------- GPS -------", as.get(i));
//		}
//		LocationProvider gpsp = lm.getProvider(LocationManager.GPS_PROVIDER);
//		Log.i("-------- GPS -------", gpsp.toString());
//		Location location;
//		i = 0;
//		do {
//			location = lm.getLastKnownLocation(LocationManager.GPS_PROVIDER);	// 通过GPS获取位置
//			i ++;
//		} while (location == null && i < 10000);
//
//		Log.i("-------- GPS -------", (location == null) + " , " + i);
//		Log.i("-------- GPS -------", location.getTime()+"");
//		Log.i("-------- GPS -------", location.getLongitude()+"");
//		Log.i("-------- GPS -------", location.getLatitude()+"");

//		lm.requestLocationUpdates(LocationManager.GPS_PROVIDER, 500, 8, new LocationListener() {
//
//			@Override
//			public void onStatusChanged(String provider, int status, Bundle extras) {
//				Log.i("-------- GPS -------", "onStatusChanged");
//			}
//
//			@Override
//			public void onProviderEnabled(String provider) {
//				// 当GPS LocationProvider可用时，更新位置
//				Log.i("-------- GPS -------", "onProviderEnabled");
//			}
//
//			@Override
//			public void onProviderDisabled(String provider) {
//				Log.i("-------- GPS -------", "onProviderDisabled");
//			}
//
//			@Override
//			public void onLocationChanged(Location location) {
//				//GPS定位信息发生改变时，更新位置
//				Log.i("-------- GPS -------", "onLocationChanged");
//			}
//		});
	}

	@Override
	protected void onResume() {
		super.onResume();

		if (demo == null) {
			if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
				db = new DbHandle (this);
				fh = new FlushUiHandle();
				fm = getFragmentManager();
				setF(frMain);

				// 加载 车属性集合的 XML 文档
				try {
					File xmlf = new File(Environment.getExternalStorageDirectory(), "Invengo/XC2910/" + xmlPath);
					if (!xmlf.exists()) {
						InputStream is = this.getAssets().open(xmlPath);
						FileOutputStream os = new FileOutputStream(xmlf);
						byte[] buf = new byte[1024];
						int s = is.read(buf);
						while (s != -1) {
							os.write(buf, 0, s);
							s = is.read(buf);
						}
						os.close();
						is.close();
					}
					BaseTag.load(new FileInputStream(xmlf));
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
		super.onDestroy();
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
//Log.i("---- k ----", "" + keyCode);
//Log.i("---- k ----", event.toString());
		if (demo != null && event.getRepeatCount() == 0) {
			switch (keyCode) {
				case 2:
					if (curf == frScan) {
						demo.qryTag();
					} else {
						setF(frScan);
					}
					return false;
				case 0:
					if (curf == frScan) {
						frScan.sav();
					}
					return false;
				case KeyEvent.KEYCODE_BACK:
					setF(frMain);
					return false;
				case 59:
					if (curf == frScan) {
						frScan.chgXiu(1);
					}
					return false;
				case 60:
					if (curf == frScan) {
						frScan.chgXiu(-1);
					}
					return false;
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

	private class FlushUiHandle extends Handler {
		@Override
		public void handleMessage(Message msg) {
			super.handleMessage(msg);
			Bundle dat = msg.getData();
			Toast toast;
			switch (dat.getString("stat")) {
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
