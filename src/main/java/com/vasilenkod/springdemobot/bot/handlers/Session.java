package com.vasilenkod.springdemobot.bot.handlers;

import com.vasilenkod.springdemobot.bot.commands.create.CreateContext;
import com.vasilenkod.springdemobot.bot.commands.wallet.WalletContext;
import lombok.Getter;
import lombok.Setter;
import org.aspectj.lang.annotation.control.CodeGenerationHint;
import org.springframework.stereotype.Component;


@Component
@Getter
@Setter
public class Session {
    WalletContext walletContext;
    CreateContext createContext;


}
