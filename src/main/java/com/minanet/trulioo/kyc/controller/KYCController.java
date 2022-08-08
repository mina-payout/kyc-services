package com.minanet.trulioo.kyc.controller;

import java.io.IOException;
import java.security.GeneralSecurityException;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.json.JSONObject;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

import com.google.gson.JsonObject;
import com.minanet.trulioo.kyc.config.TruliooConfig;
import com.minanet.trulioo.kyc.service.GoogleSheetsService;
import com.minanet.trulioo.kyc.service.TruliooKYCService;
import com.trulioo.normalizedapi.ApiClient;
import com.trulioo.normalizedapi.ApiException;
import com.trulioo.normalizedapi.api.ConnectionApi;
import com.trulioo.normalizedapi.api.VerificationsApi;
import com.trulioo.normalizedapi.model.TransactionRecordResult;
import com.trulioo.normalizedapi.model.TransactionStatus;

@RestController
public class KYCController {
	
	public static final org.slf4j.Logger logger =LoggerFactory.getLogger(KYCController.class);

	@Autowired
	RestTemplate restTemplate;
	
	@Autowired
	GoogleSheetsService sheetsService;
	
	@Autowired
	TruliooKYCService kycService;
	
	@Autowired
	TruliooConfig config;
	
	@Value("${trulioo.embedid.token.uri}")
	public String truliooEmbedIdTokenUri;
	
	@Value("${trulioo.embedid.key.fe}")
	public String truliooEmbedIdKeyFe;
	
	@Value("${trulioo.embedid.key.api}")
	public String truliooEmbedIdKeyBe;
	
	@CrossOrigin
	@GetMapping("/testTruliooConnection")
	public String testTruliooConnection() throws ApiException {
		ApiClient apiClient = new ApiClient();
		ConnectionApi connectionClient = new ConnectionApi(apiClient);
		return connectionClient.sayHello("Joe Napoli");
	}
	
	@CrossOrigin //testAuthentication
	@GetMapping("/testTruliooAuthentication")
	public String testTruliooAuthentication() throws ApiException {
		ConnectionApi connectionClient = new ConnectionApi(config.getApiClient());
		logger.info("connection client");
		
		return connectionClient.testAuthentication();
	}
	
	@CrossOrigin
	@GetMapping("/getCountryCodes")
	public Map<String, String> getCountryCodes() throws ApiException {
		return kycService.getCountryCodesService();
	}
	
	@CrossOrigin
	@GetMapping("/getRecommendedfields")
	public Object getRecommendedfields(@RequestParam String countryCode) throws ApiException{
		JsonObject result = new JsonObject();
		 	if (countryCode == null || countryCode.isEmpty()) {
		 		logger.warn(" Missing the required parameter 'countryCode' when calling getRecommendedfields()");
				return " Missing the required parameter 'countryCode' when calling getRecommendedfields()";
				//throw new ApiException("Missing the required parameter 'countryCode' when calling getRecommendedfields()");
	        }
			result = kycService.fetchCountryWiseRequiredField(countryCode);
	        return result.toString();
	}
			
	@CrossOrigin	
	@PostMapping(value = "/verifyKYCService", 		        consumes = {MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE},
	        produces = {MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE})
	public Integer verifyKYCService(final HttpServletRequest httpRequest, HttpServletResponse response
			) throws IOException, ApiException {
		kycService.verifyKYCAsyncService(httpRequest, response);
        return response.getStatus();
	}	
	
	@CrossOrigin
	@PostMapping("/trulioo-api/embedids/tokens/{publicKey}")
	public String getEmbedIdToken(@PathVariable  String publicKey) {
		HttpHeaders headers = new HttpHeaders();
		headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
		headers.add("cache-control", "no-cache");
		headers.add("content-type", "application/json");
		headers.add("x-trulioo-api-key", truliooEmbedIdKeyBe);
		
		Map<String, Object> map = new HashMap<>();
		map.put("publicKey", publicKey);
		
		HttpEntity<Map<String, Object>> entity = new HttpEntity<>(map, headers);
		ResponseEntity<String> response = restTemplate.exchange(truliooEmbedIdTokenUri, HttpMethod.POST, entity, String.class);

		return response.getBody();
	}
	
	@CrossOrigin
	@GetMapping("/getTransactionUpdate/{transactionID}")
	public String googleSheetReadValue(@PathVariable  String transactionID) throws GeneralSecurityException, IOException, ApiException {

		VerificationsApi verificationClient = new VerificationsApi(config.getApiClient());
		TransactionStatus ts = verificationClient.getTransactionStatus(transactionID);
		
		TransactionRecordResult recordResult = verificationClient.getTransactionRecord(ts.getTransactionRecordId());
		logger.info("{}",recordResult);
	
		return "Done";
	}
	
	@CrossOrigin
	@GetMapping("/googleSheetReadSpecificRange")
	public String googleSheetReadSpecificRange() throws GeneralSecurityException, IOException {
		 sheetsService.getSpreadsheetValuesForSpecificRange();
		return "Done"; 
	}
	
	@CrossOrigin
	@GetMapping("/googleSheetUpdate")
	public String googleSheetUpdate() throws GeneralSecurityException, IOException {
		 sheetsService.updateValue();
		return "Done"; 
	}
	
	/*
	 * @CrossOrigin
	 * 
	 * @GetMapping("/searchValuePOC") public String searchValue() throws
	 * IOException, GeneralSecurityException { sheetsService.searchValue(); return
	 * "Done"; }
	 */
	
	@CrossOrigin  
    @PostMapping("/verifyCallbackURL/{uuid}")
    public Integer verifyCallbackURL(@PathVariable("uuid") String uuid,
            final HttpServletRequest httpRequest, HttpServletResponse response
            ) throws IOException, ApiException {

        logger.info("\n---------------Verify Callback KYC Service Request------------");
        logger.info("{}",uuid);

        String clientRequest = httpRequest.getReader().lines().collect(Collectors.joining(System.lineSeparator()));
        JSONObject clientRequestJson = new JSONObject(clientRequest);

        VerificationsApi verificationClient = new VerificationsApi(config.getApiClient());
        TransactionStatus ts = verificationClient.getTransactionStatus(clientRequestJson.getString("TransactionId"));

        TransactionRecordResult recordResult = verificationClient.getTransactionRecord(ts.getTransactionRecordId());
        logger.info("\n---------------Verify Callback KYC Service Response------------");
        logger.info("{}",recordResult);  

        try {
        	sheetsService.updateValue();
		} catch (IOException | GeneralSecurityException e) {
			e.printStackTrace();
		}
       return response.getStatus();
    }  
}
