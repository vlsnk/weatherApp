package my.weatherApp.model;

public class LogEvent {

    private String service = "";
    private String detail = "";

    public LogEvent(String service, String detail) {
        this.service = service;
        this.detail = detail;
    }

    public static String create(String service, String detail){
        LogEvent event = new LogEvent(service,detail);
        return event.toString();
    }

    @Override
    public String toString() {
        return "[" + service + "] " + detail;
    }
}
