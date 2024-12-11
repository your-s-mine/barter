package com.barter.common;

public class KeywordHelper {

	public static String removeSpace(String keyword) {
		return keyword.replaceAll("\\s+", "");
	}
}
