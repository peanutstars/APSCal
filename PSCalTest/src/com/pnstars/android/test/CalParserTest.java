package com.pnstars.android.test;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import junit.framework.TestCase;
import android.test.suitebuilder.annotation.SmallTest;

import com.pnstars.android.helper.CalParser;
import com.pnstars.android.helper.CalResult;
import com.pnstars.android.helper.PNSDbg;

public class CalParserTest extends TestCase {

	public CalParserTest(String name) {
		super(name);
	}

	protected void setUp() throws Exception {
		super.setUp();
	}

	private class TestBedOne {
		String				inFormula;
		String				exFormula;
		CalResult.Result	exResult;
		public TestBedOne(String iF, String eF, CalResult.Result eR) {
			inFormula = iF;
			exFormula = eF;
			exResult  = eR;
		}
	}

	@SmallTest
	public void testSpliteFormulaToSeparator() {
		ArrayList<TestBedOne> testBed = new ArrayList<TestBedOne>() {{
			add( new TestBedOne("()", "( ) ", CalResult.Result.SYNTAX_ERROR) );
			add( new TestBedOne(")(", ") "+CalParser.OP_MUL+" ( ", CalResult.Result.SYNTAX_ERROR) );
			add( new TestBedOne(")1(", ") 1 ( ", CalResult.Result.SYNTAX_ERROR) );
			add( new TestBedOne("(())", "( ( ) ) ", CalResult.Result.SYNTAX_ERROR) );
			add( new TestBedOne("((1))", "( ( 1 ) ) ", CalResult.Result.PASS) );
			add( new TestBedOne("(1)", "( 1 ) ", CalResult.Result.PASS) );
			add( new TestBedOne("(4)(4)", "( 4 ) "+CalParser.OP_MUL+" ( 4 ) ", CalResult.Result.PASS) );
			add( new TestBedOne("((4))((4))", "( ( 4 ) ) "+CalParser.OP_MUL+" ( ( 4 ) ) ", CalResult.Result.PASS) );
		}};
		
		int count = 0;
		for (TestBedOne tb : testBed ) {
			PNSDbg.d("" + ++count + " : " + tb.inFormula);
			CalResult result = CalParser.spliteFormulaToSeparator(tb.inFormula);
			assertEquals("CalParser.spliteformulaToSeparator Result Error",  tb.exResult,  result.getResult());
			assertEquals("CalParser.spliteformulaToSeparator Formula Error", tb.exFormula, result.getFormula());
		}
	}
}
