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
public class SettingsHandler extends BaseHandler {
    @Override
    public List<SendMessage> handle(Update update) {
        List<SendMessage> messages = new ArrayList<>();
        if (!update.hasMessage() || !update.getMessage().hasText() ||
                !update.getMessage().getText().equals(BotCommand.SETTINGS.getName())) {
            return messages;
        }
        SendMessage message = new SendMessage();
        message.setText("Эм... Тут пока ничего нет -\\_-");
        message.setReplyMarkup(getReplyKeyboard());
        messages.add(message);
        return messages;
    }
}
