package com.vasilenkod.springdemobot.model.repository;

import com.vasilenkod.springdemobot.model.User;
import org.springframework.data.repository.CrudRepository;

public interface UserRepository extends CrudRepository<User, Long> {
}
