package com.pnstars.android.cal;

import android.app.Activity;
import android.content.res.TypedArray;
import android.graphics.Typeface;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;

import com.pnstars.android.R;
import com.pnstars.android.helper.PNSScreen;

public class CalMain extends Activity {

	private PNSScreen mScreen;
	private EventListener mListener;
	private CalHistory mHistory;
	private CalLogic mLogic;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		mScreen = new PNSScreen(this);
		mHistory = new CalHistory();
		mLogic = new CalLogic(this, mHistory);
		mListener = new EventListener(mLogic);

		mScreen.setOrientationPortrait();
		
		connectBtnEvent();
	}
	
	@Override
	protected void onPause() {
		super.onPause();
		mLogic.save();
	}
	
	public void connectBtnEvent() {
/*
		Align Center  
		if (mScreen.getWidthDpi() > PNSScreen.BASE_WIDTH_DP) {
			LinearLayout ll = (LinearLayout) findViewById(R.id.layoutButtonArray);
			ViewGroup.MarginLayoutParams mparams = (ViewGroup.MarginLayoutParams) ll.getLayoutParams();
				
			int left = Math.round((mScreen.getWidthDpi() - PNSScreen.BASE_WIDTH_DP) / 2 * mScreen.getDensity());
			int right = left;
			int top = 0;
			int bottom = Math.round(5 * mScreen.getDensity());
			
			mparams.setMargins(left, top, right, bottom);
		}
*/
	
		Typeface font = Typeface.createFromAsset(getAssets(), CalDisplay.CalFont);
		final TypedArray btns = getResources().obtainTypedArray(R.array.cal_btns);
		for (int i = 0; i < btns.length(); i++) {
			Button b = (Button) findViewById(btns.getResourceId(i, 0));
			b.setTypeface(font);
			b.setOnClickListener(mListener);
        }
		btns.recycle();
		
		TextView tvResult = (TextView) findViewById(R.id.tvResult);
		tvResult.setOnClickListener(mListener);
	}

	@Override
	public void onBackPressed() {
		if (mLogic.isVisibleHistory()) {
			mLogic.history();
		} else {
			finish();
		}
	}
}
