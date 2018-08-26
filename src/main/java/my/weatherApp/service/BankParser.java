package my.weatherApp.service;

import com.vaadin.external.org.slf4j.Logger;
import com.vaadin.external.org.slf4j.LoggerFactory;
import my.weatherApp.exception.InvalidServiceException;
import my.weatherApp.model.Currency;
import my.weatherApp.model.CurrencyRate;
import my.weatherApp.model.Error;
import my.weatherApp.model.LogEvent;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

public class BankParser {

    private static final Logger LOG = LoggerFactory.getLogger(BankParser.class);
    private static BankParser instance;
    private final static String URL = "url";
    private final static String USD_ATTR = "/bank/sberbank-rossii/valjuty/usd/";
    private final static String EUR_ATTR = "/bank/sberbank-rossii/valjuty/eur/";
    private final static String WORKING_LINK = "https://www.sravni.ru/bank/sberbank-rossii/valjuty/moskva/";
    private static final String PROPERTIES_FILE = "currency.properties";
    private static final String SERVICE_NAME = "CURRENCY";
    private final static String UNSUPPORTED_LINK = "Unsupported service link, set www.sravni.ru link";
    private static boolean isFound = false;
    private String link;
    private List<CurrencyRate> list;
    private static final ErrorService errorService = ErrorService.getInstance();
    private boolean ready = false;

    private BankParser(){
        LOG.info(LogEvent.create(SERVICE_NAME, "Create BankParser"));
        loadProperties();
    }

    public static BankParser getInstance(){
        if (instance == null) {
            instance = new BankParser();
        }
        return instance;
    }

    /**
     * Request currency rates from remote service
     * @return currency rates or null
     */
    public List<CurrencyRate> getCurrency(){
        list = null;
        try {
            if (!ready) {
                throw new InvalidServiceException("Unsupported service, set www.sravni.ru link");
            }
            isFound = false;
            Document doc = Jsoup.connect(link).timeout(30000).get();
            Element table = findTable(doc.children());
            if (table != null) {
                list = parseTable(table);
            }
            LOG.info(LogEvent.create(SERVICE_NAME, "Request currency rates successfully"));
        } catch (IOException|InvalidServiceException e) {
            Error error = new Error(SERVICE_NAME, "Error to request currency rates - " + e.toString(), e);
            LOG.error(error.toString());
            errorService.error(error);
        }
        return list;
    }

    /**
     * Search table inside Elements
     * @param parents
     * @return Element with table
    */
    private Element findTable(Elements parents){

        for (Element e : parents) {
            if (isFound) break;
            Elements table = e.getElementsByTag("table");
            if (table != null || !table.forms().isEmpty()){
                return table.first();
            } else {
                return findTable(e.children());
            }
        }
        return null;
    }

    /**
     * Search currency rate inside table
     * @param e Element with table
     * @return currency rates
     */
    private List<CurrencyRate> parseTable(Element e){
        List<CurrencyRate> list = new ArrayList<>();
        if (e.attr("class").equalsIgnoreCase("table-light bank-currency__table table-light--big-cell")){
            Element tbody = e.getElementsByTag("tbody").first();
            Elements trs = tbody.getElementsByTag("tr");
            trs.remove(0);

            for (Element tr : trs) {
                Elements tds = tr.getElementsByTag("td");
                tds.remove(3);
                CurrencyRate c = createCurrencyRate(tds);
                list.add(c);
            }
            isFound = true;
        }
        return list;
    }

    /**
     * Create currency rate from Elements
     * @param tds
     * @return
     */
    private CurrencyRate createCurrencyRate(Elements tds){
        String attr = tds.first().getElementsByTag("a").attr("href");
        CurrencyRate currencyRate = new CurrencyRate();
        if (attr.equalsIgnoreCase(USD_ATTR)){
            currencyRate.setName(Currency.USD.toString());
        }
        if (attr.equalsIgnoreCase(EUR_ATTR)){
            currencyRate.setName(Currency.EUR.toString());
        }

        String buyPrice = tds.get(1).textNodes().get(0).text();
        currencyRate.setByuPrice(buyPrice);
        
        String salePrice = tds.get(2).textNodes().get(0).text();
        currencyRate.setSalePrice(salePrice);
        return currencyRate;
    }

    /**
     * Load properties from file
     */
    private void loadProperties(){
        Properties p = new Properties();
        try (InputStream stream = WeatherConnector.class.getClassLoader().getResourceAsStream(PROPERTIES_FILE)) {

            p.load(stream);
            String varLink = p.getProperty(URL);

            if (isValidProperty(varLink)) {
                if (varLink.equalsIgnoreCase(WORKING_LINK)) {
                    link = varLink;

                } else throw new InvalidServiceException(varLink + " - " + UNSUPPORTED_LINK);
            }
            ready = true;
            LOG.info(LogEvent.create(SERVICE_NAME, "Load properties successfully"));
        } catch (IOException|NullPointerException|InvalidServiceException e) {
            String msg = "Error to read currency properties: " + e.toString();
            Error error = new Error(SERVICE_NAME, msg, e);
            errorService.error(error);
            LOG.error(error.toString());
            ready = false;
        }
    }

    static boolean isValidProperty(String s){
        boolean b1 = s != null;
        boolean b2 = !s.isEmpty();
        return b1 && b2;
    }
}
