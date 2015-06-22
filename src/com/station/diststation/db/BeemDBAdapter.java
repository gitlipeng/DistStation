package com.station.diststation.db;

import java.io.File;

import android.content.Context;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteDatabase.CursorFactory;
import android.database.sqlite.SQLiteException;
import android.database.sqlite.SQLiteOpenHelper;
import android.os.Environment;

public class BeemDBAdapter {

	public static final String DATABASE_NAME = "IContact.db";

	public static final int DATABASE_VERSION = 1;

	public SQLiteDatabase db;

	public Context context;

	public DataBaseHelper dbHelper;

	/** 分隔符 */
	public static final String sign = File.separator;

	// /**主目录路径*/
	public static final String mainPath = Environment.getExternalStorageDirectory().getPath() + sign + "station.db";

	public BeemDBAdapter(Context context) {
		this.context = context;
		// dbHelper = new DataBaseHelper(context, DATABASE_NAME, null,
		// DATABASE_VERSION);
	}

	public BeemDBAdapter open() throws SQLException {
		// 调用getWriteableDatabase可能因为磁盘空间或权限问题失败
		try {
			db = SQLiteDatabase.openOrCreateDatabase(mainPath, null);
		}
		catch (SQLiteException ex) {
			// db = dbHelper.getReadableDatabase();
			ex.printStackTrace();
		}
		return this;
	}

	public void close() {
		if (db != null)
			db.close();
	}

	protected static class DataBaseHelper extends SQLiteOpenHelper {

		public DataBaseHelper(Context context, String name, CursorFactory factory, int version) {
			super(context, name, factory, version);
		}

		@Override
		public void onCreate(SQLiteDatabase db) {
//			db.execSQL(ContactTable.CREATESQL);
//			db.execSQL(OrgTable.CREATESQL);
		}

		@Override
		public void onUpgrade(SQLiteDatabase db, int _oldVersion, int _newVersion) {
			db.execSQL("DROP TABLE IF EXISTS " + DATABASE_NAME);
			onCreate(db);
		}
	}

}
