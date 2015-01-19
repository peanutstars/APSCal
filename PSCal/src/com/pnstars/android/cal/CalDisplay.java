package com.pnstars.android.cal;

import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.ArrayList;
import java.util.Locale;

import android.app.Activity;
import android.text.Editable;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.pnstars.android.R;
import com.pnstars.android.helper.SwipeDismissListViewTouchListener;

public class CalDisplay {
	
	public enum ResultFormat { RESULT, MESSAGE };
	
	private Activity mActivity;
	private EditText mFormula;
	private TextView mResult;
	private ListView mLvHistory;
	private LinearLayout mLFormula;
	private LinearLayout mLHistory;
	private CalHistory mCalHistory;
	private LinearLayout mLPad;
	private CalListViewAdapter mAdapter;

	public CalDisplay (Activity activity) {
		mActivity = activity;
		
		mFormula = (EditText) mActivity.findViewById(R.id.tvFormula);
		mResult = (TextView) mActivity.findViewById(R.id.tvResult);
		mLvHistory = (ListView) mActivity.findViewById(R.id.lv_history);
		mLFormula = (LinearLayout) mActivity.findViewById(R.id.layoutFormula);
		mLHistory = (LinearLayout) mActivity.findViewById(R.id.layoutHistory);
		mLPad = (LinearLayout) mActivity.findViewById(R.id.layoutPad);

		mFormula.setText("");
		mFormula.setFocusable(false);
		mFormula.setCursorVisible(false);
//		mFormula.setVerticalScrollBarEnabled(true);
		
		mResult.setText("");
		mResult.setSingleLine();
	}

	public void resetFormula () {
		mFormula.setText("");
	}

	public void resetResult () {
		mResult.setText("");
	}
	public String getResultFormuat (String v) {
		Locale locale = mActivity.getResources().getConfiguration().locale;
		DecimalFormat df = new DecimalFormat("###,###.##########", new DecimalFormatSymbols(locale));
		BigDecimal bd = new BigDecimal(v);
		return df.format(bd.doubleValue());
	}
	public void setResult (ResultFormat rf, String v) {
		if (rf == ResultFormat.MESSAGE) {
			mResult.setText(v);
		} else {
			mResult.setText(v);
		}
	}
	
	public void append(String v) {
		((Editable) mFormula.getText()).append(v);
	}
	
	public void delete() {
		int length = mFormula.getText().length();
		if (length > 0) {
			((Editable) mFormula.getText()).delete(length-1, length);
		}
	}
	
	public String getFormula() {
		return mFormula.getText().toString();
	}
	
	private void fillHistory (CalHistory history) {
		ArrayList<CalItem> lhistory = history.getHistory();
		mCalHistory = history;
		mAdapter = new CalListViewAdapter(mActivity, R.layout.list_history, lhistory);
		mLvHistory.setAdapter(mAdapter);
		
		SwipeDismissListViewTouchListener touchListener = 
				new SwipeDismissListViewTouchListener(
						mLvHistory,
						new SwipeDismissListViewTouchListener.DismissCallbacks() {
							
							@Override
							public void onDismiss(ListView listView, int[] reverseSortedPositions) {
								for (int position : reverseSortedPositions) {
									mCalHistory.delItem(position);
									mAdapter.remove(mAdapter.getItem(position));
								}
								mAdapter.notifyDataSetChanged();
							}
							
							@Override
							public boolean canDismiss(int position) {
								return true;
							}
						});
		mLvHistory.setOnTouchListener(touchListener);
	}
	
	public void history(CalHistory history) {
		if (mLHistory.getVisibility() == View.GONE) {
			fillHistory(history);
			mLFormula.setVisibility(View.GONE);
			mLHistory.setVisibility(View.VISIBLE);
			mLPad.setVisibility(View.GONE);
		} else {
			mLFormula.setVisibility(View.VISIBLE);
			mLHistory.setVisibility(View.GONE);
			mLPad.setVisibility(View.VISIBLE);
		}
	}
	
	public void historyClear (CalHistory history){
		history.clear();
		fillHistory(history);
	}
}
