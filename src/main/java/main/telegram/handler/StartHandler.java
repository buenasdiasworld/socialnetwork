package main.telegram.handler;

import lombok.RequiredArgsConstructor;
import main.telegram.BotCommand;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.User;

import java.util.ArrayList;
import java.util.List;

@Profile("prod")
@Component
@RequiredArgsConstructor
public class StartHandler extends BaseHandler {

    @Override
    public List<SendMessage> handle(Update update) {
        List<SendMessage> messages = new ArrayList<>();
        if (!update.hasMessage() || !update.getMessage().hasText() ||
                !update.getMessage().getText().equals(BotCommand.START.getName())) {
            return messages;
        }
        User user = update.getMessage().getFrom();
        SendMessage message = new SendMessage();
        message.setText("Привет, " + user.getUserName() + "! Для получения увеомлений нажми 'Уведомления'" +
                "\nP.S. Уведомления умею показывать только по запросу ;P");
        message.setReplyMarkup(getReplyKeyboard());
        messages.add(message);
        return messages;
    }
}
