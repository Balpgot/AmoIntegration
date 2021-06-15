package com.sender.service;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.sender.PropertiesStorage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.ExchangeStrategies;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.StandardOpenOption;


@Service
public class AmoRequestService {

    private Logger log;
    private String refreshToken = PropertiesStorage.AMO_REFRESH_TOKEN;
    private String accessToken = PropertiesStorage.AMO_ACCESS_TOKEN;
    private String baseURL = PropertiesStorage.AMO_BASE_URL;
    private String clientId = PropertiesStorage.AMO_CLIENT_ID;
    private String clientSecret = PropertiesStorage.AMO_CLIENT_SECRET;
    private String redirectURL = PropertiesStorage.AMO_REDIRECT_URL;
    private File configurationFile = new File("./config/api.config");
    private JSONObject configuration;
    private WebClient client;

    public AmoRequestService() {
        try {
            this.configuration = JSON.parseObject(Files.readString(this.configurationFile.toPath()));
            this.client = WebClient
                    .builder()
                    .exchangeStrategies(ExchangeStrategies.builder()
                            .codecs(configurer -> configurer
                                    .defaultCodecs()
                                    .maxInMemorySize(16 * 1024 * 1024))
                            .build())
                    .baseUrl(baseURL)
                    .defaultHeader("Authorization", "Bearer " + accessToken)
                    .build();
            this.log = LoggerFactory.getLogger(AmoRequestService.class);
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }


    public JSONObject getContactInfo(String contact_id) {
        String response = client
                .get()
                .uri("/api/v4/contacts/" + contact_id)
                .retrieve()
                .bodyToMono(String.class)
                .block();
        return JSON.parseObject(response);
    }

    public JSONObject getNotesInfo(String lead_id) {
        String response;
        response = client
                .get()
                .uri(UriBuilder -> UriBuilder
                        .path("/api/v4/leads/" + lead_id + "/notes")
                        .build())
                .retrieve()
                .bodyToMono(String.class)
                .block();
        return JSON.parseObject(response);
    }

    public JSONObject getCompanyInfo(String lead_id) {
        String response = "";
        try {
            response = client
                    .get()
                    .uri(UriBuilder -> UriBuilder
                            .path("/api/v4/leads/" + lead_id)
                            .queryParam("with", "contacts")
                            .build())
                    .retrieve()
                    .bodyToMono(String.class)
                    .block();
        } catch (Exception exception) {
            WebClientResponseException ex = (WebClientResponseException) exception;
            HttpStatus status = ex.getStatusCode();
            if (status.value() == 401) {
                refreshTokens();
                response = client
                        .get()
                        .uri(UriBuilder -> UriBuilder
                                .path("/api/v4/leads/" + lead_id)
                                .queryParam("with", "contacts")
                                .build())
                        .retrieve()
                        .bodyToMono(String.class)
                        .block();
            } else {
                exception.printStackTrace();
            }
            log.error(((WebClientResponseException) exception).getResponseBodyAsString());
        }
        return JSON.parseObject(response);
    }

    public void refreshTokens() {
        File accessTokenFile = new File("./tokens/access.txt");
        File refreshTokenFile = new File("./tokens/refresh.txt");
        try {
            if (!accessTokenFile.exists()) {
                if (accessTokenFile.createNewFile()) {
                    System.out.println("AFile created");
                }
            }
            if (!refreshTokenFile.exists()) {
                if (refreshTokenFile.createNewFile()) {
                    System.out.println("RFile created");
                }
            }
            if (accessTokenFile.canWrite() && refreshTokenFile.canWrite()) {
                WebClient client = WebClient
                        .builder()
                        .baseUrl(baseURL)
                        .build();
                JSONObject requestBody = new JSONObject();
                requestBody.put("client_id", clientId);
                requestBody.put("client_secret", clientSecret);
                requestBody.put("grant_type", "refresh_token");
                requestBody.put("refresh_token", refreshToken);
                requestBody.put("redirect_uri", redirectURL);
                String responseJson = client.post()
                        .uri("/oauth2/access_token")
                        .contentType(MediaType.APPLICATION_JSON)
                        .body(BodyInserters.fromValue(requestBody))
                        .retrieve()
                        .bodyToMono(String.class)
                        .block();
                JSONObject tokensInfo = JSON.parseObject(responseJson);
                this.accessToken = tokensInfo.getString("access_token").trim();
                this.refreshToken = tokensInfo.getString("refresh_token").trim();
                this.client = WebClient
                        .builder()
                        .baseUrl(baseURL)
                        .defaultHeader("Authorization", "Bearer " + accessToken)
                        .build();
                System.out.println("RT");
                System.out.println(refreshToken);
                System.out.println("AT");
                System.out.println(accessToken);
                this.configuration.put("accessToken", accessToken);
                this.configuration.put("refreshToken", refreshToken);
                Files.writeString(configurationFile.toPath(), configuration.toJSONString(), StandardOpenOption.WRITE);
                Files.writeString(accessTokenFile.toPath(), accessToken, StandardOpenOption.WRITE);
                Files.writeString(refreshTokenFile.toPath(), refreshToken, StandardOpenOption.WRITE);
                PropertiesStorage.writeProperties(accessToken, refreshToken);
                log.info("TOKENS REFRESH");
            } else {
                System.out.println("Failed to access files");
            }
        } catch (IOException ex) {
            ex.printStackTrace();
            log.error(ex.getMessage());
        }
    }

    public void setRefreshToken(String refreshToken) {
        this.refreshToken = refreshToken;
    }
}
