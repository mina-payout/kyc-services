package com.minanet.trulioo.kyc.controller;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.minanet.trulioo.kyc.enums.CountryCodesEnum;
import com.trulioo.normalizedapi.ApiClient;
import com.trulioo.normalizedapi.ApiException;
import com.trulioo.normalizedapi.api.ConfigurationApi;
import com.trulioo.normalizedapi.api.ConnectionApi;

@RestController
public class KYCController {
	
	public static final org.slf4j.Logger logger =LoggerFactory.getLogger(KYCController.class);
	
	@Value("${trulioo.apiclient.username}")
	public String truliooUsername;
	
	@Value("${trulioo.apiclient.password}")
	public String truliooPassword;
	
	@CrossOrigin
	@GetMapping("/testTruliooConnection")
	public String testTruliooConnection() throws ApiException {
		ApiClient apiClient = new ApiClient();
		ConnectionApi connectionClient = new ConnectionApi(apiClient);
		return connectionClient.sayHello("Joe Napoli");
	}
	
	@CrossOrigin
	@GetMapping("/testTruliooAuthentication")
	public String testTruliooAuthentication() throws ApiException {
		ApiClient apiClient = new ApiClient();
		apiClient.setUsername(truliooUsername);
		apiClient.setPassword(truliooPassword);
		ConnectionApi connectionClient = new ConnectionApi(apiClient);
		logger.info("connection client");
		
		//testAuthentication
		return connectionClient.testAuthentication();
	}
	
	@CrossOrigin
	@GetMapping("/getCountryCodes")
	public Map<String, String> getCountryCodes() throws ApiException {
		ApiClient apiClient = new ApiClient();
		apiClient.setUsername(truliooUsername);
		apiClient.setPassword(truliooPassword);
		ConfigurationApi configurationClient = new ConfigurationApi(apiClient);
		
		//getCountryCodes
		List<String> countryCodes = configurationClient.getCountryCodes("Identity Verification");
		logger.info("\n---------------Country code Response------------");
		logger.info("{}",countryCodes);
		
		Map<String, String> countryList = new HashMap<>();
		countryCodes.forEach(countryCode -> {
		    countryList.put(CountryCodesEnum.valueOf(countryCode).getCountryCode(), CountryCodesEnum.valueOf(countryCode).getCountryName());
		});
		return countryList;
	}
	
	@CrossOrigin
	@GetMapping("/getRecommendedfields")
	public Object  getRecommendedfields(@RequestParam String countryCode) throws ApiException{
		
		ApiClient apiClient = new ApiClient();
		apiClient.setUsername(truliooUsername);
		apiClient.setPassword(truliooPassword);
		ConfigurationApi configurationClient = new ConfigurationApi(apiClient);

		//getRecommendedFields
		Object response = null;
		
			response = configurationClient.getRecommendedFields(countryCode, "Identity Verification");
			logger.info("\n---------------Recommended Fields Response------------");
			logger.info("{}",response);
		
		return response;
	}
}
