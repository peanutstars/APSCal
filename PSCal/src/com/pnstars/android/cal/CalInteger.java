package com.pnstars.android.cal;

import java.math.BigInteger;

import com.pnstars.android.helper.PNSDbg;

public class CalInteger extends BigInteger {

	private static final long serialVersionUID = 978859537129632573L;
	static final String PREFIX_HEXA		= "0x";
	static final String PREFIX_OCTAL	= "0o";
	static final String PREFIX_BINARY	= "0b";
	
	public static class Form {
		String value;
		int radix;
		public Form() {
			value = "";
			radix = 10;
		}
	}
	
	public CalInteger(Form form) {
		super(form.value, form.radix);
	}
	
	public static Form getForm (String value) {
		Form form = new Form();
		String prefix;
		String data;
		
		if (value.length()>2) {
			prefix = value.substring(0,2);
			data = value.substring(2);
			PNSDbg.d("prefix:" + prefix + ", data:"+data);
			
			if (prefix.compareTo(PREFIX_HEXA) == 0) {
				form.radix = 16;
				form.value = data;
			} else if (prefix.compareTo(PREFIX_BINARY) == 0) {
				form.radix = 2;
				form.value = data;
			} else if (prefix.compareTo(PREFIX_OCTAL) == 0) {
				form.radix = 8;
				form.value = data;
			} else {
				form.radix = 10;
				form.value = value;
			}
		} else {
			form.radix = 10;
			form.value = value;
		}
		PNSDbg.d("value:" + form.value + ", radix:"+form.radix);
		return form;
	}
}
