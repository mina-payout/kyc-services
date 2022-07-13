package com.minanet.trulioo.kyc.controller;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import javax.servlet.http.HttpServletRequest;

import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.minanet.trulioo.kyc.enums.CountryCodesEnum;
import com.trulioo.normalizedapi.ApiClient;
import com.trulioo.normalizedapi.ApiException;
import com.trulioo.normalizedapi.api.ConfigurationApi;
import com.trulioo.normalizedapi.api.ConnectionApi;
import com.trulioo.normalizedapi.api.VerificationsApi;
import com.trulioo.normalizedapi.model.VerifyRequest;
import com.trulioo.normalizedapi.model.VerifyResult;

@RestController
public class KYCController {
	
	public static final org.slf4j.Logger logger =LoggerFactory.getLogger(KYCController.class);
	
	@Value("${trulioo.apiclient.username}")
	public String truliooUsername;
	
	@Value("${trulioo.apiclient.password}")
	public String truliooPassword;
	
	VerifyResult response;
	
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
	public Object getRecommendedfields(@RequestParam String countryCode) throws ApiException{
		
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
			
	@CrossOrigin	
	@PostMapping(value = "/verifyKYCService", 		        consumes = {MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE},
	        produces = {MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE})
	public String verifyKYCService(final HttpServletRequest httpRequest
			) throws IOException, ApiException {
		
		ApiClient apiClient = new ApiClient();
		apiClient.setUsername(truliooUsername);
		apiClient.setPassword(truliooPassword);

        String clientRequest = httpRequest.getReader().lines().collect(Collectors.joining(System.lineSeparator()));
		
		Gson gson = new GsonBuilder().setPrettyPrinting().create(); 
		VerifyRequest request = gson.fromJson(clientRequest, VerifyRequest.class);
		 
		logger.info("\n---------------Verify KYC Service Request------------");
		logger.info("{}",request);
		
		//VerificationsApi
        VerificationsApi verificationClient = new VerificationsApi(apiClient);
		VerifyResult result = verificationClient.verify(request);
		logger.info("\n---------------Verify KYC Service Response------------");
		logger.info("{}",result);
        
		/*
		 * verificationClient.verifyAsync(request, new ApiCallback<VerifyResult>() {
		 * 
		 * @Override public void onFailure(ApiException e, int statusCode, Map<String,
		 * List<String>> responseHeaders) {
		 * Logger.getLogger(KYCController.class.getName()).log(Level.SEVERE, null, e); }
		 * 
		 * @Override public void onSuccess(VerifyResult result, int statusCode,
		 * Map<String, List<String>> responseHeaders) {
		 * System.out.println("response from trulioo-----"+result.toString()); //To
		 * change body of generated methods, choose Tools | Templates. response =
		 * result; }
		 * 
		 * @Override public void onUploadProgress(long bytesWritten, long contentLength,
		 * boolean done) { //To change body of generated methods, choose Tools |
		 * Templates. }
		 * 
		 * @Override public void onDownloadProgress(long bytesRead, long contentLength,
		 * boolean done) { //To change body of generated methods, choose Tools |
		 * Templates. } });
		 */
        
         return result.toString();
	}
}
