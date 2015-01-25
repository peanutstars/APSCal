package com.pnstars.android.test;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import android.test.ActivityInstrumentationTestCase2;
import android.test.UiThreadTest;
import android.widget.Button;
import android.widget.EditText;

import com.pnstars.android.cal.CalMain;
import com.pnstars.android.helper.PNSDbg;

public class CalMainTest extends
		ActivityInstrumentationTestCase2<CalMain> {

	CalMain mActivity;
	EditText tvFormula;
	
	HashMap<Character, Button> btnMap;
	
	public CalMainTest() {
		super(CalMain.class);
	}

	protected void setUp() throws Exception {
		super.setUp();
		
		setActivityInitialTouchMode(false);
		mActivity = getActivity();
		
		tvFormula = (EditText) mActivity.findViewById(com.pnstars.android.R.id.tvFormula);
		final Button [] btnNum = new Button[16];
		btnNum[0] = (Button) mActivity.findViewById(com.pnstars.android.R.id.btnDgt0);
		btnNum[1] = (Button) mActivity.findViewById(com.pnstars.android.R.id.btnDgt1);
		btnNum[2] = (Button) mActivity.findViewById(com.pnstars.android.R.id.btnDgt2);
		btnNum[3] = (Button) mActivity.findViewById(com.pnstars.android.R.id.btnDgt3);
		btnNum[4] = (Button) mActivity.findViewById(com.pnstars.android.R.id.btnDgt4);
		btnNum[5] = (Button) mActivity.findViewById(com.pnstars.android.R.id.btnDgt5);
		btnNum[6] = (Button) mActivity.findViewById(com.pnstars.android.R.id.btnDgt6);
		btnNum[7] = (Button) mActivity.findViewById(com.pnstars.android.R.id.btnDgt7);
		btnNum[8] = (Button) mActivity.findViewById(com.pnstars.android.R.id.btnDgt8);
		btnNum[9] = (Button) mActivity.findViewById(com.pnstars.android.R.id.btnDgt9);
		btnNum[10] = (Button) mActivity.findViewById(com.pnstars.android.R.id.btnHexaA);
		btnNum[11] = (Button) mActivity.findViewById(com.pnstars.android.R.id.btnHexaB);
		btnNum[12] = (Button) mActivity.findViewById(com.pnstars.android.R.id.btnHexaC);
		btnNum[13] = (Button) mActivity.findViewById(com.pnstars.android.R.id.btnHexaD);
		btnNum[14] = (Button) mActivity.findViewById(com.pnstars.android.R.id.btnHexaE);
		btnNum[15] = (Button) mActivity.findViewById(com.pnstars.android.R.id.btnHexaF);
		final Button btnDel		= (Button) mActivity.findViewById(com.pnstars.android.R.id.btnDel);
		final Button btnAC		= (Button) mActivity.findViewById(com.pnstars.android.R.id.btnAC);
		final Button btnDot		= (Button) mActivity.findViewById(com.pnstars.android.R.id.btnDot);
		final Button btnEnter	= (Button) mActivity.findViewById(com.pnstars.android.R.id.btnEnter);
		final Button btnOpPlus	= (Button) mActivity.findViewById(com.pnstars.android.R.id.btnOpPlus);
		final Button btnOpMinus	= (Button) mActivity.findViewById(com.pnstars.android.R.id.btnOpMinus);
		final Button btnOpMul	= (Button) mActivity.findViewById(com.pnstars.android.R.id.btnOpMul);
		final Button btnOpDiv	= (Button) mActivity.findViewById(com.pnstars.android.R.id.btnOpDiv);
		final Button btnOpAND	= (Button) mActivity.findViewById(com.pnstars.android.R.id.btnOpAND);
		final Button btnOpOR		= (Button) mActivity.findViewById(com.pnstars.android.R.id.btnOpOR);
		final Button btnOpXOR	= (Button) mActivity.findViewById(com.pnstars.android.R.id.btnOpXOR);
		final Button btnHexa		= (Button) mActivity.findViewById(com.pnstars.android.R.id.btnHexa);
		final Button btnOctal	= (Button) mActivity.findViewById(com.pnstars.android.R.id.btnOctal);
		final Button btnBinary	= (Button) mActivity.findViewById(com.pnstars.android.R.id.btnBinary);
		
		
		
		btnMap = new HashMap<Character, Button>() {{
			put('0', btnNum[0]);			put('1', btnNum[1]);
			put('2', btnNum[2]);			put('3', btnNum[3]);
			put('4', btnNum[4]);			put('5', btnNum[5]);
			put('6', btnNum[6]);			put('7', btnNum[7]);
			put('8', btnNum[8]);			put('9', btnNum[9]);
			put('a', btnNum[10]);		put('b', btnNum[11]);
			put('c', btnNum[12]);		put('d', btnNum[13]);
			put('e', btnNum[14]);		put('f', btnNum[15]);
			put('A', btnAC);				put('D', btnDel);
			put('.', btnDot);				put('=', btnEnter);
			put('+', btnOpPlus);			put('-', btnOpMinus);
			put('*', btnOpMul);			put('/', btnOpDiv);
			put('&', btnOpAND);			put('|', btnOpOR);
			put('^', btnOpXOR);			put('B', btnBinary);
			put('O', btnOctal);			put('X', btnHexa);
		}};
	}

	@UiThreadTest
	public void testKeyInput() {
		
		HashMap<String, String> testBed = new HashMap<String, String>() {{
			put( "0",					"0" );
			put( "0123000",			"123000" );
			put( ".1",					"0.1");
			put( ".1.1.3.4",			"0.1134");
			put( "-1234",				"-1234");
			put( "*1234",				"1234");
			put( "/1234",				"1234");
			put( "+1234",				"1234");
			put( "0.123+0.1234",		"0.123+0.1234");
			put( "00001234",			"1234");
			put( "0....4567",			"0.4567");
			put( "0.456.456",			"0.456456");
			put( "1234...23456",		"1234.23456");
			put( "abc12345",			"12345");
			put( "abc+1234",			"1234");
			put( "123+6B",			"123+6");
			put( "12300+X12DDB111",	"12300+0b111");
			put( "12300+X12DDB333",	"12300+0b");
			put( "12300+X12DDO333",	"12300+0o333");
			put( "789+XX456",			"789+0x456");
			put( "789+XaX456",		"789+0xA456");
			put( "789+XabX456",		"789+0xAB456");
			put( "789+XabcX456",		"789+0xABC456");
			put( "789+XO456",			"789+0o456");
			put( "789+XaO456",		"789+0xA456");
			put( "789+XabO456",		"789+0xAB456");
			put( "789+XabcO456",		"789+0xABC456");
		}};
		
		Iterator it = testBed.entrySet().iterator();
		while (it.hasNext()) {
			Map.Entry pairs = (Map.Entry)it.next();
			String in  = (String) pairs.getKey();
			String out = (String) pairs.getValue();
			
			/* make key events */
			for (int i=0; i<in.length(); i++) {
				btnMap.get(in.charAt(i)).performClick();
			}
			
			String rvFormula = tvFormula.getText().toString();
			if (out.equals(rvFormula) == false) {
				PNSDbg.e("@@@ TestBed I:" + in + " O:" + out);
				PNSDbg.e("@@@ Result Err  : " + rvFormula);
			} else {
				PNSDbg.d("@@@ TestBed I:" + in + " O:" + out);
				PNSDbg.d("@@@ Result Pass : " + rvFormula);
			}
			assertEquals("key input test", out, rvFormula);
			
			btnMap.get('A').performClick();
		}
	}
}
