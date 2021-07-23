package com.sender;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Properties;

public class PropertiesStorage {
    public static Properties properties;
    public static String AMO_ACCESS_TOKEN;
    public static String AMO_REFRESH_TOKEN;
    public static String AMO_CLIENT_ID;
    public static String AMO_CLIENT_SECRET;
    public static String AMO_REDIRECT_URL;
    public static String AMO_BASE_URL;
    public static String TELEGRAM_BOT_TOKEN;
    public static String TELEGRAM_DONATE_BOT_TOKEN;
    public static String TELEGRAM_ADMINBOT_TOKEN;
    public static Long TELEGRAM_GROUP_ID;
    public static Long TELEGRAM_CHAT_ID;
    public static Long TELEGRAM_DONATE_CHAT_ID;
    public static Long TELEGRAM_ADMIN_ID;
    public static String VK_ACCESS_TOKEN;
    public static Integer VK_GROUP_ID;
    public static Integer VK_CLIENT_ID;
    public static String VK_PHOTO_ID;
    public static String SECURITY_LOGIN;
    public static String SECURITY_PASSWORD;
    public static boolean isLoaded = false;

    public static void loadProperties() {
        try {
            properties = new Properties();
            properties.load(new FileInputStream("./config/api.properties"));
            AMO_ACCESS_TOKEN = properties.getProperty("amo.accessToken");
            //System.out.println(AMO_ACCESS_TOKEN);
            AMO_REFRESH_TOKEN = properties.getProperty("amo.refreshToken");
            //System.out.println(AMO_REFRESH_TOKEN);
            AMO_BASE_URL = properties.getProperty("amo.baseUrl");
            //System.out.println(AMO_BASE_URL);
            AMO_REDIRECT_URL = properties.getProperty("amo.redirectUrl");
            //System.out.println(AMO_REDIRECT_URL);
            AMO_CLIENT_ID = properties.getProperty("amo.clientId");
            //System.out.println(AMO_CLIENT_ID);
            AMO_CLIENT_SECRET = properties.getProperty("amo.clientSecret");
            //System.out.println(AMO_CLIENT_SECRET);
            TELEGRAM_BOT_TOKEN = properties.getProperty("telegram.token");
            //System.out.println(TELEGRAM_BOT_TOKEN);
            TELEGRAM_GROUP_ID = Long.parseLong(properties.getProperty("telegram.groupId"));
            //System.out.println(TELEGRAM_GROUP_ID);
            TELEGRAM_CHAT_ID = Long.parseLong(properties.getProperty("telegram.chatId"));
            //System.out.println(TELEGRAM_CHAT_ID);
            TELEGRAM_ADMINBOT_TOKEN = properties.getProperty("telegram.admin.token");
            //System.out.println(TELEGRAM_ADMINBOT_TOKEN);
            TELEGRAM_ADMIN_ID = Long.parseLong(properties.getProperty("telegram.admin.chatId"));
            //System.out.println(TELEGRAM_ADMIN_ID);
            TELEGRAM_DONATE_BOT_TOKEN = properties.getProperty("telegram.donate.token");
            TELEGRAM_DONATE_CHAT_ID = Long.parseLong(properties.getProperty("telegram.donate.chatId"));
            VK_ACCESS_TOKEN = properties.getProperty("vk.token");
            //System.out.println(VK_ACCESS_TOKEN);
            VK_GROUP_ID = Integer.parseInt(properties.getProperty("vk.groupId"));
            //System.out.println(VK_GROUP_ID);
            VK_CLIENT_ID = Integer.parseInt(properties.getProperty("vk.clientId"));
            //System.out.println(VK_CLIENT_ID);
            VK_PHOTO_ID = properties.getProperty("vk.photoId");
            SECURITY_LOGIN = properties.getProperty("security.login");
            SECURITY_PASSWORD = properties.getProperty("security.password");
            isLoaded = true;
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }

    public static void writeProperties(String accessToken, String refreshToken) {
        try {
            properties.setProperty("amo.accessToken", accessToken);
            AMO_ACCESS_TOKEN = accessToken;
            properties.setProperty("amo.refreshToken", refreshToken);
            AMO_REFRESH_TOKEN = refreshToken;
            properties.store(new FileOutputStream("./config/api.properties"), null);
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }
}
