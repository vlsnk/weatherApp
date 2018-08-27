package my.weatherApp.dao;

import my.weatherApp.model.CurrencyDto;
import my.weatherApp.model.CurrencyRate;

import java.time.LocalDate;

public interface CurrencyDao {

    boolean isReady();
    void addCurrency(CurrencyDto currencyDto);
    void addCurrency(CurrencyRate currencyRate, LocalDate date);
    CurrencyDto getCurrency();
    void update(CurrencyDto currencyDto);
    void remove(CurrencyDto currencyDto);
}
