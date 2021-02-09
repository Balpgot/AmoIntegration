package com.sender.bots;

import com.pengrad.telegrambot.TelegramBot;
import com.pengrad.telegrambot.request.SendMessage;
import com.sender.dao.CompanyDAO;
import org.springframework.stereotype.Service;

@Service
public class SenderTelegramBot {

    private TelegramBot bot;

    public SenderTelegramBot() {
        bot = new TelegramBot("");
    }

    public void sendLeadInfo(CompanyDAO company){
        String messageText = createMessage(company);
        SendMessage message = new SendMessage(-1001291799463L, messageText);
        bot.execute(message);
    }

    private String createMessage(CompanyDAO company){
        StringBuilder message = new StringBuilder();
        message.append("Продам ").append(company.getForm()).append("/фирму\n");
        message.append("Город: ").append(company.getCity()).append("\n");
        message.append("СНО: ").append(company.getSno()).append("\n");
        message.append("Год регистрации: ").append(company.getRegistrationYear()).append("\n");
        message.append("Счета в банках: ").append(company.getBankAccounts()).append("\n");
        message.append("Обороты: ").append(company.getOborot()).append("\n");
        message.append("Отчетость: ").append(company.getReport()).append("\n");
        message.append("Наличие ЭЦП: ").append(company.getEcp()).append("\n");
        message.append("СРО: ").append(company.getCpo()).append("\n");
        message.append("Лицензии: ").append(company.getLicensesString()).append("\n");
        message.append("Госконтракты: ").append(company.getGoszakaz()).append("\n");
        message.append("Комментарии: ").append(company.getComment()).append("\n");
        message.append("Цена: ").append(company.getBudget()).append("\n");
        message.append("ID: ").append(company.getId()).append("\n");
        message.append("Чтобы получить подробную информации об организации делайте запрос сюда: @alexbizbro24 обязательно указывайте ID интересующей фирмы.");
        return message.toString();
    }
}
