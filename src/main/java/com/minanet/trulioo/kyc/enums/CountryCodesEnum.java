package com.minanet.trulioo.kyc.enums;

public enum CountryCodesEnum {
	 
	AU("AU","Australia"),
	LU("LU","Luxembourg"),
	CN("CN","China"),
	JP("JP","Japan"),
	FR("FR","France"),
	IT("IT","Italy"),
	PT("PT","Portugal"),
	CH("CH","Switzerland"),
	DK("DK","Denmark"),
	NO("NO","Norway"),
	SE("SE","Sweden"),
	NL("NL","Netherlands"),
	HK("HK","Hong Kong"),
	SG("SG","Singapore"),
	MX("MX","Mexico"),
	US("US","United States of America"),
	ES("ES","Spain"),
	EG("EG","Egypt"),
	JO("JO","Jordan"),
	KW("KW","Kuwait"),
	AE("AE","United Arab Emirates"),
	DE("DE","Germany"),
	NG("NG","Nigeria"),
	RO("RO","Romania"),
	CR("CR","Costa Rica"),
	IE("IE","Ireland"),
	AT("AT","Austria"),
	BE("BE","Belgium"),
	CA("CA","Canada"),
	NZ("NZ","New Zealand"),
	GB("GB","United Kingdom of Great Britain and Northern Ireland"),
	TR("TR","Turkey"),
	ZA("ZA","South Africa"),
	OM("OM","Oman"),
	RU("RU","Russian Federation"),
	SA("SA","Saudi Arabia"),
	LB("LB","Lebanon"),
	FI("FI","Finland"),
	IN("IN","India"),
	BR("BR","Brazil"),
	MY("MY","Malaysia"),
	AR("AR","Argentina"),
	KR("KR","Korea (the Republic of)"),
	PE("PE","Peru"),
	TH("TH","Thailand"),
	SV("SV","El Salvador"),
	CL("CL","Chile"),
	CO("CO","Colombia"),
	EC("EC","Ecuador"),
	VE("VE","Venezuela (Bolivarian Republic of)"),
	UA("UA","Ukraine"),
	PL("PL","Poland"),
	PH("PH","Philippines"),
	KE("KE","Kenya"),
	SK("SK","Slovakia"),
	CZ("CZ","Czechia"),
	MT("MT","Malta"),
	IS("IS","Iceland"),
	BH("BH","Bahrain"),
	QA("QA","Qatar"),
	BD("BD","Bangladesh"),
	GE("GE","Georgia"),
	TW("TW","Taiwan"),
	ID("ID","Indonesia"),
	GH("GH","Ghana"),
	VN("VN","Viet Nam"),
	PK("PK","Pakistan"),
	GR("GR","Greece"),
	IL("IL","Israel");
	
	private String countryCode;
	private String countryName;
	
	CountryCodesEnum(String countryCode,String countryName){
		this.countryCode = countryCode;
		this.countryName = countryName;
	}

	public String getCountryCode() {
		return countryCode;
	}

	public void setCountryCode(String countryCode) {
		this.countryCode = countryCode;
	}

	public String getCountryName() {
		return countryName;
	}

	public void setCountryName(String countryName) {
		this.countryName = countryName;
	}
}
