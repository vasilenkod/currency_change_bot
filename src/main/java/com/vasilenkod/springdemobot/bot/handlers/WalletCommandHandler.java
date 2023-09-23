package com.vasilenkod.springdemobot.bot.handlers;


import com.vasilenkod.springdemobot.bot.Currency;
import com.vasilenkod.springdemobot.bot.TelegramBot;
import com.vasilenkod.springdemobot.bot.commands.wallet.WalletContext;
import com.vasilenkod.springdemobot.bot.commands.wallet.WalletFinalState;
import com.vasilenkod.springdemobot.bot.commands.wallet.WalletSelectInOutState;
import com.vasilenkod.springdemobot.bot.commands.wallet.WalletState;
import com.vasilenkod.springdemobot.model.DataBaseApi;
import org.apache.commons.lang3.math.NumberUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.CallbackQuery;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;

import java.math.BigDecimal;
import java.sql.Timestamp;

@Component
public class WalletCommandHandler {

    @Autowired
    @Lazy
    TelegramBot bot;

    @Autowired
    DataBaseApi dataBaseApi;

    WalletContext walletContext;

    void handleWalletCommand(Message message) {
        walletContext = new WalletContext(dataBaseApi);
        walletContext.setState(new WalletSelectInOutState(walletContext));

        long chatId = message.getChatId();
        String messageText = walletContext.getMessage(message);
        InlineKeyboardMarkup keyboardMarkup = walletContext.getKeyboard();

        Message newMessage = bot.sendMessage(chatId, messageText, keyboardMarkup);
        walletContext.setMessageId(newMessage.getMessageId());
    }


    void callbackQueryWalletHandler(CallbackQuery callbackQuery) {

        WalletState newState;

        if (callbackQuery.getData().endsWith("back")) {
            newState = walletContext.goBack();

        } else if (callbackQuery.getData().endsWith("type")) {
            newState = handleInputState(callbackQuery);

        } else if (callbackQuery.getData().endsWith("all")) {
            newState = handleAllState(callbackQuery);

        } else if (callbackQuery.getData().startsWith("bot_wallet_currency")) {
            newState = handleSelectCurrencyState(callbackQuery);

        } else if (callbackQuery.getData().endsWith("fix")) {
            handleWalletFinalState(callbackQuery);
            return;

        } else {
            newState = walletContext.goNext(callbackQuery);
        }

        walletContext.setState(newState);

        long chatId = callbackQuery.getMessage().getChatId();
        int messageId = callbackQuery.getMessage().getMessageId();
        String messageText = walletContext.getMessage(callbackQuery.getMessage());
        InlineKeyboardMarkup keyboardMarkup = walletContext.getKeyboard();

        walletContext.setMessageId(messageId);

        bot.editMessage(chatId, messageId, messageText, keyboardMarkup);
    }

    private void handleWalletFinalState(CallbackQuery callbackQuery) {
        BigDecimal storedCurrencyAmount = dataBaseApi.getCurrencyAmount(callbackQuery.getMessage().getChatId(),
                walletContext.getCurrency());
        Timestamp timestamp = new Timestamp(System.currentTimeMillis());

        if (walletContext.getType().equals("out")) {

            BigDecimal sumCurrencyAmount = storedCurrencyAmount.subtract(walletContext.getCurrencyAmount());

            dataBaseApi.setCurrencyAmount(
                    callbackQuery.getMessage().getChatId(),
                    walletContext.getCurrency(),
                    sumCurrencyAmount
            );

            dataBaseApi.addWithdrawTransaction(
                    callbackQuery.getMessage().getChatId(),
                    walletContext.getCurrency(),
                    walletContext.getCurrencyAmount(),
                    timestamp
            );

            bot.deleteMessage(callbackQuery.getMessage().getChatId(), walletContext.getMessageId());
            bot.sendMessage(callbackQuery.getMessage().getChatId(),
                    "-" + walletContext.getCurrencyAmount() + " " + walletContext.getCurrency().getTitle());

        } else if (walletContext.getType().equals("add")) {

            BigDecimal sumCurrencyAmount = storedCurrencyAmount.add(walletContext.getCurrencyAmount());

            dataBaseApi.setCurrencyAmount(
                    callbackQuery.getMessage().getChatId(),
                    walletContext.getCurrency(),
                    sumCurrencyAmount
            );

            dataBaseApi.addDepositTransaction(
                    callbackQuery.getMessage().getChatId(),
                    walletContext.getCurrency(),
                    walletContext.getCurrencyAmount(),
                    timestamp
            );


            bot.deleteMessage(callbackQuery.getMessage().getChatId(), walletContext.getMessageId());
            bot.sendMessage(callbackQuery.getMessage().getChatId(),
                    "+" + walletContext.getCurrencyAmount() + " " + walletContext.getCurrency().getTitle());
        }

        walletContext = null;
    }

    private WalletState handleSelectCurrencyState(CallbackQuery callbackQuery) {
        WalletState newState;
        String[] splitCallback = callbackQuery.getData().split("_");
        Currency currentGetCurrency = Currency.getCurrencyByString(splitCallback[splitCallback.length-1]);
        walletContext.setCurrency(currentGetCurrency);
        newState = walletContext.goNext(callbackQuery);
        return newState;
    }

    private WalletState handleAllState(CallbackQuery callbackQuery) {
        WalletState newState;
        BigDecimal currencyAmount = walletContext.getDataBaseApi().
                getCurrencyAmount(callbackQuery.getMessage().getChatId(), walletContext.getCurrency());
        walletContext.setCurrencyAmount(currencyAmount);
        newState = walletContext.goNext(callbackQuery);
        return newState;
    }

    private WalletState handleInputState(CallbackQuery callbackQuery) {
        WalletState newState;
        newState = walletContext.goNext(callbackQuery);
        walletContext.setInputState(true);
        return newState;
    }

    void walletInputHandler(Message message) {

        BigDecimal currentAmount;
        BigDecimal storedAmount = walletContext.getDataBaseApi().getCurrencyAmount(message.getChatId(),
                walletContext.getCurrency());

        if (NumberUtils.isCreatable(message.getText())) {
            currentAmount = new BigDecimal(message.getText());

        } else {
            Message newMessage = bot.sendMessage(message.getChatId(), "Вы ввели не число. Введите число");
            bot.deleteMessage(message.getChatId(), message.getMessageId());
            walletContext.getMessagesToDelete().add(newMessage.getMessageId());
            return;
        }

        if (currentAmount.compareTo(new BigDecimal("0.01")) < 0) {
            Message newMessage = bot.sendMessage(message.getChatId(), "Введите сумму больше 0");
            bot.deleteMessage(message.getChatId(), message.getMessageId());
            walletContext.getMessagesToDelete().add(newMessage.getMessageId());
            return;
        }
        if (walletContext.getType().equals("out") && currentAmount.compareTo(storedAmount) > 0) {
            Message newMessage = bot.sendMessage(message.getChatId(), "У вас нет столько средств на счете. Введите корректную сумму.");
            bot.deleteMessage(message.getChatId(), message.getMessageId());
            walletContext.getMessagesToDelete().add(newMessage.getMessageId());
            return;
        }

        if (!walletContext.getMessagesToDelete().isEmpty()) {
            for(int messageId : walletContext.getMessagesToDelete()) {
                bot.deleteMessage(message.getChatId(), messageId);
            }
        }
        bot.deleteMessage(message.getChatId(), message.getMessageId());
        walletContext.setCurrencyAmount(currentAmount);
        WalletState finalState = new WalletFinalState(walletContext);
        walletContext.setState(finalState);

        long chatId = message.getChatId();
        String messageText = walletContext.getMessage(message);
        InlineKeyboardMarkup keyboardMarkup = walletContext.getKeyboard();
        bot.editMessage(chatId, walletContext.getMessageId(), messageText, keyboardMarkup);

        walletContext.setInputState(false);


    }

}
