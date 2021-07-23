package com.sender.service;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.sender.bots.AdminBot;
import com.sender.bots.DonateTelegramBot;
import com.sender.bots.SenderTelegramBot;
import com.sender.bots.VkBot;
import com.sender.dao.CompanyDAO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class DatabaseService implements Runnable {

    private final Logger log;
    private final AmoRequestService requestService;
    private final EntityManagerService entityManager;
    private final SenderTelegramBot bot;
    private final AdminBot adminBot;
    private final VkBot vkBot;
    private final DonateTelegramBot donateBot;
    private final String STATUS_PLATINA = "37851406";
    private final String STATUS_GOLD = "36691654";
    private final String STATUS_SILVER = "37851409";
    private final String STATUS_BRONZE = "37851127";
    private final JSONObject ADDED_TAG_JSON = JSON.parseObject("{\"id\": 83057,\"name\": \"Добавлен\"}");
    private String lead_id = "";
    private boolean sendToChats = true;

    public DatabaseService(AmoRequestService requestService, EntityManagerService entityManager, SenderTelegramBot bot, AdminBot adminBot, VkBot vkBot, DonateTelegramBot donateBot) {
        this.requestService = requestService;
        this.vkBot = vkBot;
        this.entityManager = entityManager;
        this.bot = bot;
        this.adminBot = adminBot;
        this.donateBot = donateBot;
        this.log = LoggerFactory.getLogger(DatabaseService.class);
    }

    private boolean normalVoronka(String status_id) {
        return status_id.equalsIgnoreCase(STATUS_PLATINA) ||
                status_id.equalsIgnoreCase(STATUS_GOLD) ||
                status_id.equalsIgnoreCase(STATUS_SILVER) ||
                status_id.equalsIgnoreCase(STATUS_BRONZE);
    }

    private Optional<CompanyDAO> findCompanyInDatabase(Long companyId) {
        return entityManager
                .getCompanyRepository()
                .findById(companyId);
    }

    private void makeCompanyDeleted(JSONArray contacts) {
        Long companyId = Long.parseLong(
                getContactId(contacts)
        );
        Optional<CompanyDAO> companyInDB = findCompanyInDatabase(companyId);
        if (companyInDB.isPresent()) {
            CompanyDAO companyInDBObject = companyInDB.get();
            companyInDBObject.setIsDeleted(true);
            companyInDBObject.setVoronka(false);
            companyInDBObject.setIsPosted(false);
            entityManager.getCompanyRepository().save(companyInDBObject);
        }
    }

    @Override
    public void run() {
        StringBuilder builder = new StringBuilder(lead_id+"\n");
        builder.append("Отправлять? ").append(sendToChats).append("\n");
        JSONObject lead = requestService.getCompanyInfo(lead_id);
        JSONObject lead_embedded = lead.getJSONObject("_embedded");
        String status_id = lead.getString("status_id");
        JSONArray contactsArray = lead_embedded.getJSONArray("contacts");
        JSONArray tags = lead_embedded.getJSONArray("tags");
        if (normalVoronka(status_id)) {
            builder.append("Верная воронка").append("\n");
            String contact_id = getContactId(contactsArray);
            if (!contact_id.isBlank()) {
                JSONObject contact = requestService.getContactInfo(contact_id);
                log.info("Contact info: {}", contact.toString());
                boolean hasAddedTag = tags.contains(ADDED_TAG_JSON);
                boolean bronzeVoronka = status_id.equalsIgnoreCase(STATUS_BRONZE);
                CompanyDAO company = new CompanyDAO(contact);
                company.setBudget(lead.getString("price"));
                company.setTags(lead_embedded.getJSONArray("tags"));
                company.setNotes(requestService.getNotesInfo(lead_id));
                company.setIsDeleted(false);
                company.setVoronkaId(status_id);
                if (!hasAddedTag) {
                    company.setIsPosted(false);
                }
                if (bronzeVoronka) {
                    company.setVoronka(false);
                } else {
                    company.setVoronka(true);
                }
                builder.append("Бронза: ").append(bronzeVoronka).append("\n");
                builder.append("Тег: ").append(hasAddedTag).append("\n");
                if (!bronzeVoronka && hasAddedTag) {
                    Optional<CompanyDAO> companyInDBOptional = entityManager.getCompanyRepository().findById(company.getId());
                    if (companyInDBOptional.isPresent()) {
                        builder.append("Уже в базе").append("\n");
                        CompanyDAO companyInDB = companyInDBOptional.get();
                        if (companyInDB.getIsPosted()) {
                            this.sendToChats = false;
                            builder.append("Уже был пост").append("\n");
                        } else {
                            this.sendToChats = true;
                            builder.append("Поста не было").append("\n");
                        }
                        boolean alreadyHadTag = companyInDB.getTags().contains("Добавлен");
                        if (!alreadyHadTag) {
                            this.sendToChats = true;
                            builder.append("Появился тег добавлен").append("\n");
                        } else {
                            this.sendToChats = false;
                            builder.append("Тег добавлен уже был").append("\n");
                        }
                        log.info("Company {} was in DB. Post: {}. Tag: {}", companyInDB.getId(), companyInDB.getIsPosted(), alreadyHadTag);
                    } else {
                        this.sendToChats = true;
                        builder.append("В базе нет, новая сделка").append("\n");
                        this.log.info("Company {} is new.", company.getId());
                    }
                    if (this.sendToChats) {
                        this.bot.sendLeadInfo(company);
                        this.donateBot.sendLeadInfo(company);
                        this.vkBot.sendPost(company);
                        company.setIsPosted(true);
                        builder.append("Сделка запощена, статус обновлен").append("\n");
                        this.log.info("Posted lead {}", company.getId());
                    }
                }
                this.entityManager.saveCompany(company);
                this.log.info("Lead {} saved to DB", lead_id);
                this.adminBot.sendInfo(builder.toString());
            }
        }
        else {
            makeCompanyDeleted(contactsArray);
            builder.append("Удалена").append("\n");
            this.adminBot.sendInfo(builder.toString());
            this.log.info("Lead {} is deleted", lead_id);
        }
    }

    private String getContactId(JSONArray contacts) {
        String contactURI = "";
        String contactId = "";
        for (Object contact : contacts) {
            JSONObject contactJSON = (JSONObject) contact;
            if (contactJSON.getBooleanValue("is_main")) {
                contactURI = contactJSON
                        .getJSONObject("_links")
                        .getJSONObject("self")
                        .getString("href");
                break;
            }
        }
        int last_slash = contactURI.lastIndexOf("/");
        if (last_slash > 0) {
            contactId = contactURI.substring(last_slash + 1);
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
