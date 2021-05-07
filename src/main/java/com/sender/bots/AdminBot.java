package com.sender.bots;

import com.pengrad.telegrambot.TelegramBot;
import com.pengrad.telegrambot.request.SendMessage;
import com.sender.PropertiesStorage;
import org.springframework.stereotype.Service;

@Service
public class AdminBot {

    private TelegramBot bot;
    private final long testid = PropertiesStorage.TELEGRAM_ADMIN_ID;

    public AdminBot() {
        bot = new TelegramBot(PropertiesStorage.TELEGRAM_ADMINBOT_TOKEN);
}

    public void sendInfo(String text){
        SendMessage message = new SendMessage(testid,text);
        bot.execute(message);
    }


}
