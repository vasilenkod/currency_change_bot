package com.vasilenkod.springdemobot.bot.commands.handlers;

import com.vasilenkod.springdemobot.bot.Currency;
import com.vasilenkod.springdemobot.bot.TelegramBot;
import com.vasilenkod.springdemobot.bot.commands.rate.RateContext;
import com.vasilenkod.springdemobot.bot.commands.rate.RateSelectCurrencyState;
import com.vasilenkod.springdemobot.bot.commands.rate.RateState;
import com.vasilenkod.springdemobot.bot.commands.rate.RatesApi;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.CallbackQuery;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;

@Component
public class RateCommandHandler {

    @Autowired
    @Lazy
    TelegramBot bot;

    RateContext rateContext;
    void handleRateCommand(Message message) {
        rateContext = new RateContext(new RatesApi());
        rateContext.setState(new RateSelectCurrencyState(rateContext));

        long chatId = message.getChatId();
        String messageText = rateContext.getMessage(message);
        InlineKeyboardMarkup keyboardMarkup = rateContext.getKeyboard();

        bot.sendMessage(chatId, messageText, keyboardMarkup);

    }

    void callbackQueryRateHandler(CallbackQuery callbackQuery) {
        RateState newState = null;
        if (callbackQuery.getData().endsWith("back")) {
            newState = rateContext.goBack();
        } else {

            String[] splitCallback = callbackQuery.getData().split("_");
            Currency currentCurrency = Currency.getCurrencyByString(splitCallback[splitCallback.length-1]);
            rateContext.setCurrentCurrency(currentCurrency);
            newState = rateContext.goNext(callbackQuery);
        }

        rateContext.setState(newState);

        long chatId = callbackQuery.getMessage().getChatId();
        int messageId = callbackQuery.getMessage().getMessageId();
        String messageText = rateContext.getMessage(callbackQuery.getMessage());
        InlineKeyboardMarkup keyboardMarkup = rateContext.getKeyboard();

        bot.editMessage(chatId, messageId, messageText, keyboardMarkup);
    }
}
