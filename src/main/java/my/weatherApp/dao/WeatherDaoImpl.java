package my.weatherApp.dao;

import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import my.weatherApp.model.City;
import my.weatherApp.model.Weather;
import org.bson.Document;

import java.io.Serializable;

import static com.mongodb.client.model.Filters.eq;

@SuppressWarnings("unchecked")
public class WeatherDaoImpl implements WeatherDao, Serializable {

    static WeatherDaoImpl instance;
    private static MongoDatabase db;
    private static final String NAME = "weather";
    private static final String ID = "_id";
    private static final String TODAY = "today";
    private static final String TOMORROW = "tomorrow";
    private static final String UPDATE = "date";
    private static final String DB_SET = "$set";
    private MongoCollection collection;

    private WeatherDaoImpl() {
        this.collection = db.getCollection(NAME);
    }

    public static WeatherDaoImpl getInstance(){
        if (instance == null) {
            instance = new WeatherDaoImpl();
        }
        return instance;
    }

    public static void setDB(MongoDatabase mongoDatabase){
        db = mongoDatabase;
    }

    @Override
    public void addWeather(Weather weather) {
        Document weatherDB = getDoc(weather);
        collection.insertOne(weatherDB);
    }

    @Override
    public Weather getWeather(City city) {
        Document d = (Document) collection.find(eq(ID, city.getCode())).first();
        if (d == null){
            return null;
        }
        Weather weather = getWeatherFromDoc(d);
        return weather;
    }

    @Override
    public void update(Weather weather) {
        collection.updateOne(eq(ID, weather.getCity()),
                new Document("$set", new Document(TODAY, weather.getTodayWeather())
                                                .append(TOMORROW, weather.getTomorrowWeather())
                                                .append(UPDATE, weather.getLastUpdate().toString())));
    }

    Document getDoc(Weather w){
        Document weatherDB = new Document(ID, w.getCity())
                .append(TODAY, w.getTodayWeather())
                .append(TOMORROW, w.getTomorrowWeather())
                .append(UPDATE, w.getLastUpdate().toString());
        System.out.println(weatherDB);
        return weatherDB;
    }

    Weather getWeatherFromDoc(Document doc){
        Weather weather = new Weather(doc.getInteger(ID, 0),
                doc.getString(TODAY),
                doc.getString(TOMORROW),
                doc.getString(UPDATE));
        return weather;
    }
}
