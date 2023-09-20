package com.vasilenkod.springdemobot.bot.commands.create;

import org.checkerframework.checker.units.qual.A;
import org.checkerframework.checker.units.qual.C;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;

import java.util.ArrayList;
import java.util.List;

public class ChooseFiatState implements CreateState{

    private CreateContext createContext;

    public ChooseFiatState(CreateContext createContext) {
        this.createContext = createContext;
    }

    @Override
    public InlineKeyboardMarkup getStateKeyboard() {
        InlineKeyboardMarkup keyboardMarkup = new InlineKeyboardMarkup();
        List<List<InlineKeyboardButton>> rows = new ArrayList<>();

        InlineKeyboardButton typeButton = new InlineKeyboardButton();
        typeButton.setText("Ввести количество");
        typeButton.setCallbackData("bot_create_choose_type");

        InlineKeyboardButton backButton = new InlineKeyboardButton();
        backButton.setText("Назад");
        backButton.setCallbackData("bot_create_choose_back");

        rows.add(List.of(typeButton));
        rows.add(List.of(backButton));

        keyboardMarkup.setKeyboard(rows);
        return keyboardMarkup;
    }

    @Override
    public String getStateMessage() {
        return "Вы отдаете: " + createContext.getGiveCurrency() + "\n" +
                "Вы получате: " + createContext.getGetCurrency();
    }

    @Override
    public CreateState goNext() {
        return null;
    }

    @Override
    public CreateState goBack() {
        return new SelectSecondFiatState(createContext);
    }
}
