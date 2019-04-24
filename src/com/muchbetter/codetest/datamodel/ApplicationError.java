package com.muchbetter.codetest.datamodel;

import java.io.Serializable;

public class ApplicationError implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = -4874461524569383585L;
	private int responseStatus;
	private String responseText;

	public ApplicationError() {
	}

	public ApplicationError(int responseStatus, String responseText) {
		this.responseStatus = responseStatus;
		this.responseText = responseText;
	}

	public int getResponseStatus() {
		return responseStatus;
	}

	public void setResponseStatus(int responseStatus) {
		this.responseStatus = responseStatus;
	}

	public String getResponseText() {
		return responseText;
	}

	public void setResponseText(String responseText) {
		this.responseText = responseText;
	}

	@Override
	public String toString() {
		return "ApplicationError [responseStatus=" + responseStatus + ", responseText=" + responseText + "]";
	}

}
