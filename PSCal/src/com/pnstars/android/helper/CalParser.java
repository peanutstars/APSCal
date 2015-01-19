package com.pnstars.android.helper;
import java.math.BigDecimal;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.Stack;

import android.text.GetChars;
   
public class CalParser   
{  
    // Associativity constants for operators  
    private static final int LEFT_ASSOC  = 0;  
    private static final int RIGHT_ASSOC = 1;
    private static final int ROUND_UP_POSITION = 10;
    
    public static final int INPUT_MAX_DESIMALS = ROUND_UP_POSITION;
    public static final String OP_PLUS		= "+";
    public static final String OP_MINUS	= "-";
    public static final String OP_DIV		= "\u00f7";
    public static final String OP_MUL		= "\u00d7";
    public static final String P_LEFT		= "(";
    public static final String P_RIGHT		= ")";
    public static final String DOT			= ".";
    public static final String SPLITTER		= OP_PLUS + OP_MINUS + OP_DIV + OP_MUL + P_LEFT + P_RIGHT;
    public static final String OPERATOR		= OP_PLUS + OP_MINUS + OP_DIV + OP_MUL;
    
    // Operators  
	private static final Map<String, int[]> OPERATORS = new HashMap<String, int[]>();
	static {
		// Map<"token", []{precendence, associativity}>
		OPERATORS.put(OP_PLUS, new int[] { 0, LEFT_ASSOC });
		OPERATORS.put(OP_MINUS, new int[] { 0, LEFT_ASSOC });
		OPERATORS.put(OP_MUL, new int[] { 5, LEFT_ASSOC });
		OPERATORS.put(OP_DIV, new int[] { 5, LEFT_ASSOC });
	}

	// Test if token is an operator
	private static boolean isOperator(String token) {
		return OPERATORS.containsKey(token);
	}

	// Test associativity of operator token
	private static boolean isAssociative(String token, int type) {
		if (!isOperator(token)) {
			throw new IllegalArgumentException("Invalid token: " + token);
		}

		if (OPERATORS.get(token)[1] == type) {
			return true;
		}
		return false;
	}
   
    // Compare precedence of operators.      
    private static final int cmpPrecedence(String token1, String token2)   
    {  
        if (!isOperator(token1) || !isOperator(token2))   
        {  
            throw new IllegalArgumentException("Invalid tokens: " + token1  
                    + " " + token2);  
        }  
        return OPERATORS.get(token1)[0] - OPERATORS.get(token2)[0];  
    }  
   
    // Convert infix expression format into reverse Polish notation  
    public static String[] infixToRPN(String[] inputTokens)   
    {  
        ArrayList<String> out = new ArrayList<String>();  
        Stack<String> stack = new Stack<String>();  
          
        // For each token  
        for (String token : inputTokens)   
        {  
            // If token is an operator  
            if (isOperator(token))   
            {    
                // While stack not empty AND stack top element   
                // is an operator  
                while (!stack.empty() && isOperator(stack.peek()))   
                {                      
                    if ((isAssociative(token, LEFT_ASSOC)         &&   
                         cmpPrecedence(token, stack.peek()) <= 0) ||   
                        (isAssociative(token, RIGHT_ASSOC)        &&   
                         cmpPrecedence(token, stack.peek()) < 0))   
                    {  
                        out.add(stack.pop());     
                        continue;  
                    }  
                    break;  
                }  
                // Push the new operator on the stack  
                stack.push(token);  
            }   
            // If token is a left bracket '('  
            else if (token.equals(P_LEFT))   
            {  
                stack.push(token);  //   
            }   
            // If token is a right bracket ')'  
            else if (token.equals(P_RIGHT))   
            {                  
                while (!stack.empty() && !stack.peek().equals(P_LEFT))   
                {  
                    out.add(stack.pop());   
                }  
                stack.pop();   
            }   
            // If token is a number  
            else   
            {  
                out.add(token);   
            }  
        }  
        while (!stack.empty())  
        {  
            out.add(stack.pop());   
        }  
        String[] output = new String[out.size()];  
        return out.toArray(output);  
    }  
      
	public static double RPNtoDouble(String[] tokens) {
		Stack<String> stack = new Stack<String>();

		// For each token
		for (String token : tokens) {
			// If the token is a value push it onto the stack
			if (!isOperator(token)) {
				stack.push(token);
			} else {
				// Token is an operator: pop top two entries
				Double d2 = Double.valueOf(stack.pop());
				Double d1 = Double.valueOf(stack.pop());

				// Get the result
				Double result = token.compareTo(OP_PLUS) == 0 ? d1 + d2 :
								  token.compareTo(OP_MINUS) == 0 ? d1 - d2 :
								  token.compareTo(OP_MUL) == 0 ? d1 * d2 :
								  d1 / d2;

				// Push result onto stack
				stack.push(String.valueOf(result));
			}
		}

		return Double.valueOf(stack.pop());
	}
	
	public static String RPNtoString (String[] tokens) {
		Stack<String> stack = new Stack<String>();

		// For each token
		for (String token : tokens) {
			// If the token is a value push it onto the stack
			if (!isOperator(token)) {
				stack.push(token);
			} else {
				// Token is an operator: pop top two entries
				BigDecimal d2 = new BigDecimal(stack.pop());
				BigDecimal d1 = new BigDecimal(stack.pop());

				// Get the result
				BigDecimal result = token.compareTo(OP_PLUS) == 0 ? d1.add(d2) :
								      token.compareTo(OP_MINUS) == 0 ? d1.subtract(d2) :
								      token.compareTo(OP_MUL) == 0 ? d1.multiply(d2).setScale(ROUND_UP_POSITION, BigDecimal.ROUND_HALF_UP) : 
								      d2.compareTo(BigDecimal.ZERO) == 0 ? BigDecimal.ZERO :
								      d1.divide(d2, ROUND_UP_POSITION+1, BigDecimal.ROUND_HALF_UP);

				// Push result onto stack
				stack.push(result.toString());
			}
		}

		return stack.pop();
	}
	
//	public static String setFormulaToBoundary (String instr) {
//		StringBuilder sb = new StringBuilder();
//		String splitter = SPLITTER; // ()+-/*
//		boolean fgPrevDelimiter = false;
//		
//		for (int i=0; i<instr.length(); i++) {
//			char c = instr.charAt(i);
//			if (splitter.indexOf(c) == -1) {
//				sb.append(c);
//				fgPrevDelimiter = false;
//			} else {
//				if (i != 0 && ! fgPrevDelimiter ) {
//					sb.append(' ');
//				}
//				sb.append(c);
//				sb.append(' ');
//				fgPrevDelimiter = true;
//			}
//		}
//		return sb.toString();
//	}

	public enum Result { SYNTAX_ERROR, PASS };
	public static class CalResult {
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
	
	public static CalResult setFormulaToBoundary (String instr) {
		StringBuilder sb = new StringBuilder();
		String splitter = SPLITTER; // ()+-/*
		boolean fgPrevDelimiter = false;
		CalResult result = new CalResult();

		if (CalParser.OPERATOR.indexOf(instr.charAt(0)) != -1) {
			if (instr.charAt(0) == '-') {
				sb.append('0');
				sb.append(' ');
			} else {
				result.setResult(Result.SYNTAX_ERROR);
				return result;
			}
		}
		
		for (int i=0; i<instr.length(); i++) {
			char c = instr.charAt(i);
			if (splitter.indexOf(c) == -1) {
				sb.append(c);
				fgPrevDelimiter = false;
			} else {
				if (i != 0 && ! fgPrevDelimiter ) {
					sb.append(' ');
				}
				sb.append(c);
				sb.append(' ');
				fgPrevDelimiter = true;
			}
		}
		
		result.setResult(Result.PASS);
		result.setFormula(sb.toString());
		return result;
	}

	
//	public static void main(String[] args) {
////		String[] input = "( 1 + 2 ) * ( 3 / 4 ) - ( 5 + 6 )".split(" ");
////		String[] input = preprocessInputString("(1+10)*(1/3)-(5+6)").split(" ");
//		String[] input = setFormulaToBoundary("(001+10)*((1/333)-(5+6))").split(" ");
//		String[] output = infixToRPN(input);
//
//		// Build output RPN string minus the commas
//		System.out.print("Input :");
//		for (String token : input) {
//			System.out.print(token + " ");
//		}
//		System.out.println("");
//		System.out.print("Output :");
//		for (String token : output) {
//			System.out.print(token + " ");
//		}
//		System.out.println("");
//
//		// Feed the RPN string to RPNtoDouble to give result
//		Double result = RPNtoDouble(output);
//		System.out.println("Result : " + result);
//		System.out.println("Result : " + Long.toHexString(Double.doubleToRawLongBits((double)0.125)));
//	}  
}  