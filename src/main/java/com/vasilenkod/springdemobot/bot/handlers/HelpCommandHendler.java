package com.vasilenkod.springdemobot.bot.handlers;

import com.vasilenkod.springdemobot.bot.BotMessagesToAnswer;
import com.vasilenkod.springdemobot.bot.TelegramBot;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.Message;

@Component
public class HelpCommandHendler {

    @Autowired
    @Lazy
    TelegramBot bot;

    void handleHelpCommand(Message message) {
        bot.sendMessage(message.getChatId(),
                BotMessagesToAnswer.helpMessage);
    }
}
