package com.station.diststation.db;


public class StationTable {
	//表名
	public static final String TABLENAME = "Table_Station";
	//字段
	public static final String F_ID = "F_ID";//ID
	public static final String F_STATION = "F_STATION";//配电站
	public static final String F_LOCK = "F_LOCK";//智能锁
	public static final String F_LOCKNO = "F_LOCKNO";//锁号
	public static final String F_LONGITUDE = "F_LONGITUDE";//经度longitude
	public static final String F_LATITUDE  = "F_LATITUDE";//纬度latitude
	
	
	public static final String DELETESQL = "DROP TABLE IF EXISTS " + TABLENAME;
	//建表语句
	public static final String CREATESQL = "create table if not exists '" + TABLENAME + "' "
							+ " ( "
							+ F_ID 			 + " integer PRIMARY KEY AUTOINCREMENT , "
							+ F_STATION	 	 + " VARCHAR(100) , "
							+ F_LOCK		 + " VARCHAR(50) , "
							+ F_LOCKNO		 + " VARCHAR(30), "
							+ F_LONGITUDE	 + " VARCHAR(16), "
							+ F_LATITUDE	 + " VARCHAR(16) "
							+ " ) ";
}






















