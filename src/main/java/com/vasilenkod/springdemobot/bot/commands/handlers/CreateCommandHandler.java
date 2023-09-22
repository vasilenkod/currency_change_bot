package com.vasilenkod.springdemobot.bot.commands.handlers;

import com.vasilenkod.springdemobot.bot.Currency;
import com.vasilenkod.springdemobot.bot.TelegramBot;
import com.vasilenkod.springdemobot.bot.commands.create.CreateContext;
import com.vasilenkod.springdemobot.bot.commands.create.CreateFinalState;
import com.vasilenkod.springdemobot.bot.commands.create.CreateSelectFirstFiatState;
import com.vasilenkod.springdemobot.bot.commands.create.CreateState;
import com.vasilenkod.springdemobot.model.Change;
import com.vasilenkod.springdemobot.model.DataBaseApi;
import org.apache.commons.lang3.math.NumberUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.CallbackQuery;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;

import java.math.BigDecimal;
import java.math.RoundingMode;

@Component
public class CreateCommandHandler {

    @Autowired
    @Lazy
    TelegramBot bot;

    @Autowired
    DataBaseApi dataBaseApi;

    CreateContext createContext;



    void handleCreateCommand(Message message) {
        createContext = new CreateContext(dataBaseApi);
        createContext.setState(new CreateSelectFirstFiatState(createContext));
        String messageText = createContext.getMessage();
        InlineKeyboardMarkup keyboardMarkup = createContext.getKeyboard();
        long chatId = message.getChatId();
        Message newMessage = bot.sendMessage(chatId, messageText, keyboardMarkup);
        createContext.setMessageId(newMessage.getMessageId());
    }

    void callbackQueryCreateHandler(CallbackQuery callbackQuery) {

        CreateState newState;

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

        } else if (callbackQuery.getData().endsWith("fix")) {
            long telegramId = callbackQuery.getMessage().getChatId();

            dataBaseApi.removeFromWallet(telegramId,
                    createContext.getGiveCurrency(), createContext.getGiveCurrencyAmount());

            dataBaseApi.addToWallet(telegramId,
                    createContext.getGetCurrency(), createContext.getGetCurrencyAmount());

            Change change = new Change();
            change.setCurrencyFrom(createContext.getGiveCurrency());
            change.setCurrencyTo(createContext.getGetCurrency());
            change.setCurrencyFromValue(createContext.getGiveCurrencyAmount());
            change.setCurrencyToValue(createContext.getGetCurrencyAmount());
            dataBaseApi.changes().save(change);

            bot.deleteMessage(callbackQuery.getMessage().getChatId(), createContext.getMessageId());

            bot.sendMessage(callbackQuery.getMessage().getChatId(),
                    "-" + createContext.getGiveCurrencyAmount() + " " + createContext.getGiveCurrency().getTitle());

            bot.sendMessage(callbackQuery.getMessage().getChatId(),
                    "+" + createContext.getGetCurrencyAmount() + " " + createContext.getGetCurrency().getTitle());
            createContext = null;

            return;

        } else {
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

        BigDecimal storedAmount = createContext.getDataBaseApi().getCurrencyAmount(message.getChatId(),
                createContext.getGiveCurrency());

        if (NumberUtils.isCreatable(message.getText())) {
            currentAmount = new BigDecimal(message.getText());

        } else {
            Message newMessage = bot.sendMessage(message.getChatId(), "Вы ввели не число");
            bot.deleteMessage(message.getChatId(), message.getMessageId());
            createContext.getMessagesToDelete().add(newMessage.getMessageId());
            return;
        }

        if (currentAmount.compareTo(new BigDecimal("0.01")) < 0) {
            Message newMessage = bot.sendMessage(message.getChatId(), "Введите сумму больше 0");
            bot.deleteMessage(message.getChatId(), message.getMessageId());
            createContext.getMessagesToDelete().add(newMessage.getMessageId());
            return;
        }

        if (currentAmount.compareTo(storedAmount) > 0) {
            Message newMessage = bot.sendMessage(message.getChatId(),
                    "У вас нет столько средств на счете. Введите корректную сумму.");
            bot.deleteMessage(message.getChatId(), message.getMessageId());
            createContext.getMessagesToDelete().add(newMessage.getMessageId());
            return;
        }

        if (!createContext.getMessagesToDelete().isEmpty()) {
            for(int messageId : createContext.getMessagesToDelete()) {
                bot.deleteMessage(message.getChatId(), messageId);
            }
        }
        createContext.setGiveCurrencyAmount(currentAmount);

        BigDecimal currencyRate = createContext.getRates().
                getCurrencyToCurrencyRate(createContext.getGiveCurrency(), createContext.getGetCurrency());

        BigDecimal newValue = createContext.getGiveCurrencyAmount().
                divide(currencyRate, 2, RoundingMode.HALF_EVEN);

        if (newValue.compareTo(new BigDecimal("0.01")) < 0) {
            Message newMessage = bot.sendMessage(message.getChatId(),
                    "Количетсво получаемой валюты меньше 0.01. Введите большое количество меняемой валюты");
            bot.deleteMessage(message.getChatId(), message.getMessageId());
            createContext.getMessagesToDelete().add(newMessage.getMessageId());
            return;
        }

        createContext.setGetCurrencyAmount(newValue);

        bot.deleteMessage(message.getChatId(), message.getMessageId());
        CreateState finalState = new CreateFinalState(createContext);
        createContext.setState(finalState);

        long chatId = message.getChatId();
        String messageText = createContext.getMessage();
        InlineKeyboardMarkup keyboardMarkup = createContext.getKeyboard();
        bot.editMessage(chatId, createContext.getMessageId(), messageText, keyboardMarkup);

        createContext.setInputState(false);
    }
}
