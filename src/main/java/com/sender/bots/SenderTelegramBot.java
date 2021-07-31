package com.sender.bots;

import com.pengrad.telegrambot.TelegramBot;
import com.pengrad.telegrambot.request.SendPhoto;
import com.sender.PropertiesStorage;
import com.sender.dao.CompanyDAO;
import org.springframework.stereotype.Service;

import java.io.File;

@Service
public class SenderTelegramBot {

    private TelegramBot bot;
    private final long chatId = PropertiesStorage.TELEGRAM_CHAT_ID;
    private final long groupId = PropertiesStorage.TELEGRAM_GROUP_ID;

    public SenderTelegramBot() {
        bot = new TelegramBot(PropertiesStorage.TELEGRAM_BOT_TOKEN);
    }

    public void sendLeadInfo(CompanyDAO company) {
        SendPhoto chatMessagePhoto = new SendPhoto(chatId, new File("./config/picture.jpg"));
        chatMessagePhoto.caption(createMessage(company));
        bot.execute(chatMessagePhoto);
        SendPhoto groupMessagePhoto = new SendPhoto(groupId, new File("./config/picture.jpg"));
        groupMessagePhoto.caption(createMessage(company));
        bot.execute(groupMessagePhoto);
    }

    private String createMessage(CompanyDAO company) {
        String registrationYear = String.valueOf(company.getRegistrationYear());
        if (registrationYear.equalsIgnoreCase("-1")) {
            registrationYear = "";
        }
        StringBuilder message = new StringBuilder();
        message.append("Продам ").append(company.getForm()).append("/фирму\n\n");
        message.append("Город ").append(company.getCity()).append("\n");
        message.append(company.getSno()).append("\n");
        message.append("Регистрация ").append(registrationYear).append("\n");
        message.append("Счета ").append(company.getBankAccounts()).append("\n");
        message.append("Адрес ").append(company.getAddress()).append("\n");
        //message.append("Адрес можно оставить: ").append(company.getKeepAddress()).append("\n");
        message.append("ОКВЭД ").append(company.getOkvedString()).append("\n");
        String cpo = company.getCpo();
        if(!cpo.equalsIgnoreCase("Нет")) {
            message.append("СРО ").append(cpo).append("\n");
        }
        String licences = company.getLicensesString();
        if(!licences.equalsIgnoreCase("Нет")) {
            message.append("Лицензии ").append(licences).append("\n");
        }
        if(company.getOborot().equalsIgnoreCase("да")){
            message.append("С оборотами").append("\n");
        }
        else{
            message.append("Без оборотов").append("\n");
        }
        //message.append("Обороты: ").append(company.getOborot()).append("\n");
        //message.append("Отчетность: ").append(company.getReport()).append("\n");
        //message.append("Наличие ЭЦП: ").append(company.getEcp()).append("\n");
        //message.append("СРО: ").append(company.getCpo()).append("\n");
        //message.append("Лицензии: ").append(company.getLicensesString()).append("\n");
        //message.append("Госконтракты: ").append(company.getGoszakaz()).append("\n");
        message.append("Учредителей ").append(company.getFounders()).append("\n");
        message.append("Сотрудников ").append(company.getWorkersCount()).append("\n");
        message.append("Комментарии: ").append(company.getComment()).append("\n");
        message.append("Цена: ").append(company.getBudget()).append("\n\n");
        message.append("ID: ").append(company.getId()).append("\n");
        message.append("Чтобы получить подробную информации об организации делайте запрос сюда: @alexbizbro24 обязательно указывайте ID интересующей фирмы.");
        return message.toString();
    }
}
