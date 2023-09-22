package com.vasilenkod.springdemobot.bot.commands.create;

import com.vasilenkod.springdemobot.bot.Currency;
import com.vasilenkod.springdemobot.bot.commands.rate.RatesApi;
import com.vasilenkod.springdemobot.model.DataBaseApi;
import lombok.Getter;
import lombok.Setter;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
public class CreateContext {

    private DataBaseApi dataBaseApi;

    private RatesApi rates = new RatesApi();

    private CreateState state;

    boolean isInputState = false;

    private Currency giveCurrency;
    private Currency getCurrency;

    private BigDecimal giveCurrencyAmount;
    private BigDecimal getCurrencyAmount;

    private int messageId;
    private List<Integer> messagesToDelete = new ArrayList<>();

    public CreateContext(DataBaseApi dataBaseApi) {
        this.dataBaseApi = dataBaseApi;
    }

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
