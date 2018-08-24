package my.weatherApp.service;

import com.mongodb.MongoException;
import com.vaadin.external.org.slf4j.Logger;
import com.vaadin.external.org.slf4j.LoggerFactory;
import my.weatherApp.dao.ClientDao;
import my.weatherApp.dao.ClientDaoImpl;
import my.weatherApp.model.Error;
import my.weatherApp.model.Visitor;

/*
    Get information about visit count
 */
public class VisitorServiceImpl implements VisitorService {

    private static final Logger LOG = LoggerFactory.getLogger(VisitorServiceImpl.class);
    private static VisitorServiceImpl instance;
    private ClientDao clientDao;
    private ErrorService errorService = ErrorService.getInstance();
    private static final String SERVICE_NAME = "VISITOR COUNT";

    private VisitorServiceImpl(){
        clientDao = ClientDaoImpl.getInstance();
    }

    public static VisitorServiceImpl getInstance(){
        if (instance == null) {
            instance = new VisitorServiceImpl();
        }
        return instance;
    }

    /*
        @return Visitor(int count)
    */
    @Override
    public Visitor getInfo() {
        Visitor result = new Visitor();
        try {
            result = clientDao.getVisitor();
            if (result == null) {
                Visitor newVisitor = new Visitor();
                clientDao.addVisitor(newVisitor);
                result = newVisitor;
            }
        } catch (MongoException e) {
            Error error = new Error(SERVICE_NAME, e.toString(), e);
            LOG.error(error.toString());
            errorService.error(error);
        }
        return result;
    }

}