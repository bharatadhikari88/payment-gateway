package com.eagle;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.braintreegateway.BraintreeGateway;

@Configuration
@SpringBootApplication
public class PaymentGateway {

    @Value("${gateway.env}")
    private String env;

    @Value("${gateway.merchantId}")
    private String merchantId;

    @Value("${gateway.publicKey}")
    private String publicKey;

    @Value("${gateway.privateKey}")
    private String privateKey;

    @Bean
    public BraintreeGateway gateway() {
	return new BraintreeGateway(env, merchantId, publicKey, privateKey);
    }

    public static void main(String[] args) {
	SpringApplication.run(PaymentGateway.class, args);
    }

}
