package my.weatherApp.dao;

import com.mongodb.*;
import com.mongodb.client.MongoDatabase;
import com.vaadin.external.org.slf4j.Logger;
import com.vaadin.external.org.slf4j.LoggerFactory;
import my.weatherApp.model.Error;
import my.weatherApp.service.ErrorService;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public class MainMongoDao implements MainDao {

    private static MainMongoDao instance;
    private static final Logger LOG = LoggerFactory.getLogger(MainMongoDao.class);
    private static final String CONNECTION_TIME_OUT_MS = "CONNECTION_TIME_OUT_MS";
    private static int connectionTimeout = 10_000;
    private static final String SOCKET_TIME_OUT_MS = "SOCKET_TIME_OUT_MS";
    private static int socketTimeout = 0;
    private static final String SERVER_SELECTION_TIMEOUT_MS = "SERVER_SELECTION_TIMEOUT_MS";
    private static int serverSelectionTimeout = 30_000;
    private static final String USERNAME = "USERNAME";
    private static String user = null;
    private static final String PASSWORD = "PASSWORD";
    private static char[] password = null;
    private static final String DB_NAME = "DB_NAME";
    private static String dbName = "weatherApp";
    private static final String SERVER = "SERVER";
    private static String serverAddress = "localhost";
    private static final String  PORT = "PORT";
    private static int port = 27017;
    private static final String PROPERTIES_FILE = "database.properties";
    private MongoClient client;
    private ErrorService errorService = ErrorService.getInstance();
    private ClientDaoImpl clientDao = ClientDaoImpl.getInstance();
    private CurrencyDaoImpl currencyDao = CurrencyDaoImpl.getInstance();
    private WeatherDaoImpl weatherDao = WeatherDaoImpl.getInstance();
    private static final String SERVICE_NAME = "DATABASE";

    private MainMongoDao() {

        LOG.info("init()");
        init();
    }

    public static MainMongoDao getInstance() {
        if (instance == null) {
            instance = new MainMongoDao();
        }
        return instance;
    }

    public void init(){
        if (client == null) {
            try {
                client = createClient();
                MongoDatabase db = client.getDatabase(dbName);
                clientDao.setDB(db);
                currencyDao.setDB(db);
                weatherDao.setDB(db);
            } catch (MongoException m) {
                String msg = String.format("Error %s occur while initialize mongodb connection", m.toString());
                Error error = new Error(SERVICE_NAME, msg, m);
                LOG.error(error.toString());
                errorService.error(error);
            }
        }

    }

    public void close(){
        client.close();
    }

    private MongoClient createClient(){
        loadProperties();

        MongoClientOptions.Builder optionsBuilder = MongoClientOptions.builder();

        optionsBuilder.connectTimeout(connectionTimeout);
        optionsBuilder.socketTimeout(socketTimeout);
        optionsBuilder.serverSelectionTimeout(serverSelectionTimeout);

        MongoClientOptions options = optionsBuilder.build();
        ServerAddress link = new ServerAddress(serverAddress , port);

        MongoClient client;
        if(user != null) {
            MongoCredential creds = MongoCredential.createCredential(user, dbName, password);
            client = new MongoClient(link, creds, options);
        } else {
            client = new MongoClient(link, options);

        }
        return client;
    }

    private void loadProperties(){
        Properties p = new Properties();
        try (InputStream stream = MainMongoDao.class.getClassLoader().getResourceAsStream(PROPERTIES_FILE)) {

            p.load(stream);
            String varUser = p.getProperty(USERNAME);

            if (isValidProperty(varUser)) {
                user = varUser;
            }
            String varPassword = p.getProperty(PASSWORD);
            if (isValidProperty(varPassword)){
                password = varPassword.toCharArray();
            }
            String varDbName = p.getProperty(DB_NAME);
            if (isValidProperty(varDbName)) {
                dbName = varDbName;
            }
            String varServer = p.getProperty(SERVER);
            if (isValidProperty(varServer)) {
                serverAddress = varServer;
            }
            String varPort = p.getProperty(PORT);
            if (isValidProperty(varPort)) {
                port = Integer.valueOf(varPort);
            }
            String connectT = p.getProperty(CONNECTION_TIME_OUT_MS);
            if (isValidProperty(connectT)) {
                connectionTimeout = Integer.valueOf(connectT);
            }
            String socketT = p.getProperty(SOCKET_TIME_OUT_MS);
            if (isValidProperty(socketT)) {
                socketTimeout = Integer.valueOf(socketT);
            }
            String selectionT = p.getProperty(SERVER_SELECTION_TIMEOUT_MS);
            if (isValidProperty(selectionT)) {
                serverSelectionTimeout = Integer.valueOf(selectionT);
            }

        } catch (IOException e) {
            String msg = "Error to read database properties: " + e.toString();
            Error error = new Error(SERVICE_NAME, msg, e);
            errorService.error(error);
            LOG.error(error.toString());
        } catch (NumberFormatException n) {
            String msg = "Error to read database properties in " + PROPERTIES_FILE + " : " + n.toString();
            Error error = new Error(SERVICE_NAME, msg, n);
            errorService.error(error);
            LOG.error(error.toString());
        }
    }

    static boolean isValidProperty(String s){
        boolean b1 = s != null;
        boolean b2 = !s.isEmpty();
        return b1 && b2;
    }
}
