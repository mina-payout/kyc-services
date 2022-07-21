package com.minanet.trulioo.kyc.controller;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.security.GeneralSecurityException;
import java.util.ArrayList;
import java.util.Arrays;
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
import javax.websocket.server.PathParam;

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
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

import com.google.api.client.auth.oauth2.Credential;
import com.google.api.client.extensions.java6.auth.oauth2.AuthorizationCodeInstalledApp;
import com.google.api.client.extensions.jetty.auth.oauth2.LocalServerReceiver;
import com.google.api.client.googleapis.auth.oauth2.GoogleAuthorizationCodeFlow;
import com.google.api.client.googleapis.auth.oauth2.GoogleClientSecrets;
import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport;
import com.google.api.client.googleapis.json.GoogleJsonError;
import com.google.api.client.googleapis.json.GoogleJsonResponseException;
import com.google.api.client.http.HttpRequestInitializer;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.gson.GsonFactory;
import com.google.api.client.util.store.FileDataStoreFactory;
import com.google.api.services.sheets.v4.Sheets;
import com.google.api.services.sheets.v4.Sheets.Spreadsheets.Values;
import com.google.api.services.sheets.v4.SheetsScopes;
import com.google.api.services.sheets.v4.model.AppendValuesResponse;
import com.google.api.services.sheets.v4.model.BatchUpdateValuesRequest;
import com.google.api.services.sheets.v4.model.BatchUpdateValuesResponse;
import com.google.api.services.sheets.v4.model.UpdateValuesResponse;
import com.google.api.services.sheets.v4.model.ValueRange;
import com.google.auth.http.HttpCredentialsAdapter;
import com.google.auth.oauth2.GoogleCredentials;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.minanet.trulioo.core.domain.TruliooResponse;
import com.minanet.trulioo.kyc.enums.CountryCodesEnum;
import com.minanet.trulioo.tools.GoogleSheetAuthorizationConfig;
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

	private static final String APPLICATION_NAME = "Google Sheets API Java Quickstart";
    private static final JsonFactory JSON_FACTORY = GsonFactory.getDefaultInstance();
    private static final String TOKENS_DIRECTORY_PATH = "tokens";

    private static final List<String> SCOPES = Collections.singletonList(SheetsScopes.DRIVE);
    private static final String CREDENTIALS_FILE_PATH = "/googleSheetCredentials.json";
    
	
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
	
	//google sheet POC common method
	private static Credential getCredentials(final NetHttpTransport HTTP_TRANSPORT) throws IOException {
        // Load client secrets.
        InputStream in = GoogleSheetAuthorizationConfig.class.getResourceAsStream(CREDENTIALS_FILE_PATH);
        if (in == null) {
            throw new FileNotFoundException("Resource not found: " + CREDENTIALS_FILE_PATH);
        }
        GoogleClientSecrets clientSecrets = GoogleClientSecrets.load(JSON_FACTORY, new InputStreamReader(in));

        // Build flow and trigger user authorization request.
        GoogleAuthorizationCodeFlow flow = new GoogleAuthorizationCodeFlow.Builder(
                HTTP_TRANSPORT, JSON_FACTORY, clientSecrets, SCOPES)
                .setDataStoreFactory(new FileDataStoreFactory(new java.io.File(TOKENS_DIRECTORY_PATH)))
                .setAccessType("offline")
                .build();
        LocalServerReceiver receiver = new LocalServerReceiver.Builder().setPort(8888).build();
        return new AuthorizationCodeInstalledApp(flow, receiver).authorize("user");
    }
    
	
	@CrossOrigin
	@GetMapping("/googleSheetPOC")
	public String googleSheetPOC() throws GeneralSecurityException, IOException {
		 final NetHttpTransport HTTP_TRANSPORT = GoogleNetHttpTransport.newTrustedTransport();
	     final String spreadsheetId = "";
	     final String range = "A2:E10";
	     Sheets service = new Sheets.Builder(HTTP_TRANSPORT, JSON_FACTORY, getCredentials(HTTP_TRANSPORT))
	             .setApplicationName(APPLICATION_NAME)
	             .build();
	     ValueRange response = service.spreadsheets().values()
	             .get(spreadsheetId, range)
	             .execute();
	     List<List<Object>> values = response.getValues();
	     if (values == null || values.isEmpty()) {
	         System.out.println("No data found.");
	     } else {
	         System.out.println("Name, Major");
	         for (List row : values) {
	             // Print columns A and E, which correspond to indices 0 and 4.
	             System.out.printf("%s, %s\n", row.get(0), row.get(4));
	         }
	     }
	     
	/*     final String range2 = "G2";
	   
	     ValueRange requestBody = new ValueRange();
	    		*/
	     
	   /*  final List<String> SCOPES =
	             Arrays.asList(SheetsScopes.SPREADSHEETS,SheetsScopes.DRIVE);
	     
	     final java.io.File DATA_STORE_DIR = new java.io.File(
	    	        System.getProperty("user.home"), ".googleSheetCredentials/2/sheets.googleapis.com-java-quickstart.json");
	   */
	     
	    	/*	 ValueRange body = new ValueRange()
	    	      .setValues(Arrays.asList(
	    	        Arrays.asList("Expenses January"), 
	    	        Arrays.asList("books", "30"), 
	    	        Arrays.asList("pens", "10"),
	    	        Arrays.asList("Expenses February"), 
	    	        Arrays.asList("clothes", "20"),
	    	        Arrays.asList("shoes", "5")));
	    	    UpdateValuesResponse result = service.spreadsheets().values()
	    	      .update(spreadsheetId, "A1", body)
	    	      .setValueInputOption("RAW")
	    	      .execute();		 
	    	*/	 
	/*     Sheets.Spreadsheets.Values.Update request =
	    		 service.spreadsheets().values().update(spreadsheetId, range2, requestBody);
	    	    request.setValueInputOption("truliooData");

	    	    UpdateValuesResponse response1 = request.execute();

	    	    System.out.println(response1);*/
	     
	     // New code for add data
	     GoogleCredentials credentials = GoogleCredentials.getApplicationDefault()
	                .createScoped(Collections.singleton(SheetsScopes.SPREADSHEETS));
	        HttpRequestInitializer requestInitializer = new HttpCredentialsAdapter(
	                credentials);

	        // Create the sheets API client
	        Sheets service1 = new Sheets.Builder(new NetHttpTransport(),
	                GsonFactory.getDefaultInstance(),
	                requestInitializer)
	                .setApplicationName("Sheets samples")
	                .build();

	        List<ValueRange> data = new ArrayList<>();
	        data.add(new ValueRange()
	                .setRange(range)
	                .setValues(values));

	        BatchUpdateValuesResponse result = null;
	        try {
	            // Updates the values in the specified range.
	            BatchUpdateValuesRequest body = new BatchUpdateValuesRequest()
	                    .setValueInputOption("Trulioo Updated data")
	                    .setData(data);
	            result = service1.spreadsheets().values().batchUpdate(spreadsheetId, body).execute();
	            System.out.printf("%d cells updated.", result.getTotalUpdatedCells());
	        } catch (GoogleJsonResponseException e) {
	            // TODO(developer) - handle error appropriately
	            GoogleJsonError error = e.getDetails();
	            if (error.getCode() == 404) {
	                System.out.printf("Spreadsheet not found with id '%s'.\n",spreadsheetId);
	            } else {
	                throw e;
	            }
	        }
	     
		return "Done"; 
	}
	
	
}
