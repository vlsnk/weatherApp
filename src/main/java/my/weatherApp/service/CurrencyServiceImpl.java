package my.weatherApp.service;

import com.vaadin.external.org.slf4j.Logger;
import com.vaadin.external.org.slf4j.LoggerFactory;
import my.weatherApp.dao.CurrencyDao;
import my.weatherApp.dao.CurrencyDaoImpl;
import my.weatherApp.model.Currency;
import my.weatherApp.model.CurrencyDto;
import my.weatherApp.model.CurrencyRate;

import java.io.Serializable;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class CurrencyServiceImpl implements CurrencyService, Serializable {

    private static final Logger LOG = LoggerFactory.getLogger(CurrencyServiceImpl.class);
    private static CurrencyServiceImpl instance;
    List<CurrencyRate> list;
    LocalDate date = LocalDate.now().minusDays(1);
    private CurrencyDao currencyDao;
    private static final String ADD = "ADD";
    private static final String UPDATE = "UPDATE";



    private CurrencyServiceImpl() {
        currencyDao = CurrencyDaoImpl.getInstance();
        getCurrency();
    }

    public static CurrencyServiceImpl getInstance() {
        if (instance == null) {
            instance = new CurrencyServiceImpl();
        }
        return instance;
    }
    @Override
    public List<CurrencyRate> getCurrency() {
        if (this.list == null || isOld()) {
            requestFromDB();
        }
        return this.list;
    }

    @Override
    public List<CurrencyRate> getEmptyCurrency() {
        List<CurrencyRate> rates = new ArrayList<>();
        for (Currency c: Currency.values()) {
            rates.add(new CurrencyRate(c.name()));
        }
        return rates;
    }

    void requestFromDB(){
        CurrencyDto dto = currencyDao.getCurrency();
        if (dto == null) {
            requestNewRates(ADD);
        } else {
            this.date = dto.getDate();
            this.list = dto.getRates();
            if (!isValid(dto.getRates())) {
                currencyDao.remove(dto);
                requestNewRates(ADD);
            }
            if (isOld()) {
                requestNewRates(UPDATE);
            }
        }
    }


    void requestNewRates(String action) {

        this.list = BankParser.getCurrency();
        this.date = LocalDate.now();
        if (this.list == null) {
            this.list = fillDefault();
        } else {
            CurrencyDto newDto = new CurrencyDto(this.list, this.date);
            if (action.equals(ADD)) {
                currencyDao.addCurrency(newDto);
            }
            if (action.equals(UPDATE)) {
                currencyDao.update(newDto);
            }
        }
    }

    boolean isValid(List<CurrencyRate> list){
        boolean result = false;
        if (list.size() == 2) {
            boolean hasUsd = false;
            boolean hasEur = false;
            for (CurrencyRate c : list){
                if (c.getName().equalsIgnoreCase(Currency.USD.toString())) {
                    hasUsd = true;
                }
                if (c.getName().equalsIgnoreCase(Currency.EUR.toString())) {
                    hasEur = true;
                }
            }
            return hasEur & hasUsd;
        }
        return result;
    }

    boolean isOld() {
        boolean result = false;
        LocalDate now = LocalDate.now();
        if (now.isAfter(date)) result = true;
        return result;
    }

    private List<CurrencyRate> fillDefault(){
        List<CurrencyRate> newList = new ArrayList<>();
        newList.add(new CurrencyRate(Currency.USD.toString(), "-", "-"));
        newList.add(new CurrencyRate(Currency.EUR.toString(), "-", "-"));
        return newList;
    }
}
