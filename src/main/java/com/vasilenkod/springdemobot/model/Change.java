package com.vasilenkod.springdemobot.model;


import com.vasilenkod.springdemobot.bot.Currency;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.glassfish.grizzly.http.util.TimeStamp;

import java.math.BigDecimal;
import java.sql.Timestamp;


@Entity(name = "Change")
@Getter
@Setter
public class Change {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private long transactionId;

    @Enumerated(EnumType.STRING)
    private Currency currencyFrom;

    private BigDecimal currencyFromValue;

    @Enumerated(EnumType.STRING)
    private Currency currencyTo;

    private BigDecimal CurrencyToValue;

    private Timestamp dateTime;
}
