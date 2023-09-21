package com.vasilenkod.springdemobot.bot.handler;

import com.vasilenkod.springdemobot.bot.Currency;
import com.vasilenkod.springdemobot.bot.TelegramBot;
import com.vasilenkod.springdemobot.bot.commands.create.CreateContext;
import com.vasilenkod.springdemobot.bot.commands.create.CreateSelectFirstFiatState;
import com.vasilenkod.springdemobot.bot.commands.create.CreateState;
import org.apache.commons.lang3.math.NumberUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.CallbackQuery;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;

import java.math.BigDecimal;

@Component
public class CreateCommandHandler {

    @Autowired
    @Lazy
    TelegramBot bot;

    CreateContext createContext;

    void handleCreateCommand(Message message) {
        createContext = new CreateContext();
        createContext.setState(new CreateSelectFirstFiatState(createContext));
        String messageText = createContext.getMessage();
        InlineKeyboardMarkup keyboardMarkup = createContext.getKeyboard();
        long chatId = message.getChatId();
        bot.sendMessage(chatId, messageText, keyboardMarkup);
    }

    void callbackQueryCreateHandler(CallbackQuery callbackQuery) {
        CreateState newState = null;

        if (callbackQuery.getData().endsWith("back")) {

            newState = createContext.goBack();

        } else if (callbackQuery.getData().startsWith("bot_create_give")) {

            newState = createContext.goNext();
            String[] splitCallback = callbackQuery.getData().split("_");
            Currency currentGiveCurrency = Currency.getCurrencyByString(splitCallback[splitCallback.length-1]);
            createContext.setGiveCurrency(currentGiveCurrency);

        } else if (callbackQuery.getData().startsWith("bot_create_get")) {

            newState = createContext.goNext();
            String[] splitCallback = callbackQuery.getData().split("_");
            Currency currentGetCurrency = Currency.getCurrencyByString(splitCallback[splitCallback.length-1]);
            createContext.setGetCurrency(currentGetCurrency);

        } else if (callbackQuery.getData().endsWith("type")) {
            createContext.setInputState(true);
            newState = createContext.goNext();
        }
        if (newState == null) {

            throw new RuntimeException("current state is null");
        }

        createContext.setState(newState);
        long chatId = callbackQuery.getMessage().getChatId();
        int messageId = callbackQuery.getMessage().getMessageId();
        String messageText = createContext.getMessage();
        InlineKeyboardMarkup keyboardMarkup = createContext.getKeyboard();

        bot.editMessage(chatId, messageId, messageText, keyboardMarkup);

    }

    void createInputHandler(Message message) {
        BigDecimal currentAmount;
        if (NumberUtils.isCreatable(message.getText())) {
            currentAmount = new BigDecimal(message.getText());
        } else {
            bot.sendMessage(message.getChatId(), "Вы ввели не число");
        }

        bot.sendMessage(message.getChatId(), message.getText());
    }
}
