package main.telegram.handler;

import lombok.RequiredArgsConstructor;
import main.service.RegistrationService;
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
public class AuthHandler extends BaseHandler {
    private final RegistrationService registrationService;

    @Override
    public List<SendMessage> handle(Update update) {
        List<SendMessage> messages = new ArrayList<>();
        if (!update.hasMessage() || !update.getMessage().hasContact()) {
            return messages;
        }
        messages = new ArrayList<>();
        messages.add(register(update));
        return messages;
    }

    private SendMessage register(Update update) {
        Long chatId = update.getMessage().getChatId();
        String phone = update.getMessage().getContact().getPhoneNumber().replaceAll("[+(\\-)]","");
        SendMessage message = new SendMessage();
        message.setChatId(chatId);

        if (registrationService.registerTelegram(phone, chatId)) {
            message.setText("Мы внутри, можно смотреть уведомления b('w')b");
        } else {
            message.setText("Телефона в профиле нет ? Тогда не смогу войти");
        }
        message.setReplyMarkup(getRegisterKeyboard());

        return message;
    }
}
