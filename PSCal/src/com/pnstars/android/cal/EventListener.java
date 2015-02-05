package com.pnstars.android.cal;

import android.app.Activity;
import android.content.Context;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.pnstars.android.R;
import com.pnstars.android.helper.PSDbg;


public class EventListener implements View.OnClickListener
										   , View.OnLongClickListener 
										   {
	public final String TAG = "PnStars";
//	private final int VIBRATOR_MSEC = 50;
	private CalLogic mLogic;
//	private Vibrator mVib;
	
	public EventListener(CalLogic logic) {
		mLogic = logic;
//		mVib = (Vibrator) activity.getSystemService(activity.VIBRATOR_SERVICE);
	}

	@Override
	public void onClick(View view) {
		int id = view.getId();
//		mVib.vibrate(VIBRATOR_MSEC);
		
		final String keyHexa   = CalLogic.MARK_VIB_OFF + "0" + CalLogic.MARK_VIB_ON + "x";
		final String keyOctal  = CalLogic.MARK_VIB_OFF + "0" + CalLogic.MARK_VIB_ON + "o";
		final String keyBinary = CalLogic.MARK_VIB_OFF + "0" + CalLogic.MARK_VIB_ON + "b";
		
		switch (id)
		{
		case R.id.btnOpAND:			mLogic.input("&");				break;
		case R.id.btnOpOR:			mLogic.input("|");				break;
		case R.id.btnOpXOR:			mLogic.input("^");				break;
		case R.id.btnHexa:			mLogic.stringInput(keyHexa);	break;
		case R.id.btnOctal:			mLogic.stringInput(keyOctal);	break;
		case R.id.btnBinary:			mLogic.stringInput(keyBinary);	break;		
		case R.id.btnAC:				mLogic.reset();					break;
		case R.id.btnDel:				mLogic.delete();					break;
		case R.id.btnEnter:			mLogic.enter();					break;
		case R.id.tvResult:			mLogic.history();					break;
		case R.id.imgHistory:		mLogic.history();					break;
		case R.id.btnHistoryClose:	mLogic.history();					break;
		case R.id.btnHistoryClear:	mLogic.historyClear();			break;
		default:
			if (view instanceof Button) {
				String text = ((Button) view).getText().toString();
				PSDbg.d("Input : " + text);
				mLogic.input (text);
			}
		}
	}
	
	private boolean longClickFormula() {
		Activity act = mLogic.getActivity();
		String name = act.getString(R.string.strClipBoardName);
		StringBuilder value = new StringBuilder();
		if (mLogic.getDisplay().getFormula().length() > 0) {
			value.append(mLogic.getDisplay().getFormula());
			if (mLogic.getDisplay().getResult().length() > 0) {
				value.append(" = ");
				value.append(mLogic.getDisplay().getResult());
			}
			android.content.ClipboardManager cb = (android.content.ClipboardManager)
					act.getSystemService(Context.CLIPBOARD_SERVICE);
			android.content.ClipData clip = android.content.ClipData.newPlainText(name, value.toString());
			cb.setPrimaryClip(clip);
		} else {
			Toast.makeText(act, act.getString(R.string.strErrToastClipBoardEmpty), Toast.LENGTH_SHORT).show();
		}
		
		return true;
	}
	
	@Override
	public boolean onLongClick(View view) {
		boolean rv = false;
		int id = view.getId();
		
		switch (id)
		{
		case R.id.tvFormula:			rv = longClickFormula();				break;
		}
		
		return rv;
	}
}
