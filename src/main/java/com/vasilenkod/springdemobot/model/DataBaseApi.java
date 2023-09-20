package com.vasilenkod.springdemobot.model;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class DataBaseApi {
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private WalletRepository walletRepository;

    public UserRepository users() {
        return userRepository;
    }

    public WalletRepository wallets() {
        return walletRepository;
    }
}
