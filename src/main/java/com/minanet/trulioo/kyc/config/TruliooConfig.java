
package com.minanet.trulioo.kyc.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.trulioo.normalizedapi.ApiClient;

@Service
public class TruliooConfig {

	@Value("${trulioo.apiclient.username}")
	public String truliooUsername;
	
	@Value("${trulioo.apiclient.password}")
	public String truliooPassword;
	
	public ApiClient getApiClient(){
		ApiClient apiClient = new ApiClient();
		apiClient.setUsername(truliooUsername);
		apiClient.setPassword(truliooPassword);
		return apiClient;
	}
}
