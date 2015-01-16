package com.pnstars.android.cal;

import android.app.Activity;
import android.content.res.TypedArray;
import android.os.Bundle;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.pnstars.android.R;
import com.pnstars.android.helper.PNSScreen;

public class MainActivity extends Activity {

	PNSScreen mScreen;
	EventListener mListener;
	CalDisplay mDisplay;
	CalLogic mLogic;
	TextView mTvFormula;
	TextView mTvResult;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);


		setContentView(R.layout.activity_main);

		mScreen = new PNSScreen(this);
		mDisplay = new CalDisplay(this);
		mLogic = new CalLogic(mDisplay);
		mListener = new EventListener(mLogic);

		mScreen.setOrientationPortrait();
		
		connectBtnEvent();
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
	
		final TypedArray btns = getResources().obtainTypedArray(R.array.cal_btns);
		for (int i = 0; i < btns.length(); i++) {
			Button b = (Button) findViewById(btns.getResourceId(i, 0));
			b.setOnClickListener(mListener);
        }
		btns.recycle();
	}

}
