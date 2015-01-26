package com.pnstars.android.cal;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.ListIterator;

public class CalHistory {
	public static final int LIST_MAX_SIZE = 50;
	
	private LinkedList<CalItem> mListHistory;
	
	public CalHistory() {
		mListHistory = new LinkedList<CalItem>();
	}
	
	public void addItem (String formula, String result) {
		CalItem item = new CalItem (formula, result);
		int index;
		
		index = mListHistory.indexOf(item);
		// PNSDbg.d("index : " + index);
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
	
	public void load(int version, DataInput in) throws IOException {
		int size = in.readInt();
		String formula;
		String result;
		for (int i=0; i<size; i++) {
			formula = in.readUTF();
			result  = in.readUTF();
			addItem (formula, result);
		}
	}
	
	public void save(DataOutput out) throws IOException {
		out.writeInt(mListHistory.size());
		for (CalItem item : mListHistory) {
			out.writeUTF(item.getFormula());
			out.writeUTF(item.getResult());
		}
	}
}
