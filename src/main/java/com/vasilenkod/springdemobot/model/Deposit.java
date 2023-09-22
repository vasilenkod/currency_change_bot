package com.vasilenkod.springdemobot.model;

import com.vasilenkod.springdemobot.bot.Currency;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Entity(name = "deposit")
@Getter
@Setter
public class Deposit {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private long transactionId;

    private long userId;

    @Enumerated(EnumType.STRING)
    private Currency currency;

    private BigDecimal value;
}
