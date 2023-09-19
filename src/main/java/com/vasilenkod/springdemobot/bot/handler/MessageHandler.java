package com.vasilenkod.springdemobot.bot.handler;

import com.vasilenkod.springdemobot.bot.BotMessagesToAnswer;
import com.vasilenkod.springdemobot.bot.TelegramBot;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.CallbackQuery;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;


@Component
public class MessageHandler {


    @Autowired
    @Lazy
    private TelegramBot bot;

    public void handleMessage(Update update) {
        if (update.hasMessage() && update.getMessage().hasText()) {
            Message message = update.getMessage();
            String messageText = message.getText();

            switch (messageText) {
                case "/start" -> {
                    handleStartCommand(message);
                }
                case "/create" -> {
                    handleCreateCommand(message);
                }
                case "/wallet" -> {
                    handleWalletCommand(message);
                }
                case "/help" -> {
                    handleHelpCommand(message);
                }
            }
        } else if (update.hasCallbackQuery()) {
            CallbackQuery callbackQuery = update.getCallbackQuery();
            callbackQuerryHandler(callbackQuery);
        }
    }


    private void handleStartCommand(Message message) {}
    private void handleCreateCommand(Message message) {}
    private void handleWalletCommand(Message message) {}
    private void handleHelpCommand(Message message) {
        bot.sendMessage(message.getChatId(),
                BotMessagesToAnswer.helpMessage);
    }

    private static void callbackQuerryHandler(CallbackQuery callbackQuery) {}
}
