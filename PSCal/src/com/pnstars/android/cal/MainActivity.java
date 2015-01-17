package com.pnstars.android.cal;

import java.util.ArrayList;
import java.util.HashMap;

import android.app.Activity;
import android.content.res.TypedArray;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;

import com.pnstars.android.R;
import com.pnstars.android.helper.PNSScreen;

public class MainActivity extends Activity {

	PNSScreen mScreen;
	EventListener mListener;
	CalDisplay mDisplay;
	CalLogic mLogic;
	TextView mTvResult;
	ArrayList<HashMap<String, String>> mHistoryList;
	final static String TAG_FORMULA = "HL_FORMULA";
	final static String TAG_RESULT = "HL_RESULT";
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		mScreen = new PNSScreen(this);
		mDisplay = new CalDisplay(this);
		mLogic = new CalLogic(mDisplay);
		mListener = new EventListener(this, mLogic);
		mHistoryList = new ArrayList<HashMap<String, String>>();

		mScreen.setOrientationPortrait();
		
		connectBtnEvent();
		testListView();
	}
	
	public void testListView() {
		
		mHistoryList.clear();

		String [] itemValue = {
				"Test List View 1",
				"Test List View 2",
				"Test List View 3",
				"Test List View 4",
				"Test List View 5",
				"Test List View 6",
		};
		
		for (String a : itemValue) {
			HashMap<String, String> item = new HashMap<String, String>();
			item.put(TAG_FORMULA, a);
			item.put(TAG_RESULT, a);
			mHistoryList.add(item);
		}

		ListView lv = (ListView) findViewById(R.id.lv_history);
		ListAdapter adapter = new SimpleAdapter(this, mHistoryList, R.layout.list_history, 
				new String[] { TAG_FORMULA, TAG_RESULT }, 
				new int[] { R.id.itemFormula, R.id.itemResult });
		lv.setAdapter(adapter);
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
		
		mTvResult = (TextView) findViewById(R.id.tvResult);
		mTvResult.setOnClickListener(mListener);
	}

}
