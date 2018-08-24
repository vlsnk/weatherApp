package my.weatherApp.service;

import my.weatherApp.model.CurrencyRate;

import java.util.List;

public interface CurrencyService {

    List<CurrencyRate> getCurrency();
    List<CurrencyRate> getEmptyCurrency();
}
