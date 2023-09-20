package com.vasilenkod.springdemobot.bot.commands.create;

import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;

public interface CreateState {

    InlineKeyboardMarkup getStateKeyboard();
    String getStateMessage();
    CreateState goNext();
    CreateState goBack();

}
