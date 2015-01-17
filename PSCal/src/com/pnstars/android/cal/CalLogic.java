package com.pnstars.android.cal;

import java.util.Stack;

import com.pnstars.android.helper.CalParser;
import com.pnstars.android.helper.PNSDbg;

public class CalLogic {
	
	private Stack<LogicState> mInputStack;
	private CalHistory mCalHistory;
	private LogicState mLS;
	private CalDisplay mDisplay;
	
	
	public CalLogic (CalDisplay display) {
		mInputStack = new Stack<LogicState>();
		mCalHistory = new CalHistory();
		mLS = new LogicState();
		mDisplay = display;
	}
	
	public void input (String v) {
		LogicState copyLS = mLS.copy();
		
		do
		{
			// check parenthesis
			if (v.equals(CalParser.P_LEFT) == true) {
				mLS.countParenthesis ++;
			} else if (v.equals(CalParser.P_RIGHT) == true) {
				mLS.countParenthesis --;
			}
			// check operator and change operator
			if (CalParser.OPERATOR.indexOf(v) != -1) { // if operator
				if (mLS.operator.compareTo(v) == 0) {
					// PNSDbg.d("" + v);
					break ;
				}
				// PNSDbg.d(mLS.operator + ":" + CalParser.OPERATOR.indexOf(mLS.operator));
				if (mLS.operator.length() == 0 ) {
					// It can only set minus operator to first character among the operators.
					if (mInputStack.size() == 0 && v.compareTo(CalParser.OP_MINUS) != 0) {
						break ;
					}
					// PNSDbg.d("" + v);
					mLS.operator = new String(v);
				} else if (CalParser.OPERATOR.indexOf(mLS.operator) != -1) { // if changed operator
					// PNSDbg.d("" + v + ":" + mLS.operator);
					mLS.operator = new String(v);
					deleteNoUpdateLS();
				} else {
					PNSDbg.d("Do not print this message !!");
				}
			} else {
				mLS.operator = "";	// new String();
			}
			
			// check dot 
			if (CalParser.SPLITTER.indexOf(v) != -1) {
				mLS.fgDot = false;
			} else if (v.equals(CalParser.DOT) == true) {
				if (mLS.fgDot == true) {
					break ;
				} else {
					mLS.fgDot = true;
				}
			}
			PNSDbg.d("PU(" + copyLS.countParenthesis + "," + copyLS.fgDot + "," + copyLS.operator + ")" );
			mInputStack.push(copyLS);
			mDisplay.append(v);
		} while (false) ;
	}
	
	public void delete () {
		if (mInputStack.empty() == false) {
			mLS = mInputStack.pop();
		}
		PNSDbg.d("PO Update(" + mLS.countParenthesis + "," + mLS.fgDot + "," + mLS.operator + ")" );
		mDisplay.delete();
	}
	private void deleteNoUpdateLS() {
		LogicState popLS = null;
		if (mInputStack.empty() == false) {
			popLS = mInputStack.pop();
		}
		if (popLS != null) {
			PNSDbg.d("PO Delete(" + popLS.countParenthesis + "," + popLS.fgDot + "," + popLS.operator + ")" );
		} else {
			PNSDbg.d("PO Delete( None )");
		}
		mDisplay.delete();
	}
	
	public void reset() {
		mDisplay.resetFormula();
		mDisplay.resetResult();
		while (mInputStack.empty() == false) {
			mLS = mInputStack.pop();
		}
	}
	
	public void enter() {
		boolean fgErrSyntax = false;
		String formula = mDisplay.getFormula();
		PNSDbg.d("Formula : " + formula);
		
		if (mLS.countParenthesis == 0 && formula.length() > 0) {
			CalParser.CalResult result = CalParser.setFormulaToBoundary(formula);
			if (result.getResult() == CalParser.Result.PASS) {
				String [] ci = result.getFormula().split(" ");
				String [] co = CalParser.infixToRPN(ci);
				String formulaResult = mDisplay.getResultFormuat(CalParser.RPNtoString(co));
				mCalHistory.addItem(formula, formulaResult);
				mDisplay.setResult(CalDisplay.ResultFormat.RESULT,formulaResult);
			} else {
				fgErrSyntax = true;
			}
		} else {
			fgErrSyntax = true;
		}
		if (fgErrSyntax == true) {
			mDisplay.setResult(CalDisplay.ResultFormat.MESSAGE, "Syntax Error");
		}
	}
	
	public void history() {
		mDisplay.history(mCalHistory);
	}
	public void historyClear() {
		mDisplay.historyClear(mCalHistory);
	}
	
	public class LogicState{
		int			countParenthesis;
		boolean	fgDot;
		String		operator;
		
		public LogicState() {
			countParenthesis = 0;
			fgDot = false;
			operator = "";	// new String();
		}
		public LogicState(LogicState o) {
			countParenthesis = o.countParenthesis;
			fgDot = o.fgDot;
			operator = new String(o.operator);
		}
		public LogicState copy() {
			LogicState n = new LogicState(this);
			return n;
		}
	}

}
