package com.vasilenkod.springdemobot.bot.handlers;

import com.vasilenkod.springdemobot.bot.TelegramBot;
import com.vasilenkod.springdemobot.model.DataBaseApi;
import com.vasilenkod.springdemobot.model.User;
import com.vasilenkod.springdemobot.model.Wallet;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.Message;

@Slf4j
@Component
public class StartCommandHandler {

    @Autowired
    @Lazy
    TelegramBot bot;

    @Autowired
    DataBaseApi dataBaseApi;

    void handleStartCommand(Message message) {
        if (dataBaseApi.users().findById(message.getFrom().getId()).isEmpty()) {
            User user = new User();
            user.setTelegramId(message.getFrom().getId());
            user.setFirstName(message.getFrom().getFirstName());
            user.setLastName(message.getFrom().getLastName());
            user.setUserName(message.getFrom().getUserName());
            user.setWallet(new Wallet());
            dataBaseApi.users().save(user);
            log.info("user saved: " + user);
        }
        bot.sendMessage(message.getFrom().getId(),
                "Вы успешно зарегистрировались!");
    }
}
