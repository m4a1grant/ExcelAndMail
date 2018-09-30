package logic.excel;

import logic.entity.DailyExchangeRate;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;

public class DataProvider {

    private static String DATE_FORMAT = "dd.MM.yyyy";

    public static List<String> getDateList(String start, String end){
        DateTimeFormatter dtf = DateTimeFormatter.ofPattern(DATE_FORMAT);
        LocalDate startDate = LocalDate.parse(start, dtf);
        LocalDate endDate = LocalDate.parse(end, dtf);
        List<String> listDates = new ArrayList<>();
        while (!startDate.isAfter(endDate)){
            listDates.add(startDate.format(dtf));
            startDate = startDate.plusDays(1);
        }
        return listDates;
    }
}
