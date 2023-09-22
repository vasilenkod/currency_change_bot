package com.vasilenkod.springdemobot.bot.commands.rate;

import com.vasilenkod.springdemobot.bot.commands.wallet.WalletState;
import org.telegram.telegrambots.meta.api.objects.CallbackQuery;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;

public interface RateState {
    InlineKeyboardMarkup getStateKeyboard();
    String getStateMessage(Message message);
    RateState goNext(CallbackQuery callbackQuery);
    RateState goBack();
}
