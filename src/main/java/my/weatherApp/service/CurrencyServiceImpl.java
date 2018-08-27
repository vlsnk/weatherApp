package my.weatherApp.service;

import com.mongodb.MongoException;
import com.vaadin.external.org.slf4j.Logger;
import com.vaadin.external.org.slf4j.LoggerFactory;
import my.weatherApp.dao.CurrencyDao;
import my.weatherApp.dao.CurrencyDaoImpl;
import my.weatherApp.model.Currency;
import my.weatherApp.model.CurrencyDto;
import my.weatherApp.model.CurrencyRate;
import my.weatherApp.model.Error;
import my.weatherApp.model.LogEvent;

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
    private static final String SERVICE_NAME = "CURRENCY";
    private BankParser bankParser;
    private ErrorService errorService = ErrorService.getInstance();

    private CurrencyServiceImpl() {
        LOG.info(LogEvent.create(SERVICE_NAME, "Create CurrencyServiceImpl"));
        currencyDao = CurrencyDaoImpl.getInstance();
        bankParser = BankParser.getInstance();
    }

    public static CurrencyServiceImpl getInstance() {
        if (instance == null) {
            instance = new CurrencyServiceImpl();
        }
        return instance;
    }

    /**
     * @return CurrencyRates from list or request Rates from DB
     */
    @Override
    public List<CurrencyRate> getCurrency() {
        if (this.list == null || isOld()) {
            CurrencyDto ratesDB = requestFromDB();
            List<CurrencyRate> rates = null;
            if (ratesDB == null) {
                rates = requestRemote(ADD);
            } else {
                rates = ratesDB.getRates();
                if (!isValid(rates)) {
                    currencyDao.remove(ratesDB);
                    rates = requestRemote(ADD);
                }
                this.list = rates;
                this.date = ratesDB.getDate();
                if (isOld()) {
                    rates = requestRemote(UPDATE);
                }
            }
            this.list = rates;
            if (rates == null) {
                return getEmptyCurrency();
            }
        }
        return this.list;
    }

    /**
     * @return CurrencyRates with empty data
     */
    @Override
    public List<CurrencyRate> getEmptyCurrency() {
        List<CurrencyRate> rates = new ArrayList<>();
        for (Currency c : Currency.values()) {
            rates.add(new CurrencyRate(c.name()));
        }
        return rates;
    }

    /**
     *
     * @return CurrencyRates from DB
     */
    CurrencyDto requestFromDB(){
        LOG.info(LogEvent.create(SERVICE_NAME, "Request data from DB"));
        try {
            CurrencyDto dto = currencyDao.getCurrency();
            return dto;
        } catch (MongoException m) {
            my.weatherApp.model.Error error = new Error(SERVICE_NAME, m.toString(), m);
            LOG.error(error.toString());
            errorService.error(error);
        }
        return null;
    }

    /**
     * Request CurrencyRates from remote service
     * @param action what to do with requested Rates
     * @return List<CurrencyRate>
     */
    List<CurrencyRate> requestRemote(String action) {
        LOG.info(LogEvent.create(SERVICE_NAME, "Request data from remote service"));
        List<CurrencyRate> rates = bankParser.getCurrency();
        this.list = rates;
        this.date = LocalDate.now();

        if (rates != null){
            CurrencyDto newDto = new CurrencyDto(this.list, this.date);
            if (action.equals(ADD)) {
                currencyDao.addCurrency(newDto);
            }
            if (action.equals(UPDATE)) {
                currencyDao.update(newDto);
            }
        }
        return this.list;
    }

    /**
     * Check if rates from list is valid or not
     * @param list
     * @return false list don't contain USD or EUR, or list.size != 2
     */
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

    /**
     * Check if CurrencyRates is old or not
     * @return
     */
    boolean isOld() {
        boolean result = false;
        LocalDate now = LocalDate.now();
        if (now.isAfter(date)) result = true;
        return result;
    }

}
