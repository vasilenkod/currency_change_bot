package com.vasilenkod.springdemobot.bot.handlers;
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
                """
                        Возможности Бота-Обменника:
                              
                              
                        🔄 Новый обмен /create
                        Данная команда позволяет осуществить:
                        - обмен валюты.
                           \s
                        💳 Кошелек /wallet
                        Покажет ваш внутренний электронный кошелёк с возможностью:
                        - пополнение внутреннего электронного кошелька.
                        - вывода средств с внутреннего электронного кошелька.
                            \s
                        💹 Таблица курсов валют /rate
                        
                        Покажет курсы валют
                        """);
    }
}
