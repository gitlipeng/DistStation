package com.station.diststation;

import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.baidu.location.BDLocation;
import com.baidu.navisdk.BNaviPoint;
import com.baidu.navisdk.BaiduNaviManager;
import com.baidu.navisdk.BaiduNaviManager.OnStartNavigationListener;
import com.baidu.navisdk.comapi.routeplan.RoutePlanParams.NE_RoutePlan_Mode;
import com.station.diststation.adapter.StationListAdapter;
import com.station.diststation.bean.StationBean;
import com.station.diststation.inter.LocationListener;
import com.station.diststation.service.DataProvider;
import com.station.diststation.service.LocationService;

public class StationListActivity extends Activity implements LocationListener, OnClickListener {

	private ListView listView;

	private StationListAdapter adapter;

	private List<StationBean> list = new ArrayList<StationBean>();

	private DataProvider provider;

	double startLatitude = 0, startLongitude = 0, endLatitude = 0, endLongitude = 0;

	private LocationService locationService;

	// private SDKReceiver mReceiver;

	// /**
	// * 构造广播监听类，监听 SDK key 验证以及网络异常广播
	// */
	// public class SDKReceiver extends BroadcastReceiver {
	//
	// public void onReceive(Context context, Intent intent) {
	// String s = intent.getAction();
	// if
	// (s.equals(SDKInitializer.SDK_BROADTCAST_ACTION_STRING_PERMISSION_CHECK_ERROR))
	// {
	// Toast.makeText(StationListActivity.this,
	// "key 验证出错! 请在 AndroidManifest.xml 文件中检查 key 设置",
	// Toast.LENGTH_SHORT).show();
	// } else if
	// (s.equals(SDKInitializer.SDK_BROADCAST_ACTION_STRING_NETWORK_ERROR)) {
	// Toast.makeText(StationListActivity.this, "网络出错",
	// Toast.LENGTH_SHORT).show();
	// }
	// }
	// }

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.stationlist);

		locationService = new LocationService(this);
		locationService.requestLocation(this);

		// 初始化
		init();
		// // 注册 SDK 广播监听者
		// IntentFilter iFilter = new IntentFilter();
		// iFilter.addAction(SDKInitializer.SDK_BROADTCAST_ACTION_STRING_PERMISSION_CHECK_ERROR);
		// iFilter.addAction(SDKInitializer.SDK_BROADCAST_ACTION_STRING_NETWORK_ERROR);
		// mReceiver = new SDKReceiver();
		// registerReceiver(mReceiver, iFilter);
	}

	private void init() {
		// 设置标头
		((TextView) findViewById(R.id.head_title)).setText("导航");
		// 返回按钮
		findViewById(R.id.btn_return).setOnClickListener(this);

		provider = new DataProvider(this);
		listView = (ListView) findViewById(R.id.list);

		list = provider.queryContactList();
		adapter = new StationListAdapter(this, list, true);
		listView.setAdapter(adapter);
		adapter.notifyDataSetChanged();

		listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				StationBean bean = list.get(position);
				String latitude = bean.getLatitude();
				String longitude = bean.getLongitude();
				if (latitude != null && !"".equals(latitude) && longitude != null && !"".equals(longitude)) {
					try {
						endLatitude = Double.valueOf(latitude);
						endLongitude = Double.valueOf(longitude);
					}
					catch (Exception e) {
						Toast.makeText(StationListActivity.this, "经纬度输入错误，无法导航", Toast.LENGTH_SHORT).show();
						return;
					}

					if (startLatitude == 0 || startLongitude == 0) {
						// 定位失败
						locationService.requestLocation(StationListActivity.this);
						findViewById(R.id.progressbar).setVisibility(View.VISIBLE);
						Toast.makeText(StationListActivity.this, "定位中...", Toast.LENGTH_SHORT).show();
						return;
					}

					launchNavigator();

				} else {
					Toast.makeText(StationListActivity.this, "经纬度输入错误，无法导航", Toast.LENGTH_SHORT).show();
				}

			}
		});
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
			case R.id.btn_return:
				// 返回
				finish();
				break;
			default:
				break;
		}
	}

	/**
	 * 开启导航
	 */
	private void launchNavigator() {
		// 这里给出一个起终点示例，实际应用中可以通过POI检索、外部POI来源等方式获取起终点坐标
		BNaviPoint startPoint = new BNaviPoint(startLongitude, startLatitude, "", BNaviPoint.CoordinateType.BD09_MC);
		BNaviPoint endPoint = new BNaviPoint(endLongitude, endLatitude, "", BNaviPoint.CoordinateType.BD09_MC);
		BaiduNaviManager.getInstance().launchNavigator(this, startPoint, // 起点（可指定坐标系）
		                                               endPoint, // 终点（可指定坐标系）
		                                               NE_RoutePlan_Mode.ROUTE_PLAN_MOD_MIN_TIME, // 算路方式
		                                               true, // 真实导航
		                                               BaiduNaviManager.STRATEGY_FORCE_ONLINE_PRIORITY, // 在离线策略
		                                               new OnStartNavigationListener() { // 跳转监听

			                                               @Override
			                                               public void onJumpToNavigator(Bundle configParams) {
				                                               Intent intent = new Intent(StationListActivity.this, BNavigatorActivity.class);
				                                               intent.putExtras(configParams);
				                                               startActivity(intent);
			                                               }

			                                               @Override
			                                               public void onJumpToDownloader() {
			                                               }
		                                               });
	}

	@Override
	public void getLocation(BDLocation location) {

		if (location != null) {
			startLongitude = location.getLongitude();
			startLatitude = location.getLatitude();
		}

		findViewById(R.id.progressbar).setVisibility(View.GONE);
		Log.i("msg", "定位：sLongitude:" + startLongitude + ",sLatitude:" + startLatitude);
	}
}
