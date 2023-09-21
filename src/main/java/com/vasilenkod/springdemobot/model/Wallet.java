package com.vasilenkod.springdemobot.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.checkerframework.checker.units.qual.C;

import java.math.BigDecimal;

@Entity
@Getter
@Setter
public class Wallet {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private long id;

    @OneToOne(mappedBy = "wallet", cascade = CascadeType.ALL)
    private User user;

    @Column(name = "rub_balance")
    private BigDecimal rubBalance;

    @Column(name = "usd_balance")
    private BigDecimal usdBalance;

    @Column(name = "euro_balance")
    private BigDecimal euroBalance;

    @Column(name = "cny_balance")
    private BigDecimal cnyBalance;

    public Wallet() {
        this.rubBalance = BigDecimal.valueOf(0);
        this.usdBalance = BigDecimal.valueOf(0);
        this.euroBalance = BigDecimal.valueOf(0);
        this.cnyBalance = BigDecimal.valueOf(0);
    }


    @Override
    public String toString() {
        return "RUB: " + rubBalance + "\n" +
                "USD: " + usdBalance + "\n" +
                "EUR: " + euroBalance + "\n" +
                "CNY: " + cnyBalance + "\n";
    }
}
