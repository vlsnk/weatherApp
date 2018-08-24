package my.weatherApp.service;

import my.weatherApp.dao.WeatherDao;
import my.weatherApp.dao.WeatherDaoImpl;
import my.weatherApp.model.City;
import my.weatherApp.model.Weather;
import org.json.JSONException;
import org.json.JSONObject;
import java.io.*;
import java.time.LocalDateTime;
import java.util.*;

public class WeatherServiceImpl implements WeatherService{

    private static WeatherServiceImpl instance;
    Map<City, Weather> weatherMap = new HashMap<>();
    private WeatherDao weatherDao;
    private ErrorService errorService;
    private WeatherConnector weatherConnector;


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
            if (w == null) {
                this.weatherMap.remove(city);
                errorService.sendMessage(ERR_WEATHER_SERVICE_DISCONNECT);
                return new Weather(city.getCode(), "-", "-", LocalDateTime.now().minusDays(1));
            } else {
                errorService.clearMessage();
            }
        }
        return weatherMap.get(city);

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
                    errorService.clearMessage();
                    if (weather == null) weatherDao.addWeather(newWeather);
                    else weatherDao.update(newWeather);
                    weatherMap.put(city, newWeather);
                }
            } catch (IOException|JSONException e) {
                errorService.sendMessage(ERR_WEATHER_SERVICE_DISCONNECT);
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
        errorService.sendMessage(ERR_WEATHER_SERVICE_DISCONNECT);
        return new Weather(city.getCode(), "-", "-", LocalDateTime.now().minusDays(1));
    }
}
