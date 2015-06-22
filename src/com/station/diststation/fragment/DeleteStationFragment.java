package com.station.diststation.fragment;

import java.util.ArrayList;
import java.util.List;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ListView;

import com.station.diststation.R;
import com.station.diststation.adapter.StationListAdapter;
import com.station.diststation.bean.StationBean;
import com.station.diststation.service.DataProvider;

public class DeleteStationFragment extends Fragment implements OnClickListener {

	private ListView listView;

	private StationListAdapter adapter;

	private List<StationBean> list = new ArrayList<StationBean>();

	private DataProvider provider;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		provider = new DataProvider(getActivity());
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.deletestation, null);
		listView = (ListView) view.findViewById(R.id.list);
		view.findViewById(R.id.btn_delete).setOnClickListener(this);
		((CheckBox)view.findViewById(R.id.checkall)).setOnCheckedChangeListener(new MyOnCheckedChangeListener());
		
		listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				StationBean bean = list.get(position);
				if (bean.isChecked()) {
					bean.setChecked(false);
				} else {
					bean.setChecked(true);
				}
				adapter.notifyDataSetChanged();
			}

		});
		refresh();
		return view;
	}

	public void refresh() {
		list = provider.queryContactList();
		adapter = new StationListAdapter(getActivity(), list,false);
		listView.setAdapter(adapter);
		adapter.notifyDataSetChanged();
	}
	
	@Override
	public void onClick(View v) {
		switch (v.getId()) {
			case R.id.btn_delete:
				// 删除
				delete();
				break;
			default:
				break;
		}
	}
	
	private void delete(){
		Dialog bindingDialog = new AlertDialog.Builder(getActivity()).setTitle("确认删除")
		        .setNegativeButton("取消", new DialogInterface.OnClickListener() {

			        @Override
			        public void onClick(final DialogInterface dialog, final int which) {
				        dialog.dismiss();
			        }
		        }).setPositiveButton("确认", new DialogInterface.OnClickListener() {
			        @Override
			        public void onClick(final DialogInterface dialog, final int which) {
				        dialog.dismiss();
				        List<StationBean> deleteList = new ArrayList<StationBean>();
						for (StationBean bean : list) {
							if (bean.isChecked()) {
								deleteList.add(bean);
							}
						}

						if (deleteList.size() > 0) {
							try {
			                    provider.deleteContacts(deleteList);
			                    refresh();
		                    }
		                    catch (Exception e) {
			                    e.printStackTrace();
		                    }
						}
			        }
		        }).setMessage("确认删除？").setCancelable(true).create();
		bindingDialog.setCanceledOnTouchOutside(true);
		bindingDialog.show();
	}
	
	class MyOnCheckedChangeListener implements CompoundButton.OnCheckedChangeListener{

		@Override
        public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
	        if(isChecked){
	        	for (StationBean bean : list) {
	        		bean.setChecked(true);
	        	}
	        	adapter.notifyDataSetChanged();
	        }else{
	        	for (StationBean bean : list) {
	        		bean.setChecked(false);
	        	}
	        	adapter.notifyDataSetChanged();
	        }
        }
		
	}
}
