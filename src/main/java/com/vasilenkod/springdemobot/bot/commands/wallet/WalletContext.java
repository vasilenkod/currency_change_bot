package com.vasilenkod.springdemobot.bot.commands.wallet;

import com.vasilenkod.springdemobot.bot.Currency;
import com.vasilenkod.springdemobot.model.DataBaseApi;
import lombok.Getter;
import lombok.Setter;
import org.telegram.telegrambots.meta.api.objects.CallbackQuery;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;

import java.math.BigDecimal;

@Getter
@Setter
public class WalletContext {

    private WalletState state;
    private boolean isInputState = false;
    private DataBaseApi dataBaseApi;
    private String type;

    private Currency currency;
    private BigDecimal currencyAmount;

    private int messageId;

    public WalletContext(DataBaseApi dataBaseApi) {
        this.dataBaseApi = dataBaseApi;
    }

    public InlineKeyboardMarkup getKeyboard() {
        return state.getStateKeyboard();
    }

    public String getMessage(Message message) {
        return state.getStateMessage(message);
    }

    public WalletState goNext(CallbackQuery callbackQuery) {
        return state.goNext(callbackQuery);
    }
    public WalletState goBack() {
        return state.goBack();
    }
}