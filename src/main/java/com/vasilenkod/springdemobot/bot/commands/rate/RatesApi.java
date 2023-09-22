package com.vasilenkod.springdemobot.bot.commands.rate;

import com.vasilenkod.springdemobot.bot.Currency;
import org.antlr.v4.runtime.misc.Pair;
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
        eurToFiat.put(Currency.RUB, new BigDecimal("0.098"));
        eurToFiat.put(Currency.USD, new BigDecimal("1.06"));
        eurToFiat.put(Currency.CNY, new BigDecimal("0.13"));

        Map<Currency, BigDecimal> cnyToFiat = new HashMap<>();
        rates.put(Currency.CNY, cnyToFiat);
        cnyToFiat.put(Currency.RUB, new BigDecimal("0.076"));
        cnyToFiat.put(Currency.USD, new BigDecimal("7.30"));
        cnyToFiat.put(Currency.EUR, new BigDecimal("7.78"));

//        rates.put(new Pair<>(Currency.RUB, Currency.USD), new BigDecimal("0.01"));
//        rates.put(new Pair<>(Currency.RUB, Currency.EUR), new BigDecimal("0.0098"));
//        rates.put(new Pair<>(Currency.RUB, Currency.CNY), new BigDecimal("0.076"));
//
//        rates.put(new Pair<>(Currency.USD, Currency.RUB), new BigDecimal("102.6"));
//        rates.put(new Pair<>(Currency.USD, Currency.EUR), new BigDecimal("0.94"));
//        rates.put(new Pair<>(Currency.USD, Currency.CNY ), new BigDecimal("7.30"));
//
//
//        rates.put(new Pair<>(Currency.EUR, Currency.RUB), new BigDecimal("102.27"));
//        rates.put(new Pair<>(Currency.EUR, Currency.USD), new BigDecimal("1.06"));
//        rates.put(new Pair<>(Currency.EUR, Currency.CNY), new BigDecimal("7.78"));
//
//        rates.put(new Pair<>(Currency.CNY, Currency.RUB), new BigDecimal("13.30"));
//        rates.put(new Pair<>(Currency.CNY, Currency.USD), new BigDecimal("0.14"));
//        rates.put(new Pair<>(Currency.CNY, Currency.EUR), new BigDecimal("0.13"));
    }

    public BigDecimal getCurrencyToCurrencyRate(Currency c1, Currency c2) {
        return rates.get(c1).get(c2);
    }

    public Map<Currency, Map<Currency, BigDecimal>> getRates() {
        return rates;
    }


}
