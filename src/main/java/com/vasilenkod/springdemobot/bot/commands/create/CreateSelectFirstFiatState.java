package com.vasilenkod.springdemobot.bot.commands.create;

import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;

import java.util.ArrayList;
import java.util.List;

public class CreateSelectFirstFiatState implements CreateState{

    private CreateContext createContext;

    public CreateSelectFirstFiatState(CreateContext createContext) {
        this.createContext = createContext;
    }

    @Override
    public InlineKeyboardMarkup getStateKeyboard() {
        InlineKeyboardMarkup keyboardMarkup = new InlineKeyboardMarkup();
        List<List<InlineKeyboardButton>> rows = new ArrayList<>();

        InlineKeyboardButton rubButton = new InlineKeyboardButton();
        rubButton.setText("RUB");
        rubButton.setCallbackData("bot_create_give_rub");

        InlineKeyboardButton usdButton = new InlineKeyboardButton();
        usdButton.setText("USD");
        usdButton.setCallbackData("bot_create_give_usd");

        InlineKeyboardButton euroButton = new InlineKeyboardButton();
        euroButton.setText("EUR");
        euroButton.setCallbackData("bot_create_give_eur");

        InlineKeyboardButton cnyButton = new InlineKeyboardButton();
        cnyButton.setText("CNY");
        cnyButton.setCallbackData("bot_create_give_cny");

        rows.add(List.of(rubButton, usdButton));
        rows.add(List.of(euroButton, cnyButton));

        keyboardMarkup.setKeyboard(rows);

        return keyboardMarkup;
    }

    @Override
    public String getStateMessage() {
        return "Выберите валюту которую хотите обменять";
    }

    @Override
    public CreateState goNext() {

        return new CreateSelectSecondFiatState(createContext);
    }

    @Override
    public CreateState goBack() {
        return null;
    }
}
