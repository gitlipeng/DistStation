package com.station.diststation.adapter;

import java.util.List;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.TextView;

import com.station.diststation.R;
import com.station.diststation.bean.StationBean;

public class StationListAdapter extends BaseAdapter {

	private List<StationBean> list;

	private LayoutInflater inflater;

	private Context context;

	private boolean hideCheckBox;
	public StationListAdapter(Context context, List<StationBean> list,boolean hideCheckBox) {
		this.context = context;
		this.list = list;
		this.hideCheckBox = hideCheckBox;
		this.inflater = LayoutInflater.from(context);
	}

	@Override
	public int getCount() {
		return list.size();
	}

	@Override
	public Object getItem(int arg0) {
		return list.get(arg0);
	}

	@Override
	public long getItemId(int arg0) {
		return arg0;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		ViewHolder holder;
		if (convertView == null) {
			holder = new ViewHolder();
			convertView = inflater.inflate(R.layout.station_item, null);

			holder.station = (TextView) convertView.findViewById(R.id.station);
			holder.lock = (TextView) convertView.findViewById(R.id.lock);
			holder.checkdelete = (CheckBox) convertView.findViewById(R.id.checkdelete);
			convertView.setTag(holder);
		} else {
			holder = (ViewHolder) convertView.getTag();
		}

		StationBean bean = list.get(position);

		holder.station.setText(bean.getStation());
		holder.lock.setText(bean.getLock());

		if(hideCheckBox){
			holder.checkdelete.setVisibility(View.GONE);
		}else{
			holder.checkdelete.setVisibility(View.VISIBLE);
		}
		
		if (bean.isChecked()) {
			holder.checkdelete.setChecked(true);
		} else {
			holder.checkdelete.setChecked(false);
		}

		return convertView;
	}

	final class ViewHolder {

		TextView station;

		TextView lock;

		CheckBox checkdelete;
	}
}
