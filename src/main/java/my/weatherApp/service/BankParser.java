package my.weatherApp.service;

import my.weatherApp.model.Currency;
import my.weatherApp.model.CurrencyRate;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class BankParser {

    private final static String URL = "https://www.sravni.ru/bank/sberbank-rossii/valjuty/moskva/";
    private final static String USD_ATTR = "/bank/sberbank-rossii/valjuty/usd/";
    private final static String EUR_ATTR = "/bank/sberbank-rossii/valjuty/eur/";
    private static boolean isFound = false;
    static List<CurrencyRate> list;

    static List<CurrencyRate> getCurrency(){
        List<CurrencyRate> newList = new ArrayList<>();
            try {
                isFound = false;
                Document doc = Jsoup.connect(URL).timeout(5000).get();
                Element table = findTable(doc.children());
                if (table != null) {
                    newList = parseTable(table);
                } else {
                    newList.add(new CurrencyRate(Currency.USD.toString(), "-", "-"));
                    newList.add(new CurrencyRate(Currency.EUR.toString(), "-", "-"));
                }
                list = newList;
            } catch (IOException e) {
                e.printStackTrace();
            }
        return list;
    }

    static Element findTable(Elements parents){

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

    static List<CurrencyRate> parseTable(Element e){
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

    static CurrencyRate createCurrencyRate(Elements tds){
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

}
