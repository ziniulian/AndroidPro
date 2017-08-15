package com.invengo.warehouse.dao.impl;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.invengo.warehouse.enums.EmLocalSql;

/**
 * 本地数据库
 * Created by LZR on 2017/8/14.
 */

public class DbLocal extends SQLiteOpenHelper {
	public DbLocal(Context c) {
		super(c, EmLocalSql.DbNam.toString(), null, 1);
	}

	@Override
	public void onCreate(SQLiteDatabase db) {
		db.execSQL(EmLocalSql.CrtTable.toString());
		db.execSQL(EmLocalSql.CrtPlan.toString());
		db.execSQL(EmLocalSql.CrtRecord.toString());
		db.execSQL(EmLocalSql.CrtBorrow.toString());
//		db.execSQL(EmLocalSql.CrtMaintain.toString());
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

	}

	// 数据同步
	public void synDb() {}

}
