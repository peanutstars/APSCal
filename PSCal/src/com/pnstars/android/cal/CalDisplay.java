package com.pnstars.android.cal;

import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Locale;

import android.app.Activity;
import android.text.Editable;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;

import com.pnstars.android.R;

public class CalDisplay {
	
	public enum ResultFormat { RESULT, MESSAGE };
	
	private Activity mActivity;
	private TextView mFormula;
	private TextView mResult;
	private ListView mLvHistory;
	private LinearLayout mBtnPad;

	public CalDisplay (Activity activity) {
		mActivity = activity;
		
		mFormula = (TextView) mActivity.findViewById(R.id.tvFormula);
		mResult = (TextView) mActivity.findViewById(R.id.tvResult);
		mLvHistory = (ListView) mActivity.findViewById(R.id.lv_history);
		mBtnPad = (LinearLayout) mActivity.findViewById(R.id.btn_pad);

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
	public void setResult (ResultFormat rf, String v) {
		if (rf == ResultFormat.MESSAGE) {
			mResult.setText(v);
		} else {
			Locale locale = mActivity.getResources().getConfiguration().locale;
			DecimalFormat df = new DecimalFormat("###,###.#####", new DecimalFormatSymbols(locale));
			BigDecimal bd = new BigDecimal(v);
			mResult.setText(df.format(bd.doubleValue()));
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
		ArrayList<HashMap<String, String>> lhistory = history.getHistory();
		ListAdapter adapter = new SimpleAdapter(mActivity, lhistory,R.layout.list_history,
				new String[] { CalItem.TAG_FORMULA, CalItem.TAG_Result },
				new int[] { R.id.itemFormula, R.id.itemResult} );
		mLvHistory.setAdapter(adapter);
	}
	
	public void history(CalHistory history) {
		if (mLvHistory.getVisibility() == View.GONE) {
			fillHistory(history);
			mLvHistory.setVisibility(View.VISIBLE);
			mBtnPad.setVisibility(View.GONE);
		} else {
			mLvHistory.setVisibility(View.GONE);
			mBtnPad.setVisibility(View.VISIBLE);
		}
	}
}
