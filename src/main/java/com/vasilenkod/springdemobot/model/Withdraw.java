package com.vasilenkod.springdemobot.model;

import com.vasilenkod.springdemobot.bot.Currency;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.glassfish.grizzly.http.util.TimeStamp;

import java.math.BigDecimal;
import java.sql.Timestamp;


@Entity(name = "withdraw")
@Getter
@Setter
public class Withdraw {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private long transactionId;

    private long userId;

    @Enumerated(EnumType.STRING)
    private Currency currency;

    private BigDecimal value;

    private Timestamp dateTime;
}
