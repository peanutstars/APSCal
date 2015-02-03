package com.pnstars.android.cal;

import android.app.Activity;
import android.content.res.TypedArray;
import android.graphics.Typeface;
import android.os.Bundle;
import android.widget.Button;
import android.widget.ImageView;

import com.pnstars.android.R;
import com.pnstars.android.helper.PSDbg;
import com.pnstars.android.helper.PSScreen;

public class CalMain extends Activity {

	private PSScreen mScreen;
	private EventListener mListener;
	private CalHistory mHistory;
	private CalLogic mLogic;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_calmain);

		mScreen = new PSScreen(this);
		mHistory = new CalHistory();
		mLogic = new CalLogic(this, mHistory);
		mListener = new EventListener(mLogic);

		mScreen.setOrientationPortrait();
		
		connectBtnEvent();
		
//		if (savedInstanceState != null) {
//			mLogic.stringInput(savedInstanceState.getString("Formula"));
//			mLogic.getDisplay().setResult(CalDisplay.ResultFormat.RESULT,
//					savedInstanceState.getString("Result"));
//		} else {
//			PSDbg.e("savedInstanceState == NULL");
//		}
		
		mLogic.initHistory();
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
		
		ImageView history = (ImageView) findViewById(R.id.imgHistory);
		history.setOnClickListener(mListener);
	}

	@Override
	public void onBackPressed() {
		if (mLogic.isVisibleHistory()) {
			mLogic.history();
		} else {
			super.onBackPressed();
		}
	}
	
//	@Override
//	protected void onSaveInstanceState(Bundle outState) {
//		// TODO Auto-generated method stub
//		super.onSaveInstanceState(outState);
//		PSDbg.d("enter");
//		if (outState != null) {
//			outState.putString("Formula", mLogic.getDisplay().getFormula());
//			outState.putString("Result", mLogic.getDisplay().getResult());
//		} else {
//			PSDbg.e("outState == NULL");
//		}
//	}
}
