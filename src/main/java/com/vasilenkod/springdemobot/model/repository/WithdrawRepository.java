package com.vasilenkod.springdemobot.model.repository;

import com.vasilenkod.springdemobot.model.Withdraw;
import org.springframework.data.repository.CrudRepository;

public interface WithdrawRepository extends CrudRepository<Withdraw, Long> {
}
