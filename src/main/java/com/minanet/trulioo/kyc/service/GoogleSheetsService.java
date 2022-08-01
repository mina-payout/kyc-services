package com.minanet.trulioo.kyc.service;

import java.io.IOException;
import java.security.GeneralSecurityException;
import com.google.api.services.sheets.v4.model.ValueRange;

public interface GoogleSheetsService {
    void getSpreadsheetValues() throws IOException, GeneralSecurityException;
    void appendRow() throws IOException, GeneralSecurityException;
    void getSpreadsheetValuesForSpecificRange() throws IOException, GeneralSecurityException;
    String addRow(ValueRange body) throws IOException, GeneralSecurityException;
    void updateValue() throws IOException, GeneralSecurityException;
}
