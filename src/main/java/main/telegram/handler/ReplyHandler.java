package main.telegram.handler;

import lombok.RequiredArgsConstructor;
import main.telegram.BotCommand;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;

import java.util.ArrayList;
import java.util.List;

@Profile("prod")
@Component
@RequiredArgsConstructor
public class ReplyHandler extends BaseHandler {

    @Override
    public List<SendMessage> handle(Update update) {
        List<SendMessage> messages = new ArrayList<>();
        if (!update.hasMessage() || !update.getMessage().hasText()) {
            return messages;
        }
        String text = update.getMessage().getText();
        for (BotCommand command : BotCommand.values()) {
            if (text.startsWith(command.getName())) {
                return messages;
            }
        }
        SendMessage message = new SendMessage(update.getMessage().getChatId(), "Просто так не отвечаю, выбери команду!");
        message.setReplyMarkup(getReplyKeyboard());
        messages.add(message);
        return messages;
    }
}
