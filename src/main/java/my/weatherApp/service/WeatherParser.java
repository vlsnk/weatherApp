package my.weatherApp.service;

import com.vaadin.external.org.slf4j.Logger;
import com.vaadin.external.org.slf4j.LoggerFactory;
import my.weatherApp.model.Error;
import my.weatherApp.model.LogEvent;
import my.weatherApp.model.Weather;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class WeatherParser {

    private static final Logger LOG = LoggerFactory.getLogger(WeatherParser.class);
    private final static String RESPONSE_CITY_CODE = "id";
    private final static String RESPONSE_CITY = "city";
    private final static String RESPONSE_MAIN = "main";
    private final static String TEMP = "temp";
    private final static String LIST = "list";
    private final static String DT_TEXT = "dt_txt";
    private static final String SERVICE_NAME = "WEATHER";
    private static ErrorService errorService = ErrorService.getInstance();


    /**
     * Convert json response to Weather object
     *
     * @param weatherObj - today weather json response
     * @param forecastObj - tomorrow weather json response
     * @return Weather object
     */
    public static Weather getWeather(JSONObject weatherObj, JSONObject forecastObj){
        try {
            LOG.info(LogEvent.create(SERVICE_NAME, "Convert json response to Weather"));
            int id = weatherObj.getInt(RESPONSE_CITY_CODE);
            int idForecast = forecastObj.optJSONObject(RESPONSE_CITY).optInt(RESPONSE_CITY_CODE);
            if (id != idForecast) {
                return null;
            }
            String temp = weatherObj.optJSONObject(RESPONSE_MAIN).optString(TEMP);
            JSONArray forecastList = forecastObj.optJSONArray(LIST);
            JSONObject forecast = getForecast(forecastList);
            String tempForecast = forecast.optJSONObject(RESPONSE_MAIN).optString(TEMP);
            Weather w = new Weather(id, temp, tempForecast, LocalDateTime.now());
            return w;
        } catch (JSONException e) {
            String msg = "Error to read json response: " + e.toString();
            Error error = new Error(SERVICE_NAME, msg, e);
            errorService.error(error);
            LOG.error(error.toString());
        }
        return null;
    }

    static String getTomorrow(){
        DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        LocalDate today = LocalDate.now();
        LocalDate tomorrowDt = today.plusDays(1);
        return tomorrowDt.format(dateTimeFormatter);
    }

    static JSONObject getForecast(JSONArray jsonArray) {
        String tomorrow = getTomorrow() + " 12:00:00";
        for (int i = 1; i < jsonArray.length(); i++) {
            JSONObject o = jsonArray.optJSONObject(i);
            String date = o.optString(DT_TEXT);
            if (date.equalsIgnoreCase(tomorrow)) {
                return o;
            }
        }
        return null;
    }
}
