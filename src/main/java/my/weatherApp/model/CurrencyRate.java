package my.weatherApp.model;

import java.io.Serializable;

public class CurrencyRate implements Serializable {

    private final static String RUB = "\u0584";
    private final static String def = "-";
    private String name;
    private String code;
    private String byuPrice;
    private String salePrice;

    public CurrencyRate() {
    }

    public CurrencyRate(String name, String byuPrice, String salePrice) {
        this.name = name;
        if (name.equalsIgnoreCase(Currency.USD.toString())) this.code = "840";
        if (name.equalsIgnoreCase(Currency.EUR.toString())) this.code = "978";
        this.byuPrice = byuPrice;
        this.salePrice = salePrice;
    }

    public CurrencyRate(String name) {
        this.name = name;
        if (name.equalsIgnoreCase(Currency.USD.toString())) this.code = "840";
        if (name.equalsIgnoreCase(Currency.EUR.toString())) this.code = "978";
        this.byuPrice = def + RUB ;
        this.salePrice = def + RUB;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
        if (name.equalsIgnoreCase(Currency.USD.toString())) this.code = "840";
        if (name.equalsIgnoreCase(Currency.EUR.toString())) this.code = "978";
    }

    public String getByuPrice() {
        return byuPrice;
    }

    public void setByuPrice(String byuPrice) {
        this.byuPrice = byuPrice + RUB;
    }

    public String getSalePrice() {
        return salePrice;
    }

    public void setSalePrice(String salePrice) {
        this.salePrice = salePrice + RUB;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    @Override
    public String toString() {
        return name + ": " + byuPrice + " " + salePrice;
    }
}
