package my.weatherApp.service;

import com.vaadin.external.org.slf4j.Logger;
import com.vaadin.external.org.slf4j.LoggerFactory;
import my.weatherApp.dao.WeatherDao;
import my.weatherApp.dao.WeatherDaoImpl;
import my.weatherApp.model.City;
import my.weatherApp.model.Error;
import my.weatherApp.model.Weather;
import org.json.JSONException;
import org.json.JSONObject;
import java.io.*;
import java.time.LocalDateTime;
import java.util.*;

public class WeatherServiceImpl implements WeatherService {

    private static final Logger LOG = LoggerFactory.getLogger(WeatherServiceImpl.class);
    private static WeatherServiceImpl instance;
    Map<City, Weather> weatherMap = new HashMap<>();
    private WeatherDao weatherDao;
    private ErrorService errorService;
    private WeatherConnector weatherConnector;
    private static final String SERVICE_NAME = "WEATHER";

    private final static String ERR_WEATHER_SERVICE_DISCONNECT =
            "Weather service is unavailable! please, try again later";

    private WeatherServiceImpl(){
        weatherDao = WeatherDaoImpl.getInstance();
        errorService = ErrorService.getInstance();
        weatherConnector = WeatherConnector.getInstance();
    }

    public static WeatherServiceImpl getInstance(){
        if (instance == null) {
            instance = new WeatherServiceImpl();
        }
        return instance;
    }
    @Override
    public Weather getWeather(City city) {
        if (!weatherMap.containsKey(city) || isOld(city)){
            Weather w = requestFromDB(city);
//            if (w == null) {
//                this.weatherMap.remove(city);
//                errorService.sendMessage(ERR_WEATHER_SERVICE_DISCONNECT);
//                return new Weather(city.getCode(), "-", "-", LocalDateTime.now().minusDays(1));
//            } else {
//                errorService.clearMessage();
//            }
            return w;
        }
        return weatherMap.get(city);

    }

    @Override
    public Weather getEmptyWeather() {
        return new Weather();
    }

    Weather requestFromDB(City city) {
        Weather weather = weatherDao.getWeather(city);
        weatherMap.put(city, weather);
        if (weather == null || isOld(city)) {
            try {
                JSONObject weatherObj = weatherConnector.requestWeather(city.getCode());
                JSONObject forecastObj = weatherConnector.requestForecast(city.getCode());
                if (weatherObj == null || forecastObj == null){
                    return sendEmptyWeather(city);
                }
                Weather newWeather = WeatherParser.getWeather(weatherObj, forecastObj);

                if (newWeather == null) {
                    return sendEmptyWeather(city);
                } else {
//                    errorService.clearMessage();
                    if (weather == null) weatherDao.addWeather(newWeather);
                    else weatherDao.update(newWeather);
                    weatherMap.put(city, newWeather);
                }
            } catch (IOException|JSONException e) {
                Error error = new Error(SERVICE_NAME, e.toString(), e);
                errorService.error(error);
                LOG.error(error.toString());
//                errorService.sendMessage(ERR_WEATHER_SERVICE_DISCONNECT);
                return sendEmptyWeather(city);
            }
        }
        return weatherMap.get(city);
    }

    boolean isOld(City city){
        LocalDateTime now = LocalDateTime.now().minusHours(1);
        LocalDateTime lastUpdate = weatherMap.get(city).getLastUpdate();
        if (now.isAfter(lastUpdate)){
            return true;
        }
        return false;
    }

    Weather sendEmptyWeather(City city){
        this.weatherMap.remove(city);
//        errorService.sendMessage(ERR_WEATHER_SERVICE_DISCONNECT);
        new Weather();
        return new Weather(city.getCode(), "-", "-", LocalDateTime.now().minusDays(1));
    }
}
