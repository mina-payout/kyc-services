package com.minanet.trulioo.kyc.transformer;

import com.minanet.trulioo.core.domain.IdentityVerificationResponse;
import com.minanet.trulioo.tools.DateTimeFormatter;
import com.trulioo.normalizedapi.model.VerifyRequest;
import com.trulioo.normalizedapi.model.VerifyResult;

public class IdentityVerificationTransformer {
	// for add in google sheet
	public static IdentityVerificationResponse transformVerifyResponse(VerifyRequest request, VerifyResult response) {
		IdentityVerificationResponse verificationResponse = new IdentityVerificationResponse();
		verificationResponse.setSubmissionDate(DateTimeFormatter.getCurrentDateTime());
		verificationResponse.setEmail(request.getDataFields().getCommunication().getEmailAddress());
		verificationResponse.setFirstName(request.getDataFields().getPersonInfo().getFirstGivenName());
		verificationResponse.setMiddleName(request.getDataFields().getPersonInfo().getMiddleName());
		verificationResponse.setLastName(request.getDataFields().getPersonInfo().getFirstSurName());
		verificationResponse.setCountry(request.getCountryCode());
		verificationResponse.setInternalTxnID(response.getCustomerReferenceID());
		verificationResponse.setTruliooTxnIDV(response.getTransactionID());
		verificationResponse.setNumberOfAttempts("1");
		return verificationResponse;
	}
	
	//for update in google sheet
	public static IdentityVerificationResponse transformVerifyCallbackResponse() {
		IdentityVerificationResponse verificationResponse = new IdentityVerificationResponse();
		//verificationResponse.set
		return verificationResponse;
	}
	
}
