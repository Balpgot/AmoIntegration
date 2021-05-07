package com.sender.controller;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.sender.service.AmoRequestService;
import com.sender.service.DatabaseService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@RestController
public class AmoIntegrationController {

    private final AmoRequestService requestService;
    private final DatabaseService databaseService;

    @Autowired
    public AmoIntegrationController(AmoRequestService requestService, DatabaseService databaseService){
        this.requestService = requestService;
        this.databaseService = databaseService;
    }

    @PostMapping(value = "/webhook", consumes = MediaType.APPLICATION_FORM_URLENCODED_VALUE)
    public ResponseEntity<HttpStatus> getInfoFromWebhook(HttpEntity<String> httpEntity) {
        //if(params!=null) {
        String webhook = URLDecoder.decode(httpEntity.getBody(), StandardCharsets.UTF_8);
        System.out.println(webhook);
        String regex = "\\[id\\]=\\d*";
        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(webhook);
        if (matcher.find()) {
            String element_id = matcher.group();
            String lead_id = element_id.substring(element_id.indexOf("=") + 1);
            databaseService.setLead_id(lead_id);
            Thread dataSavingThread = new Thread(databaseService);
            dataSavingThread.start();
            return new ResponseEntity<>(HttpStatus.OK);
        }
        return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        /*}
        else{
        }*/
    }

    @GetMapping(value = "/import")
    public ResponseEntity<HttpStatus> importDeals(){
        try {
            /*JSONObject leadsObject = requestService.getAllCompaniesPage1();
            JSONArray leads = leadsObject.getJSONObject("_embedded").getJSONArray("leads");
            JSONObject lead;
            databaseService.setImportMode(true);
            for(Object leadObject:leads){
                lead = (JSONObject) leadObject;
                databaseService.setLead_id(lead.getString("id"));
                Thread dataSavingThread = new Thread(databaseService);
                dataSavingThread.start();
                try {
                    Thread.sleep(1000);
                }
                catch (Exception ex){
                    ex.printStackTrace();
                }
            }
            leadsObject = requestService.getAllCompaniesPage2();
            leads = leadsObject.getJSONObject("_embedded").getJSONArray("leads");
            for(Object leadObject:leads){
                lead = (JSONObject) leadObject;
                databaseService.setLead_id(lead.getString("id"));
                Thread dataSavingThread = new Thread(databaseService);
                dataSavingThread.start();
                try {
                    Thread.sleep(1000);
                }
                catch (Exception ex){
                    ex.printStackTrace();
                }
            }
            databaseService.setImportMode(false);*/
            return new ResponseEntity<>(HttpStatus.OK);
        }
        catch (Exception ex){
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
    }

    @PostMapping(value = "/tokens/refresh", consumes = MediaType.APPLICATION_JSON_VALUE)
    public void getRefreshToken(HttpEntity<String> request){
        try {
            JSONObject token = JSON.parseObject(request.getBody());
            System.out.println(token.getString("refresh_token"));
            requestService.setRefreshToken(token.getString("refresh_token"));
            requestService.refreshTokens();
        }
        catch (Exception ex){
            ex.printStackTrace();
        }
    }



}
