package com.pnstars.android.test;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import android.test.ActivityInstrumentationTestCase2;
import android.test.UiThreadTest;
import android.widget.Button;
import android.widget.EditText;

import com.pnstars.android.cal.MainActivity;
import com.pnstars.android.helper.PNSDbg;

public class CalMainActivityTest extends
		ActivityInstrumentationTestCase2<MainActivity> {

	MainActivity mActivity;
	Button btnN0;
	Button btnN1;
	Button btnN4;
	Button btnN8;
	Button btnNa;
	Button btnNb;
	Button btnNf;
	Button btnDel;
	Button btnAC;
	EditText tvFormula;
	
	public CalMainActivityTest() {
		super(MainActivity.class);
	}

	protected void setUp() throws Exception {
		super.setUp();
		
		setActivityInitialTouchMode(false);
		mActivity = getActivity();
		
		btnN0	= (Button) mActivity.findViewById(com.pnstars.android.R.id.btnDgt0);
		btnN1	= (Button) mActivity.findViewById(com.pnstars.android.R.id.btnDgt1);
		btnN4	= (Button) mActivity.findViewById(com.pnstars.android.R.id.btnDgt4);
		btnN8	= (Button) mActivity.findViewById(com.pnstars.android.R.id.btnDgt8);
		btnNa	= (Button) mActivity.findViewById(com.pnstars.android.R.id.btnHexaA);
		btnNb	= (Button) mActivity.findViewById(com.pnstars.android.R.id.btnHexaB);
		btnNf	= (Button) mActivity.findViewById(com.pnstars.android.R.id.btnHexaF);
		btnDel = (Button) mActivity.findViewById(com.pnstars.android.R.id.btnDel);
		btnAC	= (Button) mActivity.findViewById(com.pnstars.android.R.id.btnAC);
		tvFormula = (EditText) mActivity.findViewById(com.pnstars.android.R.id.tvFormula);
	}

	@UiThreadTest
	public void testKeyInput() {
		
		HashMap<String, ArrayList<Button>> testBed = new HashMap<String, ArrayList<Button>>() {{
			put( "0", new ArrayList<Button>() {{ add(btnN0); }} );
		}};
		
		Iterator it = testBed.entrySet().iterator();
		while (it.hasNext()) {
			Map.Entry pairs = (Map.Entry)it.next();
			String expValue = (String) pairs.getKey();
			ArrayList<Button> btns = (ArrayList<Button>) pairs.getValue();
			for (Button btn : btns) {
				btn.performClick();
			}
			PNSDbg.d("expValue : " + expValue);
			assertEquals("key input test", expValue, tvFormula.getText().toString());
			btnAC.performClick();
		}
	}
}
