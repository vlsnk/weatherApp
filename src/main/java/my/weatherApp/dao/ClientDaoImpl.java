package my.weatherApp.dao;

import static com.mongodb.client.model.Filters.eq;

import com.mongodb.MongoException;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.model.FindOneAndUpdateOptions;
import com.vaadin.external.org.slf4j.Logger;
import com.vaadin.external.org.slf4j.LoggerFactory;
import my.weatherApp.model.LogEvent;
import my.weatherApp.model.Visitor;
import org.bson.Document;

import java.io.Serializable;

public class ClientDaoImpl implements ClientDao, Serializable {

    private static final Logger LOG = LoggerFactory.getLogger(CurrencyDaoImpl.class);
    private static final String SERVICE_NAME = "DATABASE";
    private static ClientDaoImpl instance;
    private static final String NAME = "visitor";
    private static final String ID = "_id";
    private static final String COUNT = "count";
    private static final String INC = "$inc";
    private MongoCollection collection;
    private boolean ready = false;

    private ClientDaoImpl() {
        LOG.info(LogEvent.create(SERVICE_NAME, "Create ClientDaoImpl"));
    }

    public static ClientDaoImpl getInstance(){
        if (instance == null) {
            instance = new ClientDaoImpl();
        }
        return instance;
    }

//    private void init(){
//        Document d = (Document) collection.find().first();
//        if (d == null) {
//            addVisitor(new Visitor());
//        }
//    }

    public void setDB(MongoDatabase mongoDatabase){
        this.collection = mongoDatabase.getCollection(NAME);
        collection.find();
//        init();
        ready = true;
    }

    @Override
    public boolean isReady() {
        return ready;
    }

    @Override
    @SuppressWarnings("unchecked")
    public void addVisitor(Visitor visitor) {
        if (!ready) {

            throw new MongoException("Database is not connect");
        }
        Document visitorDB = getDoc(visitor);
        collection.insertOne(visitorDB);
    }

    @Override
    public Visitor getVisitor() {
        if (!ready) throw new MongoException("Database is not connect");
        Document result = (Document) collection.findOneAndUpdate(new Document(ID, 1),
                new Document(INC, new Document(COUNT, 1)));
        if (result == null){
            Visitor v = new Visitor();
            addVisitor(v);
            return v;
        } else {
            Visitor visitor = getVisitor(result);
            return visitor;
        }
    }

    private Document getDoc(Visitor v){
        Document visitorDB = new Document(ID, 1).append(COUNT, v.getCount());
        return visitorDB;
    }

    private Visitor getVisitor(Document doc){
        if (!ready) throw new MongoException("Database is not connect");
        Visitor visitor = new Visitor(doc.getInteger(COUNT, 1));
        return visitor;
    }
}
