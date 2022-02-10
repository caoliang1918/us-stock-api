package com.ibkr;

import com.tigerbrokers.stock.openapi.client.https.client.TigerHttpClient;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.http.converter.StringHttpMessageConverter;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.web.client.RestTemplate;

import java.nio.charset.StandardCharsets;

@EnableScheduling
@SpringBootApplication
public class IbkrApiApplication {

    @Value("${tigerId}")
    private String tigerId;
    @Value("${tiger.public.key}")
    private String publicKey;
    @Value("${private.key}")
    private String privateKey;
    @Value("${tiger.host}")
    private String tigerHost;

    @Bean
    public TigerHttpClient tigerHttpClient() {
        return new TigerHttpClient(tigerHost, tigerId, privateKey);
    }

    @Bean
    public RestTemplate restTemplate() {
        SimpleClientHttpRequestFactory simpleClientHttpRequestFactory = new SimpleClientHttpRequestFactory();
        simpleClientHttpRequestFactory.setConnectTimeout(5000);
        simpleClientHttpRequestFactory.setReadTimeout(10000);
        RestTemplate restTemplate = new RestTemplate(simpleClientHttpRequestFactory);
        restTemplate.getMessageConverters().set(1, new StringHttpMessageConverter(StandardCharsets.UTF_8));
        return restTemplate;
    }

    public static void main(String[] args) {
        SpringApplication.run(IbkrApiApplication.class, args);
    }
}
