package logic.excel;

import com.google.gson.Gson;
import jdk.internal.util.xml.impl.Input;
import logic.banking.PBAPI;
import logic.entity.DailyExchangeRate;
import logic.entity.ExchangeRate;
import org.apache.commons.collections4.list.TreeList;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.ss.usermodel.charts.*;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.w3c.dom.ls.LSInput;

import java.io.*;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * @author Vladimir Nagornyi
 */


/**
 *
 */
public class CreatorXLS {

    //private static final int HEADER_ROW = 0;
    private static final int DATE_COLUMN = 0;
    private static final int SELL_COLUMN = 1;
    private static final int PURCHASE_COLUMN = 2;
    private static final String FONT_NAME = "Arial";
    private static String DATE_FORMAT = "dd.MM.yyyy";
    //private static final String[] SHEET_NAMES = {"CAD", "CZK", "ILS", "JPY", "NOK", "CHF", "GBP", "USD", "EUR", "PLZ", "RUB"};
    private static final String URL_EXCHANGE_RATES = "https://api.privatbank.ua/p24api/exchange_rates?json";
    private static final String TEMPLATE_FILE = "settings\\Template.xls";

    Workbook wb;


    /**
     * Use PBAPI and template file to get exchange rates and write to file.
     *
     * @param firstDate - date in format dd.MM.yyyy.
     * @param lastDate - date in format dd.MM.yyyy.
     * @param fileName - the result will be written here.
     * @throws IOException
     */
    public void createXLS (String firstDate, String lastDate, String fileName) throws IOException{
        try(InputStream in = new FileInputStream(TEMPLATE_FILE);
            OutputStream out = new FileOutputStream(fileName)){
            wb = new HSSFWorkbook(in);
            fillFile(firstDate, lastDate);
            wb.write(out);
        } catch (FileNotFoundException e) {
            System.out.println(TEMPLATE_FILE + " not found. Please, put template file in settings directory.");
            //e.printStackTrace();
        }
    }

    private List<String> getDates (String firstDate, String lastDate){
        DateTimeFormatter dtf = DateTimeFormatter.ofPattern(DATE_FORMAT);
        LocalDate startDate = LocalDate.parse(firstDate, dtf);
        LocalDate endDate = LocalDate.parse(lastDate, dtf);
        List<String> dates = new ArrayList<>();
        while (!startDate.isAfter(endDate)){
            dates.add(startDate.format(dtf));
            startDate = startDate.plusDays(1);
        }
        return dates;
    }

    private void fillFile(String firstDate, String lastDate){
        List<String> dates = getDates(firstDate, lastDate);
        dates.stream()
                .map(x -> {
                    Map<String, String> params = new HashMap<>();
                    params.put("date", x);
                    PBAPI pb = new PBAPI();
                    return pb.getDailyExchangeRate(new Gson(), URL_EXCHANGE_RATES,
                            params);
                })
                .peek(x -> System.out.println(x.getDate()))
                .forEach(this::fillSheets);
    }

    private void fillSheets(DailyExchangeRate der){
        for (ExchangeRate er : der.getExchangeRate()){
            if (er.getSaleRate() > 0 && er.getPurchaseRate() > 0){
                Sheet sheet = wb.getSheet(er.getCurrency());
                if (sheet != null){
                    Row row = sheet.createRow(sheet.getLastRowNum() + 1);
                    row.createCell(DATE_COLUMN).setCellValue(der.getDate());
                    row.createCell(SELL_COLUMN).setCellValue(er.getSaleRate());
                    row.createCell(PURCHASE_COLUMN).setCellValue(er.getPurchaseRate());
                }
            }
        }
    }
}
