package logic;

import com.google.gson.Gson;
import logic.banking.PBAPI;
import logic.entity.DailyExchangeRate;

import java.util.HashMap;
import java.util.Map;

/**
 * @author Serhiy.K.Dubovenko
 */
public class Main {

    public static final String URL_EXCHANGE_RATES = "https://api.privatbank.ua/p24api/exchange_rates?json";

    public static void main(String[] args){
        Map<String, String> queryParms= new HashMap<>();
        queryParms.put("date", String.valueOf("01.12.2014"));
        PBAPI pb = new PBAPI();
        DailyExchangeRate dailyExchangeRate = pb.getDailyExchangeRate(new Gson(), URL_EXCHANGE_RATES,  queryParms );

        System.out.println("Daily exchange rate: "+ dailyExchangeRate);
    }
}
