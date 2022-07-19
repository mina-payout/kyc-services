package com.minanet.trulioo.kyc;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestTemplate;

@Configuration
public class KYCConfiguration {
	@Bean
    @Qualifier("restTemplate")
	RestTemplate restTemplate() {
        return new RestTemplate();
    }
}
