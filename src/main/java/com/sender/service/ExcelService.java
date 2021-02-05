package com.sender.service;

import com.sender.dao.CompanyDAO;
import jxl.Workbook;
import jxl.write.*;

import java.io.File;
import java.util.List;

public class ExcelService {

    private static File directoryPath = new File("./excel/");

    public static File createExcelFile(List<CompanyDAO> companies){
        try {
            if(!directoryPath.exists()){
                directoryPath.createNewFile();
            }
            File file = new File(directoryPath + "tmp.xls");
            WritableWorkbook workbook = Workbook.createWorkbook(file);
            WritableSheet sheet = workbook.createSheet("Sheet 1", 0);
            setHeaders(sheet);
            setData(sheet, companies);
            workbook.write();
            workbook.close();
            return file;
        }
        catch (Exception ex){
            ex.printStackTrace();
        }
        return null;
    }

    private static void setHeaders(WritableSheet sheet) {
        try {
            WritableCellFormat headerFormat = new WritableCellFormat();
            WritableFont font
                    = new WritableFont(WritableFont.ARIAL, 12, WritableFont.BOLD);
            headerFormat.setFont(font);
            headerFormat.setWrap(true);
            List<String> headers = List.of(
                    "id", "Теги", "Имя",
                    "Телефон", "E-mail", "Форма собственности",
                    "Название", "ИНН", "Город",
                    "СНО", "Дата регистрации", "Год регистрации",
                    "Счета в банках", "Обороты", "Юр. адрес",
                    "Адрес можно оставить", "Заметка по адресу", "Налоговая",
                    "Лицензии", "ОКВЭД", "Отчетность",
                    "ЭЦП", "СРО", "Гос. контракты",
                    "Кол-во работников", "Ликвидация", "Цена продавца",
                    "Долги", "В браке", "Комментарий",
                    "Должность");
            Label headerLabel;
            for (int i = 0; i<headers.size(); i++) {
                headerLabel = new Label(i, 0, headers.get(i), headerFormat);
                sheet.addCell(headerLabel);
            }
        }catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    private static void setData(WritableSheet sheet, List<CompanyDAO> companies){
        try{
            WritableCellFormat cellFormat = new WritableCellFormat();
            cellFormat.setWrap(true);
            Label cellLabel;
            List<String> parameters;
            for (int i = 0; i<companies.size(); i++){
                parameters = companies.get(i).getCompanyAsListOfParameters();
                System.out.println(parameters);
                for (int j = 0; j<parameters.size(); j++){
                    cellLabel = new Label(j, i+1, parameters.get(j), cellFormat);
                    sheet.addCell(cellLabel);
                }
            }
        }
        catch (Exception ex){
            ex.printStackTrace();
        }

    }

}
