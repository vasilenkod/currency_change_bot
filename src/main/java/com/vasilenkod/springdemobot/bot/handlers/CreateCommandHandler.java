package com.vasilenkod.springdemobot.bot.handlers;

import com.vasilenkod.springdemobot.bot.Currency;
import com.vasilenkod.springdemobot.bot.TelegramBot;
import com.vasilenkod.springdemobot.bot.commands.create.CreateContext;
import com.vasilenkod.springdemobot.bot.commands.create.CreateFinalState;
import com.vasilenkod.springdemobot.bot.commands.create.CreateSelectFirstFiatState;
import com.vasilenkod.springdemobot.bot.commands.create.CreateState;
import com.vasilenkod.springdemobot.model.DataBaseApi;
import com.vasilenkod.springdemobot.model.User;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.math.NumberUtils;
import org.checkerframework.checker.units.qual.Current;
import org.glassfish.grizzly.http.util.TimeStamp;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Lazy;
import org.springframework.data.jpa.convert.threeten.Jsr310JpaConverters;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.CallbackQuery;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.sql.Timestamp;
import java.time.LocalDateTime;

@Slf4j
@Component
public class CreateCommandHandler {

    @Autowired
    @Lazy
    TelegramBot bot;

    @Autowired
    DataBaseApi dataBaseApi;

    @Autowired
    Session session;

    @Autowired
    CreateContext createContext;

    @Autowired
    ApplicationContext applicationContext;

    void handleCreateCommand(Message message) {

        if (dataBaseApi.users().findById(message.getFrom().getId()).isEmpty()) {
            bot.sendMessage(message.getChatId(), "Наберите команду /start, чтобы зарегистрироваться");
            return;
        }

        long telegramId = message.getFrom().getId();

        bot.deleteRepeatedCommands(message, session, telegramId);

        if (!session.getSessions().containsKey(telegramId)) {
            session.getSessions().put(telegramId, new Contexts());
        }

        createContext = applicationContext.getBean(CreateContext.class);
        createContext.setUserId(message.getFrom().getId());
        session.sessions.get(telegramId).setCreateContext(createContext);

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
            newState = handleSelectFirstFiatState(callbackQuery);

        } else if (callbackQuery.getData().startsWith("bot_create_get")) {
            newState = handleSelectSecondFiatState(callbackQuery);

        } else if (callbackQuery.getData().endsWith("type")) {
            newState = handleInputState();

        } else if (callbackQuery.getData().endsWith("fix")) {
            handleFinalState(callbackQuery);
            return;

        } else {
            newState = createContext.goNext();
        }

        if (newState == null) {
            log.error("current state is null");
            throw new RuntimeException("current state is null");
        }

        createContext.setState(newState);
        long chatId = callbackQuery.getMessage().getChatId();
        int messageId = callbackQuery.getMessage().getMessageId();
        String messageText = createContext.getMessage();
        InlineKeyboardMarkup keyboardMarkup = createContext.getKeyboard();

        bot.editMessage(chatId, messageId, messageText, keyboardMarkup);

    }

    private void handleFinalState(CallbackQuery callbackQuery) {
        long telegramId = callbackQuery.getMessage().getChatId();
        Timestamp timestamp = new Timestamp(System.currentTimeMillis());

        dataBaseApi.processTransaction(
                telegramId,
                createContext.getGiveCurrency(),
                createContext.getGiveCurrencyAmount(),
                createContext.getGetCurrency(),
                createContext.getGetCurrencyAmount()
        );

        dataBaseApi.addChangeTransaction(
                telegramId,
                createContext.getGiveCurrency(),
                createContext.getGiveCurrencyAmount(),
                createContext.getGetCurrency(),
                createContext.getGetCurrencyAmount(),
                timestamp
        );

        log.info("user " + callbackQuery.getMessage().getChatId() + " change " + createContext.getGiveCurrencyAmount() +
                " " + createContext.getGiveCurrency() + " to " + createContext.getGetCurrencyAmount() + " " +
                createContext.getGetCurrency());

        bot.deleteMessage(callbackQuery.getMessage().getChatId(), createContext.getMessageId());

        bot.sendMessage(callbackQuery.getMessage().getChatId(),
                "-" + createContext.getGiveCurrencyAmount() + " " + createContext.getGiveCurrency().getTitle());

        bot.sendMessage(callbackQuery.getMessage().getChatId(),
                "+" + createContext.getGetCurrencyAmount() + " " + createContext.getGetCurrency().getTitle());

        createContext = null;
        session.sessions.get(telegramId).setCreateContext(null);
    }

    private CreateState handleInputState() {
        CreateState newState;
        createContext.setInputState(true);
        newState = createContext.goNext();
        return newState;
    }

    private CreateState handleSelectSecondFiatState(CallbackQuery callbackQuery) {
        CreateState newState;
        newState = createContext.goNext();
        String[] splitCallback = callbackQuery.getData().split("_");
        Currency currentGetCurrency = Currency.getCurrencyByString(splitCallback[splitCallback.length-1]);
        createContext.setGetCurrency(currentGetCurrency);
        return newState;
    }

    private CreateState handleSelectFirstFiatState(CallbackQuery callbackQuery) {
        CreateState newState;
        newState = createContext.goNext();
        String[] splitCallback = callbackQuery.getData().split("_");
        Currency currentGiveCurrency = Currency.getCurrencyByString(splitCallback[splitCallback.length-1]);
        createContext.setGiveCurrency(currentGiveCurrency);
        return newState;
    }

    void createInputHandler(Message message) {

        BigDecimal currentAmount = checkInput(message);

        if (currentAmount == null) {
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
            NotifyUserAboutIncorrectInput(message, "Количетсво получаемой валюты меньше 0.01. Введите большое количество меняемой валюты");
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

    private BigDecimal checkInput(Message message) {
        BigDecimal currentAmount;
        BigDecimal storedAmount = createContext.getDataBaseApi().getCurrencyAmount(message.getChatId(),
                createContext.getGiveCurrency());

        if (NumberUtils.isCreatable(message.getText())) {
            currentAmount = new BigDecimal(message.getText());

        } else {
            NotifyUserAboutIncorrectInput(message, "Вы ввели не число");
            return null;
        }

        if (currentAmount.compareTo(new BigDecimal("0.01")) < 0) {
            NotifyUserAboutIncorrectInput(message, "Введите сумму больше 0");
            return null;
        }
        if (currentAmount.compareTo(storedAmount) > 0) {
            NotifyUserAboutIncorrectInput(message, "У вас нет столько средств на счете. Введите корректную сумму.");
            return null;
        }
        return currentAmount;
    }

    private void NotifyUserAboutIncorrectInput(Message message, String textToSend) {
        Message newMessage = bot.sendMessage(message.getChatId(), textToSend);
        bot.deleteMessage(message.getChatId(), message.getMessageId());
        createContext.getMessagesToDelete().add(newMessage.getMessageId());
    }
}
