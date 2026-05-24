package com.compadres.na.utils;

import org.springframework.stereotype.Component;

@Component
public class FormatStringUtils {
	public static String escapeHtml(String value) {
		if (value == null) {
			return "";
		}

		return value
				.replace("&", "&amp;")
				.replace("\"", "&quot;")
				.replace("<", "&lt;")
				.replace(">", "&gt;");
	}
}
