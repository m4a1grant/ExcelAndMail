package logic.banking;

import com.google.gson.Gson;
import logic.entity.DailyExchangeRate;

import java.util.Map;

/**
 * @author Serhiy.K.Dubovenko
 */
public interface Banking {

    DailyExchangeRate getDailyExchangeRate(Gson gson, String sURL, Map<String, String> queryParms );
}
