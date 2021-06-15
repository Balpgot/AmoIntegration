package com.sender.controller;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.sender.service.AmoRequestService;
import com.sender.service.DatabaseService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@RestController
public class AmoIntegrationController {

    private Logger log;
    private final AmoRequestService requestService;
    private final DatabaseService databaseService;

    @Autowired
    public AmoIntegrationController(AmoRequestService requestService, DatabaseService databaseService) {
        this.requestService = requestService;
        this.databaseService = databaseService;
        this.log = LoggerFactory.getLogger(AmoIntegrationController.class);
    }

    @PostMapping(value = "/webhook", consumes = MediaType.APPLICATION_FORM_URLENCODED_VALUE)
    public ResponseEntity<HttpStatus> getInfoFromWebhook(HttpEntity<String> httpEntity) {
        String webhook = URLDecoder.decode(httpEntity.getBody(), StandardCharsets.UTF_8);
        System.out.println(webhook);
        String regex = "\\[id\\]=\\d*";
        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(webhook);
        log.info(webhook);
        if (matcher.find()) {
            String element_id = matcher.group();
            String lead_id = element_id.substring(element_id.indexOf("=") + 1);
            databaseService.setLead_id(lead_id);
            log.info("LEAD_ID: " + lead_id);
            Thread dataSavingThread = new Thread(databaseService);
            dataSavingThread.start();
            return new ResponseEntity<>(HttpStatus.OK);
        }
        return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
    }

    @PostMapping(value = "/tokens/refresh", consumes = MediaType.APPLICATION_JSON_VALUE)
    public void getRefreshToken(HttpEntity<String> request) {
        try {
            JSONObject token = JSON.parseObject(request.getBody());
            System.out.println(token.getString("refresh_token"));
            requestService.setRefreshToken(token.getString("refresh_token"));
            requestService.refreshTokens();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }


}
