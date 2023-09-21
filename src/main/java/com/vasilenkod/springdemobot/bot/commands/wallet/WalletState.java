package com.vasilenkod.springdemobot.bot.commands.wallet;

import org.telegram.telegrambots.meta.api.objects.CallbackQuery;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;

public interface WalletState {
    InlineKeyboardMarkup getStateKeyboard();
    String getStateMessage(Message message);
    WalletState goNext(CallbackQuery callbackQuery);
    WalletState goBack();

}