package com.vasilenkod.springdemobot.bot;

public enum Currency {
    RUB("rub"),
    USD("usd"),
    EUR("euro"),
    CNY("cny");

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
            case "euro" -> {
                return Currency.EUR;
            }
            case "cny" -> {
                return Currency.CNY;
            }
            default -> throw new IllegalArgumentException("currency not found");
        }

    }


    @Override
    public String toString() {
        return "Currency{" +
                "title='" + title + '\'' +
                '}';
    }
}


