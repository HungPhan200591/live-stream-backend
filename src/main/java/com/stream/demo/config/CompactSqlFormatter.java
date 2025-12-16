package com.stream.demo.config;

import com.p6spy.engine.spy.appender.SingleLineFormat;

public class CompactSqlFormatter extends SingleLineFormat {

	@Override
	public String formatMessage(int connectionId, String now, long elapsed,
	                            String category, String prepared, String sql, String url) {

		String caller = extractCaller();

		// Format: 2 dòng đẹp
		return String.format("\n\n[SQL]\nExecutionTime: %dms | Connection: %d | %s |\n%s\n",
				elapsed, connectionId, caller, sql);
	}

	private String extractCaller() {
		StackTraceElement[] stack = Thread.currentThread().getStackTrace();

		for (StackTraceElement element : stack) {
			String className = element.getClassName();

			// Skip framework classes và chính formatter này
			if (className.startsWith("com.stream.demo") &&
					!className.contains("$$") &&
					!className.contains("CompactSqlFormatter") &&
					!className.contains("P6Spy")) {

				String simpleName = className.substring(className.lastIndexOf('.') + 1);
				return simpleName + "." + element.getMethodName() + ":" + element.getLineNumber();
			}
		}
		return "Unknown";
	}
}