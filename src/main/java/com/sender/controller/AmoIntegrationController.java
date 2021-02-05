package com.sender.controller;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.sender.dao.CompanyDAO;
import com.sender.repository.CompanyRepository;
import com.sender.service.AmoRequestService;
import com.sender.service.EntityManagerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class AmoIntegrationController {

    private final CompanyRepository repository;
    private final AmoRequestService requestService;
    private final EntityManagerService entityManager;
    private final String STATUS_IN_BASE = "36691654";
    private final String ADDED_TAG_ID = "83057";

    @Autowired
    public AmoIntegrationController(CompanyRepository repository, AmoRequestService requestService,EntityManagerService entityManager){
        this.repository = repository;
        this.requestService = requestService;
        this.entityManager = entityManager;
    }

    @PostMapping(value = "/webhook", consumes = MediaType.APPLICATION_FORM_URLENCODED_VALUE)
    public ResponseEntity<HttpStatus> getInfoFromWebhook(@RequestParam MultiValueMap params){
        if(params!=null && params.containsKey("element_id")) {
            String lead_id = String.valueOf(params.getFirst("element_id"));
            checkLeadToAddInDatabase(lead_id);
        /*String webhook = URLDecoder.decode(httpEntity.getBody(), StandardCharsets.UTF_8);
        System.out.println(webhook);
        String regex = "\\[element_id\\]=\\d*";
        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(webhook);
        if(matcher.find()){
            String element_id = matcher.group();
            String lead_id = element_id.substring(element_id.indexOf("=")+1);*/
            return new ResponseEntity<>(HttpStatus.OK);
        }
        else{
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

    private void checkLeadToAddInDatabase(String lead_id){
        JSONObject lead = requestService.getCompanyInfo(lead_id);
        JSONObject lead_embedded = lead.getJSONObject("_embedded");
        boolean has_status_in_base = lead.getString("status_id").equalsIgnoreCase(STATUS_IN_BASE);
        JSONArray tags = lead_embedded.getJSONArray("tags");
        JSONObject tag;
        boolean has_added_tag = false;
        for(int i = 0; i<tags.size(); i++){
            tag = (JSONObject) tags.get(i);
            has_added_tag = tag.getString("id").equalsIgnoreCase(ADDED_TAG_ID);
        }
        if(has_status_in_base && has_added_tag){
            String contact_id = getContactId(lead_embedded.getJSONArray("contacts"));
            if(!contact_id.isBlank()){
                JSONObject contact = requestService.getContactInfo(contact_id);
                CompanyDAO company = new CompanyDAO(contact);
                company.setTags(lead_embedded.getJSONArray("tags"));
                entityManager.saveCompany(company);
            }
        }
    }


    private String getContactId(JSONArray contacts){
        String contactURI = "";
        String contactId = "";
        for (Object contact:contacts) {
            JSONObject contactJSON = (JSONObject) contact;
            if(contactJSON.getBooleanValue("is_main")){
                contactURI = contactJSON
                        .getJSONObject("_links")
                        .getJSONObject("self")
                        .getString("href");
                break;
            }
        }
        int last_slash = contactURI.lastIndexOf("/");
        if(last_slash>0){
            contactId = contactURI.substring(last_slash+1);
        }
        return contactId;
    }

}
