package com.minanet.trulioo.kyc.controller;

import java.io.IOException;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.json.JSONArray;
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
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.minanet.trulioo.core.domain.TruliooResponse;
import com.minanet.trulioo.kyc.enums.CountryCodesEnum;
import com.trulioo.normalizedapi.ApiCallback;
import com.trulioo.normalizedapi.ApiClient;
import com.trulioo.normalizedapi.ApiException;
import com.trulioo.normalizedapi.ApiResponse;
import com.trulioo.normalizedapi.api.ConfigurationApi;
import com.trulioo.normalizedapi.api.ConnectionApi;
import com.trulioo.normalizedapi.api.VerificationsApi;
import com.trulioo.normalizedapi.model.VerifyRequest;
import com.trulioo.normalizedapi.model.VerifyResult;

@RestController
public class KYCController {
	
	public static final org.slf4j.Logger logger =LoggerFactory.getLogger(KYCController.class);
	
	@Autowired
	RestTemplate restTemplate;
	
	@Value("${trulioo.apiclient.username}")
	public String truliooUsername;
	
	@Value("${trulioo.apiclient.password}")
	public String truliooPassword;

	@Value("${trulioo.embedid.token.uri}")
	public String truliooEmbedIdTokenUri;
	
	@Value("${trulioo.embedid.key.fe}")
	public String truliooEmbedIdKeyFe;
	
	@Value("${trulioo.embedid.key.api}")
	public String truliooEmbedIdKeyBe;
	
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
		HttpServletResponse response;
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
		return sortByValue(countryList);
	}
	
	@CrossOrigin
	@GetMapping("/getRecommendedfields")
	public Object getRecommendedfields(@RequestParam String countryCode) throws ApiException{
			
		ApiClient apiClient = new ApiClient();
	        apiClient.setUsername(truliooUsername);
	        apiClient.setPassword(truliooPassword);
	        ConfigurationApi configurationClient = new ConfigurationApi(apiClient);

	        // getRecommendedFields
	        Object response = null;

	        response = configurationClient.getRecommendedFields(countryCode, "Identity Verification");
	        Gson gson = new Gson();
	        JsonElement jsonElement = gson.toJsonTree(response);
	        JsonObject jsonObject = (JsonObject) jsonElement;

	        JsonObject personInfo = jsonObject.getAsJsonObject("properties").getAsJsonObject("PersonInfo");

	        personInfo.getAsJsonObject("properties").remove("DayOfBirth");
	        personInfo.getAsJsonObject("properties").remove("MonthOfBirth");
	        personInfo.getAsJsonObject("properties").remove("YearOfBirth");

	        JsonObject dob = new JsonObject();
	        dob.addProperty("type", "Date");
	        dob.addProperty("description", "birth date (e.g. 23/11/1975)");
	        dob.addProperty("label", "Date Of Birth");
	        personInfo.getAsJsonObject("properties").add("DateOfBirth", dob);

	        JsonArray req = personInfo.getAsJsonArray("required");
	        JsonArray finalList = new JsonArray();
	        for (int i = 0; i < req.size(); i++) {
	            if ( !("DayOfBirth".equals(req.get(i).getAsString()) || "MonthOfBirth".equals(req.get(i).getAsString())
	                    || "YearOfBirth".equals(req.get(i).getAsString()))) {
	                finalList.add(req.get(i));
	            }
	        }
	        finalList.add("DateOfBirth");
	        personInfo.remove("required");
	        personInfo.add("required", finalList);

	        JsonObject location = jsonObject.getAsJsonObject("properties").getAsJsonObject("Location");
	        JsonObject comms = jsonObject.getAsJsonObject("properties").getAsJsonObject("Communication");

	        JsonObject rp = new JsonObject();
	        rp.add("PersonInfo", personInfo);
	        rp.add("Location", location);
	        rp.add("Communication", comms);

	        JsonObject result = new JsonObject();
	        result.add("properties", rp);

	        logger.info("\n---------------Recommended Fields Response------------");
	        logger.info("{}", result);

	        return result.toString();
	}
			
	@CrossOrigin	
	@PostMapping(value = "/verifyKYCService", 		        consumes = {MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE},
	        produces = {MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE})
	public Integer verifyKYCService(final HttpServletRequest httpRequest, HttpServletResponse response
			) throws IOException, ApiException {
		
		
		ApiClient apiClient = new ApiClient();
		apiClient.setUsername(truliooUsername);
		apiClient.setPassword(truliooPassword);
		VerificationsApi verificationClient = new VerificationsApi(apiClient);

        String clientRequest = httpRequest.getReader().lines().collect(Collectors.joining(System.lineSeparator()));
        JSONObject clientRequestJson = new JSONObject(clientRequest); 
        
        JSONObject jsonObj = new JSONObject();
        jsonObj.put("AcceptTruliooTermsAndConditions", "true");
        jsonObj.put("CleansedAddress", "false");
        jsonObj.put("ConfigurationName", "Identity Verification");
        
        JSONArray consentForDataSources = new JSONArray();
        consentForDataSources.put("Birth Registry");
        consentForDataSources.put("Visa Verification");
        consentForDataSources.put("DVS ImmiCard Search");
        consentForDataSources.put("DVS Citizenship Certificate Search");
        consentForDataSources.put("Credit Agency");
        
        jsonObj.put("ConsentForDataSources", consentForDataSources);
        jsonObj.put("CountryCode", clientRequestJson.get("CountryCode"));
        jsonObj.put("DataFields", clientRequestJson.get("DataFields"));
        
		Gson gson = new GsonBuilder().setPrettyPrinting().create(); 
		VerifyRequest request = gson.fromJson(jsonObj.toString(), VerifyRequest.class);
		 
		logger.info("\n---------------Verify KYC Service Request------------");
		logger.info("{}",request);
		
		//asyn call
        verificationClient.verifyAsync(request, new ApiCallback<VerifyResult>() {
           @Override
           public void onFailure(ApiException e, int statusCode, Map<String, List<String>> responseHeaders) {
        	   Logger.getLogger(KYCController.class.getName()).log(Level.SEVERE, null, e);
           }
           @Override
           public void onSuccess(VerifyResult response, int statusCode, Map<String, List<String>> responseHeaders) {
                logger.info("\n---------------Verify KYC Service Response------------");
        		logger.info("{}",response);
        		kycResponse(response.toString());
           }
           @Override
           public void onUploadProgress(long bytesWritten, long contentLength, boolean done) {
        	   
           }
           @Override
           public void onDownloadProgress(long bytesRead, long contentLength, boolean done) {
        	   
           }
       });
       
       return response.getStatus();
	}	
	
	public String kycResponse(String response) { // get asyn call response POC
		System.out.println("************"+response);
		return response;
	}
	
	//Common method
	public static Map<String, String> sortByValue(Map<String, String> hm) {
        // Create a list from elements of HashMap
        List<Map.Entry<String, String>> list = new LinkedList<Map.Entry<String, String>>(hm.entrySet());

        // Sort the list
        Collections.sort(list, new Comparator<Map.Entry<String, String>>() {
            public int compare(Map.Entry<String, String> o1, Map.Entry<String, String> o2) {
                return (o1.getValue()).compareTo(o2.getValue());
            }
        });

        // put data from sorted list to hashmap
        HashMap<String, String> temp = new LinkedHashMap<String, String>();
        for (Map.Entry<String, String> aa : list) {
            temp.put(aa.getKey(), aa.getValue());
        }
        return temp;
    }
	
	//getRecommendedfields POC for error handling
	@CrossOrigin
	@GetMapping("/getRecommendedfieldsPOC")
	public Object getRecommendedfieldsPOC(@RequestParam String countryCode) throws ApiException{
		
		TruliooResponse truliooResponse = new TruliooResponse();
		
		if (countryCode == null || countryCode.isEmpty()) {
			try {
				throw new ApiException("Missing the required parameter 'countryCode' when calling getRecommendedfields()");
			} catch (Exception e) {
				return truliooResponse;
			}
            
        }
		 
		ApiClient apiClient = new ApiClient();
		apiClient.setUsername(truliooUsername);
		apiClient.setPassword(truliooPassword);
		ConfigurationApi configurationClient = new ConfigurationApi(apiClient);
		try {
			ApiResponse<Object> response = configurationClient.getRecommendedFieldsWithHttpInfo(countryCode, "Identity Verification");
			logger.info("\n---------------Recommended Fields Response------------");
			logger.info("{}",response.getData());
			System.out.println("getHeaders"+response.getHeaders());
			truliooResponse = handleTruliooResponse(response.getStatusCode(), "Pass", response.getData());
		} catch (ApiException e) {
			truliooResponse = handleTruliooResponse(e.getCode(), e.getResponseBody(), "The user name and password you provided is not valid or you are using an account not configured to be an API user.");
		}
		 
		return truliooResponse;
	}
	
	//POC
	private TruliooResponse handleTruliooResponse(Integer httpStatus, String truliooStatus, Object response) {
		TruliooResponse truliooResponse = new TruliooResponse();
		truliooResponse.setHttpStatus(httpStatus);
		truliooResponse.setTruliooStatus(truliooStatus);
		truliooResponse.setTruliooResponse(response);
		
		return truliooResponse;
	}
	
	@CrossOrigin
	@GetMapping("/getEmbedIdToken")
	public String getEmbedIdToken() {
		HttpHeaders headers = new HttpHeaders();
		headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
		headers.add("cache-control", "no-cache");
		headers.add("content-type", "application/json");
		headers.add("x-trulioo-api-key", truliooEmbedIdKeyBe);
		
		Map<String, Object> map = new HashMap<>();
		map.put("publicKey", truliooEmbedIdKeyFe);
		
		HttpEntity<Map<String, Object>> entity = new HttpEntity<>(map, headers);
		ResponseEntity<String> response = restTemplate.exchange(truliooEmbedIdTokenUri, HttpMethod.POST, entity, String.class);

		return response.getBody();
	}
}
