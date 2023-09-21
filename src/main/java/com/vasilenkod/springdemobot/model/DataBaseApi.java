package com.vasilenkod.springdemobot.model;

import com.vasilenkod.springdemobot.bot.Currency;
import com.vasilenkod.springdemobot.model.repository.DepositRepository;
import com.vasilenkod.springdemobot.model.repository.UserRepository;
import com.vasilenkod.springdemobot.model.repository.WalletRepository;
import com.vasilenkod.springdemobot.model.repository.WithdrawRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;

@Component
public class DataBaseApi {
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private WalletRepository walletRepository;

    @Autowired
    private DepositRepository depositRepository;

    @Autowired
    private WithdrawRepository withdrawRepository;


    public UserRepository users() {
        return userRepository;
    }

    public WalletRepository wallets() {
        return walletRepository;
    }

    public DepositRepository deposits() {
        return depositRepository;
    }

    public WithdrawRepository withdraws() {
        return withdrawRepository;
    }

    public BigDecimal getCurrencyAmount(long telegramId, Currency currency) {
        User user = users().findById(telegramId).get();
        Wallet wallet = user.getWallet();
        BigDecimal currencyAmount;
        currencyAmount = switch(currency) {
            case RUB -> wallet.getRubBalance();
            case  USD -> wallet.getUsdBalance();
            case EUR -> wallet.getEuroBalance();
            case CNY -> wallet.getCnyBalance();
        };
        return currencyAmount;

    }

    public void setCurrencyAmount(long telegramId, Currency currency, BigDecimal newValue) {
        User user = users().findById(telegramId).get();
        Wallet wallet = user.getWallet();
        switch(currency) {
            case RUB -> wallet.setRubBalance(newValue);
            case  USD -> wallet.setUsdBalance(newValue);
            case EUR -> wallet.setEuroBalance(newValue);
            case CNY -> wallet.setCnyBalance(newValue);
        };
        wallets().save(wallet);

    }




}