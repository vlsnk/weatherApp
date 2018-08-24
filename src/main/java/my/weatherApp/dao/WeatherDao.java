package my.weatherApp.dao;

import my.weatherApp.model.City;
import my.weatherApp.model.Weather;

public interface WeatherDao {

    boolean isReady();
    void addWeather(Weather weather);
    Weather getWeather(City city);
    void update(Weather weather);
}
