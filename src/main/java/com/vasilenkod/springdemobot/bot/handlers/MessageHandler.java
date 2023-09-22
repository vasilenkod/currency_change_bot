package com.vasilenkod.springdemobot.bot.handlers;


import org.checkerframework.checker.units.qual.A;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.CallbackQuery;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;




@Component
public class MessageHandler {

    @Autowired
    private StartCommandHandler startCommandHandler;

    @Autowired
    private CreateCommandHandler createCommandHandler;

    @Autowired
    private WalletCommandHandler walletCommandHandler;

    @Autowired
    private RateCommandHandler rateCommandHandler;

    @Autowired
    private HelpCommandHendler helpCommandHendler;



    public void handleMessage(Update update) {
        if (update.hasMessage() && update.getMessage().hasText()) {
            Message message = update.getMessage();
            String messageText = message.getText();

            //обработчик комманд бота
            switch (messageText) {
                case "/start" -> startCommandHandler.handleStartCommand(message);
                case "/create" -> createCommandHandler.handleCreateCommand(message);
                case "/wallet" -> walletCommandHandler.handleWalletCommand(message);
                case "/rate" -> rateCommandHandler.handleRateCommand(message);
                case "/help" -> helpCommandHendler.handleHelpCommand(message);
                
                //обработчик пользовательского текста
                default -> {
                    if (createCommandHandler.createContext != null &&
                            createCommandHandler.createContext.isInputState()) {

                        createCommandHandler.createInputHandler(message);

                    } else if (walletCommandHandler.walletContext != null &&
                            walletCommandHandler.walletContext.isInputState()) {

                        walletCommandHandler.walletInputHandler(message);
                    }
                }
            }

            //обработчик кнопок клавиатуры
        } else if (update.hasCallbackQuery()) {
            CallbackQuery callbackQuery = update.getCallbackQuery();
            callbackQueryHandler(callbackQuery);
        }
    }


    private void callbackQueryHandler(CallbackQuery callbackQuery) {
        if (callbackQuery.getData().startsWith("bot_create")) {
            createCommandHandler.callbackQueryCreateHandler(callbackQuery);

        } else if (callbackQuery.getData().startsWith("bot_wallet")) {
            walletCommandHandler.callbackQueryWalletHandler(callbackQuery);
        } else if (callbackQuery.getData().startsWith("bot_rate")) {
            rateCommandHandler.callbackQueryRateHandler(callbackQuery);
        }
    }


}
