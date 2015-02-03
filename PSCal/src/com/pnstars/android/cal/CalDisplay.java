package com.pnstars.android.cal;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;
import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.ArrayList;
import java.util.Locale;

import android.app.Activity;
import android.graphics.Typeface;
import android.text.Editable;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;

import com.pnstars.android.R;
import com.pnstars.android.helper.SwipeDismissListViewTouchListener;

public class CalDisplay implements CalFile.FileOp {
	
	public enum ResultFormat { RESULT, MESSAGE };
	public static final String CalFont			= "fonts/Lato-Regular.ttf";
	
	private Activity mActivity;
	private EditText mFormula;
	private EditText mResult;
	private ListView mLvHistory;
	private LinearLayout mLFormula;
	private LinearLayout mLHistory;
	private CalHistory mCalHistory;
	private LinearLayout mLPad;
	private CalListViewAdapter mAdapter;
	private boolean mFgVisibleHistory;

	public CalDisplay (Activity activity) {
		mActivity = activity;

		mFormula = (EditText) mActivity.findViewById(R.id.tvFormula);
		mResult = (EditText) mActivity.findViewById(R.id.tvResult);
		mLvHistory = (ListView) mActivity.findViewById(R.id.lv_history);
		mLFormula = (LinearLayout) mActivity.findViewById(R.id.layoutFormula);
		mLHistory = (LinearLayout) mActivity.findViewById(R.id.layoutHistory);
		mLPad = (LinearLayout) mActivity.findViewById(R.id.layoutPad);
		mFgVisibleHistory = false;

		Typeface font = Typeface.createFromAsset(mActivity.getAssets(), CalFont);
		
		mFormula.setText("");
		mFormula.setFocusable(false);
		mFormula.setCursorVisible(false);
		mFormula.setTypeface(font);
//		mFormula.setVerticalScrollBarEnabled(true);
		
		mResult.setText("");
		mResult.setFocusable(false);
		mResult.setSingleLine();
		mResult.setCursorVisible(false);
		mResult.setTypeface(font);
	}

	public void resetFormula () {
		mFormula.setText("");
	}

	public void resetResult () {
		mResult.setText("");
	}
	public String convertResultFormatDecimal (String v) {
		Locale locale = mActivity.getResources().getConfiguration().locale;
		DecimalFormat df = new DecimalFormat("###,###.##########", new DecimalFormatSymbols(locale));
		BigDecimal bd = new BigDecimal(v);
		return df.format(bd.doubleValue());
	}
	
	public String convertResultFormat (int radix, String v) {
		String strFormat = v;
		StringBuilder sb = new StringBuilder();
		int i;
		int c;
		if (radix == 10) {
			strFormat = convertResultFormatDecimal(v);
		} else if (radix == 16 || radix == 2 || radix == 8) {
			c = 4 - (v.length()- 2) % 4;
			for (i=0; i<v.length(); i++) {
				if (i < 2) {
					sb.append(v.charAt(i));
				} else {
					sb.append(v.charAt(i));
					if ((++c % 4) == 0) {
						sb.append(' ');
					}
				}
			}
			strFormat = sb.toString();
		}
		return strFormat;
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
	
	public char delete() {
		char ch = (char)0;
		int length = mFormula.getText().length();
		if (length > 0) {
			ch = mFormula.getText().charAt(length-1);
			((Editable) mFormula.getText()).delete(length-1, length);
		}
		// PNSDbg.d("Delete : " + ch);
		return ch;
	}
	
	public String getFormula() {
		return mFormula.getText().toString();
	}
	public String getResult() {
		return mResult.getText().toString();
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
			mFgVisibleHistory = true;
			mLPad.setVisibility(View.GONE);
		} else {
			mLFormula.setVisibility(View.VISIBLE);
			mLHistory.setVisibility(View.GONE);
			mFgVisibleHistory = false;
			mLPad.setVisibility(View.VISIBLE);
		}
	}
	
	public void historyClear (CalHistory history){
		history.clear();
		fillHistory(history);
	}
	
	public boolean isVisibleHistory() {
		return mFgVisibleHistory;
	}
	
	@Override
	public void load(int version, DataInput in) throws IOException {
	}
	@Override
	public void save(DataOutput out) throws IOException {
		out.writeUTF(getFormula());
		out.writeUTF(getResult());
	}
}
