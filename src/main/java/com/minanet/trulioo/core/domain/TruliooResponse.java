package com.minanet.trulioo.core.domain;

public class TruliooResponse {
	private Integer httpStatus;
	private String truliooStatus;
	private Object truliooResponse;
	
	public Integer getHttpStatus() {
		return httpStatus;
	}
	public void setHttpStatus(Integer httpStatus) {
		this.httpStatus = httpStatus;
	}
	public String getTruliooStatus() {
		return truliooStatus;
	}
	public void setTruliooStatus(String truliooStatus) {
		this.truliooStatus = truliooStatus;
	}
	public Object getTruliooResponse() {
		return truliooResponse;
	}
	public void setTruliooResponse(Object truliooResponse) {
		this.truliooResponse = truliooResponse;
	}
}
