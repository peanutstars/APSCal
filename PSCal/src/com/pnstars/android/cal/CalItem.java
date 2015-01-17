package com.pnstars.android.cal;

public class CalItem {
	public static String TAG_FORMULA = "Formula";
	public static String TAG_Result = "Result";

	private String mFormula;
	private String mResult;

	/* Constructor */
	public CalItem() {
		mFormula = "";
		mResult = "";
	}

	public CalItem(String formula, String result) {
		mFormula = new String(formula);
		mResult = new String(result);
	}

	/* getter / setter */
	public void setFormula(String formula) {
		mFormula = new String(formula);
	}

	public void setResult(String result) {
		mResult = new String(result);
	}

	public String getFormula() {
		return mFormula;
	}

	public String getResult() {
		return mResult;
	}

	@Override
	public String toString() {
		return mFormula.toString() + ":" + mResult.toString();
	}
	@Override
	public boolean equals(Object o) {
		if (o == null) {
			return false;
		}
		if (o == this) {
			return true;
		}
		if ( ! (o instanceof CalItem)) {
			return false;
		}
		return this.toString().equals(o.toString());
	}
}
