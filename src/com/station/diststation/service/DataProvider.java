package com.station.diststation.service;

import java.util.ArrayList;
import java.util.List;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;

import com.station.diststation.bean.StationBean;
import com.station.diststation.db.BeemDBAdapter;
import com.station.diststation.db.StationTable;

public class DataProvider extends BeemDBAdapter {

	public DataProvider(Context context) {
		super(context);
	}

	public synchronized long insertContact(StationBean bean) throws Exception {
		boolean chkflg = false;
		if (db == null) {
			// 内部打开数据库
			open();
			chkflg = true;
		}
		if (db != null) {
			ContentValues values = new ContentValues();
			values.put(StationTable.F_STATION, bean.getStation());
			values.put(StationTable.F_LOCK, bean.getLock());
			values.put(StationTable.F_LOCKNO, bean.getLockNo());
			values.put(StationTable.F_LATITUDE, bean.getLatitude());
			values.put(StationTable.F_LONGITUDE, bean.getLongitude());

			String tableName = "'" + StationTable.TABLENAME + "'";
			try {
				long number = db.insert(tableName, null, values);
				if (number == -1) {
					throw new Exception();
				}
				// return number;
			}
			catch (Exception e) {
				throw e;
			}
		}

		if (chkflg) {
			close();// 内部打开，内部关闭
			db = null;
		}
		return -1;
	}

	public synchronized List<StationBean> queryContactList() {
		boolean chkflg = false;
		if (db == null) {
			// 内部打开数据库
			open();
			chkflg = true;
		}
		if (db != null) {
			String selectSql = "SELECT " + StationTable.F_ID + ", "+ StationTable.F_STATION + ", " + StationTable.F_LOCK + ", " + StationTable.F_LOCKNO + ", "
			        + StationTable.F_LONGITUDE + ", " + StationTable.F_LATITUDE + " " + " FROM '" + StationTable.TABLENAME + "' ";
			// LogUtil.getInstance().i("selectSql:	" + selectSql);
			List<StationBean> contactList = new ArrayList<StationBean>();
			Cursor cursor = db.rawQuery(selectSql, null);

			for (cursor.moveToFirst(); !cursor.isAfterLast(); cursor.moveToNext()) {
				StationBean bean = new StationBean();
				bean.setId(cursor.getString(cursor.getColumnIndex(StationTable.F_ID)));
				bean.setStation(cursor.getString(cursor.getColumnIndex(StationTable.F_STATION)));
				bean.setLock(cursor.getString(cursor.getColumnIndex(StationTable.F_LOCK)));
				bean.setLockNo(cursor.getString(cursor.getColumnIndex(StationTable.F_LOCKNO)));
				bean.setLatitude(cursor.getString(cursor.getColumnIndex(StationTable.F_LATITUDE)));
				bean.setLongitude(cursor.getString(cursor.getColumnIndex(StationTable.F_LONGITUDE)));
				contactList.add(bean);
			}
			cursor.close();
			if (chkflg) {
				close();// 内部打开，内部关闭
				db = null;
			}
			return contactList;
		}
		if (chkflg) {
			close();// 内部打开，内部关闭
			db = null;
		}
		return null;
	}

	public synchronized long deleteContacts(List<StationBean> list) throws Exception {
		boolean chkflg = false;
		if (db == null) {
			// 内部打开数据库
			open();
			chkflg = true;
		}
		long number = 0;
		if (db != null) {
			long start = System.currentTimeMillis();
			db.beginTransaction();// 开启事务
			for (StationBean bean : list) {
				try {
					number = deleteContact(bean);
				}
				catch (Exception e) {
					db.endTransaction();
					if (chkflg) {
						close();// 内部打开，内部关闭
						db = null;
					}
					throw e;
				}
				if (number == -1) {
					db.endTransaction();// 出错，关闭事务，回滚
					if (chkflg) {
						close();// 内部打开，内部关闭
						db = null;
					}
					throw new Exception();
				}
			}
			db.setTransactionSuccessful();// 成功
			db.endTransaction();// 关闭事务，提交
		}

		if (chkflg) {
			close();// 内部打开，内部关闭
			db = null;
		}
		return number;
	}

	public synchronized long deleteContact(StationBean bean) throws Exception {
		boolean chkflg = false;
		if (db == null) {
			// 内部打开数据库
			open();
			chkflg = true;
		}
		if (db != null) {

			String tableName = "'" + StationTable.TABLENAME + "'";
			try {
				long number = db.delete(tableName, StationTable.F_ID + "==?", new String[] { bean.getId() });
				if (number == -1) {
					throw new Exception();
				}
				return number;
			}
			catch (Exception e) {
				throw e;
			}
		}

		if (chkflg) {
			close();// 内部打开，内部关闭
			db = null;
		}

		return -1;
	}
}
