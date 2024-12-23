package com.barter.domain.product.validator;

import com.barter.exception.customexceptions.ProductException;
import com.barter.exception.enums.ExceptionCode;

// 요구사항 정의서 반영 등록물품은 1~3 개의 이미지를 가져야함
public class ImageCountValidator {

	public static void checkImageCount(int newImageCount) {
		if (newImageCount > 3 || newImageCount < 1) {
			throw new ProductException(ExceptionCode.NOT_VALID_IMAGE_COUNT);
		}
	}

	public static void checkImageCount(int deletedImageCount, int newImageCount) {
		int resultCount = deletedImageCount + newImageCount;

		if (resultCount > 3 || resultCount < 1) {
			throw new ProductException(ExceptionCode.NOT_VALID_IMAGE_COUNT);
		}
	}
}
