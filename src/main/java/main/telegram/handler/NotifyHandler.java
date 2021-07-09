package main.telegram.handler;

import lombok.RequiredArgsConstructor;
import main.data.response.type.NotificationResponse;
import main.service.NotificationService;
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
public class NotifyHandler extends BaseHandler {

    private final NotificationService notificationService;

    @Override
    public List<SendMessage> handle(Update update) {
        List<SendMessage> messages = new ArrayList<>();
        if (!update.hasMessage() || !update.getMessage().hasText() ||
                !update.getMessage().getText().equals(BotCommand.NOTIFICATIONS.getName())) {
            return messages;
        }
        long chatId = update.getMessage().getChatId();

        SendMessage message = new SendMessage();
        message.setChatId(chatId);
        try {
            notificationService.list(0, 10, false, chatId)
                    .getData()
                    .forEach(n -> messages.add(createMessage(chatId, n)));
            message.setReplyMarkup(getReplyKeyboard());
            if (messages.isEmpty()) {
                message.setText("Источники сообщили, что уведомлениий для тебя нет -\\_-, зайди попозже");
            }
        } catch (Exception ex) {
            ex.printStackTrace();
            message.setText("Похоже, я не могу определить, кто ты... Так что для начала я бы зарегался)");
            message.setReplyMarkup(getRegisterKeyboard());
        }
        messages.add(message);
        return messages;
    }

    private SendMessage createMessage(Long chatId, NotificationResponse n) {
        StringBuilder sb = new StringBuilder();
        switch (n.getCode()) {
            case LIKE:
                sb.append(n.getEntityAuthor().getFirstName()).append(" ").append(n.getEntityAuthor().getLastName())
                        .append(" поставил лайк ").append(n.getInfo());
                break;
            case POST:
                sb.append(n.getEntityAuthor().getFirstName()).append(" ").append(n.getEntityAuthor().getLastName())
                        .append(" написал пост ").append(n.getInfo());
                break;
            case MESSAGE:
                sb.append(n.getEntityAuthor().getFirstName()).append(" ").append(n.getEntityAuthor().getLastName())
                        .append(" прислал сообщение ").append(n.getInfo());
                break;
            case POST_COMMENT:
                sb.append(n.getEntityAuthor().getFirstName()).append(" ").append(n.getEntityAuthor().getLastName())
                        .append(" оставил комментарий: ").append(n.getInfo());
                break;
            case FRIEND_REQUEST:
                sb.append(n.getEntityAuthor().getFirstName()).append(" ").append(n.getEntityAuthor().getLastName())
                        .append(" добавил вас друзья ").append(n.getInfo());
                break;
            case COMMENT_COMMENT:
                sb.append(n.getEntityAuthor().getFirstName()).append(" ").append(n.getEntityAuthor().getLastName())
                        .append(" оставил комментарий ").append(n.getInfo());
                break;
            case FRIEND_BIRTHDAY:
                sb.append(n.getInfo());
                break;
        }
        return new SendMessage(chatId, sb.toString());
    }
}
