package com.muchbetter.codetest.unittests;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static ratpack.jackson.Jackson.json;

import org.testng.annotations.AfterClass;
import org.testng.annotations.Test;

import com.muchbetter.codetest.datamodel.Token;
import com.muchbetter.codetest.handlers.LoginHandler;

import ratpack.http.Status;
import ratpack.jackson.JsonRender;
import ratpack.test.handling.HandlingResult;
import ratpack.test.handling.RequestFixture;

public class LoginHandlerTest {
	private HandlingResult validInvocationResult;

	public LoginHandlerTest() throws Exception {
		validInvocationResult = RequestFixture.handle(new LoginHandler(), fixture -> {
			fixture.responseHeader("Access-Control-Allow-Origin", "*").responseHeader("Accept-Language", "en-us")
					.responseHeader("Accept-Charset", "UTF-8").responseHeader("Content-Type", "application/json")
					.responseHeader("Cache-Control", "no-cache");
			fixture.uri("login");
		});
	}

	@Test
	public void login_ValidInvocation_ResponseHeadersTest() {
		assertEquals("CORS response header validation failed",
				validInvocationResult.getHeaders().get("Access-Control-Allow-Origin"), "*");
		assertEquals("Accept-Language response header validation failed",
				validInvocationResult.getHeaders().get("Accept-Language"), "en-us");
		assertEquals("Accept-Charset response header validation failed",
				validInvocationResult.getHeaders().get("Accept-Charset"), "UTF-8");
		assertEquals("Content-Type response header validation failed",
				validInvocationResult.getHeaders().get("Content-Type"), "application/json");
		assertEquals("Cache-Control response header validation failed",
				validInvocationResult.getHeaders().get("Cache-Control"), "no-cache");
	}

	@Test
	public void login_ValidInvocation_ResponseStatusTest() {
		assertEquals("Invalid response status", Status.CREATED, validInvocationResult.getStatus());
	}

	@Test
	public void login_ValidInvocation_ResponseBodyTest() {
		Token t = new Token("sample text");
		JsonRender renderer = validInvocationResult.rendered(json(t).getClass());
		assertTrue("Invalid response object received", renderer.getObject().getClass().equals(Token.class));
		Token responseToken = (Token) renderer.getObject();
		assertTrue("Invalid response data received", responseToken.toString().contains("token"));
		assertTrue("Invalid response token received", responseToken.getToken().length() > 0);
	}

	@AfterClass
	public void cleanup() {
		System.out.println("In AfterClass");
		validInvocationResult = null;
	}
}
