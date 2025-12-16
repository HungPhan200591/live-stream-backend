package com.stream.demo.config;

import com.p6spy.engine.spy.appender.MessageFormattingStrategy;

public class PrettySqlFormatter implements MessageFormattingStrategy {

	@Override
	public String formatMessage(
			int connectionId,
			String now,
			long elapsed,
			String category,
			String prepared,
			String sql,
			String url) {

		if (sql == null || sql.trim().isEmpty()) return "";

		return String.format(
				"\n\n[SQL]\n%s;\n[TIME] %d ms\n",
				sql,
				elapsed
		);
	}
}
