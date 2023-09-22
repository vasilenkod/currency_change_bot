package com.vasilenkod.springdemobot.model;

import com.vasilenkod.springdemobot.bot.Currency;
import com.vasilenkod.springdemobot.model.repository.*;
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

    @Autowired
    ChangeRepository changeRepository;


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

    public ChangeRepository changes() {
        return changeRepository;
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

    public void removeFromWallet(long telegramId, Currency currency, BigDecimal value) {
        User user = users().findById(telegramId).get();
        Wallet wallet = user.getWallet();
        BigDecimal storedAmount = wallet.getBalanceByCurrency(currency);
        BigDecimal newAmount = storedAmount.subtract(value);
        wallet.setBalanceByCurrency(currency, newAmount);

        wallets().save(wallet);
    }

    public void addToWallet(long telegramId, Currency currency, BigDecimal value) {
        User user = users().findById(telegramId).get();
        Wallet wallet = user.getWallet();
        BigDecimal storedAmount = wallet.getBalanceByCurrency(currency);
        BigDecimal newAmount = storedAmount.add(value);
        wallet.setBalanceByCurrency(currency, newAmount);

        wallets().save(wallet);
    }

    public void processTransaction(long telegramId, Currency currencyFrom, BigDecimal valueFrom,
                                   Currency currencyTo, BigDecimal valueTo) {

        User user = users().findById(telegramId).get();
        Wallet wallet = user.getWallet();

        BigDecimal storedAmount = wallet.getBalanceByCurrency(currencyFrom);
        BigDecimal newAmount = storedAmount.subtract(valueFrom);
        wallet.setBalanceByCurrency(currencyFrom, newAmount);

        storedAmount = wallet.getBalanceByCurrency(currencyTo);
        newAmount = storedAmount.add(valueTo);
        wallet.setBalanceByCurrency(currencyTo, newAmount);

        wallets().save(wallet);
    }

    public void addChangeTransaction(Currency currencyFrom, BigDecimal valueFrom,
                                     Currency currencyTo, BigDecimal valueTo) {
        Change change = new Change();
        change.setCurrencyFrom(currencyFrom);
        change.setCurrencyTo(currencyTo);
        change.setCurrencyFromValue(valueFrom);
        change.setCurrencyToValue(valueTo);

        changes().save(change);
    }

    public void addDepositTransaction(long userId, Currency currency, BigDecimal value) {
        Deposit deposit = new Deposit();
        deposit.setUserId(userId);
        deposit.setCurrency(currency);
        deposit.setValue(value);

        deposits().save(deposit);
    }
    public void addWithdrawTransaction(long userId, Currency currency, BigDecimal value) {
        Withdraw withdraw = new Withdraw();
        withdraw.setUserId(userId);
        withdraw.setCurrency(currency);
        withdraw.setValue(value);

        withdraws().save(withdraw);
    }

}
