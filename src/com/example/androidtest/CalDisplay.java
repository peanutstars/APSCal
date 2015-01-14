package com.example.androidtest;

import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.text.NumberFormat;
import java.util.Locale;

import android.app.Activity;
import android.text.Editable;
import android.widget.TextView;

public class CalDisplay {
	
	public enum ResultFormat { RESULT, MESSAGE };
	
	private Activity mActivity;
	private TextView mFormula;
	private TextView mResult;

	public CalDisplay (Activity activity) {
		mActivity = activity;
		
		mFormula = (TextView) mActivity.findViewById(R.id.tvFormula);
		mResult = (TextView) mActivity.findViewById(R.id.tvResult);

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
}
