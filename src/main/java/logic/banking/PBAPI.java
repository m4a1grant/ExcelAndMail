package logic.banking;

/**
 * @author Serhiy.K.Dubovenko
 */

import com.google.gson.Gson;
import com.jayway.restassured.RestAssured;
import com.jayway.restassured.response.Response;
import logic.entity.DailyExchangeRate;
import java.util.Map;

public class PBAPI implements Banking{

    /**
     *
     * @param gson - The instance of Gson. It will used for parsing response body.
     * @param sURL - https://api.privatbank.ua/p24api/exchange_rates?json&date=01.12.2014.
     * @param queryParms - date in format dd.MM.yyyy. (Example: 01.12.2014)
     * @return - object of DailyExchangeRate.java wich will contain  info about exchange rates.
     */

    public DailyExchangeRate getDailyExchangeRate(Gson gson, String sURL, Map<String, String> queryParms ){
        Response response =  RestAssured.expect().statusCode(200).given()
                .queryParameters(queryParms)
                //.log().all()
                .when()
                .get(sURL)
                .then().assertThat()
                //.log().all()
                .extract().response();
        //System.out.println(response.getBody().asString());
        return gson.fromJson(response.getBody().asString(), DailyExchangeRate.class);
    }

}
