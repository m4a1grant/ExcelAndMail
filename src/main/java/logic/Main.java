package logic;

import com.google.gson.Gson;
import logic.banking.PBAPI;
import logic.entity.DailyExchangeRate;
import logic.excel.CreatorXLS;
import logic.excel.DataProvider;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * @author Serhiy.K.Dubovenko
 */
public class Main {

    public static final String URL_EXCHANGE_RATES = "https://api.privatbank.ua/p24api/exchange_rates?json";

     private static final String FIRST_DATE = "01.01.2016";
     private static final String LAST_DATE = "31.12.2017";

    public static void main(String[] args){
        Map<String, String> queryParms= new HashMap<>();
        PBAPI pb = new PBAPI();
        //queryParms.put("date", String.valueOf("01.12.2014"));
        //DailyExchangeRate dailyExchangeRate = pb.getDailyExchangeRate(new Gson(), URL_EXCHANGE_RATES,  queryParms );
        //System.out.println("Daily exchange rate: "+ dailyExchangeRate);
        List<DailyExchangeRate> dailyExchangeRates = new ArrayList<>();
        List<String> dateList = DataProvider.getDateList(FIRST_DATE, LAST_DATE);
/*        for (String date : dateList){
            queryParms.put("date", date);
            dailyExchangeRates.add(pb.getDailyExchangeRate(new Gson(), URL_EXCHANGE_RATES, queryParms));
        }*/

        dailyExchangeRates = dateList.parallelStream()
                .map(x -> {
                    Map<String, String> parms= new HashMap<>();
                    parms.put("date", x);
                    return parms;
                })
                .map(x -> pb.getDailyExchangeRate(new Gson(), URL_EXCHANGE_RATES, x))
                .peek(x -> System.out.println("DER : " + x.getDate()))
                .collect(Collectors.toList());

        CreatorXLS creator = new CreatorXLS();
        creator.createXLS(dailyExchangeRates);


    }
}
