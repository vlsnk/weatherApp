package my.weatherApp.model;

import java.io.Serializable;

public class CurrencyRate implements Serializable {

    String name;
    String byuPrice;
    String salePrice;

    public CurrencyRate() {
    }

    public CurrencyRate(String name, String byuPrice, String salePrice) {
        this.name = name;
        this.byuPrice = byuPrice;
        this.salePrice = salePrice;
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
