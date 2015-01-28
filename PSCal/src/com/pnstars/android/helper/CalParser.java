package com.pnstars.android.helper;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Stack;

import com.pnstars.android.cal.CalInteger;
import com.pnstars.android.cal.CalResult;
   
public class CalParser   
{  
    // Associativity constants for operators  
    private static final int LEFT_ASSOC  = 0;  
    private static final int RIGHT_ASSOC = 1;
    private static final int ROUND_UP_POSITION = 10;
    
    public static final int INPUT_MAX_DESIMALS = ROUND_UP_POSITION;
    public static final String OP_PLUS		= "+" ;
    public static final String OP_MINUS		= "-" ;
    public static final String OP_DIV		= "\u00f7" ;
    public static final String OP_MUL		= "*"; //"\u00d7" ;
    public static final String OP_AND		= "&" ;
    public static final String OP_OR			= "|" ;
    public static final String OP_XOR		= "^" ;
    public static final String P_LEFT		= "(" ;
    public static final String P_RIGHT		= ")" ;
    public static final String DOT			= "." ;
    public static final String BITWISE		= OP_AND + OP_OR + OP_XOR;
    public static final String OPERATOR	= OP_PLUS + OP_MINUS + OP_DIV + OP_MUL + BITWISE ;
    public static final String SPLITTER	= OPERATOR + P_LEFT + P_RIGHT ;
    
    // Operators  
	private static final Map<String, int[]> PoolOPERATORS = new HashMap<String, int[]>();
	static {
		// Map<"token", []{precendence, associativity}>
		PoolOPERATORS.put(OP_AND, new int[] { -5, LEFT_ASSOC });
		PoolOPERATORS.put(OP_OR, new int[] { -5, LEFT_ASSOC });
		PoolOPERATORS.put(OP_XOR, new int[] { -5, LEFT_ASSOC });
		PoolOPERATORS.put(OP_PLUS, new int[] { 0, LEFT_ASSOC });
		PoolOPERATORS.put(OP_MINUS, new int[] { 0, LEFT_ASSOC });
		PoolOPERATORS.put(OP_MUL, new int[] { 5, LEFT_ASSOC });
		PoolOPERATORS.put(OP_DIV, new int[] { 5, LEFT_ASSOC });
	}

	// Test if token is an operator
	private static boolean isOperator(String token) {
		return PoolOPERATORS.containsKey(token);
	}

	// Test associativity of operator token
	private static boolean isAssociative(String token, int type) {
		if (!isOperator(token)) {
			throw new IllegalArgumentException("Invalid token: " + token);
		}

		if (PoolOPERATORS.get(token)[1] == type) {
			return true;
		}
		return false;
	}
   
    // Compare precedence of operators.      
    private static final int cmpPrecedence(String token1, String token2)   
    {  
        if (!isOperator(token1) || !isOperator(token2))   
        {  
            throw new IllegalArgumentException("Invalid tokens: " + token1 + " " + token2);  
        }  
        return PoolOPERATORS.get(token1)[0] - PoolOPERATORS.get(token2)[0];  
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
	
	public static String RPNtoBigDecimal (String[] tokens) {
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

	public static String RPNtoCalInteger (String[] tokens) {
		Stack<String> stack = new Stack<String>();
		boolean fgErr = false;

		// For each token
		for (String token : tokens) {
			// If the token is a value push it onto the stack
			if (!isOperator(token)) {
				CalInteger d = new CalInteger(CalInteger.getForm(token));
				stack.push(d.toString(10));
			} else {
				// Token is an operator: pop top two entries
				CalInteger d2 = new CalInteger(CalInteger.getForm(stack.pop()));
				CalInteger d1 = new CalInteger(CalInteger.getForm(stack.pop()));

				// Get the result
				BigInteger result;
				if (token.compareTo(OP_PLUS) == 0) {
					result = d1.add(d2) ;
				} else if (token.compareTo(OP_MINUS) == 0) {
					result = d1.subtract(d2);
				} else if (token.compareTo(OP_MUL) == 0) {
					result = d1.multiply(d2);
				} else if (token.compareTo(OP_DIV) == 0) {
					if (d2.compareTo(BigInteger.ZERO) == 0) {
						fgErr = true;
						result = BigInteger.ZERO;
						break;
					} else {
						result = d1.divide(d2);
					}
				} else if (token.compareTo(OP_AND) == 0) {
					result = d1.and(d2);
				} else if (token.compareTo(OP_OR) == 0) {
					result = d1.or(d2);
				} else if (token.compareTo(OP_XOR) == 0) {
					result = d1.xor(d2);
				} else {
					fgErr = true;
					result = BigInteger.ZERO;
				}

				// Push result onto stack
				stack.push(result.toString(10));
			}
		}

		return stack.pop();
	}

	public static CalResult spliteFormulaToSeparator (String instr) {
		StringBuilder sb = new StringBuilder();
		CalResult result = new CalResult();
		CalResult.Result emResult =  CalResult.Result.SYNTAX_ERROR;
		boolean fgPreviousDelimiter = false;
		boolean fgPR = false;	/* Parenthesis Right */
		boolean fgPLStart = false;
		int countPLR = 0;  /* Parenthesis Left Right */

		do {
			if (OPERATOR.indexOf(instr.charAt(0)) != -1) {
				if (instr.charAt(0) == '-') {
					sb.append('0');
					sb.append(' ');
				} else {
					break;
				}
			}

			boolean fgErr = false;
			boolean fgPrevInputNumber = false;
			boolean fgInputNumber = false;
			boolean fgInputPRight = false;
			boolean fgPrevInputPRight = false;
			int numCount = 0;
			Stack<Integer> numCountStack = new Stack<Integer>();
			
			for (int i = 0; i < instr.length(); i++) {
				char c = instr.charAt(i);
				fgPrevInputNumber = fgInputNumber;
				fgPrevInputPRight = fgInputPRight;
				
				if (SPLITTER.indexOf(c) == -1) {
					/* Non Delimiter */
					fgInputNumber = true;
					fgInputPRight = false;
					
					if (fgPrevInputPRight) {
						/* In case of converting from ")Number" to ")*Number" */
						sb.append(CalParser.OP_MUL);
						sb.append(' ');
					}
					
					sb.append(c);
					numCount ++;
					fgPreviousDelimiter = false;
					fgPR = false;
				} else {
					/* Delimiter */
					fgInputNumber = false;
					fgInputPRight = false;
					if (i != 0 && !fgPreviousDelimiter) {
						sb.append(' ');
					}
					
					/* check parenthesis */
					if (c == '(') {
						if (fgPrevInputNumber) {
							/* In case of converting from "Number(" to "Number*(" */ 
							sb.append(CalParser.OP_MUL);
							sb.append(' ');
						}
						fgPLStart = true;
					}
					if (fgPLStart) {
						if (c == '(') {
							countPLR ++;
							numCountStack.push(numCount);
						}
						if (c == ')') {
							fgInputPRight = true;
							countPLR --;
							
							/* it is error, if it is inputed "()" or "(((())))" */
							int prevNumCount = numCountStack.pop();
							if (prevNumCount == numCount) {
								fgErr = true;
								break;
							}
						}
					}
					
					/* in case of ")(" */
					if (fgPR && c == '(') {
						sb.append(OP_MUL);
						sb.append(' ');
					}
					if (c == ')') {
						fgPR = true;
					} else {
						fgPR = false;
					}

					sb.append(c);
					sb.append(' ');
					fgPreviousDelimiter = true;
				}
			}

			if (fgErr == true)	break;
			if (numCount == 0) 	break;
			if (countPLR != 0)	break;
			
			emResult = CalResult.Result.PASS;
			
		} while (false);
		
		result.setResult(emResult);
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