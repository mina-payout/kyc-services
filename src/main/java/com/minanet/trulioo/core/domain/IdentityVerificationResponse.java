package com.minanet.trulioo.core.domain;

import java.util.ArrayList;
import java.util.Arrays;

import com.google.api.services.sheets.v4.model.ValueRange;

public class IdentityVerificationResponse {
	private String submissionDate;
	private String email;
	private String delegationAddress;
	private String acknowledged;
	private String numberOfAttempts;
	private String firstName;
	private String middleName;
	private String lastName;
	private String country;
	private String pbKeyEntered;
	private String internalTxnID;
	private String truliooTxnIDV;
	private String statusOfIDSubmission;
	private String timestampVerification;
	private String statusOfIDV;
	private String reasonsForNoMatchIDV;
	private String timestampDocVSubmission;
	private String truliooTxnIDDocV;
	private String statusOfDocSubmission;
	private String timestampDocV;
	private String statusOfDocV;
	private String reasonsForNoMatchDocV;
	      
	/**
	 * @return the submissionDate
	 */
	public String getSubmissionDate() {
		return submissionDate;
	}

	/**
	 * @param submissionDate the submissionDate to set
	 */
	public void setSubmissionDate(String submissionDate) {
		this.submissionDate = submissionDate;
	}

	/**
	 * @return the email
	 */
	public String getEmail() {
		return email;
	}

	/**
	 * @param email the email to set
	 */
	public void setEmail(String email) {
		this.email = email;
	}

	/**
	 * @return the delegationAddress
	 */
	public String getDelegationAddress() {
		return delegationAddress;
	}

	/**
	 * @param delegationAddress the delegationAddress to set
	 */
	public void setDelegationAddress(String delegationAddress) {
		this.delegationAddress = delegationAddress;
	}

	/**
	 * @return the acknowledged
	 */
	public String getAcknowledged() {
		return acknowledged;
	}

	/**
	 * @param acknowledged the acknowledged to set
	 */
	public void setAcknowledged(String acknowledged) {
		this.acknowledged = acknowledged;
	}

	/**
	 * @return the numberOfAttempts
	 */
	public String getNumberOfAttempts() {
		return numberOfAttempts;
	}

	/**
	 * @param numberOfAttempts the numberOfAttempts to set
	 */
	public void setNumberOfAttempts(String numberOfAttempts) {
		this.numberOfAttempts = numberOfAttempts;
	}

	/**
	 * @return the firstName
	 */
	public String getFirstName() {
		return firstName;
	}

	/**
	 * @param firstName the firstName to set
	 */
	public void setFirstName(String firstName) {
		this.firstName = firstName;
	}

	/**
	 * @return the middleName
	 */
	public String getMiddleName() {
		return middleName;
	}

	/**
	 * @param middleName the middleName to set
	 */
	public void setMiddleName(String middleName) {
		this.middleName = middleName;
	}

	/**
	 * @return the lastName
	 */
	public String getLastName() {
		return lastName;
	}

	/**
	 * @param lastName the lastName to set
	 */
	public void setLastName(String lastName) {
		this.lastName = lastName;
	}

	/**
	 * @return the country
	 */
	public String getCountry() {
		return country;
	}

	/**
	 * @param country the country to set
	 */
	public void setCountry(String country) {
		this.country = country;
	}

	/**
	 * @return the pbKeyEntered
	 */
	public String getPbKeyEntered() {
		return pbKeyEntered;
	}

	/**
	 * @param pbKeyEntered the pbKeyEntered to set
	 */
	public void setPbKeyEntered(String pbKeyEntered) {
		this.pbKeyEntered = pbKeyEntered;
	}

	/**
	 * @return the internalTxnID
	 */
	public String getInternalTxnID() {
		return internalTxnID;
	}

	/**
	 * @param internalTxnID the internalTxnID to set
	 */
	public void setInternalTxnID(String internalTxnID) {
		this.internalTxnID = internalTxnID;
	}

	/**
	 * @return the truliooTxnIDV
	 */
	public String getTruliooTxnIDV() {
		return truliooTxnIDV;
	}

	/**
	 * @param truliooTxnIDV the truliooTxnIDV to set
	 */
	public void setTruliooTxnIDV(String truliooTxnIDV) {
		this.truliooTxnIDV = truliooTxnIDV;
	}

	/**
	 * @return the statusOfIDSubmission
	 */
	public String getStatusOfIDSubmission() {
		return statusOfIDSubmission;
	}

	/**
	 * @param statusOfIDSubmission the statusOfIDSubmission to set
	 */
	public void setStatusOfIDSubmission(String statusOfIDSubmission) {
		this.statusOfIDSubmission = statusOfIDSubmission;
	}

	/**
	 * @return the timestampVerification
	 */
	public String getTimestampVerification() {
		return timestampVerification;
	}

	/**
	 * @param timestampVerification the timestampVerification to set
	 */
	public void setTimestampVerification(String timestampVerification) {
		this.timestampVerification = timestampVerification;
	}

	/**
	 * @return the statusOfIDV
	 */
	public String getStatusOfIDV() {
		return statusOfIDV;
	}

	/**
	 * @param statusOfIDV the statusOfIDV to set
	 */
	public void setStatusOfIDV(String statusOfIDV) {
		this.statusOfIDV = statusOfIDV;
	}

	/**
	 * @return the reasonsForNoMatchIDV
	 */
	public String getReasonsForNoMatchIDV() {
		return reasonsForNoMatchIDV;
	}

	/**
	 * @param reasonsForNoMatchIDV the reasonsForNoMatchIDV to set
	 */
	public void setReasonsForNoMatchIDV(String reasonsForNoMatchIDV) {
		this.reasonsForNoMatchIDV = reasonsForNoMatchIDV;
	}

	/**
	 * @return the timestampDocVSubmission
	 */
	public String getTimestampDocVSubmission() {
		return timestampDocVSubmission;
	}

	/**
	 * @param timestampDocVSubmission the timestampDocVSubmission to set
	 */
	public void setTimestampDocVSubmission(String timestampDocVSubmission) {
		this.timestampDocVSubmission = timestampDocVSubmission;
	}

	/**
	 * @return the truliooTxnIDDocV
	 */
	public String getTruliooTxnIDDocV() {
		return truliooTxnIDDocV;
	}

	/**
	 * @param truliooTxnIDDocV the truliooTxnIDDocV to set
	 */
	public void setTruliooTxnIDDocV(String truliooTxnIDDocV) {
		this.truliooTxnIDDocV = truliooTxnIDDocV;
	}

	/**
	 * @return the statusOfDocSubmission
	 */
	public String getStatusOfDocSubmission() {
		return statusOfDocSubmission;
	}

	/**
	 * @param statusOfDocSubmission the statusOfDocSubmission to set
	 */
	public void setStatusOfDocSubmission(String statusOfDocSubmission) {
		this.statusOfDocSubmission = statusOfDocSubmission;
	}

	/**
	 * @return the timestampDocV
	 */
	public String getTimestampDocV() {
		return timestampDocV;
	}

	/**
	 * @param timestampDocV the timestampDocV to set
	 */
	public void setTimestampDocV(String timestampDocV) {
		this.timestampDocV = timestampDocV;
	}

	/**
	 * @return the statusOfDocV
	 */
	public String getStatusOfDocV() {
		return statusOfDocV;
	}

	/**
	 * @param statusOfDocV the statusOfDocV to set
	 */
	public void setStatusOfDocV(String statusOfDocV) {
		this.statusOfDocV = statusOfDocV;
	}

	/**
	 * @return the reasonsForNoMatchDocV
	 */
	public String getReasonsForNoMatchDocV() {
		return reasonsForNoMatchDocV;
	}

	/**
	 * @param reasonsForNoMatchDocV the reasonsForNoMatchDocV to set
	 */
	public void setReasonsForNoMatchDocV(String reasonsForNoMatchDocV) {
		this.reasonsForNoMatchDocV = reasonsForNoMatchDocV;
	}

	private ArrayList<String> toList() {
		ArrayList<String> list = new ArrayList<>();
		list.add(submissionDate == null ? "" : submissionDate);
		list.add(email == null ? "" : email);
		list.add(delegationAddress == null ? "" : delegationAddress);
		list.add(acknowledged == null ? "" : acknowledged); 
		list.add(numberOfAttempts == null ? "" : numberOfAttempts);
		list.add(firstName == null ? "" : firstName);
		list.add(middleName == null ? "" : middleName);
		list.add(lastName == null ? "" : lastName);
		list.add(country == null ? "" : country);
		list.add(pbKeyEntered == null ? "" : pbKeyEntered);
		list.add(internalTxnID == null ? "" : internalTxnID);
		list.add(truliooTxnIDV == null ? "" : truliooTxnIDV);
		list.add(statusOfIDSubmission == null ? "" : statusOfIDSubmission);
		list.add(timestampVerification == null ? "" : timestampVerification);
		list.add(statusOfIDV == null ? "" : statusOfIDV);
		list.add(reasonsForNoMatchIDV == null ? "" : reasonsForNoMatchIDV);
		list.add(timestampDocVSubmission == null ? "" : timestampDocVSubmission);
		list.add(truliooTxnIDDocV == null ? "" : truliooTxnIDDocV);
		list.add(statusOfDocSubmission == null ? "" : statusOfDocSubmission);
		list.add(timestampDocV == null ? "" : timestampDocV);
		list.add(statusOfDocV == null ? "" : statusOfDocV);
		list.add(reasonsForNoMatchDocV == null ? "" : reasonsForNoMatchDocV);
		
		return list;
	}
	
	public ValueRange validateValueRange() {
		return new ValueRange()
  		      .setValues(Arrays.asList(
  		        Arrays.asList(this.toList().toArray())
  		        ));
	}
}
