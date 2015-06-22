package com.station.diststation;

import java.util.ArrayList;

import android.app.Activity;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.baidu.location.BDLocation;
import com.baidu.navisdk.BaiduNaviManager;
import com.baidu.navisdk.CommonParams.Const.ModelName;
import com.baidu.navisdk.CommonParams.NL_Net_Mode;
import com.baidu.navisdk.comapi.mapcontrol.BNMapController;
import com.baidu.navisdk.comapi.mapcontrol.MapParams.Const.LayerMode;
import com.baidu.navisdk.comapi.routeguide.RouteGuideParams.RGLocationMode;
import com.baidu.navisdk.comapi.routeplan.BNRoutePlaner;
import com.baidu.navisdk.comapi.routeplan.IRouteResultObserver;
import com.baidu.navisdk.comapi.routeplan.RoutePlanParams.NE_RoutePlan_Mode;
import com.baidu.navisdk.model.NaviDataEngine;
import com.baidu.navisdk.model.RoutePlanModel;
import com.baidu.navisdk.model.datastruct.RoutePlanNode;
import com.baidu.navisdk.ui.routeguide.BNavConfig;
import com.baidu.navisdk.ui.routeguide.BNavigator;
import com.baidu.navisdk.ui.widget.RoutePlanObserver;
import com.baidu.navisdk.util.common.ScreenUtil;
import com.baidu.nplatform.comapi.map.MapGLSurfaceView;
import com.station.diststation.inter.LocationListener;
import com.station.diststation.service.LocationService;

public class MapActivity extends Activity implements LocationListener {

	private LocationService locationService;

	private RoutePlanModel mRoutePlanModel = null;

	private MapGLSurfaceView mMapView = null;

	double startLatitude = 0, startLongitude = 0, endLatitude = 0, endLongitude = 0;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.map);

		Intent intent = getIntent();
		if (intent != null) {
			endLatitude = intent.getDoubleExtra("latitude", 0);
			endLongitude = intent.getDoubleExtra("longitude", 0);
		}

		locationService = new LocationService(this);
		locationService.requestLocation(this);

	}

	@Override
	public void onResume() {
		super.onResume();
		initMapView();
		((ViewGroup) (findViewById(R.id.mapview_layout))).addView(mMapView);
		BNMapController.getInstance().onResume();
	}

	@Override
	public void onPause() {
		super.onPause();
		BNRoutePlaner.getInstance().setRouteResultObserver(null);
		((ViewGroup) (findViewById(R.id.mapview_layout))).removeAllViews();
		BNMapController.getInstance().onPause();
	}

	/**
	 * 开始导航
	 * 
	 * @param v
	 */
	public void naviMap(View v) {
		startNavi(true);
	}

	private void initMapView() {
		if (Build.VERSION.SDK_INT < 14) {
			BaiduNaviManager.getInstance().destroyNMapView();
		}

		mMapView = BaiduNaviManager.getInstance().createNMapView(this);
		BNMapController.getInstance().setLevel(14);
		BNMapController.getInstance().setLayerMode(LayerMode.MAP_LAYER_MODE_BROWSE_MAP);
		updateCompassPosition();

		BNMapController.getInstance().locateWithAnimation((int) (endLongitude * 1e5), (int) (endLatitude * 1e5));
	}

	/**
	 * 更新指南针位置
	 */
	private void updateCompassPosition() {
		int screenW = this.getResources().getDisplayMetrics().widthPixels;
		BNMapController.getInstance().resetCompassPosition(screenW - ScreenUtil.dip2px(this, 30), ScreenUtil.dip2px(this, 126), -1);
	}

	/**
	 * 规划线路
	 * 
	 * @param netmode
	 */
	private void startCalcRoute(int netmode) {
		// 起点
		// GeoPoint startPoint = new GeoPoint((int) (sX * 1E6), (int) (sY *
		// 1E6));
		// RoutePlanNode startNode = new RoutePlanNode(startPoint,
		// RoutePlanNode.FROM_MAP_POINT, "", "");

		RoutePlanNode startNode = new RoutePlanNode((int) (startLatitude * 1E5), (int) (startLongitude * 1E5), RoutePlanNode.FROM_MAP_POINT, "", "");
		// 终点

		// GeoPoint endPoint = new GeoPoint((int) (eX * 1E6), (int) (eY * 1E6));
		// RoutePlanNode endNode = new RoutePlanNode(endPoint,
		// RoutePlanNode.FROM_MAP_POINT, "", "");

		RoutePlanNode endNode = new RoutePlanNode((int) (endLatitude * 1E5), (int) (endLongitude * 1E5), RoutePlanNode.FROM_MAP_POINT, "", "");
		// 将起终点添加到nodeList
		ArrayList<RoutePlanNode> nodeList = new ArrayList<RoutePlanNode>(2);
		nodeList.add(startNode);
		nodeList.add(endNode);
		BNRoutePlaner.getInstance().setObserver(new RoutePlanObserver(this, null));
		// 设置算路方式
		BNRoutePlaner.getInstance().setCalcMode(NE_RoutePlan_Mode.ROUTE_PLAN_MOD_MIN_TIME);
		// 设置算路结果回调
		BNRoutePlaner.getInstance().setRouteResultObserver(mRouteResultObserver);
		// 设置起终点并算路
		boolean ret = BNRoutePlaner.getInstance().setPointsToCalcRoute(nodeList, NL_Net_Mode.NL_Net_Mode_OnLine);
		if (!ret) {
			Toast.makeText(this, "规划失败", Toast.LENGTH_SHORT).show();
			findViewById(R.id.progressbar).setVisibility(View.GONE);
		}
	}

	private void startNavi(boolean isReal) {
		if (mRoutePlanModel == null) {
			Toast.makeText(this, "正在规划路线，请稍等！", Toast.LENGTH_LONG).show();
			return;
		}
		// 获取路线规划结果起点
		RoutePlanNode startNode = mRoutePlanModel.getStartNode();
		// 获取路线规划结果终点
		RoutePlanNode endNode = mRoutePlanModel.getEndNode();
		if (null == startNode || null == endNode) {
			return;
		}
		// 获取路线规划算路模式
		int calcMode = BNRoutePlaner.getInstance().getCalcMode();
		Bundle bundle = new Bundle();
		bundle.putInt(BNavConfig.KEY_ROUTEGUIDE_VIEW_MODE, BNavigator.CONFIG_VIEW_MODE_INFLATE_MAP);
		bundle.putInt(BNavConfig.KEY_ROUTEGUIDE_CALCROUTE_DONE, BNavigator.CONFIG_CLACROUTE_DONE);
		bundle.putInt(BNavConfig.KEY_ROUTEGUIDE_START_X, startNode.getLongitudeE6());
		bundle.putInt(BNavConfig.KEY_ROUTEGUIDE_START_Y, startNode.getLatitudeE6());
		bundle.putInt(BNavConfig.KEY_ROUTEGUIDE_END_X, endNode.getLongitudeE6());
		bundle.putInt(BNavConfig.KEY_ROUTEGUIDE_END_Y, endNode.getLatitudeE6());
		bundle.putString(BNavConfig.KEY_ROUTEGUIDE_START_NAME, mRoutePlanModel.getStartName(this, false));
		bundle.putString(BNavConfig.KEY_ROUTEGUIDE_END_NAME, mRoutePlanModel.getEndName(this, false));
		bundle.putInt(BNavConfig.KEY_ROUTEGUIDE_CALCROUTE_MODE, calcMode);
		if (!isReal) {
			// 模拟导航
			bundle.putInt(BNavConfig.KEY_ROUTEGUIDE_LOCATE_MODE, RGLocationMode.NE_Locate_Mode_RouteDemoGPS);
		} else {
			// GPS 导航
			bundle.putInt(BNavConfig.KEY_ROUTEGUIDE_LOCATE_MODE, RGLocationMode.NE_Locate_Mode_GPS);
		}

		Intent intent = new Intent(this, BNavigatorActivity.class);
		intent.putExtras(bundle);
		startActivity(intent);
		this.finish();
	}

	private IRouteResultObserver mRouteResultObserver = new IRouteResultObserver() {

		@Override
		public void onRoutePlanYawingSuccess() {
		}

		@Override
		public void onRoutePlanYawingFail() {
			findViewById(R.id.progressbar).setVisibility(View.GONE);
			Toast.makeText(MapActivity.this, "规划失败", Toast.LENGTH_SHORT).show();
		}

		@Override
		public void onRoutePlanSuccess() {
			BNMapController.getInstance().setLayerMode(LayerMode.MAP_LAYER_MODE_ROUTE_DETAIL);
			mRoutePlanModel = (RoutePlanModel) NaviDataEngine.getInstance().getModel(ModelName.ROUTE_PLAN);
			findViewById(R.id.progressbar).setVisibility(View.GONE);
		}

		@Override
		public void onRoutePlanFail() {
		}

		@Override
		public void onRoutePlanCanceled() {
		}

		@Override
		public void onRoutePlanStart() {

		}

	};

	@Override
	public void getLocation(BDLocation location) {
		if (location != null) {
		startLongitude = location.getLongitude();
		startLatitude = location.getLatitude();
		}
		Log.i("msg", "定位：sLongitude:" + startLongitude + ",sLatitude:" + startLatitude);

		startCalcRoute(NL_Net_Mode.NL_Net_Mode_OnLine);
	}
}
