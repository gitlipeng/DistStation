package com.station.diststation.service;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.widget.Toast;

import com.baidu.location.BDLocation;
import com.baidu.location.BDLocationListener;
import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;
import com.baidu.location.LocationClientOption.LocationMode;
import com.station.diststation.inter.LocationListener;

public class LocationService {

	private Context context;

	public LocationClient mLocClient;

	private MyLocationListener mMyLocationListener;

	private boolean mLocationInit;

	private LocationListener listener;

	public LocationService(Context context) {
		this.context = context;
		mLocClient = new LocationClient(context.getApplicationContext());
		mMyLocationListener = new MyLocationListener();
		mLocClient.registerLocationListener(mMyLocationListener);
		
		setLocationOption();
	}

	// 设置Option
	private void setLocationOption() {
		try {
			LocationClientOption option = new LocationClientOption();
			option.setLocationMode(LocationMode.Hight_Accuracy);
			option.setCoorType("bd09ll");
			option.setScanSpan(1000);
			option.setNeedDeviceDirect(false);
			option.setIsNeedAddress(true);
			mLocClient.setLocOption(option);
			mLocationInit = true;
		}
		catch (Exception e) {
			e.printStackTrace();
			mLocationInit = false;
		}
	}

	public void requestLocation(LocationListener listener) {
		// 开始定位
		if (mLocationInit) {
			mLocClient.start();
		} else {
			Toast.makeText(context, "请设置定位相关的参数", Toast.LENGTH_SHORT).show();
			return;
		}
		
		if(isNetworkAvailable(context)){
			if (mLocClient != null && mLocClient.isStarted()) {
				// 单次请求定位
				mLocClient.requestLocation();
			}
		}else{
			if (mLocClient != null && mLocClient.isStarted()) {
				// 单次请求定位
				mLocClient.requestOfflineLocation();
			}
		}

		
		this.listener = listener;
	}

	/**
	 * 实现实位回调监听
	 */
	public class MyLocationListener implements BDLocationListener {

		@Override
		public void onReceiveLocation(BDLocation location) {

			mLocClient.stop();
			if (location != null && listener != null) {
				int locType = location.getLocType();
				if(locType == 62 || locType == 63 || locType == 67 || locType == 61 || locType == 162 ||
						locType == 163 || locType == 164 || locType == 165 || locType == 166 || locType == 167){
					listener.getLocation(null);
					Toast.makeText(context, "定位失败，请打开网络", Toast.LENGTH_SHORT).show();
				}else{
					listener.getLocation(location);
				}
			}
		}

		@Override
		public void onReceivePoi(BDLocation arg0) {

		}
	}
	
	public boolean isNetworkAvailable(Context context) {
		ConnectivityManager connectivity = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
		if (connectivity == null) {
			return false;
		} else {
			NetworkInfo in = connectivity.getActiveNetworkInfo();
			if (in != null && in.getState() == NetworkInfo.State.CONNECTED) {
				return true;
			}
			NetworkInfo[] info = connectivity.getAllNetworkInfo();
			if (info != null) {
				for (int i = 0; i < info.length; i++) {
					if (info[i].getState() == NetworkInfo.State.CONNECTED) {
						return true;
					}
				}
			}
		}
		return false;
	}

}
