package my.weatherApp.service;

import org.apache.http.HttpResponse;
import org.apache.http.StatusLine;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.HttpClientBuilder;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;

/*
    Connect to weather service and get data
 */
public class WeatherConnector {

    private static WeatherConnector instance;
    private HttpClient httpClient;
    private final static String KEY = "&appid=4e64aeb3bb0d5e92cff8a75087ee6751";
    private final static String REQUEST_FORECAST = "http://api.openweathermap.org/data/2.5/forecast?id=%d&units=metric";
    private final static String REQUEST_WEATHER = "https://api.openweathermap.org/data/2.5/weather?id=%d&cnt=10&units=metric";

    private WeatherConnector() {
        HttpClientBuilder builder = HttpClientBuilder.create();
        this.httpClient = builder.build();
    }

    public static WeatherConnector getInstance(){
        if (instance == null) {
            instance = new WeatherConnector();
        }
        return instance;
    }

    public JSONObject requestWeather(int code) throws IOException, JSONException {
        JSONObject weatherObj = doQuery(REQUEST_WEATHER, code);
        return weatherObj;
    }

    public JSONObject requestForecast(int code) throws IOException, JSONException {
        JSONObject forecastObj = doQuery(REQUEST_FORECAST, code);
        return forecastObj;
    }

    private JSONObject doQuery (String url, int code) throws IOException, JSONException {
        String responseBody = null;
        String requestUrl = String.format(url, code);
        HttpGet httpget = new HttpGet (requestUrl+KEY);

        HttpResponse response = this.httpClient.execute (httpget);

        StatusLine statusLine = response.getStatusLine();
        if (statusLine == null) {
            throw new IOException ("Unable to get a response from OWM server");
        }
        int statusCode = statusLine.getStatusCode ();
        if (statusCode < 200 && statusCode >= 300) {
            throw new IOException(
                    String.format ("OWM server responded with status code %d: %s", statusCode, statusLine));
        }
        try (InputStream contentStream = response.getEntity().getContent();
             Reader isReader = new InputStreamReader(contentStream)) {

            StringBuilder sb = new StringBuilder();
            char[] buffer = new char[8*1024];
            int n = 0;
            while ((n = isReader.read(buffer)) != -1) {
                sb.append(buffer, 0, n);
            }
            responseBody = sb.toString();
        } catch (IOException e) {
            throw e;
        } catch (RuntimeException re) {
            httpget.abort ();
            throw re;
        }
        return new JSONObject (responseBody);
    }
}
