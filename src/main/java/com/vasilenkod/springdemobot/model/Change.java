package com.vasilenkod.springdemobot.model;


import com.vasilenkod.springdemobot.bot.Currency;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;


@Entity(name = "Change")
@Getter
@Setter
public class Change {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private long transactionId;

    private Currency currencyFrom;

    private BigDecimal currencyFromValue;

    private Currency currencyTo;

    private BigDecimal getCurrencyToValue;
}
