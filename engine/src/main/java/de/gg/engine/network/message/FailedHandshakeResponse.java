package de.gg.engine.network.message;

public class FailedHandshakeResponse {

	private String msg;

	public FailedHandshakeResponse() {
		// default public constructor
	}

	public FailedHandshakeResponse(String msg) {
		this.msg = msg;
	}

	public String getMsg() {
		return msg;
	}

}
