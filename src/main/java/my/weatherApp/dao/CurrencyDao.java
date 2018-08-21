package my.weatherApp.dao;

import my.weatherApp.model.CurrencyDto;

public interface CurrencyDao {

    void addCurrency(CurrencyDto currencyDto);
    CurrencyDto getCurrency();
    void update(CurrencyDto currencyDto);
    void remove(CurrencyDto currencyDto);
}
