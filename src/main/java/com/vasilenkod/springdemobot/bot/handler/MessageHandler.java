package com.vasilenkod.springdemobot.bot.handler;

import com.vasilenkod.springdemobot.bot.BotMessagesToAnswer;
import com.vasilenkod.springdemobot.bot.Currency;
import com.vasilenkod.springdemobot.bot.TelegramBot;
import com.vasilenkod.springdemobot.bot.commands.wallet.WalletContext;
import com.vasilenkod.springdemobot.bot.commands.wallet.WalletFinalState;
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
        }

        else if (callbackQuery.getData().endsWith("type")) {
            newState = walletContext.goNext(callbackQuery);
            walletContext.setInputState(true);
        } else if (callbackQuery.getData().endsWith("all")) {
            BigDecimal currencyAmount = walletContext.getDataBaseApi().
                    getCurrencyAmount(callbackQuery.getMessage().getChatId(), walletContext.getCurrency());
            walletContext.setCurrencyAmount(currencyAmount);
            newState = walletContext.goNext(callbackQuery);
        }

        else if (callbackQuery.getData().startsWith("bot_wallet_currency")) {
            String[] splitCallback = callbackQuery.getData().split("_");
            Currency currentGetCurrency = Currency.getCurrencyByString(splitCallback[splitCallback.length-1]);
            walletContext.setCurrency(currentGetCurrency);
            newState = walletContext.goNext(callbackQuery);

        } else if (callbackQuery.getData().endsWith("fix")) {
            if (walletContext.getType().equals("out")) {
                BigDecimal storedCurrencyAmount = dataBaseApi.getCurrencyAmount(callbackQuery.getMessage().getChatId(),
                        walletContext.getCurrency());
                BigDecimal sumCurrencyAmount = storedCurrencyAmount.subtract(walletContext.getCurrencyAmount());
                dataBaseApi.setCurrencyAmount(callbackQuery.getMessage().getChatId(), walletContext.getCurrency(),
                        sumCurrencyAmount);
                Withdraw withdraw = new Withdraw();
                withdraw.setUserId(callbackQuery.getMessage().getChatId());
                withdraw.setCurrency(walletContext.getCurrency());
                withdraw.setValue(walletContext.getCurrencyAmount());
                dataBaseApi.withdraws().save(withdraw);
                bot.deleteMessage(callbackQuery.getMessage().getChatId(), walletContext.getMessageId());
                walletContext = null;
                return;

            } else if (walletContext.getType().equals("add")) {
                BigDecimal storedCurrencyAmount = dataBaseApi.getCurrencyAmount(callbackQuery.getMessage().getChatId(),
                        walletContext.getCurrency());
                BigDecimal sumCurrencyAmount = storedCurrencyAmount.add(walletContext.getCurrencyAmount());
                dataBaseApi.setCurrencyAmount(callbackQuery.getMessage().getChatId(), walletContext.getCurrency(),
                        sumCurrencyAmount);
                Deposit deposit = new Deposit();
                deposit.setUserId(callbackQuery.getMessage().getChatId());
                deposit.setCurrency(walletContext.getCurrency());
                deposit.setValue(walletContext.getCurrencyAmount());
                dataBaseApi.deposits().save(deposit);
                bot.deleteMessage(callbackQuery.getMessage().getChatId(), walletContext.getMessageId());
                walletContext = null;
                return;
            }
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

    private void walletInputHandler(Message message) {
        BigDecimal currentAmount = BigDecimal.valueOf(0);
        BigDecimal storedAmount = walletContext.getDataBaseApi().getCurrencyAmount(message.getChatId(),
                walletContext.getCurrency());
        if (NumberUtils.isCreatable(message.getText())) {
            currentAmount = new BigDecimal(message.getText());

        } else {
            bot.sendMessage(message.getChatId(), "Вы ввели не число. Введите число");
            bot.deleteMessage(message.getChatId(), message.getMessageId());
            return;

        }

        if (walletContext.getType().equals("out") && currentAmount.compareTo(BigDecimal.valueOf(0)) <= 0) {
            bot.sendMessage(message.getChatId(), "Введите сумму больше 0");
            bot.deleteMessage(message.getChatId(), message.getMessageId());
            return;
        }
        if (walletContext.getType().equals("out") && currentAmount.compareTo(storedAmount) > 0) {
            bot.sendMessage(message.getChatId(), "У вас нет столько средств на счете. Введите корректную сумму.");
            bot.deleteMessage(message.getChatId(), message.getMessageId());
            return;
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
