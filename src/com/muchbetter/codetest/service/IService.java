package com.muchbetter.codetest.service;

import com.fasterxml.jackson.databind.ObjectMapper;

import ratpack.handling.Context;

public interface IService<T> {
	public static final ObjectMapper JSON_MAPPER = new ObjectMapper(); 
	public T perform(Context ctx) throws Exception;
}
