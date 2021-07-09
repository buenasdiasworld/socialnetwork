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
public class HelpHandler extends BaseHandler {

    @Override
    public List<SendMessage> handle(Update update) {
        List<SendMessage> messages = new ArrayList<>();
        if (!update.hasMessage() || !update.getMessage().hasText() ||
                !update.getMessage().getText().equals(BotCommand.HELP.getName())) {
            return messages;
        }
        String text = "Получить уведомления можно по кнопке 'Уведомления', если я тебя при этом узнаю)" +
                " Если нет, то придется дать номерок :D\n";
//                "Или можешь писать мне коммандами:\n" +
//                "/help - это же сообщение(Помощь)\n" +
//                "/notifications - получить уведомления\n" +
//                "/register - зарегистрироваться(чтобы я мог искать именно твои уведомления)";
        SendMessage message = new SendMessage(update.getMessage().getChatId(), text);
        message.setReplyMarkup(getReplyKeyboard());
        messages.add(message);
        return messages;
    }
}
