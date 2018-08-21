package my.weatherApp.service;

public class ErrorService {

    private static ErrorService instance;
    String error = "";

    private ErrorService(){
    }

    public static ErrorService getInstance(){
        if (instance == null) {
            instance = new ErrorService();
        }
        return instance;
    }

    public void sendMessage(String err){
        this.error = err;
    }

    public void clearMessage() {
        this.error = "";
    }

    public String getMessage(){
        return this.error;
    }
}
