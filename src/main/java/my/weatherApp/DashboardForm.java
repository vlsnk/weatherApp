package my.weatherApp;

import com.vaadin.annotations.Theme;
import com.vaadin.data.HasValue;
import com.vaadin.external.org.slf4j.Logger;
import com.vaadin.external.org.slf4j.LoggerFactory;
import com.vaadin.ui.ProgressBar;
import my.weatherApp.model.City;
import my.weatherApp.model.CurrencyRate;
import my.weatherApp.model.Visitor;
import my.weatherApp.service.*;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;


@Theme("mytheme")
public class DashboardForm extends Weather {

    private static final Logger LOG = LoggerFactory.getLogger(DashboardForm.class);
    private CurrencyService currencyService = CurrencyServiceImpl.getInstance();
    private WeatherService weatherService = WeatherServiceImpl.getInstance();
    private VisitorService visitorService = VisitorServiceImpl.getInstance();
    private ErrorService errorService = ErrorService.getInstance();
    private static final String IP = "Ваш IP: ";
    private static final String DATE = "По состоянию на ";
    private static final String TODAY_WEATHER = "Текущая температура ";
    private static final String TOMORROW_WEATHER = "Температура на завтра ";
    private static final String SERVICE_NAME = "DASHBOARD";
    private static final String SERVICE_WEATHER = "WEATHER";
    private static final String SERVICE_CURRENCY = "CURRENCY";

    public DashboardForm(){
        currencyTable.setItems(currencyService.getEmptyCurrency());

        cityName.setItems(City.getNames());
        cityName.setValue(City.NOVOSIBIRSK.getName());
        cityName.setSelectedItem(City.NOVOSIBIRSK.getName());
        cityName.addValueChangeListener(new HasValue.ValueChangeListener<String>() {
            @Override
            public void valueChange(HasValue.ValueChangeEvent<String> valueChangeEvent) {
                cityName.setValue(valueChangeEvent.getValue());
            }
        });

        my.weatherApp.model.Weather w = weatherService.getEmptyWeather();
        todayWeather.setValue(TODAY_WEATHER + w.getTodayWeather());
        tomorrowWeather.setValue(TOMORROW_WEATHER + w.getTomorrowWeather());

        updateWeather.addClickListener(e -> {
            errorService.clearMessage(SERVICE_WEATHER);
            updateWeather();
        });

        updateCurrency.addClickListener(e -> {
            errorService.clearMessage(SERVICE_CURRENCY);
            updateCurrency();

        });
    }

    public void getDashBoard(String ip){
        ipInfo.setValue(IP + ip);
        dateInfo.setValue(DATE + getDate());
        updateCount();
        updateCurrency();
        updateWeather();
    }

    private void updateWeather(){
        String s = cityName.getSelectedItem().get();
        City n = City.getCity(s);
        my.weatherApp.model.Weather w = weatherService.getWeather(n);
        todayWeather.setValue(TODAY_WEATHER + w.getTodayWeather());
        tomorrowWeather.setValue(TOMORROW_WEATHER + w.getTomorrowWeather());
        updateErrors();
    }

    private void updateCurrency(){
        List<CurrencyRate> rate = currencyService.getCurrency();
        currencyTable.setItems(rate);
        updateErrors();
    }

    private void updateCount(){
        Visitor v = visitorService.getInfo();
        int result = v.getCount();
        counter.setValue(String.valueOf(result));
    }

    private void updateErrors(){
        errorService.fillError();
    }

    private String getDate(){
        DateFormat dateFormat = new SimpleDateFormat("dd.MM.yyyy HH:mm");
        Date date = new Date();
        return dateFormat.format(date);
    }

}
