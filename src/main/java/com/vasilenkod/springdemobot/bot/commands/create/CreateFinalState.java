package com.vasilenkod.springdemobot.bot.commands.create;

import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.List;

public class CreateFinalState implements CreateState{

    private CreateContext createContext;

    public CreateFinalState(CreateContext createContext) {
        this.createContext = createContext;
    }

    @Override
    public InlineKeyboardMarkup getStateKeyboard() {
        InlineKeyboardMarkup keyboardMarkup = new InlineKeyboardMarkup();
        List<List<InlineKeyboardButton>> rows = new ArrayList<>();

        InlineKeyboardButton backButton = new InlineKeyboardButton();
        backButton.setText("Назад");
        backButton.setCallbackData("bot_create_final_back");

        InlineKeyboardButton fixButton = new InlineKeyboardButton();
        fixButton.setText("Подтвердить");
        fixButton.setCallbackData("bot_create_final_fix");


        rows.add(List.of(fixButton, backButton));

        keyboardMarkup.setKeyboard(rows);

        return keyboardMarkup;
    }

    @Override
    public String getStateMessage() {
        BigDecimal currencyRate = createContext.getRates().
                getCurrencyToCurrencyRate(createContext.getGiveCurrency(), createContext.getGetCurrency());

        System.out.println(createContext.getGiveCurrencyAmount());

        createContext.setGetCurrencyAmount(createContext.getGiveCurrencyAmount().
                divide(currencyRate, 3, RoundingMode.HALF_EVEN));

        System.out.println(createContext.getGiveCurrency().getTitle() + createContext.getGetCurrency().getTitle());
        String messageText = "";
        messageText += "Вы отдаете: " + createContext.getGiveCurrency().getTitle() + " " +
                createContext.getGiveCurrencyAmount() + "\n";
        messageText += "Вы получаете: " + createContext.getGetCurrency().getTitle() + " " +
                createContext.getGetCurrencyAmount();

        return messageText;

    }

    @Override
    public CreateState goNext() {
        return null;
    }

    @Override
    public CreateState goBack() {
        return new CreateChooseFiatState(createContext);
    }
}
