package com.minanet.trulioo.kyc.config;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.security.GeneralSecurityException;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import com.google.api.client.auth.oauth2.Credential;
import com.google.api.client.extensions.java6.auth.oauth2.AuthorizationCodeInstalledApp;
import com.google.api.client.extensions.jetty.auth.oauth2.LocalServerReceiver;
import com.google.api.client.googleapis.auth.oauth2.GoogleAuthorizationCodeFlow;
import com.google.api.client.googleapis.auth.oauth2.GoogleClientSecrets;
import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.gson.GsonFactory;
import com.google.api.client.util.store.FileDataStoreFactory;
import com.google.api.services.sheets.v4.Sheets;
import com.google.api.services.sheets.v4.SheetsScopes;
import com.google.api.services.sheets.v4.model.ValueRange;

import ch.qos.logback.core.subst.Token;

public class GoogleSheetAuthorizationConfig {
	/*
	 * private static final String APPLICATION_NAME =
	 * "Google Sheets API Java Quickstart"; private static final JsonFactory
	 * JSON_FACTORY = GsonFactory.getDefaultInstance(); private static final String
	 * TOKENS_DIRECTORY_PATH = "tokens";
	 */

	    /**
	     * Global instance of the scopes required by this quickstart.
	     * If modifying these scopes, delete your previously saved tokens/ folder.
	     */
		/*
		 * private static final List<String> SCOPES =
		 * Collections.singletonList(SheetsScopes.SPREADSHEETS_READONLY); private static
		 * final String CREDENTIALS_FILE_PATH = "/googleSheetCredentials.json";
		 */
	    
	    /**
	     * Creates an authorized Credential object.
	     * @param HTTP_TRANSPORT The network HTTP Transport.
	     * @return An authorized Credential object.
	     * @throws IOException If the credentials.json file cannot be found.
	     */
		/*
		 * private static Credential getCredentials(final NetHttpTransport
		 * HTTP_TRANSPORT) throws IOException { // Load client secrets. InputStream in =
		 * GoogleSheetAuthorizationConfig.class.getResourceAsStream(
		 * CREDENTIALS_FILE_PATH); if (in == null) { throw new
		 * FileNotFoundException("Resource not found: " + CREDENTIALS_FILE_PATH); }
		 * GoogleClientSecrets clientSecrets = GoogleClientSecrets.load(JSON_FACTORY,
		 * new InputStreamReader(in));
		 * 
		 * // Build flow and trigger user authorization request.
		 * GoogleAuthorizationCodeFlow flow = new GoogleAuthorizationCodeFlow.Builder(
		 * HTTP_TRANSPORT, JSON_FACTORY, clientSecrets, SCOPES) .setDataStoreFactory(new
		 * FileDataStoreFactory(new java.io.File(TOKENS_DIRECTORY_PATH)))
		 * .setAccessType("offline") .build(); LocalServerReceiver receiver = new
		 * LocalServerReceiver.Builder().setPort(5000).build(); return new
		 * AuthorizationCodeInstalledApp(flow, receiver).authorize("user"); }
		 */
	    
	    /**
	     * Prints the names and majors of students in a sample spreadsheet:
	     * https://docs.google.com/spreadsheets/d/1BxiMVs0XRA5nFMdKvBdBZjgmUUqptlbs74OgvE2upms/edit
	     */
	  /*  public static void main(String... args) throws IOException, GeneralSecurityException {
	    	 // Build a new authorized API client service.
	        final NetHttpTransport HTTP_TRANSPORT = GoogleNetHttpTransport.newTrustedTransport();
	        final String spreadsheetId = "";
	        final String range = "Class Data!A2:E";
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
	    	
	    	
	    	
	    }
	    
	    */
	    
/*	    public static Credential authorize() throws IOException, GeneralSecurityException {
	    	InputStream in = GoogleSheetAuthorizationConfig.class.getResourceAsStream(CREDENTIALS_FILE_PATH);
	    	 if (in == null) {
		            throw new FileNotFoundException("Resource not found: " + CREDENTIALS_FILE_PATH);
		        }
	    	GoogleClientSecrets clientSecrets = GoogleClientSecrets.load(JSON_FACTORY, new InputStreamReader(in));
	        List<String> scopes = Arrays.asList(SheetsScopes.SPREADSHEETS);
	        GoogleAuthorizationCodeFlow flow = new GoogleAuthorizationCodeFlow.Builder(
	        		GoogleNetHttpTransport.newTrustedTransport(),JSON_FACTORY, clientSecrets, scopes)
	        		.setDataStoreFactory(new FileDataStoreFactory(new java.io.File(TOKENS_DIRECTORY_PATH)))
	                .setAccessType("offline")
	                .build();

	       Credential credential = new AuthorizationCodeInstalledApp(flow, new LocalServerReceiver()).authorize("user");

	        return credential;
	    }
	    
	    public static Sheets getSheetService() throws IOException, GeneralSecurityException {
	    	Credential credential = authorize();
	    	return new Sheets.Builder(GoogleNetHttpTransport.newTrustedTransport(), JSON_FACTORY, credential)
	    			.setApplicationName(APPLICATION_NAME)
	    			.build();
	    }
	    
	    public static void main(String... args) throws IOException, GeneralSecurityException {
	    	final String spreadsheetId = "";
	    	final String range = "TruliooKYCSheet!A2:E10";
	    	Sheets service = getSheetService();
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
	    }*/
}
