package com.pnstars.android.cal;

import android.app.Activity;
import android.os.Vibrator;
import android.view.View;
import android.widget.Button;

import com.pnstars.android.R;
import com.pnstars.android.helper.PNSDbg;


public class EventListener implements View.OnClickListener
										   // , View.OnLongClickListener 
										   {
	public final String TAG = "PnStars";
	private final int VIBRATOR_MSEC = 50;
	private CalLogic mLogic;
	private Vibrator mVib;
	
	public EventListener(Activity activity, CalLogic logic) {
		mLogic = logic;
		mVib = (Vibrator) activity.getSystemService(activity.VIBRATOR_SERVICE);
	}

	@Override
	public void onClick(View view) {
		int id = view.getId();
		mVib.vibrate(VIBRATOR_MSEC);
		
		switch (id)
		{
		case R.id.btnOpAND:
		case R.id.btnOpOR:
		case R.id.btnOpXOR:
		case R.id.btnHexa:
		case R.id.btnOtal:
		case R.id.btnBinary:
			break;
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
			mLogic.history();
			break;
		case R.id.btnHistoryClose:
			mLogic.history();
			break;
		case R.id.btnHistoryClear:
			mLogic.historyClear();
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
