package com.pnstars.android.test;

import android.test.ActivityInstrumentationTestCase2;
import android.test.UiThreadTest;
import android.widget.Button;
import android.widget.EditText;

import com.pnstars.android.cal.MainActivity;
import com.pnstars.android.helper.PNSDbg;

public class CalMainActivityTest extends
		ActivityInstrumentationTestCase2<MainActivity> {

	MainActivity mActivity;
	
	public CalMainActivityTest() {
		super(MainActivity.class);
	}

	protected void setUp() throws Exception {
		super.setUp();
		
		setActivityInitialTouchMode(false);
		mActivity = getActivity();
	}

	@UiThreadTest
	public void testKeyInput() {
		Button btnDgt0 = (Button) mActivity.findViewById(com.pnstars.android.R.id.btnDgt0);
		EditText tvFormula = (EditText) mActivity.findViewById(com.pnstars.android.R.id.tvFormula);
		
		btnDgt0.performClick();
		PNSDbg.d("" + tvFormula.getText().toString());
		assertEquals("key input test", "0", tvFormula.getText().toString());
	}
}
