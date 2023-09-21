package com.vasilenkod.springdemobot.bot.commands.wallet;

import com.vasilenkod.springdemobot.model.User;
import com.vasilenkod.springdemobot.model.Wallet;
import org.telegram.telegrambots.meta.api.objects.CallbackQuery;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;

import java.util.ArrayList;
import java.util.List;

public class WalletSelectInOutState implements WalletState{

    private WalletContext walletContext;

    public WalletSelectInOutState(WalletContext walletContext) {
        this.walletContext = walletContext;
    }

    @Override
    public InlineKeyboardMarkup getStateKeyboard() {
        InlineKeyboardMarkup keyboardMarkup = new InlineKeyboardMarkup();
        List<List<InlineKeyboardButton>> rows = new ArrayList<>();

        InlineKeyboardButton buttonAdd = new InlineKeyboardButton();
        buttonAdd.setText("Пополнить");
        buttonAdd.setCallbackData("bot_wallet_add");

        InlineKeyboardButton buttonOut = new InlineKeyboardButton();
        buttonOut.setText("Вывести");
        buttonOut.setCallbackData("bot_wallet_out");


        rows.add(List.of(buttonAdd, buttonOut));


        keyboardMarkup.setKeyboard(rows);

        return keyboardMarkup;
    }

    @Override
    public String getStateMessage(Message message) {
        long id = message.getChatId();
        var userOpt = walletContext.getDataBaseApi().users().findById(id);
        if (userOpt.isEmpty()) {
            throw new RuntimeException("user not found");
        }
        User user = userOpt.get();
        Wallet wallet = user.getWallet();
        return wallet.toString();



    }

    @Override
    public WalletState goNext(CallbackQuery callbackQuery) {
        if (callbackQuery.getData().endsWith("add")) {
            walletContext.setType("add");
        } else if (callbackQuery.getData().endsWith("out")) {
            walletContext.setType("out");
        } else {
            throw new IllegalArgumentException("state doesnt exist");
        }
        return new WalletSelectCurrencyState(walletContext);
    }

    @Override
    public WalletState goBack() {
        return null;
    }
}
