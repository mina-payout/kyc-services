package com.minanet.trulioo.kyc.service;


import com.google.api.services.sheets.v4.Sheets;
import com.google.api.services.sheets.v4.model.AppendValuesResponse;
import com.google.api.services.sheets.v4.model.BatchGetValuesResponse;
import com.google.api.services.sheets.v4.model.Sheet;
import com.google.api.services.sheets.v4.model.Spreadsheet;
import com.google.api.services.sheets.v4.model.UpdateValuesResponse;
import com.google.api.services.sheets.v4.model.ValueRange;
import com.google.common.collect.Table.Cell;
import com.minanet.trulioo.kyc.config.GoogleAuthorizationConfig;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.security.GeneralSecurityException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

@Service
public class GoogleSheetsServiceImpl implements GoogleSheetsService {

    private static final Logger LOGGER = LoggerFactory.getLogger(GoogleSheetsServiceImpl.class);

    @Value("${spreadsheet.id}")
    private String spreadsheetId;

    @Autowired
    private GoogleAuthorizationConfig googleAuthorizationConfig;

    @Override
    public void getSpreadsheetValues() throws IOException, GeneralSecurityException {
        Sheets sheetsService = googleAuthorizationConfig.getSheetsService();
        Sheets.Spreadsheets.Values.BatchGet request =
                sheetsService.spreadsheets().values().batchGet(spreadsheetId);
        request.setRanges(getSpreadSheetRange());
        request.setMajorDimension("ROWS");
        BatchGetValuesResponse response = request.execute();
        List<List<Object>> spreadSheetValues = response.getValueRanges().get(0).getValues();
        List<Object> headers = spreadSheetValues.remove(0);
        for ( List<Object> row : spreadSheetValues ) {
            LOGGER.info("{}: {}, {}: {}, {}: {}, {}: {}",
                    headers.get(0),row.get(0), headers.get(1),row.get(1),
                    headers.get(2),row.get(2), headers.get(3),row.get(3));
        }
    }

    private List<String> getSpreadSheetRange() throws IOException, GeneralSecurityException {
        Sheets sheetsService = googleAuthorizationConfig.getSheetsService();
        Sheets.Spreadsheets.Get request = sheetsService.spreadsheets().get(spreadsheetId);
        Spreadsheet spreadsheet = request.execute();
        Sheet sheet = spreadsheet.getSheets().get(0);
        int row = sheet.getProperties().getGridProperties().getRowCount();
        int col = sheet.getProperties().getGridProperties().getColumnCount();
        return Collections.singletonList("R1C1:R".concat(String.valueOf(row))
                .concat("C").concat(String.valueOf(col)));
    }
    
    @Override
    public void appendRow() throws IOException, GeneralSecurityException {
    	Sheets sheetsService = googleAuthorizationConfig.getSheetsService();
    	ValueRange body = new ValueRange()
    		      .setValues(Arrays.asList(
    		        Arrays.asList("Expenses January"), 
    		        Arrays.asList("books", "30"), 
    		        Arrays.asList("pens", "10"),
    		        Arrays.asList("Expenses February"), 
    		        Arrays.asList("clothes", "20"),
    		        Arrays.asList("shoes", "5")));
    	AppendValuesResponse  result = sheetsService.spreadsheets().values()
    		    		.append(spreadsheetId, "A1", body)
    		    		  .setValueInputOption("USER_ENTERED")
    		    		  .setInsertDataOption("INSERT_ROWS")
    		    		  .setIncludeValuesInResponse(true)
    		    		  .execute();
    	LOGGER.info("Appended: {}", result);
    }
    
    
    @Override
    public void addRow(ArrayList<String> list) throws IOException, GeneralSecurityException {
    	Sheets sheetsService = googleAuthorizationConfig.getSheetsService();
    	ValueRange body = new ValueRange()
    		      .setValues(Arrays.asList(
    		        Arrays.asList(list.toArray())
    		        ));
    	AppendValuesResponse  result = sheetsService.spreadsheets().values()
    		    		.append(spreadsheetId, "A1", body)
    		    		  .setValueInputOption("USER_ENTERED")
    		    		  .setInsertDataOption("INSERT_ROWS")
    		    		  .setIncludeValuesInResponse(true)
    		    		  .execute();
    	LOGGER.info("Appended: {}", result);
    }
    
   
    @SuppressWarnings("rawtypes")
	@Override
	public void getSpreadsheetValuesForSpecificRange() throws IOException, GeneralSecurityException {
    	 Sheets service = googleAuthorizationConfig.getSheetsService();
	     ValueRange response = service.spreadsheets().values()
	             .get(spreadsheetId, "A16:B16")
	             .execute();
	     List<List<Object>> values = response.getValues();
	     if (values == null || values.isEmpty()) {
	         System.out.println("No data found.");
	     } else {
	         System.out.println("Name, Major");
	         for (List row : values) {
	             // Print columns A and E, which correspond to indices 0 and 4.
	             System.out.printf("%s, %s\n", row.get(0), row.get(1));
	         }
	     }
    }

	@Override
	public void updateValue() throws IOException, GeneralSecurityException {
		// TODO Auto-generated method stub
		Sheets sheetsService = googleAuthorizationConfig.getSheetsService();
    	ValueRange body = new ValueRange()
    		      .setValues(Arrays.asList(
    		        Arrays.asList("updated value","dasdas")));
    	Sheets.Spreadsheets.Values.Update request =
    	        sheetsService.spreadsheets().values().update(spreadsheetId, "B18", body);
    	    request.setValueInputOption("USER_ENTERED");

    	    UpdateValuesResponse response = request.execute();
    	LOGGER.info("Updated: {}", response);
	}
}