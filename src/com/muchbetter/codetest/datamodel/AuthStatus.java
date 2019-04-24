package com.muchbetter.codetest.datamodel;

import java.io.Serializable;
import java.util.UUID;

public class AuthStatus implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 2983122816600086811L;
	private boolean isAuthSuccess;
	private String failureCause;
	private UUID userId;

	public AuthStatus() {
		// TODO Auto-generated constructor stub
	}

	public boolean isAuthSuccess() {
		return isAuthSuccess;
	}

	public void setAuthSuccess(boolean isAuthSuccess) {
		this.isAuthSuccess = isAuthSuccess;
	}

	public String getFailureCause() {
		return failureCause;
	}

	public void setFailureCause(String failureCause) {
		this.failureCause = failureCause;
	}

	public UUID getUserId() {
		return userId;
	}

	public void setUserId(UUID userId) {
		this.userId = userId;
	}

	@Override
	public String toString() {
		return "AuthStatus [isAuthSuccess=" + isAuthSuccess + ", failureCause=" + failureCause + ", userId=" + userId
				+ "]";
	}
}
