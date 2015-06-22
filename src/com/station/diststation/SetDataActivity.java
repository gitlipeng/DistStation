package com.station.diststation;

import com.station.diststation.fragment.AddStationFragment;
import com.station.diststation.fragment.DeleteStationFragment;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentTransaction;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

public class SetDataActivity extends FragmentActivity implements OnClickListener {

	private FrameLayout mFragmentLayout;

	Fragment addFragment;

	DeleteStationFragment deleteFragment;

	private TextView mAddStationTv;

	private ImageView mAddStationArrow;

	private TextView mDeleteStationTv;

	private ImageView mDeleteStationArrow;

	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.setdata);

		addFragment = new AddStationFragment();
		deleteFragment = new DeleteStationFragment();
		FragmentTransaction ft = this.getSupportFragmentManager().beginTransaction();
		ft.replace(R.id.fragment_layout, addFragment);
		ft.commit();

		initView();
	}

	private void initView() {
		// 设置标头
		((TextView) findViewById(R.id.head_title)).setText("基础信息建设");
		// 返回按钮
		findViewById(R.id.btn_return).setOnClickListener(this);

		mAddStationTv = (TextView) findViewById(R.id.tv_addstation);
		mAddStationTv.setOnClickListener(this);
		mAddStationArrow = (ImageView) findViewById(R.id.arrow_loginbyphone);

		mDeleteStationTv = (TextView) findViewById(R.id.tv_deletestation);
		mDeleteStationTv.setOnClickListener(this);
		mDeleteStationArrow = (ImageView) findViewById(R.id.normal_loginbyphone);

		mFragmentLayout = (FrameLayout) findViewById(R.id.fragment_layout);
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
			case R.id.btn_return:
				// 返回
				finish();
				break;
			case R.id.tv_addstation:
				mAddStationTv.setBackgroundResource(R.drawable.login_gray_bg);
				mAddStationArrow.setVisibility(View.VISIBLE);

				mDeleteStationTv.setBackgroundDrawable(null);
				mDeleteStationArrow.setVisibility(View.GONE);
				if (addFragment.isAdded()) {
					FragmentTransaction ft = this.getSupportFragmentManager().beginTransaction();
					ft.show(addFragment).hide(deleteFragment);
					ft.commit();
				}
				break;
			case R.id.tv_deletestation:
				mDeleteStationTv.setBackgroundResource(R.drawable.login_gray_bg);
				mDeleteStationArrow.setVisibility(View.VISIBLE);
				mAddStationTv.setBackgroundDrawable(null);
				mAddStationArrow.setVisibility(View.GONE);
				
				if (deleteFragment.isAdded()) {
					FragmentTransaction ft = this.getSupportFragmentManager().beginTransaction();
					ft.show(deleteFragment).hide(addFragment);
					ft.commit();
				} else {
					FragmentTransaction ft = this.getSupportFragmentManager().beginTransaction();
					ft.add(R.id.fragment_layout, deleteFragment).hide(addFragment);
					ft.commit();
				}
				break;
			default:
				break;
		}
	};
	
	public void refresh(){
		if(deleteFragment.isAdded()){
			deleteFragment.refresh();
		}
	}
}
