package com.vasilenkod.springdemobot.bot.commands.wallet;

import org.telegram.telegrambots.meta.api.objects.CallbackQuery;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

public class WalletChooseFiatState implements WalletState{

    private WalletContext walletContext;

    public WalletChooseFiatState(WalletContext walletContext) {
        this.walletContext = walletContext;
    }

    @Override
    public InlineKeyboardMarkup getStateKeyboard() {
        InlineKeyboardMarkup keyboardMarkup = new InlineKeyboardMarkup();
        List<List<InlineKeyboardButton>> rows = new ArrayList<>();

        InlineKeyboardButton allButton = new InlineKeyboardButton();
        allButton.setText("Вывести все");
        allButton.setCallbackData("bot_wallet_choose_all");


        InlineKeyboardButton typeButton = new InlineKeyboardButton();
        typeButton.setText("Ввести количество");
        typeButton.setCallbackData("bot_wallet_choose_type");

        InlineKeyboardButton backButton = new InlineKeyboardButton();
        backButton.setText("Назад");
        backButton.setCallbackData("bot_wallet_choose_back");

        if (walletContext.getType().equals("out")) {
            rows.add(List.of(allButton));
        }
        rows.add(List.of(typeButton));
        rows.add(List.of(backButton));

        keyboardMarkup.setKeyboard(rows);

        return keyboardMarkup;

    }

    @Override
    public String getStateMessage(Message message) {
        BigDecimal currencyAmount = walletContext.getDataBaseApi().getCurrencyAmount(message.getChatId(),
                walletContext.getCurrency());
        String messageText = "Валюта: " + walletContext.getCurrency().getTitle() + "\n" +
                             "На счете: " +  currencyAmount;
        return messageText;
    }

    @Override
    public WalletState goNext(CallbackQuery callbackQuery) {
        if (callbackQuery.getData().endsWith("all")) {
            return new WalletFinalState(walletContext);
        } else if (callbackQuery.getData().endsWith("type")) {
            return new WalletInputState(walletContext);
        }
        throw new IllegalArgumentException("state not found");
    }

    @Override
    public WalletState goBack() {
        return new WalletSelectCurrencyState(walletContext);
    }
}
