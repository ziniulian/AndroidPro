package com.invengo.warehouse.dao.impl;

import android.content.Context;
import android.content.ContextWrapper;
import android.database.DatabaseErrorHandler;
import android.database.sqlite.SQLiteDatabase;
import android.os.Environment;

import com.invengo.warehouse.enums.EmLocalSql;

import java.io.File;

/**
 * 数据库位置
 * Created by LZR on 2017/9/21.
 */

public class SdDb extends ContextWrapper {
	public SdDb (Context c) {
		super(c);
	}

	@Override
	public File getDatabasePath(String name) {
		String p = EmLocalSql.sdDir.toString() + "DB/" + name;
		if (!p.endsWith(".db")) {
			p += ".db";
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
