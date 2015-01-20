package com.pnstars.android.helper;

public class CalResult {
	
	public enum Result { SYNTAX_ERROR, PASS };
	
	private Result	result;
	private String	formula;
	
	public CalResult() {
		result = Result.SYNTAX_ERROR;
		formula = "";
	}
	public void setResult (Result result) {
		this.result = result;
	}
	public Result getResult () {
		return result;
	}
	public void setFormula (String formula) {
		this.formula = formula;
	}
	public String getFormula () {
		return formula;
	}
}