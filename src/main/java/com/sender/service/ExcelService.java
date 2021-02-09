package com.sender.service;

import com.sender.dao.CompanyDAO;
import jxl.Workbook;
import jxl.write.*;

import java.io.File;
import java.util.List;

public class ExcelService {

    private static File directoryPath = new File("./excel/");

    public static File createExcelFile(List<CompanyDAO> companies, String mode){
        try {
            if(!directoryPath.exists()){
                directoryPath.createNewFile();
            }
            File file = new File(directoryPath + "tmp.xls");
            WritableWorkbook workbook = Workbook.createWorkbook(file);
            WritableSheet sheet = workbook.createSheet("Sheet 1", 0);
            switch (mode){
                case("admin"):
                    setHeadersAdmin(sheet);
                    break;
                case("client"):
                    setHeadersClient(sheet);
                    break;
                case ("full"):
                    setHeadersClientFull(sheet);
                    break;
            }
            setData(sheet, companies, mode);
            workbook.write();
            workbook.close();
            return file;
        }
        catch (Exception ex){
            ex.printStackTrace();
        }
        return null;
    }

    private static void setHeadersAdmin(WritableSheet sheet) {
        try {
            WritableCellFormat headerFormat = new WritableCellFormat();
            WritableFont font
                    = new WritableFont(WritableFont.ARIAL, 12, WritableFont.BOLD);
            headerFormat.setFont(font);
            headerFormat.setWrap(true);
            List<String> headers = List.of(
                    "id", "Цена", "Цена продавца", "Теги", "Имя",
                    "Телефон", "E-mail", "Форма собственности",
                    "Название", "ИНН", "Город",
                    "СНО", "Дата регистрации", "Год регистрации",
                    "Счета в банках", "Обороты", "Юр. адрес",
                    "Адрес можно оставить", "Адрес отметка", "Налоговая",
                    "ОКВЭД", "Отчетность",
                    "ЭЦП", "СРО", "Лицензии", "Гос. контракты",
                    "Кол-во работников", "Ликвидация",
                    "Долги", "В браке", "Учредителей",
                    "Собственник","Комментарий","Примечания");
            Label headerLabel;
            for (int i = 0; i<headers.size(); i++) {
                headerLabel = new Label(i, 0, headers.get(i), headerFormat);
                sheet.addCell(headerLabel);
            }
        }catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    private static void setHeadersClient(WritableSheet sheet) {
        try {
            WritableCellFormat headerFormat = new WritableCellFormat();
            WritableFont font
                    = new WritableFont(WritableFont.ARIAL, 12, WritableFont.BOLD);
            headerFormat.setFont(font);
            headerFormat.setWrap(true);
            List<String> headers = List.of(
                    "id", "Цена", "Город",
                    "СНО", "Год регистрации",
                    "Счета в банках", "Обороты", "Юр. адрес",
                    "Адрес можно оставить", "Адрес отметка", "Налоговая",
                    "ОКВЭД", "Отчетность", "ЭЦП",
                    "СРО", "Лицензии", "Гос. контракты",
                    "Ликвидация", "Долги", "Учредителей",
                    "Собственник", "Комментарий");
            Label headerLabel;
            for (int i = 0; i<headers.size(); i++) {
                headerLabel = new Label(i, 0, headers.get(i), headerFormat);
                sheet.addCell(headerLabel);
            }
        }catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    private static void setHeadersClientFull(WritableSheet sheet) {
        try {
            WritableCellFormat headerFormat = new WritableCellFormat();
            WritableFont font
                    = new WritableFont(WritableFont.ARIAL, 12, WritableFont.BOLD);
            headerFormat.setFont(font);
            headerFormat.setWrap(true);
            List<String> headers = List.of(
                    "id", "ИНН", "Цена", "Город",
                    "СНО", "Дата регистрации",
                    "Счета в банках", "Обороты", "Юр. адрес",
                    "Адрес можно оставить", "Адрес отметка", "Налоговая",
                    "ОКВЭД", "Отчетность", "ЭЦП",
                    "СРО", "Лицензии", "Гос. контракты",
                    "Ликвидация", "Долги", "Учредителей",
                    "Собственник", "Комментарий");
            Label headerLabel;
            for (int i = 0; i<headers.size(); i++) {
                headerLabel = new Label(i, 0, headers.get(i), headerFormat);
                sheet.addCell(headerLabel);
            }
        }catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    private static void setData(WritableSheet sheet, List<CompanyDAO> companies, String mode){
        try{
            WritableCellFormat cellFormat = new WritableCellFormat();
            cellFormat.setWrap(true);
            Label cellLabel;
            List<String> parameters;
            List<String> notes;
            for (int i = 0; i<companies.size(); i++){
                if(mode.equalsIgnoreCase("admin")) {
                    parameters = companies.get(i).getCompanyAsListOfParametersAdmin();
                }
                else if(mode.equalsIgnoreCase("client")){
                    parameters = companies.get(i).getCompanyAsListOfParametersClient();
                }
                else{
                    parameters = companies.get(i).getCompanyAsListOfParametersClientFull();

                }
                for (int j = 0; j<parameters.size(); j++){
                    cellLabel = new Label(j, i+1, parameters.get(j), cellFormat);
                    sheet.addCell(cellLabel);
                }
                if (mode.equalsIgnoreCase("admin")){
                    notes = companies.get(i).getNotesAsList();
                    for(int j = 0; j<notes.size(); j++){
                        cellLabel = new Label(parameters.size() + j, i+1, notes.get(j), cellFormat);
                        sheet.addCell(cellLabel);
                    }
                }
            }
        }
        catch (Exception ex){
            ex.printStackTrace();
        }
    }

}
