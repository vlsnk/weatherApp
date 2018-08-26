package my.weatherApp.model;

import java.time.LocalDateTime;

public class Weather {

    private int cityCode;
    private String todayWeather;
    private String tomorrowWeather;
    private LocalDateTime lastUpdate;

    public Weather() {
        this.todayWeather = "-\u00b0C";
        this.tomorrowWeather = "-\u00b0C";
    }

    public Weather(City city) {
        this.cityCode = city.code;
        this.todayWeather = "-\u00b0C";
        this.tomorrowWeather = "-\u00b0C";
    }

    public Weather(int cityCode){
        this.cityCode = cityCode;
    }

    public Weather(int cityCode, String todayWeather, String tomorrowWeather, LocalDateTime lastUpdate) {
        this.cityCode = cityCode;
        this.todayWeather = todayWeather + "\u00b0C";
        this.tomorrowWeather = tomorrowWeather + "\u00b0C";
        this.lastUpdate = lastUpdate;
    }

    public Weather(int cityCode, String todayWeather, String tomorrowWeather, String lastUpdate) {
        this.cityCode = cityCode;
        this.todayWeather = todayWeather + "\u00b0C";
        this.tomorrowWeather = tomorrowWeather + "\u00b0C";
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

    public void setLastUpdate(String lastUpdate){
        this.lastUpdate = LocalDateTime.parse(lastUpdate);
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
