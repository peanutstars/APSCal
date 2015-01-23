package com.pnstars.android.cal;

import java.util.Arrays;
import java.util.Stack;

import android.app.Activity;
import android.os.Vibrator;

import com.pnstars.android.R;
import com.pnstars.android.helper.CalParser;
import com.pnstars.android.helper.PNSDbg;

public class CalLogic {
	
	public enum NumType { LS_DECIMAL, LS_BINARY, LS_OCTAL, LS_HEXA }
	public static final String MARK_HEXA		= "x";
	public static final String MARK_OCTAL		= "o";
	public static final String MARK_BIN		= "b";
	public static final String NON_DECIMAL		= MARK_HEXA + MARK_OCTAL + MARK_BIN;
	public static final String AcceptDECIMAL	= "0123456789";
	public static final String AcceptHEXA		= "0123456789ABCDEF";
	public static final String AcceptOCTAL		= "01234567";
	public static final String AcceptBINARY	= "01";
	
	private final int VIBRATOR_MSEC = 50;
	private Activity mActivity;
	private Stack<LogicState> mInputStack;
	private CalHistory mCalHistory;
	private CalDisplay mDisplay;
	private LogicState mLS;
	private CalFile mFile;
	private Vibrator mVib;
	
	
	public CalLogic (Activity activity, CalHistory history) {
		mActivity = activity;
		mInputStack = new Stack<LogicState>();
		mCalHistory = history;
		mDisplay = new CalDisplay(activity);
		mLS = new LogicState();
		mFile = new CalFile(activity, history);
		mVib = (Vibrator) activity.getSystemService(activity.VIBRATOR_SERVICE);
		
		mFile.load();
	}
	
	private void appendInput (LogicState copyLS, String v) {
		PNSDbg.d("PU " + copyLS.toString() );
		mInputStack.push(copyLS);
		mDisplay.append(v);
		mVib.vibrate(VIBRATOR_MSEC);

		/* Clear Result */
		mDisplay.resetResult();
	}
	
	public void input (String v) {
		LogicState copyLS = mLS.copy();

		do
		{
			// check parenthesis
			if (v.equals(CalParser.P_LEFT) == true) {
				mLS.countParenthesis ++;
				mLS.countInputNumbers = 0;
				mLS.fgFirstZero = false;
				mLS.numType = NumType.LS_DECIMAL;
			} else if (v.equals(CalParser.P_RIGHT) == true) {
				mLS.countParenthesis --;
				mLS.countInputNumbers = 0;
				mLS.fgFirstZero = false;
				mLS.numType = NumType.LS_DECIMAL;
			}
			
			// check operator and change operator
			else if (CalParser.OPERATOR.indexOf(v) != -1) { // if operator
				
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
					mDisplay.delete();
					mDisplay.append(v);
					mLS.operator = new String(v);
					mVib.vibrate(VIBRATOR_MSEC);
					break;
				} else {
					PNSDbg.d("Do not print this message !!");
				}
				mLS.countInputNumbers = 0;
				mLS.fgFirstZero = false;
				mLS.numType = NumType.LS_DECIMAL;
			} else {
				/* input numbers */

				// check dot 
				if (CalParser.SPLITTER.indexOf(v) != -1) {
					mLS.fgDot = false;
					mLS.countDecimals = 0;
				} else if (v.equals(CalParser.DOT) == true) {
					if (mLS.fgDot == true) {
						break ;
					} else {
						mLS.fgDot = true;
					}
				}
				if (mLS.fgDot) {
					if (mLS.countDecimals <= CalParser.INPUT_MAX_DESIMALS) {
						/* It is counting with dot */
						mLS.countDecimals++;
					} else {
						PNSDbg.d("Input over a " + CalParser.INPUT_MAX_DESIMALS + " decimal");
						break;
					}
				}
				
				// filtering zero
				if (v.equals("0") == true) {
					if (mLS.countInputNumbers == 0) {
						mLS.fgFirstZero = true;
					} else if (copyLS.fgFirstZero == true) {
						break;
					}
				} else {
					mLS.fgFirstZero = false;
					
					/* process Hexa, octal, binary */
					if (NON_DECIMAL.contains(v)) {
						NumType tmpNT;

						if (v.equals(MARK_HEXA)) {
							tmpNT = NumType.LS_HEXA;
						} else if (v.equals(MARK_BIN)) {
							tmpNT = NumType.LS_BINARY;
						} else if (v.equals(MARK_OCTAL)) {
							tmpNT = NumType.LS_OCTAL;
						} else {
							tmpNT = NumType.LS_DECIMAL;
						}
					
						/* check double input */
						if (mLS.countInputNumbers != 1) {
							delete(); /* remove Zero */
							break;
						}

						mLS.numType = tmpNT;
						mLS.fgIntegerMode = true;
					}
					else if (copyLS.fgFirstZero == true) {
						mDisplay.delete();
						mDisplay.append(v);
						mVib.vibrate(VIBRATOR_MSEC);
						PNSDbg.d("");
						break;
					}
					
					PNSDbg.d(mLS.numTypeToString());
					if (mLS.numType == NumType.LS_DECIMAL && AcceptDECIMAL.indexOf(v) == -1) {
						PNSDbg.d("");
						break;
					} else if (mLS.numType == NumType.LS_HEXA && AcceptHEXA.indexOf(v) == -1) {
						PNSDbg.d("");
						break;
					} else if (mLS.numType == NumType.LS_OCTAL && AcceptOCTAL.indexOf(v) == -1) {
						PNSDbg.d("");
						break;
					} else if (mLS.numType == NumType.LS_BINARY && AcceptBINARY.indexOf(v) == -1) {
						PNSDbg.d("");
						break;
					}
				}
				mLS.countInputNumbers++;
				mLS.operator = "";
			}

			appendInput(copyLS, v);
		} while (false) ;
	}
	
	public void delete () {
		if (mInputStack.empty() == false) {
			mLS = mInputStack.pop();
		}
		PNSDbg.d("PO Update" + mLS.toString());
		mDisplay.delete();
		mVib.vibrate(VIBRATOR_MSEC);
	}

	public void reset() {
		mDisplay.resetFormula();
		mDisplay.resetResult();
		mInputStack.clear();
		mLS = new LogicState();
		mVib.vibrate(VIBRATOR_MSEC);
	}
	
	public void enter() {
		boolean fgErrSyntax = true;
		String formula = mDisplay.getFormula();
		PNSDbg.d("Formula : " + formula);
		mVib.vibrate(VIBRATOR_MSEC);
		
		if (mLS.operator.length() > 0) { // && CalParser.OPERATOR.indexOf(mLS.operator) == -1) {
			PNSDbg.d("Syntax Err : Formula is ended with operator.");
//		} else if (mLS.fgDot && mLS.countDecimals <= 1) {
//			PNSDbg.d("Syntax Err : The last input is Dot and then need more inputs.");
		} else if (mLS.countParenthesis == 0 && formula.length() > 0) {
			CalResult result = CalParser.spliteFormulaToSeparator(formula);
			if (result.getResult() == CalResult.Result.PASS) {
				String [] ci = result.getFormula().split(" ");
				PNSDbg.d("ci : " + Arrays.toString(ci));
				String [] co = CalParser.infixToRPN(ci);
				PNSDbg.d("co : " + Arrays.toString(co));
				String formulaResult = mDisplay.getResultFormuat(CalParser.RPNtoString(co));
				mCalHistory.addItem(formula, formulaResult);
				mDisplay.setResult(CalDisplay.ResultFormat.RESULT,formulaResult);
				fgErrSyntax = false;
			}
		} else {
			PNSDbg.d("Syntax Err : Caused others ...\n" + mLS.toString());
		}
		
		if (fgErrSyntax == true) {
			mDisplay.setResult(CalDisplay.ResultFormat.MESSAGE, mActivity.getString(R.string.strErrSyntax));
		}
	}
	
	public void history() {
		mVib.vibrate(VIBRATOR_MSEC);
		mDisplay.history(mCalHistory);
	}
	public void historyClear() {
		mVib.vibrate(VIBRATOR_MSEC);
		mDisplay.historyClear(mCalHistory);
	}
	
	public void save() {
		mFile.save();
	}
	
	private class LogicState{
		int			countParenthesis;
		boolean	fgDot;
		int			countDecimals;
		boolean	fgFirstZero;
		int			countInputNumbers;
		String		operator;
		boolean	fgIntegerMode;
		NumType	numType;

		
		public LogicState() {
			countParenthesis = 0;
			fgDot = false;
			countDecimals = 0;
			fgFirstZero = false;
			countInputNumbers = 0;
			operator = "";	// new String();
			fgIntegerMode = false;
			numType = NumType.LS_DECIMAL;
		}
		public LogicState(LogicState o) {
			countParenthesis = o.countParenthesis;
			fgDot = o.fgDot;
			countDecimals = o.countDecimals;
			fgFirstZero = o.fgFirstZero;
			countInputNumbers = o.countInputNumbers;
			operator = new String(o.operator);
			fgIntegerMode = o.fgIntegerMode;
			numType = o.numType;
		}
		public LogicState copy() {
			LogicState n = new LogicState(this);
			return n;
		}
		public String numTypeToString() {
			String rv = "";
			switch (numType) {
				case LS_DECIMAL:	rv = "Decimal";	break;
				case LS_BINARY:	rv = "Binary";	break;
				case LS_OCTAL:	rv = "Octal";		break;
				case LS_HEXA:		rv = "Hexa";		break;
				default:			rv = "Unknown";	break;
			}
			return rv;
		}
		@Override
		public String toString() {
			return "[Parenthesis:" + countParenthesis +
					" Dot(" + (fgDot?"T:":"F:") + String.valueOf(countDecimals) + ")" +
					" " + (fgFirstZero?"SZ,":"NZ,") + String.valueOf(countInputNumbers) +
					" Op(" + operator + ")]" +
					" NT(" + (fgIntegerMode?"Int:":"Double:") + numTypeToString()+ ")";
		}
	}

}
