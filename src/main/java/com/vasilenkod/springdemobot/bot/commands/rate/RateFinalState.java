package com.vasilenkod.springdemobot.bot.commands.rate;

import com.vasilenkod.springdemobot.bot.Currency;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.CallbackQuery;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;

import java.util.ArrayList;
import java.util.List;
@Component
public class RateFinalState implements RateState{

    RateContext rateContext;

    public RateFinalState(RateContext rateContext) {
        this.rateContext = rateContext;
    }

    @Override
    public InlineKeyboardMarkup getStateKeyboard() {
        InlineKeyboardMarkup keyboardMarkup = new InlineKeyboardMarkup();
        List<List<InlineKeyboardButton>> rows = new ArrayList<>();

        InlineKeyboardButton backButton = new InlineKeyboardButton();
        backButton.setText("Назад");
        backButton.setCallbackData("bot_rate_final_back");

        rows.add(List.of(backButton));

        keyboardMarkup.setKeyboard(rows);

        return keyboardMarkup;
    }

    @Override
    public String getStateMessage(Message message) {
        StringBuilder messageText = new StringBuilder();
        Currency currency = rateContext.getCurrentCurrency();
        var rates =  rateContext.getRatesApi().getRates();
        var ratesToCurrency = rates.get(currency);
        for (var entry: ratesToCurrency.entrySet()) {
            messageText.append(entry.getKey().getTitle()).append("->").append(currency.getTitle()).append(" ").append(entry.getValue()).append("\n");
        }
        return messageText.toString();
    }

    @Override
    public RateState goNext(CallbackQuery callbackQuery) {
        return null;
    }

    @Override
    public RateState goBack() {
        return new RateSelectCurrencyState(rateContext);
    }
}
