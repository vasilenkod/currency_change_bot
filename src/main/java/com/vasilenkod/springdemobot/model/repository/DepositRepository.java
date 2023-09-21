package com.vasilenkod.springdemobot.model.repository;

import com.vasilenkod.springdemobot.model.Deposit;
import org.springframework.data.repository.CrudRepository;

public interface DepositRepository extends CrudRepository<Deposit, Long> {
}
