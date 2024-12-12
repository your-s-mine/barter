package com.barter.domain.product.validator;

// 요구사항 정의서 반영 등록물품은 1~3 개의 이미지를 가져야함
public class ImageCountValidator {

	public static void checkImageCount(int newImageCount) {
		if (newImageCount > 3 || newImageCount < 1) {
			throw new IllegalArgumentException("1 ~ 3개 사이의 이미지를 가져야 합니다.");
		}
	}

	public static void checkImageCount(int deletedImageCount, int newImageCount) {
		int resultCount = deletedImageCount + newImageCount;

		if (resultCount > 3 || resultCount < 1) {
			throw new IllegalArgumentException("1 ~ 3개 사이의 이미지를 가져야 합니다.");
		}
	}
}
