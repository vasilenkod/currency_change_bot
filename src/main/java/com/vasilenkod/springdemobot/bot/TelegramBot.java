package com.vasilenkod.springdemobot.bot;

import com.vasilenkod.springdemobot.bot.handlers.Contexts;
import com.vasilenkod.springdemobot.bot.handlers.MessageHandler;
import com.vasilenkod.springdemobot.bot.handlers.Session;
import com.vasilenkod.springdemobot.config.BotConfig;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.DeleteMessage;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.EditMessageText;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

@Slf4j
@Component
public class TelegramBot extends TelegramLongPollingBot {

    final BotConfig config;

    @Autowired
    private MessageHandler messageHandler;

    public TelegramBot(BotConfig config) {
        this.config = config;
    }

    @Override
    public String getBotUsername() {
        return config.getBotName();
    }

    @Override
    public String getBotToken() {
        return config.getToken();
    }

    @Override
    public void onUpdateReceived(Update update) {
        messageHandler.handleMessage(update);
    }

    public Message sendMessage(long chatId, String textToSend) {
        Message newMessage;
        SendMessage message = new SendMessage();
        message.setChatId(String.valueOf(chatId));
        message.setText(textToSend);
        try {
            newMessage = execute(message);
        } catch (TelegramApiException e) {
            log.error("Message can`t be send:" + e.getMessage());
            throw new RuntimeException(e.getMessage());
        }
        return newMessage;
    }

    public Message sendMessage(long chatId, String textToSend, InlineKeyboardMarkup keyboardMarkup) {
        Message newMessage;
        SendMessage message = new SendMessage();
        message.setChatId(String.valueOf(chatId));
        message.setText(textToSend);
        message.setReplyMarkup(keyboardMarkup);
        try {
            newMessage = execute(message);
        } catch (TelegramApiException e) {
            log.error("Message with keyboard can`t be send:" + e.getMessage());
            throw new RuntimeException(e.getMessage());
        }
        return newMessage;
    }

    public void editMessage(long chatId, int messageId, String textToSend, InlineKeyboardMarkup keyboardMarkup) {
        EditMessageText message = new EditMessageText();
        message.setChatId(String.valueOf(chatId));
        message.setMessageId(messageId);
        message.setText(textToSend);
        System.out.println(textToSend);
        message.setReplyMarkup(keyboardMarkup);
        try {
            execute(message);
        } catch (TelegramApiException e) {
            log.error("Message with keyboard can`t be edited:" + e.getMessage());
        }

    }

    public void deleteMessage(long chatId, int messageId) {
        DeleteMessage message = new DeleteMessage();
        message.setChatId(String.valueOf(chatId));
        message.setMessageId(messageId);
        try {
            execute(message);
        } catch (TelegramApiException e) {
            log.error("Message can`t be deleted:" + e.getMessage());
        }

    }

    public void deleteRepeatedCommands(Message message, Session session, long telegramId) {

        if (!session.getSessions().containsKey(telegramId)) {
            return;
        }

        Contexts contexts = session.getSessions().get(telegramId);
        if (contexts.getCreateContext() != null) {
            deleteMessage(message.getChatId(), contexts.getCreateContext().getMessageId());
            if (contexts.getCreateContext().getMessagesToDelete() != null) {
                for (var messageToDelete : contexts.getCreateContext().getMessagesToDelete()) {
                    deleteMessage(message.getChatId(), messageToDelete);
                }
            }
        }
        session.getSessions().get(telegramId).setCreateContext(null);

        if (contexts.getWalletContext() != null) {
            deleteMessage(message.getChatId(), contexts.getWalletContext().getMessageId());
            if (contexts.getWalletContext().getMessagesToDelete() != null) {
                for (var messageToDelete : contexts.getWalletContext().getMessagesToDelete()) {
                    deleteMessage(message.getChatId(), messageToDelete);
                }
            }
        }
        session.getSessions().get(telegramId).setWalletContext(null);
    }

}
