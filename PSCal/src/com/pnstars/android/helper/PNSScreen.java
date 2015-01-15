package com.pnstars.android.helper;

import android.app.Activity;
import android.content.pm.ActivityInfo;
import android.util.DisplayMetrics;
import android.view.Window;
import android.view.WindowManager;


public class PNSScreen {
	
	public static final float		BASE_WIDTH_DP = 360.0f;
	
	Activity activity;
	DisplayMetrics metrics;
	
	public PNSScreen (Activity activity) {
		this.activity = activity;
		metrics = activity.getResources().getDisplayMetrics();
		
		PNSDbg.i("Display Density : " + metrics.density);
		PNSDbg.i("Display width(dpi) : " + getWidthDpi());
		PNSDbg.i("Display height(dpi) : " + getHeightDpi());
	}
	
	public float getDensity() {
		return metrics.density;
	}
	public int getWidth() {
		return metrics.widthPixels;
	}
	public int getHeight() {
		return metrics.heightPixels;
	}
	public float getWidthDpi() {
		return metrics.widthPixels / metrics.density;
	}
	public float getHeightDpi() {
		return metrics.heightPixels / metrics.density;
	}
	
	public void setNoTitle () {
		activity.requestWindowFeature(Window.FEATURE_NO_TITLE);
	}
	public void setFullScreen() {
		activity.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
	}
	public void setOrientationPortrait () {
		activity.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
	}
}
