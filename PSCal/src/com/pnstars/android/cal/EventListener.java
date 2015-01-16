package com.pnstars.android.cal;

import android.app.Activity;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ListView;

import com.pnstars.android.R;
import com.pnstars.android.helper.PNSDbg;


public class EventListener implements View.OnClickListener
										   // , View.OnLongClickListener 
										   {
	public final String TAG = "PnStars";
	private Activity mActivity;
	private CalLogic mLogic;
	
	public EventListener(Activity activity, CalLogic logic) {
		mActivity = activity;
		mLogic = logic;
	}

	@Override
	public void onClick(View view) {
		int id = view.getId();
		switch (id)
		{
		case R.id.btnAC:
			mLogic.reset();
			break;
		case R.id.btnDel:
			mLogic.delete();
			break;
		case R.id.btnEnter:
			mLogic.enter();
			break;
		case R.id.tvResult:
			ListView lv = (ListView) mActivity.findViewById(R.id.lv_history);
			LinearLayout ll = (LinearLayout) mActivity.findViewById(R.id.btn_pad);
			if (lv.getVisibility() == View.GONE) {
				lv.setVisibility(View.VISIBLE);
				ll.setVisibility(View.GONE);
			} else {
				lv.setVisibility(View.GONE);
				ll.setVisibility(View.VISIBLE);
			}
			break;
		default:
			if (view instanceof Button) {
				String text = ((Button) view).getText().toString();
				PNSDbg.d("Input : " + text);
				mLogic.input (text);
			}
		}
	}
	
//	@Override
//	public boolean onLongClick(View view) {
//		int id = view.getId();
//		Log.d(TAG, "" + id);
//		return false;
//	}
}
