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

    private String refreshToken = "***";
    private String accessToken = "***";
    private final String baseURL = "https://***.amocrm.ru";
    private final String clientId = "***";
    private final String clientSecret = "***";
    private final String redirectURL = "***";
    private WebClient client = WebClient
            .builder()
            .exchangeStrategies(ExchangeStrategies.builder()
                    .codecs(configurer -> configurer
                            .defaultCodecs()
                            .maxInMemorySize(16 * 1024 * 1024))
                    .build())
            .baseUrl(baseURL)
            .defaultHeader("Authorization", "Bearer " + accessToken)
            .build();

    public AmoRequestService() {
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
        catch (WebClientResponseException exception){
            HttpStatus status = exception.getStatusCode();
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
                accessToken = tokensInfo.getString("access_token").trim();
                refreshToken = tokensInfo.getString("refresh_token").trim();
                this.client = WebClient
                            .builder()
                            .baseUrl(baseURL)
                            .defaultHeader("Authorization", "Bearer " + accessToken)
                            .build();
                System.out.println("RT");
                System.out.println(refreshToken);
                System.out.println("AT");
                System.out.println(accessToken);
                Files.writeString(accessTokenFile.toPath(),"ATnew:"+accessToken, StandardOpenOption.APPEND);
                Files.writeString(refreshTokenFile.toPath(),"RTnew:"+refreshToken, StandardOpenOption.APPEND);
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
