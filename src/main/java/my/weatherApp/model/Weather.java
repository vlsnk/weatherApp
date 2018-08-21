package my.weatherApp.model;

import java.time.LocalDateTime;

public class Weather {

    int cityCode;
    String todayWeather;
    String tomorrowWeather;
    LocalDateTime lastUpdate;

    public Weather(int cityCode, String todayWeather, String tomorrowWeather, LocalDateTime lastUpdate) {
        this.cityCode = cityCode;
        this.todayWeather = todayWeather;
        this.tomorrowWeather = tomorrowWeather;
        this.lastUpdate = lastUpdate;
    }

    public Weather(int cityCode, String todayWeather, String tomorrowWeather, String lastUpdate) {
        this.cityCode = cityCode;
        this.todayWeather = todayWeather;
        this.tomorrowWeather = tomorrowWeather;
        this.lastUpdate = LocalDateTime.parse(lastUpdate);
    }

    public int getCity() {
        return cityCode;
    }

    public void setCity(int city) {
        this.cityCode = city;
    }

    public String getTodayWeather() {
        return todayWeather;
    }

    public void setTodayWeather(String todayWeather) {
        this.todayWeather = todayWeather;
    }

    public String getTomorrowWeather() {
        return tomorrowWeather;
    }

    public void setTomorrowWeather(String tomorrowWeather) {
        this.tomorrowWeather = tomorrowWeather;
    }

    public LocalDateTime getLastUpdate() {
        return lastUpdate;
    }

    public void setLastUpdate(LocalDateTime lastUpdate) {
        this.lastUpdate = lastUpdate;
    }

    @Override
    public String toString() {
        return "WeatherService{" +
                "city=" + cityCode +
                ", todayWeather='" + todayWeather + '\'' +
                ", tomorrowWeather='" + tomorrowWeather + '\'' +
                '}';
    }

}
