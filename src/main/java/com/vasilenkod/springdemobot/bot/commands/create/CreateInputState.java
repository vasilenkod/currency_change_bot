package com.vasilenkod.springdemobot.bot.commands.create;

import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;

import java.util.ArrayList;
import java.util.List;

public class CreateInputState implements CreateState{

    private CreateContext createContext;

    public CreateInputState(CreateContext createContext) {
        this.createContext = createContext;
    }

    @Override
    public InlineKeyboardMarkup getStateKeyboard() {

        InlineKeyboardMarkup keyboardMarkup = new InlineKeyboardMarkup();
        List<List<InlineKeyboardButton>> rows = new ArrayList<>();

        InlineKeyboardButton backButton = new InlineKeyboardButton();
        backButton.setText("Назад");
        backButton.setCallbackData("bot_create_input_back");

        rows.add(List.of(backButton));

        keyboardMarkup.setKeyboard(rows);

        return keyboardMarkup;
    }

    @Override
    public String getStateMessage() {
        return "Введите количество валюты";
    }

    @Override
    public CreateState goNext() {
        return new CreateFinalState(createContext);
    }

    @Override
    public CreateState goBack() {
        return new CreateChooseFiatState(createContext);
    }
}
