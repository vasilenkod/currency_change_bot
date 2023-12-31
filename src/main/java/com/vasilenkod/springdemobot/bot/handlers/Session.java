package com.vasilenkod.springdemobot.bot.handlers;

import com.vasilenkod.springdemobot.bot.commands.create.CreateContext;
import com.vasilenkod.springdemobot.bot.commands.wallet.WalletContext;
import com.vasilenkod.springdemobot.model.User;
import lombok.Getter;
import lombok.Setter;
import org.aspectj.lang.annotation.control.CodeGenerationHint;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;


@Component
@Getter
@Setter
public class Session {
    Map<Long, Contexts> sessions = new HashMap<>();

}
