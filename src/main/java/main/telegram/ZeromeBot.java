package main.telegram;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import main.telegram.handler.BaseHandler;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.util.List;

@Getter
@Setter
@Component
@Profile("prod")
@RequiredArgsConstructor
@Slf4j
public class ZeromeBot extends TelegramLongPollingBot {

    @Value("${bot.name}")
    private String botUsername;
    @Value("${bot.token}")
    private String botToken;

    private final List<BaseHandler> handlers;
    
    @Override
    public void onUpdateReceived(Update update) {
        long chatId = update.getMessage().getChatId();
        handlers.forEach(
                handler -> {
                    List<SendMessage> messages = handler.handle(update);
                    messages.forEach(m -> sendTextMessage(chatId, m));
                }
        );
    }

    public synchronized void sendTextMessage(Long chatId, SendMessage message) {
        message.enableMarkdown(true);
        message.setChatId(chatId);

        try {
            execute(message);
        } catch (TelegramApiException e) {
            log.error(e.getMessage());
        }
    }
}