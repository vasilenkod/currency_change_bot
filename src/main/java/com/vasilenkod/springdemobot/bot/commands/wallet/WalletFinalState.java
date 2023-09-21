package com.vasilenkod.springdemobot.bot.commands.wallet;

import org.telegram.telegrambots.meta.api.objects.CallbackQuery;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;

import java.util.ArrayList;
import java.util.List;

public class WalletFinalState implements WalletState{

    private WalletContext walletContext;

    public WalletFinalState(WalletContext walletContext) {
        this.walletContext = walletContext;
    }

    @Override
    public InlineKeyboardMarkup getStateKeyboard() {
        InlineKeyboardMarkup keyboardMarkup = new InlineKeyboardMarkup();
        List<List<InlineKeyboardButton>> rows = new ArrayList<>();

        InlineKeyboardButton backButton = new InlineKeyboardButton();
        backButton.setText("Назад");
        backButton.setCallbackData("bot_wallet_final_back");

        InlineKeyboardButton fixButton = new InlineKeyboardButton();
        fixButton.setText("Подтвердить");
        fixButton.setCallbackData("bot_wallet_final_fix");


        rows.add(List.of(fixButton, backButton));


        keyboardMarkup.setKeyboard(rows);

        return keyboardMarkup;
    }

    @Override
    public String getStateMessage(Message message) {
        String messageText = "";
        if (walletContext.getType().equals("add")) {
            messageText += "Вы получаете: " + walletContext.getCurrencyAmount() + " " +
                    walletContext.getCurrency().getTitle();
        } else if(walletContext.getType().equals("out")) {
            messageText += "Вы выводите: " + walletContext.getCurrencyAmount() + " " +
                    walletContext.getCurrency().getTitle();
        }
        return messageText;
    }

    @Override
    public WalletState goNext(CallbackQuery callbackQuery) {
        return null;
    }

    @Override
    public WalletState goBack() {
        return new WalletChooseFiatState(walletContext);
    }
}
