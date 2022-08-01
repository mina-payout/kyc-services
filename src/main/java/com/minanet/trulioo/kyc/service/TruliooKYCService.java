package com.minanet.trulioo.kyc.service;

import java.io.IOException;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.google.gson.JsonObject;

public interface TruliooKYCService {
	void verifyKYCAsyncService(final HttpServletRequest httpRequest, HttpServletResponse response) throws IOException;
	JsonObject fetchCountryWiseRequiredField(String countryCode);
	Map<String, String> getCountryCodesService();
}
