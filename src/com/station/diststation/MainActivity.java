package com.station.diststation;

import android.app.Activity;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.os.Environment;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.TextView;
import android.widget.Toast;

import com.baidu.lbsapi.auth.LBSAuthManagerListener;
import com.baidu.navisdk.BaiduNaviManager;
import com.baidu.navisdk.BNaviEngineManager.NaviEngineInitListener;
import com.station.diststation.db.BeemDBAdapter;
import com.station.diststation.db.StationTable;

public class MainActivity extends Activity implements OnClickListener {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		createTable();
		initView();
		initBaiduMap();
		
	}

	private void initView() {
		// 设置标头
		((TextView) findViewById(R.id.head_title)).setText("基础数据添加");
		// 返回按钮
		findViewById(R.id.btn_return).setVisibility(View.GONE);

		findViewById(R.id.setdata).setOnClickListener(this);
		findViewById(R.id.map).setOnClickListener(this);
	}
	
	private void initBaiduMap(){
		BaiduNaviManager.getInstance().initEngine(this, getSdcardDir(), mNaviEngineInitListener, new LBSAuthManagerListener() {

			@Override
			public void onAuthResult(int status, String msg) {
				String str = null;
				if (0 == status) {
					str = "key校验成功!";
				} else {
					str = "key校验失败, " + msg;
				}
				Toast.makeText(MainActivity.this, str, Toast.LENGTH_LONG).show();
			}
		});
	}

	private void createTable() {
		SQLiteDatabase sqLiteDatabase = SQLiteDatabase.openOrCreateDatabase(BeemDBAdapter.mainPath, null);
		// 创建用户信息表
		if (sqLiteDatabase.getVersion() != 3) {
			sqLiteDatabase.execSQL(StationTable.DELETESQL);
			sqLiteDatabase.execSQL(StationTable.CREATESQL);
			sqLiteDatabase.setVersion(3);
		}
		sqLiteDatabase.close();
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
			case R.id.setdata:
				// 基础数据建设
				Intent intent = new Intent(this, SetDataActivity.class);
				intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
				startActivity(intent);
				break;
			case R.id.map:
				// 导航
				intent = new Intent(this, StationListActivity.class);
				intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
				startActivity(intent);
				break;
			default:
				break;
		}
	}
	
	private String getSdcardDir() {
		if (Environment.getExternalStorageState().equalsIgnoreCase(Environment.MEDIA_MOUNTED)) {
			return Environment.getExternalStorageDirectory().toString();
		}
		return null;
	}

	private NaviEngineInitListener mNaviEngineInitListener = new NaviEngineInitListener() {

		public void engineInitSuccess() {
		}

		public void engineInitStart() {
		}

		public void engineInitFail() {
		}
	};

}
