package logic.excel;

import logic.entity.DailyExchangeRate;
import logic.entity.ExchangeRate;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.*;
import org.w3c.dom.ls.LSInput;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;

public class CreatorXLS {

    private static final int HEADER_ROW = 0;
    private static final int DATE_COLUMN = 0;
    private static final int SELL_COLUMN = 1;
    private static final int PURCHASE_COLUMN = 2;
    private static final String FONT_NAME = "Arial";

    Workbook wb;

    public void createXLS (List <DailyExchangeRate> der){
        wb = new HSSFWorkbook();
        createSheets(der);
        fillSheets(der);
        try(OutputStream out = new FileOutputStream("Result.xls")){
            wb.write(out);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void createSheets(List <DailyExchangeRate> der){
        Sheet sheet;
        Row row;
        CellStyle style = wb.createCellStyle();
        Font font = wb.createFont();
        font.setFontName(FONT_NAME);
        font.setFontHeightInPoints((short) 10);
        font.setBold(true);
        style.setFont(font);
        for (ExchangeRate er : der.get(0).getExchangeRate()){
            sheet = wb.createSheet(er.getCurrency());
            row = sheet.createRow(HEADER_ROW);
            row.setRowStyle(style);
            row.createCell(DATE_COLUMN).setCellValue("Date");
            row.createCell(SELL_COLUMN).setCellValue("Sell Rate");
            row.createCell(PURCHASE_COLUMN).setCellValue("Purchase Rate");
        }
    }

/*
    private void fillSheetsStream(List<DailyExchangeRate> der){
        der.stream()
                .map(x -> x.getExchangeRate())
                .flatMap(x -> x.stream())
                .map(x -> x.)
                .forEach(x -> );
    }

    private void fillRowStream(ExchangeRate er){
        Sheet sheet = wb.getSheet(er.getCurrency());
        Row row = sheet.createRow(sheet.getLastRowNum() + 1);
    }
*/

    private void fillSheets(List<DailyExchangeRate> der){
        Sheet sheet;
        Row row;
        for(DailyExchangeRate temp : der){
            for (ExchangeRate er : temp.getExchangeRate()){
                sheet = wb.getSheet(er.getCurrency());
                row = sheet.createRow(sheet.getLastRowNum() + 1);
                row.createCell(DATE_COLUMN).setCellValue(temp.getDate());
                row.createCell(SELL_COLUMN).setCellValue(er.getSaleRate());
                row.createCell(PURCHASE_COLUMN).setCellValue(er.getPurchaseRate());
            }
        }
    }

}
