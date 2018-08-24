package my.weatherApp.model;

public class Visitor {
    private int count;

    public Visitor() {
        this.count = 1;
    }
    public Visitor(int count) {
        this.count = count;
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
