package com.invengo.xc2910;

import android.content.ContentValues;
import android.content.Context;
import android.content.ContextWrapper;
import android.database.Cursor;
import android.database.DatabaseErrorHandler;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
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
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class MainActivity extends AppCompatActivity {
	private static final String xmlPath = "DataDic.xml";	// XML 文件路径
	protected SimpleDateFormat timFmt = new SimpleDateFormat ("yyyyMMddHHmmss");

	protected FrMain frMain = new FrMain();
	protected FrScan frScan = new FrScan();
	protected FrShow frShow = new FrShow();
	protected DatSender ds = new DatSender();
	protected XC2910 demo = null;
	private FlushUiHandle fh = null;

	protected DbHandle db = null;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
			db = new DbHandle (this);
			fh = new FlushUiHandle();
			getFragmentManager().beginTransaction().replace(R.id.frdiv, frMain).commit();
		} else {
			Toast.makeText(getApplicationContext(), "找不到 SD 卡！", Toast.LENGTH_LONG).show();
		}

	}

	@Override
	protected void onResume() {
		super.onResume();

		if (demo == null) {
			// 加载 车属性集合的 XML 文档
			try {
				BaseTag.load(this.getAssets().open(xmlPath));
			} catch (IOException e) {
//				e.printStackTrace();
			}

			// 创建读出器对象
			demo = new XC2910();
			demo.setOnTagListener(new OnTagListener() {
				@Override
				public void onTag(InfViewTag t) {
					if (frScan.isAdded()) {
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
//						deleteFile(path);
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
		demo.close();
		super.onStop();
	}

	@Override
	protected void onDestroy() {
		demo.close();
		super.onDestroy();
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
		private final String sqlHasTab = "select count(*) from sqlite_master where type='table' and name='tbMotor'";	// 查询数据表是否存在
		private final String sqlCrtTab = "create table tbMotor(mid integer primary key autoincrement, readtime text, tagcode text, repairclass text, updated integer)";
		private final String sqlFindAll = "select * from tbMotor";
		private final String sqlAddOne = "insert into tbMotor(readtime, tagcode, repairclass, updated) values(?, ?, ?, 0)";
		private final String sqlDelAll = "delete from tbMotor";
		private final String sqlCount = "select count(*) from tbMotor";

		public DbHandle (Context con) {
			super(new DbContext(con), "motor", null, 1);	// 使用外部 SD 卡存储数据库
//			super(con, "motor", null, 1);	// 数据库存储在data文件夹里
		}

		@Override
		public void onCreate(SQLiteDatabase db) {
			db.execSQL(sqlCrtTab);
		}

		@Override
		public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

		}

		public void add (String[] args) {
			SQLiteDatabase db = this.getWritableDatabase();
			db.execSQL(sqlAddOne, args);
			db.close();
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

		public int count () {
			int r = 0;
			SQLiteDatabase db = this.getReadableDatabase();
			Cursor c = db.rawQuery(sqlCount, null);
			if (c.moveToNext()) {
				r = c.getInt(0);
			}
			c.close();
			db.close();
			return r;
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
//Log.i("--- tim ---", r.get("tim"));
//Log.i("--- cod ---", r.get("cod"));
//Log.i("--- xiu ---", r.get("xiu"));
			}
			c.close();
			db.close();
		}

		// 数据库读写测试
		private void test () {
Log.i("--- count ---", this.count() + "");
			this.add(new String[] {"123464", "zhgsdhlsdgflh", "C"});
			this.add ("ttt", "ccc", "F");
Log.i("--- count ---", this.count() + "");
			this.find(new ArrayList<HashMap<String, String>>());
			this.del();
Log.i("--- count ---", this.count() + "");
		}
	}

	private class DbContext extends ContextWrapper {
		public DbContext (Context c) {
			super(c);
		}

		// 修改数据库至 SD 卡
		@Override
		public File getDatabasePath(String name) {
			String sdPath = "Invengo/XC2910/DB/" + name;
			if (!sdPath.endsWith(".db")) {
				sdPath += ".db";
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
