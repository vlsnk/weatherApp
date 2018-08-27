package my.weatherApp.dao;

import com.mongodb.MongoException;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.vaadin.external.org.slf4j.Logger;
import com.vaadin.external.org.slf4j.LoggerFactory;
import my.weatherApp.model.City;
import my.weatherApp.model.LogEvent;
import my.weatherApp.model.Weather;
import org.bson.Document;

import java.io.Serializable;

import static com.mongodb.client.model.Filters.eq;

@SuppressWarnings("unchecked")
public class WeatherDaoImpl implements WeatherDao, Serializable {

    private static final Logger LOG = LoggerFactory.getLogger(CurrencyDaoImpl.class);
    private static final String SERVICE_NAME = "DATABASE";
    private static WeatherDaoImpl instance;
    private static final String NAME = "weather";
    private static final String ID = "_id";
    private static final String TODAY = "today";
    private static final String TOMORROW = "tomorrow";
    private static final String UPDATE = "date";
    private static final String DB_SET = "$set";
    private MongoCollection collection;
    private boolean ready = false;

    private WeatherDaoImpl() {
        LOG.info(LogEvent.create(SERVICE_NAME, "Create WeatherDaoImpl"));
    }

    public static WeatherDaoImpl getInstance(){
        if (instance == null) {
            instance = new WeatherDaoImpl();
        }
        return instance;
    }

    public void setDB(MongoDatabase mongoDatabase){
        this.collection = mongoDatabase.getCollection(NAME);
        ready = true;
    }

    @Override
    public boolean isReady() {
        return ready;
    }

    @Override
    public void addWeather(Weather weather) {
        if (!ready) throw new MongoException("Database is not connect");

        Document weatherDB = getDoc(weather);
        collection.insertOne(weatherDB);
    }

    @Override
    public Weather getWeather(City city) {
        if (!ready) throw new MongoException("Database is not connect");

        Document d = (Document) collection.find(eq(ID, city.getCode())).first();
        if (d == null){
            return null;
        }
        Weather weather = getWeatherFromDoc(d);
        return weather;
    }

    @Override
    public void update(Weather weather) {
        if (!ready) throw new MongoException("Database is not connect");

        Document d = (Document) collection.findOneAndUpdate(new Document(ID, weather.getCity()),
                new Document("$set", new Document(TODAY, weather.getTodayWeather())
                                                .append(TOMORROW, weather.getTomorrowWeather())
                                                .append(UPDATE, weather.getLastUpdate().toString())));
        if (d == null){
            addWeather(weather);
        }
    }

    Document getDoc(Weather w){
        Document weatherDB = new Document(ID, w.getCity())
                .append(TODAY, w.getTodayWeather())
                .append(TOMORROW, w.getTomorrowWeather())
                .append(UPDATE, w.getLastUpdate().toString());
        return weatherDB;
    }

    Weather getWeatherFromDoc(Document doc){
        Weather weather = new Weather(doc.getInteger(ID, 0));
        weather.setTodayWeather(doc.getString(TODAY));
        weather.setTomorrowWeather(doc.getString(TOMORROW));
        weather.setLastUpdate(doc.getString(UPDATE));
        return weather;
    }
}
