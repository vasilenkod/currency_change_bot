package com.vasilenkod.springdemobot.bot.commands.rate;

import com.vasilenkod.springdemobot.bot.Currency;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;

@Component
public class RatesApi {
    private Map<Currency, Map<Currency, BigDecimal>> rates;


    public RatesApi() {
        rates = new HashMap<>();

        Map<Currency, BigDecimal> rubToFiat = new HashMap<>();
        rates.put(Currency.RUB, rubToFiat);
        rubToFiat.put(Currency.USD, new BigDecimal("96.07"));
        rubToFiat.put(Currency.EUR, new BigDecimal("102.27"));
        rubToFiat.put(Currency.CNY, new BigDecimal("13.30"));

        Map<Currency, BigDecimal> usdToFiat = new HashMap<>();
        rates.put(Currency.USD, usdToFiat);
        usdToFiat.put(Currency.RUB, new BigDecimal("0.01"));
        usdToFiat.put(Currency.EUR, new BigDecimal("0.94"));
        usdToFiat.put(Currency.CNY, new BigDecimal("0.14"));


        Map<Currency, BigDecimal> eurToFiat = new HashMap<>();
        rates.put(Currency.EUR, eurToFiat);
        eurToFiat.put(Currency.RUB, new BigDecimal("0.0098"));
        eurToFiat.put(Currency.USD, new BigDecimal("1.06"));
        eurToFiat.put(Currency.CNY, new BigDecimal("0.13"));

        Map<Currency, BigDecimal> cnyToFiat = new HashMap<>();
        rates.put(Currency.CNY, cnyToFiat);
        cnyToFiat.put(Currency.RUB, new BigDecimal("0.076"));
        cnyToFiat.put(Currency.USD, new BigDecimal("7.30"));
        cnyToFiat.put(Currency.EUR, new BigDecimal("7.78"));

    }

    public BigDecimal getCurrencyToCurrencyRate(Currency c1, Currency c2) {
        return rates.get(c1).get(c2);
    }

    public Map<Currency, Map<Currency, BigDecimal>> getRates() {
        return rates;
    }


}
