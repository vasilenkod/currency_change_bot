package com.vasilenkod.springdemobot.bot.commands.rate;

import com.vasilenkod.springdemobot.bot.Currency;
import com.vasilenkod.springdemobot.bot.commands.wallet.WalletState;
import lombok.Getter;
import lombok.Setter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.CallbackQuery;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;

@Getter
@Setter
@Component
public class RateContext {

    private final RatesApi ratesApi;
    private RateState state;

    private Currency currentCurrency;

    public RateContext(RatesApi ratesApi) {
        this.ratesApi = ratesApi;
    }

    public InlineKeyboardMarkup getKeyboard() {
        return state.getStateKeyboard();
    }

    public String getMessage(Message message) {
        return state.getStateMessage(message);
    }

    public RateState goNext(CallbackQuery callbackQuery) {
        return state.goNext(callbackQuery);
    }
    public RateState goBack() {
        return state.goBack();
    }


}
