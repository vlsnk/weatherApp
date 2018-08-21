package my.weatherApp.model;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public enum City {

    NOVOSIBIRSK (1496747, "Новосибирск"),
    MOSCOW(524901, "Москва"),
    SPETERBURG(536203, "Сантк-Петербург");

    int code;
    String name;
    City(int code, String name) {
        this.code = code;
        this.name = name;
    }
    public int getCode(){
        return this.code;
    }

    public String getName(){
        return this.name;
    }

    public static Collection<String> getNames() {
        List<String> list = new ArrayList<>();
        for (City c : City.values()) {
            list.add(c.name);
        }
        return list;
    }

    public static City getCity(String name) {
        for (City c : City.values()) {
            if (c.name.equalsIgnoreCase(name)) return c;
        }
        return null;
    }
}
