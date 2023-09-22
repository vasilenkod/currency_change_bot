package com.vasilenkod.springdemobot.model.repository;

import com.vasilenkod.springdemobot.model.Change;
import org.springframework.data.repository.CrudRepository;

public interface ChangeRepository extends CrudRepository<Change, Long> {}
