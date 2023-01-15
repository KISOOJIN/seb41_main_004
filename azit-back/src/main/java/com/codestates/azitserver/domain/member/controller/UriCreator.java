package com.codestates.azitserver.domain.member.controller;

import java.net.URI;

import org.springframework.web.util.UriComponentsBuilder;

public class UriCreator {
	public static URI createUri(String defaultUrl, long resourceId) {
		return UriComponentsBuilder
			.newInstance()
			.path(defaultUrl + "/{resource-id}")
			.buildAndExpand(resourceId)
			.toUri();
	}
}
