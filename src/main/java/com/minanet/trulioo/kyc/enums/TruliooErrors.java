package com.minanet.trulioo.kyc.enums;

public enum TruliooErrors {
	SUCCESS("200", "Success"),
	INVALID_REQUEST("400", "Your request could not be processed, there should be more details in the response."),
	UNAUTHORIZED_EXCEPTION("401", "The user name and password you provided is not valid or you are using an account not configured to be an API user."),
	REQUEST_TOOK_LONGER_PROCESS("408", "The request took longer to process than we waited."),
	CHECK_HEADER("415", "You asked for a media type that we do not support. You should request application/json in the Content-Type header."),
	SERVER_ERROR("500", "An error happened on the server without a specific message."),
	DEFAULT("DEFAULT", "Error.");
	
	/** variable for error code. */
	private String errorCode;

	/** variable for error code. */
	private String errorDescription;
	
    TruliooErrors(String errorCode, String errorDescription) {
		this.errorCode = errorCode;
		this.errorDescription = errorDescription;
	}

	public String getErrorCode() {
		return errorCode;
	}

	public String getErrorDescription() {
		return errorDescription;
	}


	@Override
	public String toString() {
		return this.errorCode + ":" + this.errorDescription;
	}
}
