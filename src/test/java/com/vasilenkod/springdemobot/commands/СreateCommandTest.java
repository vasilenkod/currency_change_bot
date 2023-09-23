package com.vasilenkod.springdemobot.commands;

import com.vasilenkod.springdemobot.bot.Currency;
import com.vasilenkod.springdemobot.bot.commands.create.*;
import com.vasilenkod.springdemobot.model.DataBaseApi;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;


public class СreateCommandTest {

    @Autowired
    DataBaseApi dataBaseApi;

    private CreateContext createContext;

    @Test
    public void selectFirstFiatStateTest() {
        createContext = new CreateContext(dataBaseApi);
        createContext.setState(new CreateSelectFirstFiatState(createContext));

        String messageToAnswer = createContext.getMessage();
        CreateState nextState = createContext.goNext();


        Assertions.assertEquals(messageToAnswer, "Выберите валюту которую хотите обменять");
        Assertions.assertEquals(nextState.getClass(), CreateSelectSecondFiatState.class);

    }

    @Test
    public void selectSecondFiatStateTest() {
        createContext = new CreateContext(dataBaseApi);
        createContext.setState(new CreateSelectFirstFiatState(createContext));

        createContext.setState(createContext.goNext());

        String messageToAnswer = createContext.getMessage();
        CreateState nextState = createContext.goNext();
        CreateState prevState = createContext.goBack();

        Assertions.assertEquals(messageToAnswer, "Выберите валюту которую хотите получить");
        Assertions.assertEquals(nextState.getClass(), CreateChooseFiatState.class);
        Assertions.assertEquals(prevState.getClass(), CreateSelectFirstFiatState.class);
    }

    @Test
    public void selectChooseFiatStateTest() {
        createContext = new CreateContext(dataBaseApi);
        createContext.setState(new CreateSelectFirstFiatState(createContext));

        createContext.setState(createContext.goNext());
        createContext.setState(createContext.goNext());

        createContext.setGiveCurrency(Currency.RUB);
        createContext.setGetCurrency(Currency.USD);

        String messageToAnswer = createContext.getMessage();
        CreateState nextState = createContext.goNext();
        CreateState prevState = createContext.goBack();

        Assertions.assertEquals(messageToAnswer, "Вы отдаете: RUB\nВы получаете: USD");
        Assertions.assertEquals(nextState.getClass(), CreateInputState.class);
        Assertions.assertEquals(prevState.getClass(), CreateSelectSecondFiatState.class);

    }

    @Test
    public void InputStateTest() {
        createContext = new CreateContext(dataBaseApi);
        createContext.setState(new CreateSelectFirstFiatState(createContext));

        createContext.setState(createContext.goNext());
        createContext.setState(createContext.goNext());
        createContext.setState(createContext.goNext());

        createContext.setGiveCurrency(Currency.RUB);
        createContext.setGetCurrency(Currency.USD);

        String messageToAnswer = createContext.getMessage();
        CreateState nextState = createContext.goNext();
        CreateState prevState = createContext.goBack();

        Assertions.assertEquals(messageToAnswer, "Введите количество валюты");
        Assertions.assertEquals(nextState.getClass(), CreateFinalState.class);
        Assertions.assertEquals(prevState.getClass(), CreateChooseFiatState.class);

    }


}
