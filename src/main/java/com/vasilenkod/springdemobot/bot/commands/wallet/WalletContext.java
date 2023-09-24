package com.vasilenkod.springdemobot.bot.commands.wallet;

import com.vasilenkod.springdemobot.bot.Currency;
import com.vasilenkod.springdemobot.model.DataBaseApi;
import lombok.Getter;
import lombok.Setter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.CallbackQuery;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@Component
@Scope("prototype")
public class WalletContext {
    private DataBaseApi dataBaseApi;

    private WalletState state;
    private boolean isInputState = false;
    private String type;

    private Currency currency;
    private BigDecimal currencyAmount;

    private int messageId;
    private List<Integer> messagesToDelete = new ArrayList<>();

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