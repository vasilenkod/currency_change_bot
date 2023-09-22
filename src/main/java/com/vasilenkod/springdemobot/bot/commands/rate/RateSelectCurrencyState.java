package com.vasilenkod.springdemobot.bot.commands.rate;

import org.telegram.telegrambots.meta.api.objects.CallbackQuery;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;

import java.util.ArrayList;
import java.util.List;

public class RateSelectCurrencyState implements RateState{

    private RateContext rateContext;

    public RateSelectCurrencyState(RateContext rateContext) {
        this.rateContext = rateContext;
    }

    @Override
    public InlineKeyboardMarkup getStateKeyboard() {
        InlineKeyboardMarkup keyboardMarkup = new InlineKeyboardMarkup();
        List<List<InlineKeyboardButton>> rows = new ArrayList<>();

        InlineKeyboardButton rubButton = new InlineKeyboardButton();
        rubButton.setText("RUB");
        rubButton.setCallbackData("bot_rate_rub");

        InlineKeyboardButton usdButton = new InlineKeyboardButton();
        usdButton.setText("USD");
        usdButton.setCallbackData("bot_rate_usd");

        InlineKeyboardButton euroButton = new InlineKeyboardButton();
        euroButton.setText("EUR");
        euroButton.setCallbackData("bot_rate_eur");

        InlineKeyboardButton cnyButton = new InlineKeyboardButton();
        cnyButton.setText("CNY");
        cnyButton.setCallbackData("bot_rate_cny");

        rows.add(List.of(rubButton, usdButton));
        rows.add(List.of(euroButton, cnyButton));

        keyboardMarkup.setKeyboard(rows);

        return keyboardMarkup;
    }

    @Override
    public String getStateMessage(Message message) {
        return "Выберите валюту";
    }

    @Override
    public RateState goNext(CallbackQuery callbackQuery) {
        return new RateFinalState(rateContext);
    }

    @Override
    public RateState goBack() {
        return null;
    }
}
