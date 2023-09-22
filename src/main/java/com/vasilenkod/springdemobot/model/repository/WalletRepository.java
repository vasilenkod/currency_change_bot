package com.vasilenkod.springdemobot.model.repository;

import com.vasilenkod.springdemobot.model.Wallet;
import org.springframework.data.repository.CrudRepository;

public interface WalletRepository extends CrudRepository<Wallet, Long> {}
