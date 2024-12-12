package com.barter.common;

import java.util.Arrays;
import java.util.List;

public class KeywordHelper {

	public static String removeSpace(String keyword) {
		return keyword.replaceAll("\\s+", "");
	}

	public static List<String> extractKeywords(String productName) {
		return Arrays.stream(productName.split(" ")).toList();
	}
}
