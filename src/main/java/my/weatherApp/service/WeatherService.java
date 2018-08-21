package my.weatherApp.service;

import my.weatherApp.model.City;
import my.weatherApp.model.Weather;

public interface WeatherService {

    Weather getWeather(City city);

}
