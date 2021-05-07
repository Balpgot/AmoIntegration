package com.sender.service;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.sender.bots.AdminBot;
import com.sender.bots.SenderTelegramBot;
import com.sender.bots.VkBot;
import com.sender.dao.CompanyDAO;
import lombok.Setter;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class DatabaseService implements Runnable {

    private final AmoRequestService requestService;
    private final EntityManagerService entityManager;
    private final SenderTelegramBot bot;
    private final AdminBot adminBot;
    private final VkBot vkBot;
    private final String STATUS_PLATINA = "37851406";
    private final String STATUS_GOLD = "36691654";
    private final String STATUS_SILVER = "37851409";
    private final String STATUS_BRONZE = "37851127";
    private final String ADDED_TAG_ID = "83057";
    private String lead_id = "";
    private boolean sendToChats = true;
    @Setter
    private boolean importMode = false;

    public DatabaseService(AmoRequestService requestService, EntityManagerService entityManager, SenderTelegramBot bot, AdminBot adminBot, VkBot vkBot) {
        this.requestService = requestService;
        this.vkBot = vkBot;
        this.entityManager = entityManager;
        this.bot = bot;
        this.adminBot = adminBot;
    }

    @Override
    public void run() {
        StringBuilder builder = new StringBuilder(lead_id);
        builder.append("\n");
        builder.append("Отправлять? ").append(sendToChats).append("\n");
        System.out.println("Saving " + lead_id);
        JSONObject lead = requestService.getCompanyInfo(lead_id);
        System.out.println(lead);
        JSONObject lead_embedded = lead.getJSONObject("_embedded");
        boolean voronka = false;
        String status_id = lead.getString("status_id");
        if(
                status_id.equalsIgnoreCase(STATUS_PLATINA) ||
                status_id.equalsIgnoreCase(STATUS_GOLD) ||
                status_id.equalsIgnoreCase(STATUS_SILVER) ||
                status_id.equalsIgnoreCase(STATUS_BRONZE)
        ) {
            voronka = true;
            builder.append("Верная воронка").append("\n");
        }
        else{
            Optional<CompanyDAO> companyInDB = entityManager
                    .getCompanyRepository()
                    .findById(
                            Long.parseLong(
                                    getContactId(
                                            lead_embedded.getJSONArray("contacts")
                                    )
                            )
                    );
            if(companyInDB.isPresent()){
                CompanyDAO companyInDBObject = companyInDB.get();
                companyInDBObject.setIsDeleted(true);
                companyInDBObject.setVoronka(false);
                companyInDBObject.setIsPosted(false);
                entityManager.getCompanyRepository().save(companyInDBObject);
            }
            builder.append("Удалена").append("\n");
            adminBot.sendInfo(builder.toString());
        }
        JSONArray tags = lead_embedded.getJSONArray("tags");
        JSONObject tag;
        boolean has_added_tag = false;
        for(int i = 0; i<tags.size(); i++){
            tag = (JSONObject) tags.get(i);
            has_added_tag = tag.getString("id").equalsIgnoreCase(ADDED_TAG_ID);
        }
        if(importMode){
            has_added_tag = true;
        }
        boolean bronze = status_id.equalsIgnoreCase(STATUS_BRONZE);
        if(voronka){
            String contact_id = getContactId(lead_embedded.getJSONArray("contacts"));
            if(!contact_id.isBlank()){
                JSONObject contact = requestService.getContactInfo(contact_id);
                CompanyDAO company = new CompanyDAO(contact);
                company.setBudget(lead.getString("price"));
                company.setTags(lead_embedded.getJSONArray("tags"));
                if(!has_added_tag){
                    company.setIsPosted(false);
                }
                company.setNotes(requestService.getNotesInfo(lead_id));
                company.setIsDeleted(false);
                if(bronze) {
                    company.setVoronka(false);
                }
                else{
                    company.setVoronka(true);
                }
                company.setVoronkaId(status_id);
                builder.append("Бронза: ").append(bronze).append("\n");
                builder.append("Тег: ").append(has_added_tag).append("\n");
                if(!bronze && has_added_tag) {
                    if(entityManager.getCompanyRepository().existsById(company.getId())) {
                        builder.append("Уже в базе").append("\n");
                        CompanyDAO companyInDB = entityManager.getCompanyRepository().findById(company.getId()).get();
                        if (companyInDB.getIsPosted()) {
                            sendToChats = false;
                            builder.append("Уже был пост").append("\n");
                        }
                        else{
                            sendToChats = true;
                            builder.append("Поста не было").append("\n");
                        }
                        if (!companyInDB.getTags().contains("Добавлен")) {
                            this.sendToChats = true;
                            builder.append("Появился тег добавлен").append("\n");
                        } else {
                            this.sendToChats = false;
                            builder.append("Тег добавлен уже был").append("\n");
                        }
                    }
                    else{
                        sendToChats = true;
                        builder.append("В базе нет, новая сделка").append("\n");
                    }
                    if(sendToChats) {
                        bot.sendLeadInfo(company);
                        vkBot.sendPost(company);
                        company.setIsPosted(true);
                        builder.append("Сделка запощена, статус обновлен").append("\n");
                    }
                }
                entityManager.saveCompany(company);
                adminBot.sendInfo(builder.toString());
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

    public void setLead_id(String lead_id) {
        this.lead_id = lead_id;
    }

    public void setSendToChats(boolean sendToChats) {
        this.sendToChats = sendToChats;
    }
}
