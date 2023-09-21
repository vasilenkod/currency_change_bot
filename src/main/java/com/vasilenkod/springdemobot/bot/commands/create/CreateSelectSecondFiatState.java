package com.vasilenkod.springdemobot.bot.commands.create;

import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;

import java.util.ArrayList;
import java.util.List;


public class CreateSelectSecondFiatState implements CreateState{
    private CreateContext createContext;

    public CreateSelectSecondFiatState(CreateContext createContext) {
        this.createContext = createContext;
    }

    @Override
    public InlineKeyboardMarkup getStateKeyboard() {
        InlineKeyboardMarkup keyboardMarkup = new InlineKeyboardMarkup();
        List<List<InlineKeyboardButton>> rows = new ArrayList<>();

        InlineKeyboardButton rubButton = new InlineKeyboardButton();
        rubButton.setText("RUB");
        rubButton.setCallbackData("bot_create_get_rub");

        InlineKeyboardButton usdButton = new InlineKeyboardButton();
        usdButton.setText("USD");
        usdButton.setCallbackData("bot_create_get_usd");

        InlineKeyboardButton euroButton = new InlineKeyboardButton();
        euroButton.setText("EUR");
        euroButton.setCallbackData("bot_create_get_eur");

        InlineKeyboardButton cnyButton = new InlineKeyboardButton();
        cnyButton.setText("CNY");
        cnyButton.setCallbackData("bot_create_get_cny");

        InlineKeyboardButton backButton = new InlineKeyboardButton();
        backButton.setText("Назад");
        backButton.setCallbackData("bot_create_get_back");

        switch (createContext.getGiveCurrency()) {
            case RUB -> {
                rows.add(List.of(usdButton));
                rows.add(List.of(euroButton, cnyButton));
            }
            case USD -> {
                rows.add(List.of(rubButton));
                rows.add(List.of(euroButton, cnyButton));
            }
            case EUR -> {
                rows.add(List.of(rubButton, usdButton));
                rows.add(List.of(cnyButton));
            }
            case CNY -> {
                rows.add(List.of(rubButton, usdButton));
                rows.add(List.of(euroButton));
            }
        }
        rows.add(List.of(backButton));

        keyboardMarkup.setKeyboard(rows);

        return keyboardMarkup;
    }

    @Override
    public String getStateMessage() {
        return "Выберите валюту которую хотите получить";
    }

    @Override
    public CreateState goNext() {
        return new CreateChooseFiatState(createContext);
    }

    @Override
    public CreateState goBack() {
        return new CreateSelectFirstFiatState(createContext);
    }
}
