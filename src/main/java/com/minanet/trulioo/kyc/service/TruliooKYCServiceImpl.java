package com.minanet.trulioo.kyc.service;

import java.io.IOException;
import java.security.GeneralSecurityException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.json.JSONArray;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.minanet.trulioo.core.domain.IdentityVerificationResponse;
import com.minanet.trulioo.kyc.config.TruliooConfig;
import com.minanet.trulioo.kyc.enums.CountryCodesEnum;
import com.minanet.trulioo.kyc.enums.TruliooErrors;
import com.minanet.trulioo.kyc.transformer.IdentityVerificationTransformer;
import com.minanet.trulioo.tools.CollectionFormatter;
import com.trulioo.normalizedapi.ApiCallback;
import com.trulioo.normalizedapi.ApiException;
import com.trulioo.normalizedapi.api.ConfigurationApi;
import com.trulioo.normalizedapi.api.VerificationsApi;
import com.trulioo.normalizedapi.model.VerifyRequest;
import com.trulioo.normalizedapi.model.VerifyResult;

@Service
public class TruliooKYCServiceImpl implements TruliooKYCService{
	private static final Logger logger = LoggerFactory.getLogger(TruliooKYCServiceImpl.class);
	private static String cellId = null; 
	@Autowired
	GoogleSheetsService sheetsService;
	
	@Autowired
	TruliooConfig config;

	@Override
	public void verifyKYCAsyncService(HttpServletRequest httpRequest, HttpServletResponse response) throws IOException {
		VerificationsApi verificationClient = new VerificationsApi(config.getApiClient());
		
		
		//get sheet data (using email id and number of att.) -> error == 3 (Search POC for sheet)
		
        String clientRequest = httpRequest.getReader().lines().collect(Collectors.joining(System.lineSeparator()));
        JSONObject clientRequestJson = new JSONObject(clientRequest); 
        
        UUID uuid = UUID.randomUUID();
        //String callBackURL = "http://minakycservicedev-env.eba-zmicm36h.us-east-1.elasticbeanstalk.com/KYCService/verifyCallbackURL/";
        
        String callBackURL = "https://webhook.site/44b5717f-701b-40b4-809a-4e8c4b35275f/KYCService/verifyCallbackURL/";
        JSONObject jsonObj = new JSONObject();
        jsonObj.put("AcceptTruliooTermsAndConditions", "true");
        jsonObj.put("CleansedAddress", "false");
        jsonObj.put("ConfigurationName", "Identity Verification");
        jsonObj.put("CallBackUrl", callBackURL + uuid + cellId);
        jsonObj.put("CustomerReferenceID", uuid);
        
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
        try {
			verificationClient.verifyAsync(request, new ApiCallback<VerifyResult>() {
				@Override
			   public void onFailure(ApiException e, int statusCode, Map<String, List<String>> responseHeaders) {
					 logger.error("Error occured: {},{}, {}", e.getCode(), e.getResponseBody(),e);
			   }
			   @Override
			   public void onSuccess(VerifyResult response, int statusCode, Map<String, List<String>> responseHeaders) {
			        logger.info("\n--------------- onSuccess Verify KYC Service Response------------");
					logger.info("{}",response);
					
					try {
						IdentityVerificationResponse verificationResponse = IdentityVerificationTransformer.transformVerifyResponse(request, response);
						cellId = sheetsService.addRow(verificationResponse.validateValueRange());
						System.out.println("cellId"+cellId);
						
					} catch (IOException | GeneralSecurityException e) {
						e.printStackTrace();
					}
			   }
			   @Override
			   public void onUploadProgress(long bytesWritten, long contentLength, boolean done) {
				   
			   }
			   @Override
			   public void onDownloadProgress(long bytesRead, long contentLength, boolean done) {
				   
			   }
      });
		} catch (ApiException e) {
			handleTruliooApiException(e);
		}
	}

	@Override
	public JsonObject fetchCountryWiseRequiredField(String countryCode) {
		ConfigurationApi configurationClient = new ConfigurationApi(config.getApiClient());
        Object response = null;
        JsonObject result = new JsonObject();
        
        try {
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
	
	        result.add("properties", rp);
	
	        logger.info("\n---------------Recommended Fields Response------------");
	        logger.info("{}", result);
        
		} catch (ApiException e) {
			result = (JsonObject) handleTruliooApiException(e);
		}
        return result;
	}
	
	private Object handleTruliooApiException(ApiException e) {
		int errorCode = e.getCode();
		String errorFun = "";
		
		if (errorCode == 400) errorFun = "INVALID_REQUEST";
		else if (errorCode == 401) errorFun = "UNAUTHORIZED_EXCEPTION";
		else if (errorCode == 408) errorFun = "REQUEST_TOOK_LONGER_PROCESS";
		else if (errorCode == 415) errorFun = "CHECK_HEADER";
		else if (errorCode == 500) errorFun = "SERVER_ERROR";
		else errorFun = "DEFAULT";
		
		String errorDes = TruliooErrors.valueOf(errorFun).getErrorDescription();
		
		JsonObject errorObj = new JsonObject();
		errorObj.addProperty("httpStatus", e.getCode());
		errorObj.addProperty("truliooStatus", e.getResponseBody());
		errorObj.addProperty("response", errorDes);
		
		logger.error("Error occured: {}, {}, {}, {}", e.getCode(), e.getResponseBody(),errorDes, e);
		
		return errorObj;
	}

	@Override
	public Map<String, String> getCountryCodesService() {
		ConfigurationApi configurationClient = new ConfigurationApi(config.getApiClient());
		
		//getCountryCodes
		List<String> countryCodes;
		Map<String, String> country = new HashMap<>();
		try {
			countryCodes = configurationClient.getCountryCodes("Identity Verification");
			logger.info("\n---------------Country code Response------------");
			logger.info("{}",countryCodes);
			
			Map<String, String> countryList = new HashMap<>();
			countryCodes.forEach(countryCode -> {
			    countryList.put(CountryCodesEnum.valueOf(countryCode).getCountryCode(), CountryCodesEnum.valueOf(countryCode).getCountryName());
			});
			country = CollectionFormatter.sortByValue(countryList);
		} catch (ApiException e) {
			country.put("Error", handleTruliooApiException(e).toString());
		}
		return country;
	}

}
