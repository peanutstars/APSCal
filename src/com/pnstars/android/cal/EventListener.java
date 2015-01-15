package com.pnstars.android.cal;

import android.view.View;
import android.widget.Button;

import com.pnstars.android.R;
import com.pnstars.android.helper.PNSDbg;


public class EventListener implements View.OnClickListener
										   // , View.OnLongClickListener 
										   {
	public final String TAG = "PnStars";
	private CalLogic mLogic;
	
	public EventListener(CalLogic logic) {
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
