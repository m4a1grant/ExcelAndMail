package logic.excel;

import com.google.gson.Gson;
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

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
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

public class CreatorXLS {

    private static final int HEADER_ROW = 0;
    private static final int DATE_COLUMN = 0;
    private static final int SELL_COLUMN = 1;
    private static final int PURCHASE_COLUMN = 2;
    private static final String FONT_NAME = "Arial";
    private static String DATE_FORMAT = "dd.MM.yyyy";
    private static final String[] SHEET_NAMES = {"CAD", "CZK", "ILS", "JPY", "NOK", "CHF", "GBP", "USD", "EUR", "PLZ"};
    private static final String URL_EXCHANGE_RATES = "https://api.privatbank.ua/p24api/exchange_rates?json";

    Workbook wb;

    public void createXLS (String firstDate, String lastDate, String fileName){

        wb = new XSSFWorkbook();
        createSheets(prepareData(firstDate, lastDate));
        drawCharts();
        try(OutputStream out = new FileOutputStream(fileName)){
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
        for (String name : SHEET_NAMES){
            sheet = wb.createSheet(name);
            row = sheet.createRow(HEADER_ROW);
            row.setRowStyle(style);
            row.createCell(DATE_COLUMN).setCellValue("Date");
            row.createCell(SELL_COLUMN).setCellValue("Sell Rate");
            row.createCell(PURCHASE_COLUMN).setCellValue("Purchase Rate");
        }
        System.out.println("Sheets created!");
        fillSheets(der);
    }


    private void fillSheets(List<DailyExchangeRate> der){
        for(DailyExchangeRate temp : der){
            for (ExchangeRate er : temp.getExchangeRate()){
                Sheet sheet = wb.getSheet(er.getCurrency());
                if (sheet != null){
                    Row row = sheet.createRow(sheet.getLastRowNum() + 1);
                    row.createCell(DATE_COLUMN).setCellValue(temp.getDate());
                    row.createCell(SELL_COLUMN).setCellValue(er.getSaleRate());
                    row.createCell(PURCHASE_COLUMN).setCellValue(er.getPurchaseRate());
                }
            }
        }
    }

    private void drawCharts(){
        for(Sheet sheet : wb){
            Drawing drawing = sheet.createDrawingPatriarch();
            ClientAnchor anchor = drawing.createAnchor(0,0,0,0,5, 1, 20, 12);
            Chart chart = drawing.createChart(anchor);
            ChartLegend legend = chart.getOrCreateLegend();
            legend.setPosition(LegendPosition.TOP_RIGHT);
            LineChartData data = chart.getChartDataFactory().createLineChartData();
            ChartAxis bottomAxis = chart.getChartAxisFactory().createCategoryAxis(AxisPosition.BOTTOM);
            ValueAxis leftAxis = chart.getChartAxisFactory().createValueAxis(AxisPosition.LEFT);
            leftAxis.setCrosses(AxisCrosses.AUTO_ZERO);

            ChartDataSource<String> date = DataSources.fromStringCellRange(sheet,
                    new CellRangeAddress(1, sheet.getLastRowNum(), DATE_COLUMN, DATE_COLUMN));
            ChartDataSource<Number> sell = DataSources.fromNumericCellRange(sheet,
                    new CellRangeAddress(1, sheet.getLastRowNum(), SELL_COLUMN, SELL_COLUMN));
            ChartDataSource<Number> purchase = DataSources.fromNumericCellRange(sheet,
                    new CellRangeAddress(1, sheet.getLastRowNum(), PURCHASE_COLUMN, PURCHASE_COLUMN));
            LineChartSeries series1 = data.addSeries(date, sell);
            series1.setTitle("Sell");
            LineChartSeries series2 = data.addSeries(date, purchase);
            series2.setTitle("Purchase");
            chart.plot(data, bottomAxis, leftAxis);
        }
    }

    private List<DailyExchangeRate> prepareData (String start, String end){
        DateTimeFormatter dtf = DateTimeFormatter.ofPattern(DATE_FORMAT);
        LocalDate startDate = LocalDate.parse(start, dtf);
        LocalDate endDate = LocalDate.parse(end, dtf);
        List<String> dates = new ArrayList<>();
        while (!startDate.isAfter(endDate)){
            dates.add(startDate.format(dtf));
            startDate = startDate.plusDays(1);
        }
        Map<String, String> queryParms= new HashMap<>();
        PBAPI pb = new PBAPI();

        List<DailyExchangeRate> der;

        der = dates.stream()
                .map(x -> {
                    Map<String, String> parms = new HashMap<>();
                    parms.put("date", x);
                    return parms;
                })
                .map(x -> pb.getDailyExchangeRate(new Gson(), URL_EXCHANGE_RATES, x))
                .peek(x -> System.out.println("DER : " + x.getDate()))
                .collect(Collectors.toList());

        return der;
    }

}
