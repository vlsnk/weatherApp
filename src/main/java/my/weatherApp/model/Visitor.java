package my.weatherApp.model;

public class Visitor {
    String ipAddress;
    int count;

    public Visitor(String ipAddress, int count) {
        this.ipAddress = ipAddress;
        this.count = count;
    }

    public String getIpAddress() {
        return ipAddress;
    }

    public void setIpAddress(String ipAddress) {
        this.ipAddress = ipAddress;
    }

    public int getCount() {
        return count;
    }

    public void setCount(int count) {
        this.count = count;
    }

    public Visitor increaseCount(){
        this.count++;
        return this;
    }
}
