package com.vasilenkod.springdemobot.bot.handler;

import com.vasilenkod.springdemobot.bot.BotMessagesToAnswer;
import com.vasilenkod.springdemobot.bot.Currency;
import com.vasilenkod.springdemobot.bot.TelegramBot;
import com.vasilenkod.springdemobot.bot.commands.wallet.WalletContext;
import com.vasilenkod.springdemobot.bot.commands.wallet.WalletSelectInOutState;
import com.vasilenkod.springdemobot.bot.commands.wallet.WalletState;
import com.vasilenkod.springdemobot.bot.commands.create.CreateState;
import com.vasilenkod.springdemobot.bot.commands.create.CreateContext;
import com.vasilenkod.springdemobot.bot.commands.create.CreateSelectFirstFiatState;
import com.vasilenkod.springdemobot.model.*;
import org.apache.commons.lang3.math.NumberUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.CallbackQuery;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;

import java.math.BigDecimal;


@Component
public class MessageHandler {


    @Autowired
    @Lazy
    private TelegramBot bot;

    @Autowired
    DataBaseApi dataBaseApi;

    @Autowired
    ApplicationContext applicationContext;

    private CreateContext createContext;

    private WalletContext walletContext;



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
                default -> {
                    if (createContext != null && createContext.isInputState()) {
                        createInputHandler(message);
                    } else if (walletContext != null && walletContext.isInputState()) {
                        walletInputHandler(message);
                    }
                }
            }
        } else if (update.hasCallbackQuery()) {
            CallbackQuery callbackQuery = update.getCallbackQuery();
            callbackQuerryHandler(callbackQuery);
        }
    }


    private void handleStartCommand(Message message) {
        if (dataBaseApi.users().findById(message.getFrom().getId()).isEmpty()) {
            User user = new User();
            user.setTelegramId(message.getFrom().getId());
            user.setFirstName(message.getFrom().getFirstName());
            user.setLastName(message.getFrom().getLastName());
            user.setUserName(message.getFrom().getUserName());
            user.setWallet(new Wallet());
            dataBaseApi.users().save(user);
        }
        bot.sendMessage(message.getFrom().getId(),
                "Вы успешно зарегистрировались!");
    }
    private void handleCreateCommand(Message message) {
        createContext = new CreateContext();
        createContext.setState(new CreateSelectFirstFiatState(createContext));
        String messageText = createContext.getMessage();
        InlineKeyboardMarkup keyboardMarkup = createContext.getKeyboard();
        long chatId = message.getChatId();
        bot.sendMessage(chatId, messageText, keyboardMarkup);
    }
    private void handleWalletCommand(Message message) {
        var user = dataBaseApi.users().findById(message.getFrom().getId());
        System.out.println(user.get());
        walletContext = new WalletContext(dataBaseApi);
        walletContext.setState(new WalletSelectInOutState(walletContext));
        String messageText = walletContext.getMessage(message);
        InlineKeyboardMarkup keyboardMarkup = walletContext.getKeyboard();
        long chatId = message.getChatId();
        bot.sendMessage(chatId, messageText, keyboardMarkup);
    }
    private void handleHelpCommand(Message message) {
        bot.sendMessage(message.getChatId(),
                BotMessagesToAnswer.helpMessage);
    }

    private void callbackQuerryHandler(CallbackQuery callbackQuery) {
        if (callbackQuery.getData().startsWith("bot_create")) {
            callbackQueryCreateHandler(callbackQuery);

        } else if (callbackQuery.getData().startsWith("bot_wallet")) {
            callbackQueryWalletHandler(callbackQuery);
        }
    }

    private void callbackQueryCreateHandler(CallbackQuery callbackQuery) {
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
    private void createInputHandler(Message message) {
        BigDecimal currentAmount;
        if (NumberUtils.isCreatable(message.getText())) {
            currentAmount = new BigDecimal(message.getText());
        } else {
            bot.sendMessage(message.getChatId(), "Вы ввели не число");
        }

        bot.sendMessage(message.getChatId(), message.getText());
    }
    private void callbackQueryWalletHandler(CallbackQuery callbackQuery) {
        WalletState newState = null;
        if (callbackQuery.getData().endsWith("back")) {
            newState = walletContext.goBack();
        }  else if (callbackQuery.getData().endsWith("add") ||
                callbackQuery.getData().endsWith("out")) {
            newState = walletContext.goNext(callbackQuery);
        }

        walletContext.setState(newState);
        long chatId = callbackQuery.getMessage().getChatId();
        int messageId = callbackQuery.getMessage().getMessageId();
        String messageText = walletContext.getMessage(callbackQuery.getMessage());
        InlineKeyboardMarkup keyboardMarkup = walletContext.getKeyboard();

        bot.editMessage(chatId, messageId, messageText, keyboardMarkup);
    }

    private void walletInputHandler(Message message) {

    }
}
