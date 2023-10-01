package com.vasilenkod.springdemobot.bot.handlers;

import com.vasilenkod.springdemobot.bot.commands.create.CreateContext;
import com.vasilenkod.springdemobot.bot.commands.wallet.WalletContext;
import lombok.Getter;
import lombok.Setter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Getter
@Setter
public class Contexts {
    WalletContext walletContext;
    CreateContext createContext;
}
