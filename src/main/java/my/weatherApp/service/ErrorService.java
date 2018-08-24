package my.weatherApp.service;

import com.mongodb.MongoException;
import com.mongodb.MongoTimeoutException;
import com.vaadin.server.ErrorEvent;
import com.vaadin.server.ErrorHandler;
import com.vaadin.ui.Label;
import my.weatherApp.model.Error;

import java.util.ArrayList;
import java.util.List;

public class ErrorService implements ErrorHandler {

    private static ErrorService instance;
    private static Label label;
    List<String> errorList = new ArrayList<>();

    private ErrorService(){
    }

    public static ErrorService getInstance(){
        if (instance == null) {
            instance = new ErrorService();
        }
        return instance;
    }

    public void  setLabel(Label newLabel){
        label = newLabel;
    }

//    public void sendMessage(String err){
//        this.error = err;
//    }

    public void clearMessage() {
        this.errorList.clear();
        label.setValue("");
    }

//    public String getMessage(){
//        return this.error;
//    }

    @Override
    public void error(ErrorEvent event) {
        if (event.getThrowable() instanceof MongoException){
            errorList.add(String.format("Error %s occure while initialize mongodb connection", event.getThrowable().toString()));
        } else if (event instanceof Error) {
            errorList.add(event.toString());
        } else {
            String s = event.getThrowable().toString();
            errorList.add(String.format("Error %s occure while programm running", s));
        }
        fillError();
    }

    public void fillError(){
        if (label == null) return;
        StringBuilder sb = new StringBuilder();
        for (String error : errorList) {
            sb.append(error + "'\n");
        }
        label.setValue(sb.toString());
    }
}
