package com.pnstars.android.cal;

import java.util.List;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.pnstars.android.R;

public class CalListViewAdapter extends ArrayAdapter<CalItem> {

	private int mResourceId;
	private Activity mActivity;
	private List<CalItem> mData;
	private static LayoutInflater mInflater = null;
	
	public CalListViewAdapter (Activity activity, int resourceId, List<CalItem> data) {
		super (activity, resourceId, data);
		mResourceId = resourceId;
		mActivity = activity;
		mData = data;
		mInflater = (LayoutInflater) mActivity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
	}
	
	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		TextView formula;
		TextView result;
		CalItem item;
		
		if (convertView == null) {
			convertView = mInflater.inflate(mResourceId, null);
		}
		
		formula = (TextView) convertView.findViewById(R.id.itemFormula);
		result  = (TextView) convertView.findViewById(R.id.itemResult);
		item = mData.get(position);
		
		formula.setText(item.getFormula());
		result.setText(item.getResult());
		
		return convertView;
	}
}
