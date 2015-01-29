package com.pnstars.android.cal;

public class CalParseResult {
	
	public enum Result { SYNTAX_ERROR, PASS };
	
	private Result	result;
	private String	formula;
	
	public CalParseResult() {
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