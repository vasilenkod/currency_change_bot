package com.vasilenkod.springdemobot.bot.commands.create;

import com.vasilenkod.springdemobot.bot.Currency;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;

import java.math.BigDecimal;

@Getter
@Setter
public class CreateContext {

    private CreateState state;

    private Currency giveCurrency;
    private Currency getCurrency;

    private BigDecimal giveCurrencyAmount;
    private BigDecimal getGiveCurrencyAmount;

    public InlineKeyboardMarkup getKeyboard() {
        return state.getStateKeyboard();
    }

    public String getMessage() {
        return state.getStateMessage();
    }

    public CreateState goNext() {
        return state.goNext();
    }
    public CreateState goBack() {
        return state.goBack();
    }
}
