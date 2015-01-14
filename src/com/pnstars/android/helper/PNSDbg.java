package com.pnstars.android.helper;

import android.util.Log;

public class PNSDbg {
	final static String TAG = "PNStars";
	public PNSDbg () {
	}

	public static String getTag () {
		int _depth = 4;
		String _class  = Thread.currentThread().getStackTrace()[_depth].getClassName();
		_class = _class.substring(_class.lastIndexOf('.')+1);
		String _method = Thread.currentThread().getStackTrace()[_depth].getMethodName();
		int   _line = Thread.currentThread().getStackTrace()[_depth].getLineNumber();
		
		return "[" + _class + "." + _method + "(" + _line + ")] ";
	}
	public static int d(String msg) {
		return Log.d (TAG, getTag() + msg);
	}
	public static int i(String msg) {
		return Log.i (TAG, getTag() + msg);
	}
	public static int v(String msg) {
		return Log.d (TAG, getTag() + msg);
	}
}
