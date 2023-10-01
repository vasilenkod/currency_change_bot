package com.vasilenkod.springdemobot.bot;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public enum Currency {
    RUB("RUB"),
    USD("USD"),
    EUR("EUR"),
    CNY("CNY");

    private final String title;

    Currency(String title) {
        this.title = title;
    }

    public String getTitle() {
        return title;
    }

    public static Currency getCurrencyByString(String title) {
        switch (title) {
            case "rub" -> {
                return Currency.RUB;
            }
            case "usd" -> {
                return Currency.USD;
            }
            case "eur" -> {
                return Currency.EUR;
            }
            case "cny" -> {
                return Currency.CNY;
            }
            default -> {
                log.error("currency not found");
                throw new IllegalArgumentException("currency not found");
            }
        }

    }


    @Override
    public String toString() {
        return "Currency{" +
                "title='" + title + '\'' +
                '}';
    }
}


