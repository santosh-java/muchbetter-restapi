package com.muchbetter.codetest.utils;

import java.io.PrintWriter;
import java.io.StringWriter;

public class StackTraceUtil {
	public static String getStackTraceAsString(Exception e) {
		StringWriter stackTraceAsString = new StringWriter();
		e.printStackTrace(new PrintWriter(stackTraceAsString));
		return stackTraceAsString.toString();
	}
}
