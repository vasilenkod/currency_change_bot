package com.vasilenkod.springdemobot.model;

import com.vasilenkod.springdemobot.bot.Currency;
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

    public BigDecimal getBalanceByCurrency(Currency currency) {
        BigDecimal balance = switch (currency) {
            case RUB -> getRubBalance();
            case USD -> getUsdBalance();
            case EUR -> getEuroBalance();
            case CNY -> getCnyBalance();
        };
        return balance;
    }

    public void setBalanceByCurrency(Currency currency, BigDecimal newBalance) {
        switch (currency) {
            case RUB -> setRubBalance(newBalance);
            case USD -> setUsdBalance(newBalance);
            case EUR -> setEuroBalance(newBalance);
            case CNY -> setCnyBalance(newBalance);
        };
    }


    @Override
    public String toString() {
        return "RUB: " + rubBalance + "\n" +
                "USD: " + usdBalance + "\n" +
                "EUR: " + euroBalance + "\n" +
                "CNY: " + cnyBalance + "\n";
    }


}
