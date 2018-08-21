package my.weatherApp;

import com.vaadin.annotations.Theme;
import com.vaadin.data.HasValue;
import my.weatherApp.model.City;
import my.weatherApp.model.CurrencyRate;
import my.weatherApp.service.*;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

@Theme("mytheme")
public class DashboardForm extends Weather {

    private CurrencyService currencyService = CurrencyServiceImpl.getInstance();
    private WeatherService weatherService = WeatherServiceImpl.getInstance();
    private VisitorService visitorService = VisitorServiceImpl.getInstance();
    private ErrorService errorService = ErrorService.getInstance();
    private static final String IP = "Ваш IP: ";
    private static final String DATE = "По состоянию на ";
    private static final String TODAY_WEATHER = "Текущая температура ";
    private static final String TOMORROW_WEATHER = "Температура на завтра ";


    public DashboardForm(){
        currencyTable.setItems(currencyService.getCurrency());
        cityName.setItems(City.getNames());
        cityName.setValue(City.NOVOSIBIRSK.getName());
        cityName.setSelectedItem(City.NOVOSIBIRSK.getName());
        cityName.addValueChangeListener(new HasValue.ValueChangeListener<String>() {
            @Override
            public void valueChange(HasValue.ValueChangeEvent<String> valueChangeEvent) {
                cityName.setValue(valueChangeEvent.getValue());
            }
        });
        updateWeather.addClickListener(e -> updateWeather());
        updateCurency.addClickListener(e -> updateCurrency());

    }
    void getDashBoard(String ip){
        ipInfo.setValue(IP + ip);
        dateInfo.setValue(DATE + getDate());
        counter.setValue(String.valueOf(visitorService.getInfo(ip).getCount()));
        updateCurrency();
        updateWeather();
    }

    void updateWeather(){
        String s = cityName.getSelectedItem().get();
        City n = City.getCity(s);
        my.weatherApp.model.Weather w = weatherService.getWeather(n);
        todayWeather.setValue(TODAY_WEATHER + w.getTodayWeather());
        tomorrowWeather.setValue(TOMORROW_WEATHER + w.getTomorrowWeather());
    }

    void updateCurrency(){
        List<CurrencyRate> rate = currencyService.getCurrency();
        currencyTable.setItems(rate);
    }

    String getDate(){
        DateFormat dateFormat = new SimpleDateFormat("dd.MM.yyyy HH:mm");
        Date date = new Date();
        return dateFormat.format(date);
    }
}
