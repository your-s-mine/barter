package com.barter.domain.product.controller;

import org.springframework.web.bind.annotation.RestController;

import com.barter.domain.product.service.ProductService;

import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
public class ProductController {

	private final ProductService productService;
}
