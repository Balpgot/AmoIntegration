package com.sender.service;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
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

    private String refreshToken = "";
    private String accessToken = "";
    private String baseURL = "";
    private String clientId = "";
    private String clientSecret = "";
    private String redirectURL = "";
    private File configurationFile = new File("C:\\Users\\cinil\\Documents\\GitHub\\AmoIntegration\\api.config");
    private JSONObject configuration;
    private WebClient client;

    public AmoRequestService() {
        try {
            this.configuration = JSON.parseObject(Files.readString(this.configurationFile.toPath()));
            this.accessToken = configuration.getString("accessToken").trim();
            this.refreshToken = configuration.getString("refreshToken").trim();
            this.baseURL = configuration.getString("baseURL").trim();
            this.clientId = configuration.getString("clientId").trim();
            this.clientSecret = configuration.getString("clientSecret").trim();
            this.redirectURL = configuration.getString("redirectURL").trim();
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
        }
        catch (IOException ex){
            ex.printStackTrace();
        }
    }

    public JSONObject getContactInfo(String contact_id){
        String response = client
                .get()
                .uri("/api/v4/contacts/" + contact_id)
                .retrieve()
                .bodyToMono(String.class)
                .block();
        System.out.println("CONTACT");
        System.out.println(response);

        return JSON.parseObject(response);
    }

    public JSONObject getCompanyInfo(String lead_id){
        String response;
        try{
            response = client
                    .get()
                    .uri(UriBuilder -> UriBuilder
                            .path("/api/v4/leads/"+lead_id)
                            .queryParam("with","contacts")
                            .build())
                    .retrieve()
                    .bodyToMono(String.class)
                    .block();
        }
        catch (Exception exception){
            exception.printStackTrace();
            WebClientResponseException ex = (WebClientResponseException) exception;
            HttpStatus status = ex.getStatusCode();
            if(status.value()==401){
                refreshTokens();
            }
            response = client
                    .get()
                    .uri(UriBuilder -> UriBuilder
                            .path("/api/v4/leads/"+lead_id)
                            .queryParam("with","contacts")
                            .build())
                    .retrieve()
                    .bodyToMono(String.class)
                    .block();

        }
        System.out.println("LEAD");
        System.out.println(response);
        return JSON.parseObject(response);
    }

    public void refreshTokens(){
        File accessTokenFile = new File("./access.txt");
        File refreshTokenFile = new File("./refresh.txt");
        try{
            if(!accessTokenFile.exists()){
                if(accessTokenFile.createNewFile()){
                    System.out.println("AFile created");
                }
            }
            if(!refreshTokenFile.exists()){
                if(refreshTokenFile.createNewFile()){
                    System.out.println("RFile created");
                }
            }
            if(accessTokenFile.canWrite() && refreshTokenFile.canWrite()) {
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
                this.configuration.put("accessToken",accessToken);
                this.configuration.put("refreshToken",refreshToken);
                Files.writeString(configurationFile.toPath(),configuration.toJSONString(),StandardOpenOption.WRITE);
                Files.writeString(accessTokenFile.toPath(),accessToken, StandardOpenOption.WRITE);
                Files.writeString(refreshTokenFile.toPath(),refreshToken, StandardOpenOption.WRITE);
            }
            else{
                System.out.println("Failed to access files");
            }
        }
        catch (IOException ex){
            ex.printStackTrace();
        }
    }

    public String getRefreshToken() {
        return refreshToken;
    }

    public void setRefreshToken(String refreshToken) {
        this.refreshToken = refreshToken;
    }

    public String getAccessToken() {
        return accessToken;
    }

    public void setAccessToken(String accessToken) {
        this.accessToken = accessToken;
    }
}
