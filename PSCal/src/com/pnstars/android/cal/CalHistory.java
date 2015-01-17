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
		// Reverse Order
		mListHistory.remove(mListHistory.size() - index - 1);
	}
	public void clear() {
		mListHistory.clear();
	}
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
