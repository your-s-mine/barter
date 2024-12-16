package com.barter.converter;

import org.springframework.core.convert.converter.Converter;

import com.barter.domain.oauth.enums.OAuthProvider;

public class OAuthProviderConverter implements Converter<String, OAuthProvider> {
	@Override
	public OAuthProvider convert(String source) {
		try {
			return OAuthProvider.valueOf(source.toUpperCase());
		} catch (RuntimeException ex) {
			throw new IllegalArgumentException("존재하지 않는 provider입니다.");
		}
	}
}
