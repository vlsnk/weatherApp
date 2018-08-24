package my.weatherApp.dao;

import com.mongodb.MongoSecurityException;
import com.mongodb.client.*;
import my.weatherApp.model.CurrencyDto;
import my.weatherApp.model.CurrencyRate;
import org.bson.Document;

import java.io.Serializable;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import static com.mongodb.client.model.Filters.eq;

public class CurrencyDaoImpl implements CurrencyDao, Serializable {

    static CurrencyDaoImpl instance;
    private static final String NAME = "currency";
    private static final String ID = "_id";
    private static final String SALE = "sale";
    private static final String BUY = "buy";
    private static final String DATE = "date";
    private static final String DB_SET = "$set";
    private MongoCollection collection;
    private boolean ready = false;

    private CurrencyDaoImpl() {
    }

    public static CurrencyDaoImpl getInstance(){
        if (instance == null) {
            instance = new CurrencyDaoImpl();
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
    public void addCurrency(CurrencyDto currencyDto) {
        List<Document> documents = new ArrayList<>();
        for (CurrencyRate c : currencyDto.getRates()) {
            documents.add(getDoc(c, currencyDto.getDate()));
        }
        collection.insertMany(documents);
    }

    @Override
    public CurrencyDto getCurrency() throws MongoSecurityException {
        List<CurrencyRate> currencyRates = new ArrayList<>();
        MongoCursor list = collection.find().iterator();
        String date = null;
        for (MongoCursor it = list; it.hasNext(); ) {
            Document d = (Document) it.next();
            currencyRates.add(getCurrency(d));
            date = d.getString(DATE);
        }
        if (currencyRates.isEmpty()) return null;
        return new CurrencyDto(currencyRates, date);
    }

    @Override
    public void update(CurrencyDto currencyDto) {
        LocalDate date = currencyDto.getDate();
        for (CurrencyRate c : currencyDto.getRates()) {
            Document d = new Document(SALE, c.getSalePrice()).append(BUY, c.getByuPrice()).append(DATE, date.toString());
            collection.updateOne(eq(ID, c.getName()), new Document(DB_SET, d));
        }
    }

    @Override
    public void remove(CurrencyDto currencyDto) {
        collection.deleteMany(new Document());
    }

    Document getDoc(CurrencyRate c, LocalDate date){
        Document currencyDB = new Document(ID, c.getName())
                                .append(BUY, c.getByuPrice())
                                .append(SALE, c.getSalePrice())
                                .append(DATE, date.toString());
        System.out.println(currencyDB);
        return currencyDB;
    }

    CurrencyRate getCurrency(Document doc){
        CurrencyRate currency = new CurrencyRate(doc.getString(ID),
                doc.getString(BUY),
                doc.getString(SALE));
        return currency;
    }
}
