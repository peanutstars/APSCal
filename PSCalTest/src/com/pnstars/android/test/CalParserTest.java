package com.pnstars.android.test;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import junit.framework.TestCase;
import android.test.suitebuilder.annotation.SmallTest;

import com.pnstars.android.cal.CalParseResult;
import com.pnstars.android.helper.CalParser;
import com.pnstars.android.helper.PSDbg;

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
		CalParseResult.Result	exResult;
		public TBFormula(String iF, String eF, CalParseResult.Result eR) {
			inFormula = iF;
			exFormula = eF;
			exResult  = eR;
		}
	}

	@SmallTest
	public void testSpliteFormulaToSeparator() {
		ArrayList<TBFormula> testBed = new ArrayList<TBFormula>() {{
			add( new TBFormula("()",				"Invalid",										CalParseResult.Result.SYNTAX_ERROR) );
			add( new TBFormula(")(",				") "+OP_MUL+" ( ",							CalParseResult.Result.SYNTAX_ERROR) );
			add( new TBFormula(")1(",			"Invalid",										CalParseResult.Result.SYNTAX_ERROR) );
			add( new TBFormula("(())",			"Invalid",										CalParseResult.Result.SYNTAX_ERROR) );
			add( new TBFormula("((1))",			"( ( 1 ) ) ",									CalParseResult.Result.PASS) );
			add( new TBFormula("(1)",			"( 1 ) ",										CalParseResult.Result.PASS) );
			add( new TBFormula("(4)(4)",		"( 4 ) "+OP_MUL+" ( 4 ) ",					CalParseResult.Result.PASS) );
			add( new TBFormula("((4))((4))",	"( ( 4 ) ) "+OP_MUL+" ( ( 4 ) ) ",			CalParseResult.Result.PASS) );
			add( new TBFormula("4(4)",			"4 "+OP_MUL+" ( 4 ) ",						CalParseResult.Result.PASS) );
			add( new TBFormula("4((4))",		"4 "+OP_MUL+" ( ( 4 ) ) ",					CalParseResult.Result.PASS) );
			add( new TBFormula("4(((4)))",		"4 "+OP_MUL+" ( ( ( 4 ) ) ) ",				CalParseResult.Result.PASS) );
			add( new TBFormula("(4)4",			"( 4 ) "+OP_MUL+" 4",						CalParseResult.Result.PASS) );
			add( new TBFormula("((4))4",		"( ( 4 ) ) "+OP_MUL+" 4",					CalParseResult.Result.PASS) );
			add( new TBFormula("(((4)))4",		"( ( ( 4 ) ) ) "+OP_MUL+" 4",				CalParseResult.Result.PASS) );
			add( new TBFormula("(4)4(4)",		"( 4 ) "+OP_MUL+" 4 "+OP_MUL+" ( 4 ) ",	CalParseResult.Result.PASS) );
		}};
		
		int count = 0;
		for (TBFormula tb : testBed ) {
			CalParseResult result = CalParser.spliteFormulaToSeparator(tb.inFormula);
			PSDbg.d("" + ++count + " : " + tb.inFormula + " : " + result.getFormula());
			assertEquals("CalParser.spliteformulaToSeparator Result Error",  tb.exResult,  result.getResult());
			
			if (tb.exFormula.length() > 0 && tb.exResult == CalParseResult.Result.PASS) {
				assertEquals("CalParser.spliteformulaToSeparator Formula Error", tb.exFormula, result.getFormula());
			}
		}
	}
}
