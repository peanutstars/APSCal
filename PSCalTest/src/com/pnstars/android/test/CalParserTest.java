package com.pnstars.android.test;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import junit.framework.TestCase;
import android.test.suitebuilder.annotation.SmallTest;

import com.pnstars.android.cal.CalResult;
import com.pnstars.android.helper.CalParser;
import com.pnstars.android.helper.PNSDbg;

public class CalParserTest extends TestCase {

	static final String OP_MUL = CalParser.OP_MUL;
	
	public CalParserTest(String name) {
		super(name);
	}

	protected void setUp() throws Exception {
		super.setUp();
	}

	private class TBFormula {
		String				inFormula;
		String				exFormula;
		CalResult.Result	exResult;
		public TBFormula(String iF, String eF, CalResult.Result eR) {
			inFormula = iF;
			exFormula = eF;
			exResult  = eR;
		}
	}

	@SmallTest
	public void testSpliteFormulaToSeparator() {
		ArrayList<TBFormula> testBed = new ArrayList<TBFormula>() {{
			add( new TBFormula("()",				"Invalid",										CalResult.Result.SYNTAX_ERROR) );
			add( new TBFormula(")(",				") "+OP_MUL+" ( ",							CalResult.Result.SYNTAX_ERROR) );
			add( new TBFormula(")1(",			"Invalid",										CalResult.Result.SYNTAX_ERROR) );
			add( new TBFormula("(())",			"Invalid",										CalResult.Result.SYNTAX_ERROR) );
			add( new TBFormula("((1))",			"( ( 1 ) ) ",									CalResult.Result.PASS) );
			add( new TBFormula("(1)",			"( 1 ) ",										CalResult.Result.PASS) );
			add( new TBFormula("(4)(4)",		"( 4 ) "+OP_MUL+" ( 4 ) ",					CalResult.Result.PASS) );
			add( new TBFormula("((4))((4))",	"( ( 4 ) ) "+OP_MUL+" ( ( 4 ) ) ",			CalResult.Result.PASS) );
			add( new TBFormula("4(4)",			"4 "+OP_MUL+" ( 4 ) ",						CalResult.Result.PASS) );
			add( new TBFormula("4((4))",		"4 "+OP_MUL+" ( ( 4 ) ) ",					CalResult.Result.PASS) );
			add( new TBFormula("4(((4)))",		"4 "+OP_MUL+" ( ( ( 4 ) ) ) ",				CalResult.Result.PASS) );
			add( new TBFormula("(4)4",			"( 4 ) "+OP_MUL+" 4",						CalResult.Result.PASS) );
			add( new TBFormula("((4))4",		"( ( 4 ) ) "+OP_MUL+" 4",					CalResult.Result.PASS) );
			add( new TBFormula("(((4)))4",		"( ( ( 4 ) ) ) "+OP_MUL+" 4",				CalResult.Result.PASS) );
			add( new TBFormula("(4)4(4)",		"( 4 ) "+OP_MUL+" 4 "+OP_MUL+" ( 4 ) ",	CalResult.Result.PASS) );
		}};
		
		int count = 0;
		for (TBFormula tb : testBed ) {
			CalResult result = CalParser.spliteFormulaToSeparator(tb.inFormula);
			PNSDbg.d("" + ++count + " : " + tb.inFormula + " : " + result.getFormula());
			assertEquals("CalParser.spliteformulaToSeparator Result Error",  tb.exResult,  result.getResult());
			
			if (tb.exFormula.length() > 0 && tb.exResult == CalResult.Result.PASS) {
				assertEquals("CalParser.spliteformulaToSeparator Formula Error", tb.exFormula, result.getFormula());
			}
		}
	}
}
