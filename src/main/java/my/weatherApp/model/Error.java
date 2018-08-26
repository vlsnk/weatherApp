package my.weatherApp.model;

import com.vaadin.server.ErrorEvent;

public class Error extends ErrorEvent {

    private String service = "";
    private String detail = "";

    public Error(String service, String detail, Throwable t) {
        super(t);
        this.service = service;
        this.detail = detail;
    }

    public String getService() {
        return service;
    }

    public void setService(String service) {
        this.service = service;
    }

    public String getDetail() {
        return detail;
    }

    public void setDetail(String detail) {
        this.detail = detail;
    }

    @Override
    public String toString() {
        return "[" + service + "] " + detail;
    }
}
