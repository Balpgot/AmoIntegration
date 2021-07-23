package com.sender.bots;

import com.sender.PropertiesStorage;
import com.sender.dao.CompanyDAO;
import com.vk.api.sdk.client.TransportClient;
import com.vk.api.sdk.client.VkApiClient;
import com.vk.api.sdk.client.actors.UserActor;
import com.vk.api.sdk.httpclient.HttpTransportClient;
import org.springframework.stereotype.Service;

@Service
public class VkBot {

    private TransportClient transportClient = HttpTransportClient.getInstance();
    private VkApiClient vk = new VkApiClient(transportClient);
    private UserActor actorUser;
    private int groupId = PropertiesStorage.VK_GROUP_ID;
    private int userId = PropertiesStorage.VK_CLIENT_ID;
    private String accessToken = PropertiesStorage.VK_ACCESS_TOKEN;
    private String photoId = PropertiesStorage.VK_PHOTO_ID;

    public VkBot() {
        try {
            actorUser = new UserActor(userId, accessToken);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    public void sendPost(CompanyDAO company) {
        try {
            vk
                    .wall()
                    .post(actorUser)
                    .fromGroup(true)
                    .ownerId(groupId)
                    .message(createMessage(company))
                    .attachments(photoId)
                    .execute();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
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
        message.append("Чтобы получить подробную информации об организации делайте запрос сюда: @alexbizbro(Александр) обязательно указывайте ID интересующей фирмы.");
        return message.toString();
    }

}
