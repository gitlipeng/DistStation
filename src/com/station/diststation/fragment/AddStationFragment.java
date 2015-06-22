package com.station.diststation.fragment;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.baidu.location.BDLocation;
import com.station.diststation.R;
import com.station.diststation.SetDataActivity;
import com.station.diststation.bean.StationBean;
import com.station.diststation.inter.LocationListener;
import com.station.diststation.service.DataProvider;
import com.station.diststation.service.LocationService;

public class AddStationFragment extends Fragment implements OnClickListener,LocationListener {

	private EditText mStationText;

	private EditText mLockText;

	private EditText mLockNoText;

	private EditText mLong;

	private EditText mLat;

	private DataProvider provider;
	
	private LocationService locationService;
	
	private ProgressBar mProgressBar;
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		provider = new DataProvider(getActivity());
		locationService = new LocationService(getActivity());
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.addstation, null);
		initView(view);
		return view;
	}

	private void initView(View view) {
		mStationText = (EditText) view.findViewById(R.id.station);
		mLockText = (EditText) view.findViewById(R.id.lock);
		mLockNoText = (EditText) view.findViewById(R.id.lock_no);
		mLong = (EditText) view.findViewById(R.id.longitude);
		mLat = (EditText) view.findViewById(R.id.latitude);
		mProgressBar = (ProgressBar)  view.findViewById(R.id.progressBar);

		view.findViewById(R.id.btn_getlong).setOnClickListener(this);
		view.findViewById(R.id.btn_getlat).setOnClickListener(this);
		view.findViewById(R.id.btn_back).setOnClickListener(this);
		view.findViewById(R.id.btn_add).setOnClickListener(this);
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
			case R.id.btn_getlong:
			case R.id.btn_getlat:
				// 获取经纬度
				locationService.requestLocation(this);
				mProgressBar.setVisibility(View.VISIBLE);
				break;
			case R.id.btn_back:
				// 返回主页面
				if (getActivity() != null)
					getActivity().finish();
				break;
			case R.id.btn_add:
				// 添加
				String station = mStationText.getText().toString();
				String lock = mLockText.getText().toString();
				String lockNo = mLockNoText.getText().toString();
				String longitude = mLong.getText().toString();
				String latitude = mLat.getText().toString();
				try {
	                Double.valueOf(longitude);
	                Double.valueOf(latitude);
                }
                catch (Exception e) {
                	Toast.makeText(getActivity(), "经纬度输入错误，请重新输入", Toast.LENGTH_SHORT).show();
                	return;
                }
				StationBean bean = new StationBean();
				bean.setStation(station);
				bean.setLock(lock);
				bean.setLockNo(lockNo);
				bean.setLongitude(longitude);
				bean.setLatitude(latitude);

				try {
	                provider.insertContact(bean);
	                Toast.makeText(getActivity(), "添加成功", Toast.LENGTH_SHORT).show();
	                ((SetDataActivity)getActivity()).refresh();
                }
                catch (Exception e) {
	                e.printStackTrace();
                }
				break;
			default:
				break;
		}
	}

	@Override
    public void getLocation(BDLocation location) {
		if (location != null) {
			mLong.setText(String.valueOf(location.getLongitude()));
			mLat.setText(String.valueOf(location.getLatitude()));
		}
		mProgressBar.setVisibility(View.GONE);
    }
}
