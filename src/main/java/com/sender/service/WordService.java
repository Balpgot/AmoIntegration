package com.sender.service;

import com.sender.dao.CompanyDAO;
import fr.opensagres.poi.xwpf.converter.pdf.PdfConverter;
import fr.opensagres.poi.xwpf.converter.pdf.PdfOptions;
import org.apache.poi.xwpf.usermodel.ParagraphAlignment;
import org.apache.poi.xwpf.usermodel.XWPFDocument;
import org.apache.poi.xwpf.usermodel.XWPFParagraph;
import org.apache.poi.xwpf.usermodel.XWPFRun;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.util.List;

public class WordService {

    public static File createWordFile(List<CompanyDAO> companies, String mode){
        try {
            File templateFile = new File("./template.docx");
            FileInputStream inputStream = new FileInputStream(templateFile);
            XWPFDocument wordDoc = new XWPFDocument(inputStream);
            XWPFParagraph title = wordDoc.createParagraph();
            modifyTitle(title);
            for (CompanyDAO company : companies) {
                XWPFParagraph companyParagraph = wordDoc.createParagraph();
                addCompanyText(companyParagraph, company, mode);
            }
            wordDoc.createStyles();
            File wordFile = writeToFile(wordDoc);
            inputStream.close();
            return wordFile;
        }
        catch (IOException ex){
            ex.printStackTrace();
            try {
                return Files.createTempFile("empty-", ".docx").toFile();
            }
            catch (IOException exception){
                exception.printStackTrace();
            }
        }
        return null;
    }

    public static File createPdfFile(List<CompanyDAO> companies, String mode){
        try {
            File wordTempFile = createWordFile(companies,mode);
            FileInputStream inputStream = new FileInputStream(wordTempFile);
            XWPFDocument wordDoc = new XWPFDocument(inputStream);
            File pdfTempFile = Files.createTempFile("pdf-", ".pdf").toFile();
            FileOutputStream outputStream = new FileOutputStream(pdfTempFile);
            PdfOptions options = PdfOptions.create();
            PdfConverter.getInstance().convert(wordDoc, outputStream ,options);
            inputStream.close();
            outputStream.close();
            return pdfTempFile;
        }
        catch (IOException ex){
            ex.printStackTrace();
            try {
                return Files.createTempFile("empty-", ".pdf").toFile();
            }
            catch (IOException exception){
                exception.printStackTrace();
            }
        }
        return null;
    }




    private static File writeToFile(XWPFDocument wordDoc){
        try {
            File tempFile = File.createTempFile("word-", ".docx");
            FileOutputStream out = new FileOutputStream(tempFile);
            wordDoc.write(out);
            out.close();
            return tempFile;
        }
        catch (IOException ex){
            ex.printStackTrace();
            return null;
        }
    }

    private static void modifyTitle(XWPFParagraph titleParagraph){
        titleParagraph.setAlignment(ParagraphAlignment.CENTER);
        XWPFRun titleRun = titleParagraph.createRun();
        titleRun.setText("Список компаний");
        titleRun.setBold(true);
        titleRun.setFontFamily("Times New Roman");
        titleRun.setFontSize(20);
    }

    private static void addCompanyText(XWPFParagraph companyParagraph, CompanyDAO company, String mode){
        companyParagraph.setAlignment(ParagraphAlignment.LEFT);
        XWPFRun mainTextRun = companyParagraph.createRun();
        if (mode.equalsIgnoreCase("admin")) {
            getCompanyAsTextAdmin(mainTextRun, company);
        }
        else{
            getCompanyAsText(mainTextRun, company, mode);
        }
        mainTextRun.setFontFamily("Times New Roman");
        mainTextRun.setFontSize(14);
    }

    private static void getCompanyAsText(XWPFRun mainTextRun, CompanyDAO company, String mode){
        StringBuilder builder = new StringBuilder();
        builder.append("ID ").append(company.getId());
        mainTextRun.setText(builder.toString());
        mainTextRun.addBreak();
        builder.setLength(0);
        if(mode.equalsIgnoreCase("full")) {
            builder.append(company.getForm()).append(" ").append(company.getCompanyName());
            mainTextRun.setText(builder.toString());
            mainTextRun.addBreak();
            builder.setLength(0);
            builder.append("ИНН ").append(company.getInn());
            mainTextRun.setText(builder.toString());
            mainTextRun.addBreak();
            builder.setLength(0);
        }
        builder.append("Город ").append(company.getCity());
        mainTextRun.setText(builder.toString());
        mainTextRun.addBreak();
        builder.setLength(0);
        builder.append(company.getSno());
        mainTextRun.setText(builder.toString());
        mainTextRun.addBreak();
        builder.setLength(0);
        if(mode.equalsIgnoreCase("full")) {
            builder.append("Регистрация ").append(company.getDateString());
        }
        else{
            builder.append("Регистрация ").append(company.getRegistrationYear());
        }
        mainTextRun.setText(builder.toString());
        mainTextRun.addBreak();
        builder.setLength(0);
        String bankAccounts = company.getBankAccounts();
        if (!bankAccounts.equalsIgnoreCase("Нет")) {
            builder.append("Счета ").append(bankAccounts);
            mainTextRun.setText(builder.toString());
            mainTextRun.addBreak();
            builder.setLength(0);
        }
        builder.append("Адрес ").append(company.getAddress());
        mainTextRun.setText(builder.toString());
        mainTextRun.addBreak();
        builder.setLength(0);
        builder.append("ОКВЭД ").append(company.getOkvedString());
        mainTextRun.setText(builder.toString());
        mainTextRun.addBreak();
        builder.setLength(0);
        String cpo = company.getCpo();
        if(!cpo.equalsIgnoreCase("Нет")) {
            builder.append("СРО ").append(cpo);
            mainTextRun.setText(builder.toString());
            mainTextRun.addBreak();
            builder.setLength(0);
        }
        String licences = company.getLicensesString();
        if(!licences.equalsIgnoreCase("Нет")) {
            builder.append("Лицензии ").append(licences);
            mainTextRun.setText(builder.toString());
            mainTextRun.addBreak();
            builder.setLength(0);
        }
        if (company.getOborot().equalsIgnoreCase("Нет")) {
            builder.append("Без оборотов");
        }
        else {
            builder.append("С оборотами");
        }
        mainTextRun.setText(builder.toString());
        mainTextRun.addBreak();
        builder.setLength(0);
        builder.append("Учредителей ").append(company.getFounders());
        mainTextRun.setText(builder.toString());
        mainTextRun.addBreak();
        builder.setLength(0);
        builder.append("Сотрудников: ").append(company.getWorkersCount());
        mainTextRun.setText(builder.toString());
        mainTextRun.addBreak();
        builder.setLength(0);
        String comments = company.getComment();
        if (!comments.isBlank()) {
            builder.append("Комментарии: ").append(company.getComment());
            mainTextRun.setText(builder.toString());
            mainTextRun.addBreak();
            builder.setLength(0);
        }
        builder.append("Цена: ").append(company.getBudget());
        mainTextRun.setText(builder.toString());
        mainTextRun.addBreak();
        builder.setLength(0);
    }


    private static void getCompanyAsTextAdmin(XWPFRun mainTextRun, CompanyDAO company) {
        StringBuilder builder = new StringBuilder();
        builder.append("ID: ").append(company.getId());
        mainTextRun.setText(builder.toString());
        mainTextRun.addBreak();
        builder.setLength(0);
        builder.append("Цена: ").append(company.getBudget());
        mainTextRun.setText(builder.toString());
        mainTextRun.addBreak();
        mainTextRun.addBreak();
        builder.setLength(0);
        builder.append("СНО: ").append(company.getSno());
        mainTextRun.setText(builder.toString());
        mainTextRun.addBreak();
        builder.setLength(0);
        builder.append("Город: ").append(company.getCity());
        mainTextRun.setText(builder.toString());
        mainTextRun.addBreak();
        builder.setLength(0);
        builder.append("ИНН: ").append(company.getInn());
        mainTextRun.setText(builder.toString());
        mainTextRun.addBreak();
        builder.setLength(0);
        builder.append("Название: ").append(company.getCompanyName());
        mainTextRun.setText(builder.toString());
        mainTextRun.addBreak();
        builder.setLength(0);
        builder.append("Дата регистрации: ").append(company.getDateString());
        mainTextRun.setText(builder.toString());
        mainTextRun.addBreak();
        builder.setLength(0);
        String bankAccounts = company.getBankAccounts();
        if (!bankAccounts.equalsIgnoreCase("Нет")) {
            builder.append("Счета в банках: ").append(bankAccounts);
            mainTextRun.setText(builder.toString());
            mainTextRun.addBreak();
            builder.setLength(0);
        }
        String oborot = company.getOborot();
        if (!oborot.equalsIgnoreCase("Нет")) {
            builder.append("Обороты: ").append(oborot);
            mainTextRun.setText(builder.toString());
            mainTextRun.addBreak();
            builder.setLength(0);
        }
        builder.append("Адрес: ").append(company.getAddress());
        mainTextRun.setText(builder.toString());
        mainTextRun.addBreak();
        builder.setLength(0);
        builder.append("Адрес можно оставить: ").append(company.getKeepAddress());
        mainTextRun.setText(builder.toString());
        mainTextRun.addBreak();
        builder.setLength(0);
        builder.append("ОКВЭД: ").append(company.getOkvedString());
        mainTextRun.setText(builder.toString());
        mainTextRun.addBreak();
        builder.setLength(0);
        builder.append("Налоговая: ").append(company.getNalog());
        mainTextRun.setText(builder.toString());
        mainTextRun.addBreak();
        builder.setLength(0);
        builder.append("Отчетность: ").append(company.getReport());
        mainTextRun.setText(builder.toString());
        mainTextRun.addBreak();
        builder.setLength(0);
        String ecp = company.getEcp();
        if (!ecp.equalsIgnoreCase("Нет")) {
            builder.append("ЭЦП: ").append(ecp);
            mainTextRun.setText(builder.toString());
            mainTextRun.addBreak();
            builder.setLength(0);
        }
        String cpo = company.getCpo();
        if (!cpo.equalsIgnoreCase("Нет")) {
            builder.append("СРО: ").append(cpo);
            mainTextRun.setText(builder.toString());
            mainTextRun.addBreak();
            builder.setLength(0);
        }
        String licences = company.getLicensesString();
        if (!licences.equalsIgnoreCase("Нет")) {
            builder.append("Лицензии: ").append(licences);
            mainTextRun.setText(builder.toString());
            mainTextRun.addBreak();
            builder.setLength(0);
        }
        String goszakaz = company.getGoszakaz();
        if (!goszakaz.equalsIgnoreCase("Нет")) {
            builder.append("Гос. контракты: ").append(company.getGoszakaz());
            mainTextRun.setText(builder.toString());
            mainTextRun.addBreak();
            builder.setLength(0);
        }
        builder.append("Учредители: ").append(company.getFounders());
        mainTextRun.setText(builder.toString());
        mainTextRun.addBreak();
        builder.setLength(0);
        builder.append("Работников: ").append(company.getWorkersCount());
        mainTextRun.setText(builder.toString());
        mainTextRun.addBreak();
        builder.setLength(0);
        String comments = company.getComment();
        if (!comments.isBlank()) {
            builder.append("Комментарии: ").append(company.getComment());
            mainTextRun.setText(builder.toString());
            mainTextRun.addBreak();
            builder.setLength(0);
        }
        builder.append("Телефон: ").append(company.getMobile());
        mainTextRun.setText(builder.toString());
        mainTextRun.addBreak();
        builder.setLength(0);
        String notes = company.getNotesString();
        if (!notes.isBlank()) {
            builder.append("Примечания: ").append(company.getNotesString());
            mainTextRun.setText(builder.toString());
            mainTextRun.addBreak();
            builder.setLength(0);
        }
    }
}
