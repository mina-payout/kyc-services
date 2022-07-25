package com.minanet.trulioo.kyc.service;

import java.io.IOException;
import java.security.GeneralSecurityException;
import java.util.ArrayList;

public interface GoogleSheetsService {
    void getSpreadsheetValues() throws IOException, GeneralSecurityException;
    void appendRow() throws IOException, GeneralSecurityException;
    void getSpreadsheetValuesForSpecificRange() throws IOException, GeneralSecurityException;
    void addRow(ArrayList<String> list) throws IOException, GeneralSecurityException;
    void updateValue() throws IOException, GeneralSecurityException;
}
