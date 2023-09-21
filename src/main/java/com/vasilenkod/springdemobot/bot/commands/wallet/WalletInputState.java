package com.vasilenkod.springdemobot.bot.commands.wallet;

import org.telegram.telegrambots.meta.api.objects.CallbackQuery;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;

import java.util.ArrayList;
import java.util.List;

public class WalletInputState implements WalletState{

    private WalletContext walletContext;

    public WalletInputState(WalletContext walletContext) {
        this.walletContext = walletContext;
    }

    @Override
    public InlineKeyboardMarkup getStateKeyboard() {
        InlineKeyboardMarkup keyboardMarkup = new InlineKeyboardMarkup();
        List<List<InlineKeyboardButton>> rows = new ArrayList<>();

        InlineKeyboardButton backButton = new InlineKeyboardButton();
        backButton.setText("Назад");
        backButton.setCallbackData("bot_wallet_input_back");

        rows.add(List.of(backButton));

        keyboardMarkup.setKeyboard(rows);

        return keyboardMarkup;
    }

    @Override
    public String getStateMessage(Message message)  {
        return "Введите количество валюты";
    }

    @Override
    public WalletState goNext(CallbackQuery callbackQuery) {
        return new WalletFinalState(walletContext);
    }

    @Override
    public WalletState goBack() {
        return new WalletChooseFiatState(walletContext);
    }
}
