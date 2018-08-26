package my.weatherApp.service;

import com.mongodb.MongoException;
import com.vaadin.external.org.slf4j.Logger;
import com.vaadin.external.org.slf4j.LoggerFactory;
import com.vaadin.server.ErrorEvent;
import com.vaadin.server.ErrorHandler;
import com.vaadin.ui.Label;
import my.weatherApp.model.Error;
import my.weatherApp.model.LogEvent;

import java.util.HashMap;
import java.util.Map;

public class ErrorService implements ErrorHandler {

    private static final Logger LOG = LoggerFactory.getLogger(ErrorService.class);

    private static ErrorService instance;
    private static Label label;
    Map<String, String> errors = new HashMap<>();
    private final static String DATABASE = "DATABASE";
    private final static String APPLICATION = "APPLICATION";
    private final static String SERVICE_NAME = "ERROR SERVICE";

    private ErrorService(){
        label = new Label();
    }

    public static ErrorService getInstance(){
        if (instance == null) {
            instance = new ErrorService();
        }
        return instance;
    }

    /**
     * @return Label for errors display
     */
    public Label getLabel(){
        return label;
    }

    /**
     * clear errors
     */
    public void clearMessage(String service) {
        this.errors.remove(service);
        fillError();
    }

    /**
     *
     * @param event error in application
     */
    @Override
    public void error(ErrorEvent event) {
        if (event.getThrowable() instanceof MongoException){
            errors.put(DATABASE, event.getThrowable().toString());
        } else if (event instanceof Error) {
            Error e = (Error) event;
            errors.put(e.getService(), e.toString());
        } else {
            String s = event.getThrowable().toString();
            errors.put(APPLICATION, String.format("Error %s occur while program running", s));
        }
//        fillError();
    }

    /**
     * Fill label with current errors
     */
    public void fillError(){
        if (label == null) return;
        StringBuilder sbLabel = new StringBuilder();
        StringBuilder sbLog = new StringBuilder();
        for (Map.Entry<String, String> error : errors.entrySet()) {
            sbLog.append(error.getValue() + "\n");
            sbLabel.append(error.getKey() + " service is unavailable " + "\n");
        }
        label.setValue(sbLabel.toString());
        LOG.error(LogEvent.create("CURRENT ERRORS", sbLog.toString()));
    }
}
