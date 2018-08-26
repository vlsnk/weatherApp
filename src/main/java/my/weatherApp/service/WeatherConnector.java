package my.weatherApp.service;

import com.vaadin.external.org.slf4j.Logger;
import com.vaadin.external.org.slf4j.LoggerFactory;
import my.weatherApp.exception.InvalidServiceException;
import my.weatherApp.model.Error;
import my.weatherApp.model.LogEvent;
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
import java.util.Properties;


public class WeatherConnector {

    private static final String PROPERTIES_FILE = "weather.properties";
    private static final Logger LOG = LoggerFactory.getLogger(WeatherConnector.class);
    private ErrorService errorService = ErrorService.getInstance();
    private static final String LINK = "link";
    private static final String API_KEY = "key";
    private static WeatherConnector instance;
    private HttpClient httpClient;
    private final static String KEY = "&appid=";
    private final static String WORKING_LINK = "http://api.openweathermap.org";
    private final static String FORECAST = "/data/2.5/forecast?id=%d&units=metric";
    private final static String WEATHER = "/data/2.5/weather?id=%d&cnt=10&units=metric";
    private final static String UNSUPPORTED_LINK = "Unsupported service link, set openweathermap.org link";
    private static final String SERVICE_NAME = "WEATHER";
    private String requestWeather;
    private String requestForecast;
    private String appID;
    private boolean ready = false;

    private WeatherConnector() {
        HttpClientBuilder builder = HttpClientBuilder.create();
        this.httpClient = builder.build();
        loadProperties();
    }

    public static WeatherConnector getInstance(){
        if (instance == null) {
            instance = new WeatherConnector();
        }
        return instance;
    }

    /**
     * Request current weather
     *
     * @param code city code
     * @return json response
     * @throws IOException
     * @throws JSONException
     * @throws InvalidServiceException
     */
    public JSONObject requestWeather(int code) throws IOException, JSONException, InvalidServiceException {
        LOG.info(LogEvent.create(SERVICE_NAME, "Request current weather from remote service"));
        if (!ready) {
            LOG.error(LogEvent.create(SERVICE_NAME, UNSUPPORTED_LINK));
            throw new InvalidServiceException(UNSUPPORTED_LINK);
        }
        JSONObject weatherObj = doQuery(requestWeather, code);
        return weatherObj;
    }

    /**
     * Request forecast weather
     *
     * @param code city code
     * @return json response
     * @throws IOException
     * @throws JSONException
     * @throws InvalidServiceException
     */
    public JSONObject requestForecast(int code) throws IOException, JSONException, InvalidServiceException {
        if (!ready) {
            LOG.error(LogEvent.create(SERVICE_NAME, UNSUPPORTED_LINK));
            throw new InvalidServiceException(UNSUPPORTED_LINK);
        }
        JSONObject forecastObj = doQuery(requestForecast, code);
        return forecastObj;
    }

    /**
     * Request data from remote service
     *
     * @param url request link
     * @param code city code
     * @return json response
     * @throws IOException
     * @throws JSONException
     */
    private JSONObject doQuery (String url, int code) throws IOException, JSONException {
        String responseBody = null;
        String requestUrl = String.format(url, code);
        HttpGet httpget = new HttpGet (requestUrl+appID);
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

    /**
     * Load properties from file
     */
    private void loadProperties(){
        Properties p = new Properties();
        try (InputStream stream = WeatherConnector.class.getClassLoader().getResourceAsStream(PROPERTIES_FILE)) {

            p.load(stream);
            String varLink = p.getProperty(LINK);

            if (isValidProperty(varLink)) {
                if (varLink.equalsIgnoreCase(WORKING_LINK)) {
                    requestWeather = varLink + WEATHER;
                    requestForecast = varLink + FORECAST;
                } else throw new InvalidServiceException(varLink + " - " + UNSUPPORTED_LINK);
            }
            String varKey = p.getProperty(API_KEY);
            if (isValidProperty(varKey)){
                appID = KEY + varKey;
            }
            ready = true;
        } catch (IOException|NullPointerException|InvalidServiceException e) {
            String msg = "Error to read weather properties: " + e.toString();
            Error error = new Error(SERVICE_NAME, msg, e);
            errorService.error(error);
            LOG.error(error.toString());
            ready = false;
        }
    }

    static boolean isValidProperty(String s){
        boolean b1 = s != null;
        boolean b2 = !s.isEmpty();
        return b1 && b2;
    }
}
