package my.weatherApp.model;

import java.io.Serializable;

public class CurrencyRate implements Serializable {

    private final static String RUB = "\u0584";
    private final static String def = "-";
    private String name;
    private String byuPrice;
    private String salePrice;

    public CurrencyRate() {
    }

    public CurrencyRate(String name, String byuPrice, String salePrice) {
        this.name = name;
        this.byuPrice = byuPrice + RUB ;
        this.salePrice = salePrice + RUB;
    }

    public CurrencyRate(String name) {
        this.name = name;
        this.byuPrice = def + RUB ;
        this.salePrice = def + RUB;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getByuPrice() {
        return byuPrice;
    }

    public void setByuPrice(String byuPrice) {
        this.byuPrice = byuPrice;
    }

    public String getSalePrice() {
        return salePrice;
    }

    public void setSalePrice(String salePrice) {
        this.salePrice = salePrice;
    }

    @Override
    public String toString() {
        return name + ": " + byuPrice + " " + salePrice;
    }
}
