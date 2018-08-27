package my.weatherApp.service;

import com.mongodb.MongoException;
import com.vaadin.external.org.slf4j.Logger;
import com.vaadin.external.org.slf4j.LoggerFactory;
import my.weatherApp.dao.WeatherDao;
import my.weatherApp.dao.WeatherDaoImpl;
import my.weatherApp.exception.InvalidServiceException;
import my.weatherApp.model.City;
import my.weatherApp.model.Error;
import my.weatherApp.model.LogEvent;
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

    /**
     * @return Weather from weatherMap or request Weather from DB
     */
    @Override
    public Weather getWeather(City city) {
        if (!weatherMap.containsKey(city) || isOld(weatherMap.get(city))){
            Weather w = requestFromDB(city);
            if (w == null || isOld(w)) {
                w = requestRemote(city);
                if (w == null) {
                    return getEmptyWeather();
                }
            }
            weatherMap.put(city, w);
        }
        return weatherMap.get(city);

    }

    /**
     *
     * @return Weather with empty data
     */
    @Override
    public Weather getEmptyWeather() {
        return new Weather();
    }

    /**
     *
     * @param city
     * @return Weather from DB
     */
    Weather requestFromDB(City city) {
        errorService.clearMessage("DATABASE");
        LOG.info(LogEvent.create(SERVICE_NAME, "Request data from DB"));
        Weather weather = null;
        try {
            weather = weatherDao.getWeather(city);
        } catch (MongoException e) {
            Error error = new Error(SERVICE_NAME, "Error to request data from DB - " + e.toString(), e);
            LOG.error(error.toString());
            errorService.error(error);
        }
        return weather;
    }

    /**
     * Request weather from remote service
     * @param city
     */
    private Weather requestRemote(City city){
        Weather w = null;
        try {
            LOG.info(LogEvent.create(SERVICE_NAME, "Request data from remote service"));
            JSONObject weatherObj = weatherConnector.requestWeather(city.getCode());
            JSONObject forecastObj = weatherConnector.requestForecast(city.getCode());
            if (weatherObj == null || forecastObj == null){
                return w;
            }
            Weather newWeather = WeatherParser.getWeather(weatherObj, forecastObj);
            if (newWeather != null){
                weatherMap.put(city, newWeather);
                weatherDao.update(newWeather);
                return weatherMap.get(city);
            }

        } catch (IOException|JSONException|NullPointerException e){
            Error error = new Error(SERVICE_NAME, "Error to request data from remote service - " + e.toString(), e);
            errorService.error(error);
            LOG.error(error.toString());
        } catch (InvalidServiceException e) {
            String msg = "Error to request weather - " + e.toString();
            Error error = new Error(SERVICE_NAME, msg, e);
            LOG.error(error.toString());
            errorService.error(error);
        } catch (MongoException e) {
            Error error = new Error(SERVICE_NAME, "Error when connect to DB - " + e.toString(), e);
            LOG.error(error.toString());
            errorService.error(error);
            return weatherMap.get(city);
        }
        return w;
    }

    /**
     * Check if Weather is old or not
     * @param weather
     * @return
     */
    boolean isOld(Weather weather){
        if (weather == null) return true;
        LocalDateTime now = LocalDateTime.now().minusHours(1);
        LocalDateTime lastUpdate = weather.getLastUpdate();
        if (now.isAfter(lastUpdate)){
            return true;
        }
        return false;
    }

}
