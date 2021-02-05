package com.sender.dao;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
@Data
@Table(name = "company")
@NoArgsConstructor
@AllArgsConstructor
public class CompanyDAO {

    @Id
    private Long id;
    private String tags;
    private String name;
    private String mobile;
    private String email;
    private String form;
    private String companyName;
    private String inn;
    private String city;
    private String sno;
    private String registrationDate;
    private Integer registrationYear;
    private String bankAccounts;
    private String oborot;
    private String address;
    private String keepAddress;
    private String addressNote;
    private String nalog;
    private String licensesString;
    private String okvedString;
    private String report;
    private String ecp;
    private String cpo;
    private String goszakaz;
    private Integer workersCount;
    private String elimination;
    private Integer price;
    private String debt;
    private String marriage;
    private String comment;
    private String post;

    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(
            name = "company_license",
            joinColumns = @JoinColumn(name = "company_id"),
            inverseJoinColumns = @JoinColumn(name = "license_id"))
    private List<LicenseDAO> license_list = new ArrayList<>();

    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(
            name = "company_okved",
            joinColumns = @JoinColumn(name = "company_id"),
            inverseJoinColumns = @JoinColumn(name = "okved_id"))
    private List<OKVEDDAO> okved_list = new ArrayList<>();

    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(
            name = "company_cpo",
            joinColumns = @JoinColumn(name = "company_id"),
            inverseJoinColumns = @JoinColumn(name = "cpo_id"))
    private List<CPODAO> cpo_list = new ArrayList<>();

    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(
            name = "company_bank",
            joinColumns = @JoinColumn(name = "company_id"),
            inverseJoinColumns = @JoinColumn(name = "bank_id"))
    private List<BankDAO> bank_list = new ArrayList<>();

    public CompanyDAO(Long id, String mobile, String email, String form, String companyName, String inn, String city, LicenseDAO license, BankDAO bank) {
        this.id = id;
        this.mobile = mobile;
        this.email = email;
        this.form = form;
        this.companyName = companyName;
        this.inn = inn;
        this.city = city;
        this.bank_list.add(bank);
        this.license_list.add(license);
    }

    public CompanyDAO(Long id, String name)
    {
        this.id = id;
        this.companyName = name;
    }

    public CompanyDAO(JSONObject company){
        try {
            this.id = company.getLong("id");
            this.name = company.getString("name");
            JSONArray custom_fields = company.getJSONArray("custom_fields_values");
            JSONObject fieldJSON;
            for (Object field : custom_fields) {
                fieldJSON = (JSONObject) field;
                JSONArray values = fieldJSON.getJSONArray("values");
                String value = values.getJSONObject(0).getString("value");
                switch (fieldJSON.getString("field_name")) {
                    case "Телефон":
                        this.mobile = value;
                        break;
                    case "Email":
                        this.email = value;
                        break;
                    case "Форма собственности":
                        this.form = value;
                        break;
                    case "Название":
                        this.companyName = value;
                        break;
                    case "ИНН":
                        this.inn = value;
                        break;
                    case "Город":
                        this.city = value;
                        break;
                    case "СНО":
                        this.sno = value;
                        break;
                    case "Дата регистрации":
                        this.registrationDate = value;
                        break;
                    case "Год регистрации":
                        this.registrationYear = Integer.parseInt(value);
                        break;
                    case "Счета в банках":
                        this.bankAccounts = setMultiSelectValue(values);
                        break;
                    case "Обороты":
                        this.oborot = value;
                        break;
                    case "Адрес":
                        this.address = value;
                        break;
                    case "Адрес можно оставить":
                        this.keepAddress = value;
                        break;
                    case "Адрес отметка":
                        this.addressNote = value;
                        break;
                    case "Налоговая":
                        this.nalog = value;
                        break;
                    case "ОКВЭД":
                        this.okvedString = value;
                        break;
                    case "Отчетность":
                        this.report = value;
                        break;
                    case "Количество сотрудников":
                        this.workersCount = Integer.parseInt(value);
                        break;
                    case "Ликвидация":
                        this.elimination = value;
                        break;
                    case "Наличие ЭЦП":
                        this.ecp = value;
                        break;
                    case "СРО":
                        this.cpo = setMultiSelectValue(values);
                        break;
                    case "Лицензии":
                        this.licensesString = setMultiSelectValue(values);
                        break;
                    case "Гос. контракты":
                        this.goszakaz = value;
                        break;
                    case "Цена продавца":
                        this.price = Integer.parseInt(value);
                        break;
                    case "Долги":
                        this.debt = value;
                        break;
                    case "Брак":
                        this.marriage = value;
                        break;
                    case "Комментарии":
                        this.comment = value;
                        break;
                    case "Должность":
                        this.post = value;
                        break;
                }
            }
        }
        catch (Exception ex){
            ex.printStackTrace();
            System.out.println(company);
        }
    }

    public void setTags(JSONArray tagsArray){
        String tags = "";
        for (Object tag:tagsArray) {
            JSONObject tagJSON = (JSONObject) tag;
            tags = tags.concat(tagJSON.getString("name")).concat(";");
        }
        this.tags = tags;
    }

    private String setMultiSelectValue(JSONArray valueArray){
        StringBuilder resultString = new StringBuilder();
        for (Object valueObject: valueArray) {
            JSONObject valueJSON = (JSONObject) valueObject;
            resultString.append(valueJSON.getString("value"));
            resultString.append(";");
        }
        resultString.deleteCharAt(resultString.lastIndexOf(";"));
        return resultString.toString().trim();
    }

    public List<String> getCompanyAsListOfParameters() {
        List<String> parameters = List.of(
                String.valueOf(id),
                tags,
                name,
                mobile,
                email,
                form,
                companyName,
                inn,
                city,
                sno,
                registrationDate,
                String.valueOf(registrationYear),
                bankAccounts,
                oborot,
                address,
                keepAddress,
                addressNote,
                nalog,
                licensesString,
                okvedString,
                report,
                ecp,
                cpo,
                goszakaz,
                String.valueOf(workersCount),
                elimination,
                String.valueOf(price),
                debt,
                marriage,
                comment,
                post
        );
        return parameters;
    }
}
