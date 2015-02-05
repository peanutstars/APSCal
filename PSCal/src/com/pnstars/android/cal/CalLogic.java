package com.pnstars.android.cal;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.Stack;

import android.app.Activity;
import android.os.Vibrator;

import com.pnstars.android.R;
import com.pnstars.android.cal.CalDisplay.ResultFormat;
import com.pnstars.android.helper.CalParser;
import com.pnstars.android.helper.CalParser.CalError;
import com.pnstars.android.helper.PSDbg;

public class CalLogic implements CalFile.FileOp {
	
	public static enum NumType { 
		LS_DECIMAL, LS_HEXA, LS_BINARY, LS_OCTAL;
		private static NumType[] vals = values();
		private static int [] radix = { 10, 16, 2, 8 };
		private static String [] prefix = { "", "0x", "0b", "0o"	};
		public NumType next() {
			return vals[(this.ordinal()+1) % vals.length];
		}
		public int getRadix() {
			 return radix[this.ordinal()];
		}
		public String getPrefix() {
			return prefix[this.ordinal()];
		}
	}
	public static final int BitBinary			= 1 << NumType.LS_BINARY.ordinal();
	public static final int BitOctal				= 1 << NumType.LS_OCTAL.ordinal();
	public static final int BitHexa				= 1 << NumType.LS_HEXA.ordinal();
	public static final int BitDecimal			= 1 << NumType.LS_DECIMAL.ordinal();
	
	public static final String MARK_HEXA		= "x";
	public static final String MARK_OCTAL		= "o";
	public static final String MARK_BIN		= "b";
	public static final String MARK_VIB_OFF	= "v";
	public static final String MARK_VIB_ON		= "V";

	public static final String NON_DECIMAL		= MARK_HEXA + MARK_OCTAL + MARK_BIN;
	public static final String AcceptDECIMAL	= "0123456789";
	public static final String AcceptHEXA		= "0123456789ABCDEF";
	public static final String AcceptOCTAL		= "01234567";
	public static final String AcceptBINARY		= "01";
	
	private final int VIBRATOR_MSEC = 50;
	private Activity mActivity;
	private Stack<LogicState> mInputStack;
	private CalHistory mHistory;
	private CalDisplay mDisplay;
	private LogicState mLS;
	private CalFile mFile;
	private Vibrator mVib;
	private OutputResult mOR;
	private boolean mFgVibEnable;
	
	
	public CalLogic (Activity activity, CalHistory history) {
		mActivity = activity;
		mInputStack = new Stack<LogicState>();
		mHistory = history;
		mDisplay = new CalDisplay(activity);
		mLS = new LogicState();
		mFile = new CalFile(activity);
		mVib = (Vibrator) activity.getSystemService(Activity.VIBRATOR_SERVICE);
		mOR = new OutputResult();
		mFgVibEnable = true;
		
		mFile.register(this);
		mFile.register(mDisplay);
		mFile.register(mHistory);
		mFile.load();
	}
	
	private void appendInput (LogicState copyLS, String v) {
		PSDbg.d("PU  " + copyLS.toString() );
//		PNSDbg.d("mLS " + mLS.toString());
		mInputStack.push(copyLS);
		mDisplay.append(v);
		if (mFgVibEnable) {
			mVib.vibrate(VIBRATOR_MSEC);
		}

		/* Clear Result */
		mDisplay.resetResult();
		mOR.reset();
	}
	
	private boolean inputParenthesis(LogicState cLS, String v) {
		boolean pass = false;
		if (v.equals(CalParser.P_LEFT) == true) {
			mLS.incParenthesis();
			mLS.resetForNewNumber();
			appendInput(cLS, v);
			pass = true;
		} else if (v.equals(CalParser.P_RIGHT) == true) {
			mLS.decParenthesis();
			mLS.resetForNewNumber();
			appendInput(cLS, v);
			pass = true;
		}
		return pass;
	}
	private boolean inputOperator(LogicState cLS, String v) {
		boolean pass = false;
		do
		{
			if (CalParser.OPERATOR.indexOf(v) != -1) { // if operator
				pass = true;
				if (mLS.operator.compareTo(v) == 0) {
					// PNSDbg.d("" + v);
					break;
				}
				// PNSDbg.d(mLS.operator + ":" +
				if (mLS.operator.length() == 0) {
					// It can only set minus operator to first character among the operators.
					if (mInputStack.size() == 0	&& v.compareTo(CalParser.OP_MINUS) != 0) {
						break;
					}
					// PNSDbg.d("" + v);
					mLS.operator = new String(v);
				} else if (CalParser.OPERATOR.indexOf(mLS.operator) != -1) { // if changed operator
					// PNSDbg.d("" + v + ":" + mLS.operator);
					mDisplay.delete();
					if (CalParser.BITWISE.indexOf(v) != -1) {
						mLS.setIntegerMode(true);
					} else {
						mLS.setIntegerMode(false|cLS.getIntegerMode());
					}
					mDisplay.append(v);
					mLS.operator = new String(v);
					if (mFgVibEnable) {
						mVib.vibrate(VIBRATOR_MSEC);
					}
					break;
				} else {
					PSDbg.e("Do not print this message !!");
				}
				if (CalParser.BITWISE.indexOf(v) != -1) {
					mLS.setIntegerMode(true);
				}
				mLS.resetForNewNumber();
				appendInput(cLS, v);
			} else {
				pass = false;
			}
		} while (false);
		return pass;
	}
	private boolean inputDot (LogicState cLS, String v) {
		boolean pass = false;
 
		if (v.equals(CalParser.DOT) == true) {
			pass = true;
			if (mLS.getDot() == true) {
				/* Nothing */
			} else {
				if (mLS.getInputNumbers() == 0) {
					input("0");
					cLS = mLS.copy();
				}
				mLS.setDot();
				if (mLS.getDecimals() <= CalParser.INPUT_MAX_DESIMALS) {
					mLS.incDecimals();
					mLS.incInputNumbers();
					PSDbg.d("inc");
					mLS.setFirstZero(false);
					appendInput(cLS, v);
				} else {
					PSDbg.d("Input over a " + CalParser.INPUT_MAX_DESIMALS + " decimal");
				}
			}
		}
		return pass;
	}
	private boolean inputNumber (LogicState cLS, String v) {
		
		if (inputDot(cLS, v)) {
		} else {
			if (v.equals("0") == true) {
				if (mLS.getInputNumbers() == 0) {
					mLS.setFirstZero(true);
					mLS.incInputNumbers();
					PSDbg.d("inc");
					mLS.setOperator("");
					appendInput(cLS, v);
				} else if (cLS.getFirstZero() == true) {
					/* remove duplicated zeros */
				} else {
					mLS.setFirstZero(false);
					mLS.incInputNumbers();
					PSDbg.d("inc");
					mLS.setOperator("");
					appendInput(cLS, v);					
				}
			} else {
				do
				{
					/* process Hexa, octal and binary marks */
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
						if (mLS.getInputNumbers() == 3) {
							if (mLS.getNumType() != NumType.LS_DECIMAL) {
								if (mLS.getNumType() == tmpNT) {
									internalDelete(); /* remove zero */
									break;
								} else {
									internalDelete(); /* remove zero */
									internalDelete(); /* remove b or x or o */
									cLS = mLS.copy();
									/* go to next step */
								} 
							} else {
								internalDelete(); /* remove zero */
								break;								
							}
						} else if (mLS.getInputNumbers() != 1) {
							internalDelete(); /* remove Zero */
							break;
						}
		
						mLS.incErrRadix();
						mLS.setNumTypes(1 << tmpNT.ordinal());
						mLS.setFirstZero(false);
						mLS.setNumType(tmpNT);
						mLS.setIntegerMode(true);
						mLS.incInputNumbers();
						PSDbg.d("inc");
						mLS.setOperator("");
						appendInput(cLS, v);
						break;
					} else {
						mLS.setNumTypes(1 << NumType.LS_DECIMAL.ordinal());
					}
	
					if (mLS.getNumType() == NumType.LS_DECIMAL && AcceptDECIMAL.indexOf(v) == -1) {
						break;
					} else if (mLS.getNumType() == NumType.LS_HEXA && AcceptHEXA.indexOf(v) == -1) {
						break;
					} else if (mLS.getNumType() == NumType.LS_OCTAL && AcceptOCTAL.indexOf(v) == -1) {
						break;
					} else if (mLS.getNumType() == NumType.LS_BINARY && AcceptBINARY.indexOf(v) == -1) {
						break;
					}
	
					if (mLS.getFirstZero() == true) {
						/* first input is zero and then replace from zero to numbers */
						mLS.setFirstZero(false);
						mDisplay.delete();
						mDisplay.append(v);
						if (mFgVibEnable) {
							mVib.vibrate(VIBRATOR_MSEC);
						}
						break;
					}
				
					mLS.setFirstZero(false);
					mLS.incInputNumbers();
					PSDbg.d("inc");
					mLS.setOperator("");
					if (mLS.getNumType() != NumType.LS_DECIMAL && mLS.getInputNumbers() == 3) {
						mLS.decErrRadix();
					}
					appendInput(cLS, v);
				} while (false);
			}
		}
		return true;
	}
		
	public void input (String v) {
		LogicState cLS = mLS.copy();
		
		if (v.compareTo(MARK_VIB_OFF) == 0) {
			mFgVibEnable = false;
			return;
		} else if (v.compareTo(MARK_VIB_ON) == 0) {
			mFgVibEnable = true;
			return;
		}
		
		if (inputParenthesis(cLS, v)) {
		} else if (inputOperator(cLS, v)) {
		} else if (inputNumber(cLS, v)) {
		} else {
			PSDbg.e("Do not print this message !!");
		}
	}
	public void stringInput (String str) {
		for (int i=0; i<str.length(); i++) {
			input("" + str.charAt(i));
		}
	}

	public void delete () {
		if (mInputStack.empty() == false) {
			mLS = mInputStack.pop();
		}
		PSDbg.d("PO Update" + mLS.toString());
		char delChar = mDisplay.delete();
		if (NON_DECIMAL.contains(""+delChar)) {
			delete();
		} else {
			mVib.vibrate(VIBRATOR_MSEC);
		}
	}
	private void internalDelete () {
		if (mInputStack.empty() == false) {
			mLS = mInputStack.pop();
		}
		PSDbg.d("PO Update" + mLS.toString());
		mDisplay.delete();
		if (mFgVibEnable) {
			mVib.vibrate(VIBRATOR_MSEC);
		}
	}

	public void reset() {
		mDisplay.resetFormula();
		mDisplay.resetResult();
		mInputStack.clear();
		mLS = new LogicState();
		mVib.vibrate(VIBRATOR_MSEC);

	}
	public CalDisplay getDisplay() {
		return mDisplay;
	}
	public Activity getActivity() {
		return mActivity;
	}
	
	private class OutputResult {
		NumType numType;
		public OutputResult() {
			reset();
		}
		public void reset() {
			numType = NumType.LS_DECIMAL;
		}
	}
	
	public String resultToStringInteger(String strInteger) {
		String strResult;
		if (mOR.numType == NumType.LS_DECIMAL) {
			strResult = mDisplay.convertResultFormatDecimal(strInteger);
		} else {
			BigInteger bi = new BigInteger(strInteger);
			strResult = mOR.numType.getPrefix() 
					+ bi.toString(mOR.numType.getRadix()).toUpperCase(mActivity.getResources().getConfiguration().locale);
			strResult = mDisplay.convertResultFormat(mOR.numType.getRadix(), strResult);
		}
		mOR.numType = mOR.numType.next();
		return strResult;
	}
	public String resultToStringError(CalParser.CalError enErr) {
		String strResult = "";
		
		switch (enErr) {
		case CErr_DivideZero:
			strResult = mActivity.getString(R.string.strInfinity);		break;
//		case CErr_Underflow:
//			strResult = mACtivity.getString(R.string.strErrUnderflow);	break;
		default:
			break;
		}
		return strResult;
	}
	
	public String runCalculate(String strFormula) {
		String [] ci = strFormula.split(" ");
//		PSDbg.d("ci : " + Arrays.toString(ci));
		String [] co = CalParser.infixToRPN(ci);
//		PSDbg.d("co : " + Arrays.toString(co));
		CalParser.CalResult cResult;
		String strResult;
		if (mLS.getIntegerMode() || (mLS.getNumTypes() != BitDecimal)) {
			cResult = CalParser.RPNtoCalInteger(co);
			if (cResult.getErr() == CalError.CErr_OK) {
				strResult = resultToStringInteger(cResult.getValue());
			} else {
				strResult = resultToStringError(cResult.getErr());
			}
		} else {
			cResult = CalParser.RPNtoBigDecimal(co);
			BigDecimal bd;
			if (cResult.getErr() == CalError.CErr_OK) {
				bd = new BigDecimal(cResult.getValue());
				if (bd.stripTrailingZeros().scale() <= 0) {
					strResult = resultToStringInteger(bd.toBigInteger()
							.toString());
				} else {
					strResult = mDisplay.convertResultFormatDecimal(bd
							.toString());
				}
			} else {
				strResult = resultToStringError(cResult.getErr());
			}
		}
		return strResult + " ";
	}
	
	public void enter() {
		boolean fgErrSyntax = true;
		String formula = mDisplay.getFormula();
		PSDbg.d("Formula : " + formula);
		mVib.vibrate(VIBRATOR_MSEC);
		
		if (mLS.operator.length() > 0) { // && CalParser.OPERATOR.indexOf(mLS.operator) == -1) {
			PSDbg.d("Syntax Err : Formula is ended with operator.");
//		} else if (mLS.fgDot && mLS.countDecimals <= 1) {
//			PNSDbg.d("Syntax Err : The last input is Dot and then need more inputs.");
		} else if (mLS.getErrRadix() != 0) {
			PSDbg.d("Syntax Err : Radix");
		} else if (mLS.countParenthesis == 0 && formula.length() > 0) {
			CalParseResult result = CalParser.spliteFormulaToSeparator(formula);
			if (result.getResult() == CalParseResult.Result.PASS)
			{
				String formulaResult = runCalculate(result.getFormula());
				mHistory.addItem(formula, formulaResult);
				mDisplay.setResult(CalDisplay.ResultFormat.RESULT,formulaResult);
				fgErrSyntax = false;
			}
		} else {
			if (formula.length() == 0) {
				fgErrSyntax = false;
			} else {
				PSDbg.d("Syntax Err : Caused others ...\n" + mLS.toString());
			}
		}
		
		if (fgErrSyntax == true) {
			mDisplay.setResult(CalDisplay.ResultFormat.MESSAGE, mActivity.getString(R.string.strErrSyntax));
		}
	}
	
	public void history() {
		mVib.vibrate(VIBRATOR_MSEC);
		mDisplay.history(mHistory);
	}
	public void historyClear() {
		mVib.vibrate(VIBRATOR_MSEC);
		mDisplay.historyClear(mHistory);
	}
	
	public void save() {
		mFile.save();
	}
	
	public boolean isVisibleHistory() {
		return mDisplay.isVisibleHistory();
	}
	
	@Override
	public void load(int version, DataInput in) throws IOException {
		String	formula = in.readUTF();
		String	result  = in.readUTF();
		
		if (version > 1) {
			stringInput(MARK_VIB_OFF + formula + MARK_VIB_ON);
			mDisplay.setResult(ResultFormat.RESULT, result);
		}		
	}
	@Override
	public void save(DataOutput out) throws IOException {		
	}
	
	
	private class LogicState {
		private int			countParenthesis;
		private boolean		fgDot;
		private int			countDecimals;
		private boolean		fgFirstZero;
		private int			lengthInputNum;
		private String		operator;
		private boolean		fgIntegerMode;
		private NumType		numType;
		private int			numTypes;
		private int			countErrRadix;
		
		public LogicState() {
			countParenthesis = 0;
			fgDot = false;
			countDecimals = 0;
			fgFirstZero = false;
			lengthInputNum = 0;
			operator = "";	// new String();
			fgIntegerMode = false;
			numType = NumType.LS_DECIMAL;
			numTypes = 0;
			countErrRadix = 0;
		}
		public LogicState(LogicState o) {
			countParenthesis = o.countParenthesis;
			fgDot = o.fgDot;
			countDecimals = o.countDecimals;
			fgFirstZero = o.fgFirstZero;
			lengthInputNum = o.lengthInputNum;
			operator = new String(o.operator);
			fgIntegerMode = o.fgIntegerMode;
			numType = o.numType;
			numTypes = o.numTypes;
			countErrRadix = o.countErrRadix;
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
			return "[P:" + countParenthesis +
					" Dot(" + (fgDot?"T:":"F:") + String.valueOf(countDecimals) + ")" +
					" " + (fgFirstZero?"SZ,":"NZ,") + String.valueOf(lengthInputNum) +
					" Op(" + operator + ")]" +
					" NT(" + (fgIntegerMode?"Int:":"Double:") + numTypeToString()+ ":0x"+ String.format("%X",numTypes) +")" +
					" E(" + String.valueOf(countErrRadix) +")";
		}

		public void incParenthesis() {
			countParenthesis ++;
		}
		public void decParenthesis() {
			countParenthesis --;
		}
		public void setDot () {
			fgDot = true;
		}
		public void setIntegerMode(boolean mode) {
			fgIntegerMode = mode;
		}
		public void setFirstZero(boolean firstZero) {
			fgFirstZero = firstZero;
		}
		public void setNumType (NumType nt) {
			numType = nt;
		}
		public void setOperator (String o) {
			operator = o;
		}
		public void setNumTypes (int nt) {
			numTypes |= nt;
		}
		public void incErrRadix () {
			countErrRadix ++;
		}
		public void decErrRadix () {
			countErrRadix --;
		}
		public int getErrRadix () {
			return countErrRadix;
		}
		public boolean getDot () {
			return fgDot;
		}
		public boolean getFirstZero() {
			return fgFirstZero;
		}
		public int getInputNumbers() {
			return lengthInputNum;
		}
		public int getDecimals() {
			return countDecimals;
		}
		public NumType getNumType() {
			return numType;
		}
		public int getNumTypes() {
			return numTypes;
		}
		public boolean getIntegerMode() {
			return fgIntegerMode;
		}
		public void incDecimals() {
			countDecimals ++;
		}
		public void incInputNumbers() {
			lengthInputNum ++;
		}
		public void resetForNewNumber() {
			lengthInputNum = 0;
			fgFirstZero = false;
			fgDot = false;
			countDecimals = 0;
			numType = NumType.LS_DECIMAL;
		}
	}

}
