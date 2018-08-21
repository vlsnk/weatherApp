package my.weatherApp.dao;

import static com.mongodb.client.model.Filters.eq;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import my.weatherApp.model.Visitor;
import org.bson.Document;

import java.io.Serializable;

public class ClientDaoImpl implements ClientDao, Serializable {

    static ClientDaoImpl instance;
    private static MongoDatabase db;
    private static final String NAME = "visitor";
    private static final String ID = "_id";
    private static final String COUNT = "count";
    private MongoCollection collection;

    private ClientDaoImpl() {
        this.collection = db.getCollection(NAME);
    }

    public static ClientDaoImpl getInstance(){
        if (instance == null) {
            instance = new ClientDaoImpl();
        }
        return instance;
    }

    public static void setDB(MongoDatabase mongoDatabase){
        db = mongoDatabase;
    }

    @Override
    public void addVisitor(Visitor visitor) {
        Document visitorDB = getDoc(visitor);
        collection.insertOne(visitorDB);
    }

    @Override
    public Visitor getVisitor(String ip) {
        Document d = (Document) collection.find(eq(ID, ip)).first();
        if (d == null){
            return null;
        }
        Visitor visitor = getVisitor(d);
        return visitor;
    }

    @Override
    public void update(Visitor v) {
        collection.updateOne(eq(ID, v.getIpAddress()),
                             new Document("$set", new Document(COUNT, v.getCount())));
    }

    Document getDoc(Visitor v){
        Document visitorDB = new Document(ID, v.getIpAddress())
                                        .append(COUNT, v.getCount());
        return visitorDB;
    }

    Visitor getVisitor(Document doc){
        Visitor visitor = new Visitor(doc.get(ID, String.class),
                                    doc.getInteger(COUNT, 0));
        return visitor;
    }
}
