package com.sender.bots;

import com.pengrad.telegrambot.TelegramBot;
import com.pengrad.telegrambot.request.SendPhoto;
import com.sender.PropertiesStorage;
import com.sender.dao.CompanyDAO;
import org.springframework.stereotype.Service;

import java.io.File;

@Service
public class DonateTelegramBot {
    private TelegramBot bot;
    private final long chatId = PropertiesStorage.TELEGRAM_DONATE_CHAT_ID;

    public DonateTelegramBot() {
        bot = new TelegramBot(PropertiesStorage.TELEGRAM_DONATE_BOT_TOKEN);
    }

    public void sendLeadInfo(CompanyDAO company) {
        SendPhoto chatMessagePhoto = new SendPhoto(chatId, new File("./config/picture.jpg"));
        chatMessagePhoto.caption(createMessage(company));
        bot.execute(chatMessagePhoto);
    }

    private String createMessage(CompanyDAO company) {
        StringBuilder message = new StringBuilder();
        message.append(company.getForm()).append(" ").append(company.getCompanyName()).append("\n");
        message.append("ИНН ").append(company.getInn()).append("\n");
        message.append("Город ").append(company.getCity()).append("\n");
        message.append(company.getSno()).append("\n");
        message.append("Регистрация ").append(company.getDateString()).append("\n");
        message.append("Счета ").append(company.getBankAccounts()).append("\n");
        message.append("Адрес ").append(company.getAddress()).append("\n");
        message.append("ОКВЭД ").append(company.getOkvedString()).append("\n");
        message.append("Налоговая ").append(company.getNalog()).append("\n");
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
        message.append("Учредителей ").append(company.getFounders()).append("\n");
        message.append("Сотрудников ").append(company.getWorkersCount()).append("\n");
        message.append("Комментарии: ").append(company.getComment()).append("\n");
        message.append("Цена: ").append(company.getBudget()).append("\n\n");
        message.append("ID: ").append(company.getId()).append("\n");
        return message.toString();
    }
}
