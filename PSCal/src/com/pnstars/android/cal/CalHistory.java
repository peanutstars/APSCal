package com.pnstars.android.cal;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.ListIterator;

import com.pnstars.android.helper.PNSDbg;

public class CalHistory {
	public static final int LIST_MAX_SIZE = 10;
	
	private LinkedList<CalItem> mListHistory;
	
	public CalHistory() {
		mListHistory = new LinkedList<CalItem>();
	}
	
	public void addItem (String formula, String result) {
		CalItem item = new CalItem (formula, result);
		int index;
		
		index = mListHistory.indexOf(item);
		PNSDbg.d("index : " + index);
		if (index >= 0) {
			/* Already has item and then replace position */
			mListHistory.remove(index);
		} else if (mListHistory.size() < LIST_MAX_SIZE){
			/* Under List Size */
		} else {
			/* Over List Size */
			mListHistory.removeFirst();
		}
		mListHistory.add(item);
	}
	public void delItem (int index) {
		mListHistory.remove(index);
	}
	public void clear() {
		mListHistory.clear();
	}
//	public ArrayList<HashMap<String,String>> getHistory() {
//		ArrayList<HashMap<String, String>> larray = new ArrayList<HashMap<String,String>>();
//		/*
//		for (CalItem item : mListHistory) {
//			HashMap<String, String> list = new HashMap<String, String>();
//			list.put(CalItem.TAG_FORMULA, item.getFormula());
//			list.put(CalItem.TAG_Result, item.getResult());
//			larray.add(list);
//		}
//		*/
//		ListIterator<CalItem> li = mListHistory.listIterator(mListHistory.size());
//		while (li.hasPrevious()) {
//			CalItem item = li.previous();
//			HashMap<String, String> list = new HashMap<String, String>();
//			list.put(CalItem.TAG_FORMULA, item.getFormula());
//			list.put(CalItem.TAG_Result, item.getResult());
//			larray.add(list);
//		}
//		return larray;
//	}
	public ArrayList<CalItem> getHistory() {
		ArrayList<CalItem> larray = new ArrayList<CalItem>();
		ListIterator<CalItem> li = mListHistory.listIterator(mListHistory.size());
		while (li.hasPrevious()) {
			CalItem item = li.previous();
			larray.add(item);
		}
		return larray;
		
	}
}
