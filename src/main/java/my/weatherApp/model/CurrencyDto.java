package my.weatherApp.model;

import java.time.LocalDate;
import java.util.List;

public class CurrencyDto {

    private LocalDate date;
    private List<CurrencyRate> rates;

    public CurrencyDto(List<CurrencyRate> rates, LocalDate date) {
        this.date = date;
        this.rates = rates;
    }

    public CurrencyDto(List<CurrencyRate> rates, String date) {
        if (date == null) {
            this.date = LocalDate.now().minusDays(1);
        } else {
            this.date = LocalDate.parse(date);
        }
        this.rates = rates;
    }

    public LocalDate getDate() {
        return date;
    }

    public void setDate(LocalDate date) {
        this.date = date;
    }

    public List<CurrencyRate> getRates() {
        return rates;
    }

    public void setRates(List<CurrencyRate> rates) {
        this.rates = rates;
    }
}
