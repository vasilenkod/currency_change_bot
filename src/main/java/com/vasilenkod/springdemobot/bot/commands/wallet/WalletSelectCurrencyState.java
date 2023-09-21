package com.vasilenkod.springdemobot.bot.commands.wallet;

import org.telegram.telegrambots.meta.api.objects.CallbackQuery;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;

import java.util.ArrayList;
import java.util.List;

public class WalletSelectCurrencyState implements WalletState{

    private WalletContext walletContext;

    public WalletSelectCurrencyState(WalletContext walletContext) {
        this.walletContext = walletContext;
    }

    @Override
    public InlineKeyboardMarkup getStateKeyboard() {
        InlineKeyboardMarkup keyboardMarkup = new InlineKeyboardMarkup();
        List<List<InlineKeyboardButton>> rows = new ArrayList<>();

        InlineKeyboardButton rubButton = new InlineKeyboardButton();
        rubButton.setText("RUB");
        rubButton.setCallbackData("bot_wallet_currency_rub");

        InlineKeyboardButton usdButton = new InlineKeyboardButton();
        usdButton.setText("USD");
        usdButton.setCallbackData("bot_wallet_currency_add_usd");

        InlineKeyboardButton euroButton = new InlineKeyboardButton();
        euroButton.setText("EUR");
        euroButton.setCallbackData("bot_wallet_currency_eur");

        InlineKeyboardButton cnyButton = new InlineKeyboardButton();
        cnyButton.setText("CNY");
        cnyButton.setCallbackData("bot_wallet_currency_cny");

        InlineKeyboardButton backButton = new InlineKeyboardButton();
        backButton.setText("Назад");
        backButton.setCallbackData("bot_wallet_currency_back");

        rows.add(List.of(rubButton, usdButton));
        rows.add(List.of(euroButton, cnyButton));
        rows.add(List.of(backButton));

        keyboardMarkup.setKeyboard(rows);

        return keyboardMarkup;
    }

    @Override
    public String getStateMessage(Message message) {
        String messageToSend = "";
        if (walletContext.getType().equals("add")) {
            messageToSend = "Выберите валюту пополнения";
        } else if (walletContext.getType().equals("out")) {
            messageToSend = "Выберите валюту вывода";
        }
        return messageToSend;
    }

    @Override
    public WalletState goNext(CallbackQuery callbackQuery) {
        return new WalletChooseFiatState(walletContext);
    }

    @Override
    public WalletState goBack() {
        return new WalletSelectInOutState(walletContext);
    }
}
