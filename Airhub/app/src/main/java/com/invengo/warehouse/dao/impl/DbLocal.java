package com.invengo.warehouse.dao.impl;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.invengo.warehouse.enums.EmLocalSql;

/**
 * 本地数据库
 * Created by LZR on 2017/8/14.
 */

public class DbLocal extends SQLiteOpenHelper {
	public DbLocal(Context c) {
		super(new SdDb(c), EmLocalSql.DbNam.toString(), null, 1);
	}

	@Override
	public void onCreate(SQLiteDatabase db) {
		db.execSQL(EmLocalSql.CrtCatch.toString());
		db.execSQL(EmLocalSql.CrtTable.toString());
		db.execSQL(EmLocalSql.CrtBorrow.toString());
//		db.execSQL(EmLocalSql.CrtPlan.toString());
//		db.execSQL(EmLocalSql.CrtRecord.toString());
//		db.execSQL(EmLocalSql.CrtMaintain.toString());

		// 初始同步时间
		db.execSQL(parseSql(EmLocalSql.AddV.toString(), new String[] {"syntim", "0"}));
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

	}

	private String getStr (Cursor c, int i) {
		String s = null;
		s = c.getString(i);
		if (s != null) {
			return '\"' + s + '\"';
		} else {
			return null;
		}
	}

	private Long getTim (Cursor c, int i) {
		Long t = null;
		t = c.getLong(i);
		return t;
	}

	private String parseSql (String sql, String[] args) {
		for (int i = 0; i < args.length; i ++) {
			sql = sql.replaceAll(("<" + i + '>'), args[i]);
		}
		return sql;
	}

	// 执行SQL语句
	public void exe (String s, String[] args) {
		SQLiteDatabase db = this.getWritableDatabase();
		db.execSQL(parseSql(s, args));
		db.close();
	}

	// 获取键值对
	public String getV (String k) {
		SQLiteDatabase db = this.getReadableDatabase();
		Cursor c = db.rawQuery(parseSql(EmLocalSql.GetV.toString(), new String[] {k}), null);
		if (c.moveToNext()) {
			return c.getString(0);
		} else {
			return null;
		}
	}

	// 数据同步
	public void synDevIn(String[] ad) {
		exe(EmLocalSql.AddDevs.toString(), ad);
	}
	public void synDevUp(String[] ad) {
		exe(EmLocalSql.SetDevs.toString(), ad);
	}
	public void synOutIn(String[] ad) {
		exe(EmLocalSql.AddBorrows.toString(), ad);
	}
	public void synOutUp(String[] ad) {
		exe(EmLocalSql.SetBorrows.toString(), ad);
	}

	// 获取设备的详细信息
	public StringBuilder getDevInfo (String id) {
		boolean b = false;
		StringBuilder r = new StringBuilder("{\"ok\":");
		SQLiteDatabase db = this.getReadableDatabase();
		Cursor c = db.rawQuery(parseSql(EmLocalSql.GetDevInfo.toString(), new String[] {id}), null);

		if (c.moveToNext()) {
 			b = true;
			r.append(b);
			r.append(",\"devid\":");
			r.append(getStr(c, 0));
			r.append(",\"nam\":");
			r.append(getStr(c, 1));
			r.append(",\"brand\":");
			r.append(getStr(c, 2));
			r.append(",\"mod\":");
			r.append(getStr(c, 3));
			r.append(",\"unit\":");
			r.append(getStr(c, 4));
			r.append(",\"price\":");
			r.append(getStr(c, 5));
			r.append(",\"level\":");
			r.append(getStr(c, 6));
			r.append(",\"sn\":");
			r.append(getStr(c, 7));
			r.append(",\"tim\":");
			r.append(getTim(c, 8));
			r.append(",\"intim\":");
			r.append(getTim(c, 9));
			r.append(",\"isout\":");
			r.append(c.getString(10));
			r.append(",\"cont\":");
			r.append(getStr(c, 11));
			r.append(",\"location\":");
			r.append(getStr(c, 12));
			r.append(",\"fegularcycle\":");
			r.append(c.getInt(13));
			r.append(",\"iscrap\":");
			r.append(c.getString(14));
			r.append(",\"remark\":");
			r.append(getStr(c, 15));
			r.append(",\"tid\":");
			r.append(getStr(c, 16));
		}
		c.close();
		db.close();

		if (!b) {
			r.append(b);
		}

		r.append('}');
		return r;
	}

	// 获取设备的领用记录
	public StringBuilder getBorrowByDev (String id) {
		boolean b = false;
		StringBuilder r = new StringBuilder("{\"ok\":");
		SQLiteDatabase db = this.getReadableDatabase();
		Cursor c = db.rawQuery(parseSql(EmLocalSql.GetBorrowByDev.toString(), new String[] {id}), null);

		while (c.moveToNext()) {
			if (!b) {
				b = true;
				r.append(b);
				r.append(",\"dat\":[");
			}

			r.append("{\"devid\":");
			r.append(getStr(c, 1));
			r.append(",\"rid\":");
			r.append(getStr(c, 0));
			r.append(",\"tim\":");
			r.append(getTim(c, 2));
			r.append(",\"dep\":");
			r.append(getStr(c, 3));
			r.append(",\"person\":");
			r.append(getStr(c, 4));
			r.append(",\"reason\":");
			r.append(getStr(c, 5));
			r.append(",\"comtim\":");
			r.append(getTim(c, 6));
			r.append('}');
			r.append(',');
		}
		c.close();
		db.close();

		if (!b) {
			r.append(b);
		} else {
			r.deleteCharAt(r.length() - 1);
			r.append(']');
		}

		r.append('}');
//Log.i("-------", r.toString());
		return r;
	}

}
