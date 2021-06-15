package com.sender.dao;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

@Entity
@Data
@Table(name = "company")
@NoArgsConstructor
@AllArgsConstructor
public class CompanyDAO {

    @Id
    private Long id;
    private String budget = "-1";
    private String tags = "";
    private String name = "";
    private String mobile = "";
    private String email = "";
    private String form = "";
    private String companyName = "";
    private String inn = "";
    private String city = "";
    private String sno = "";
    private String registrationDate = "";
    private Integer registrationYear = -1;
    private String bankAccounts = "";
    private String oborot = "";
    private String registration = "";
    private String address = "";
    private String keepAddress = "";
    private String addressNote = "";
    private String nalog = "";
    private String licensesString = "";
    private String okvedString = "";
    private String report = "";
    private String ecp = "";
    private String cpo = "";
    private String goszakaz = "";
    private Integer founders = -1;
    private Integer workersCount = -1;
    private String elimination = "";
    private Integer price = -1;
    private String debt = "";
    private String marriage = "";
    private String owner = "";
    private String aim = "";
    private String comment = "";
    private String post = "";
    @Column(columnDefinition = "text")
    private String notesString = "";
    private Boolean voronka = false;
    private String voronkaId = "";
    private Boolean isDeleted = false;
    private Boolean isPosted = false;

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

    public CompanyDAO(JSONObject company) {
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
                        if (value.startsWith("\"")) {
                            this.companyName = value.substring(1, value.length() - 1);
                        } else {
                            this.companyName = value;
                        }
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
                    case "Оформление":
                        this.registration = value;
                        break;
                    case "Сотрудников":
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
                    case "Учредителей":
                        this.founders = Integer.parseInt(value);
                        break;
                    case "Собственник/посредник":
                        this.owner = value;
                        break;
                    case "Цель покупки(Для покупателей)":
                        this.aim = value;
                        break;
                    case "Должность":
                        this.post = value;
                        break;
                }
            }
        } catch (Exception ex) {
            ex.printStackTrace();
            System.out.println(company);
        }
    }

    public void setTags(JSONArray tagsArray) {
        String tags = "";
        for (Object tag : tagsArray) {
            JSONObject tagJSON = (JSONObject) tag;
            tags = tags.concat(tagJSON.getString("name")).concat(";");
        }
        this.tags = tags;
    }

    private String setMultiSelectValue(JSONArray valueArray) {
        StringBuilder resultString = new StringBuilder();
        for (Object valueObject : valueArray) {
            JSONObject valueJSON = (JSONObject) valueObject;
            resultString.append(valueJSON.getString("value"));
            resultString.append(";");
        }
        resultString.deleteCharAt(resultString.lastIndexOf(";"));
        return resultString.toString().trim();
    }

    private String getDateString() {
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(Long.parseLong(registrationDate) * 1000);
        StringBuilder dateString = new StringBuilder();
        int day = calendar.get(Calendar.DAY_OF_MONTH);
        int month = calendar.get(Calendar.MONTH) + 1;
        int year = calendar.get(Calendar.YEAR);
        if (day < 10) {
            dateString.append(0);
        }
        dateString.append(day);
        dateString.append(".");
        if (month < 10) {
            dateString.append(0);
        }
        dateString.append(month);
        dateString.append(".");
        dateString.append(year);
        return dateString.toString();
    }

    public List<String> getCompanyAsListOfParametersClientFull() {
        String budget = this.budget;
        if (budget.equalsIgnoreCase("-1")) {
            budget = "";
        }
        String founders = String.valueOf(this.founders);
        if (founders.equalsIgnoreCase("-1")) {
            founders = "";
        }
        return List.of(
                String.valueOf(id),
                inn,
                companyName,
                budget,
                city,
                sno,
                getDateString(),
                bankAccounts,
                oborot,
                address,
                keepAddress,
                addressNote,
                nalog,
                okvedString,
                report,
                ecp,
                cpo,
                licensesString,
                goszakaz,
                elimination,
                debt,
                founders,
                String.valueOf(workersCount),
                comment
        );
    }


    public List<String> getCompanyAsListOfParametersClient() {
        String budget = this.budget;
        if (budget.equalsIgnoreCase("-1")) {
            budget = "";
        }
        String founders = String.valueOf(this.founders);
        if (founders.equalsIgnoreCase("-1")) {
            founders = "";
        }
        String registrationYear = String.valueOf(this.registrationYear);
        if (registrationYear.equalsIgnoreCase("-1")) {
            registrationYear = "";
        }
        return List.of(
                String.valueOf(id),
                budget,
                city,
                sno,
                registrationYear,
                bankAccounts,
                oborot,
                address,
                keepAddress,
                addressNote,
                nalog,
                okvedString,
                report,
                ecp,
                cpo,
                licensesString,
                goszakaz,
                elimination,
                debt,
                founders,
                owner,
                comment
        );
    }

    public List<String> getCompanyAsListOfParametersAdmin() {
        String voronkaName = "";
        switch (this.voronkaId) {
            case ("37851406"):
                voronkaName = "Платина";
                break;
            case ("36691654"):
                voronkaName = "Золото";
                break;
            case ("37851409"):
                voronkaName = "Серебро";
                break;
            case ("37851127"):
                voronkaName = "Бронза";
                break;
        }
        String budget = this.budget;
        if (budget.equalsIgnoreCase("-1")) {
            budget = "";
        }
        String founders = String.valueOf(this.founders);
        if (founders.equalsIgnoreCase("-1")) {
            founders = "";
        }
        String workersCount = String.valueOf(this.workersCount);
        if (workersCount.equalsIgnoreCase("-1")) {
            workersCount = "";
        }
        String registrationYear = String.valueOf(this.registrationYear);
        if (registrationYear.equalsIgnoreCase("-1")) {
            registrationYear = "";
        }
        return List.of(
                String.valueOf(id),
                voronkaName,
                budget,
                String.valueOf(price),
                tags,
                name,
                mobile,
                email,
                form,
                companyName,
                inn,
                city,
                sno,
                getDateString(),
                registrationYear,
                bankAccounts,
                oborot,
                address,
                keepAddress,
                addressNote,
                nalog,
                okvedString,
                report,
                registration,
                ecp,
                cpo,
                licensesString,
                goszakaz,
                workersCount,
                elimination,
                debt,
                marriage,
                founders,
                owner,
                comment,
                post
        );
    }

    public void setBudget(String budget) {
        this.budget = budget;
    }

    public void setNotes(JSONObject notes) {
        if (notes != null) {
            JSONArray notesArray = ((JSONObject) notes.get("_embedded")).getJSONArray("notes");
            JSONObject note;
            StringBuilder builder = new StringBuilder();
            for (Object noteObj : notesArray) {
                note = (JSONObject) noteObj;
                builder.append(note.getJSONObject("params").getString("text"));
                builder.append(";");
            }
            builder.deleteCharAt(builder.length() - 1);
            this.notesString = builder.toString();
        }
    }

    public List<String> getNotesAsList() {
        return List.of(
                notesString.split(";")
        );
    }
}
