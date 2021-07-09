package main.telegram.handler;

import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardButton;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardRow;

import java.util.ArrayList;
import java.util.List;

public abstract class BaseHandler {
    public abstract List<SendMessage> handle(Update update);

    private ReplyKeyboardMarkup getReplyKeyboardTemplate() {
        ReplyKeyboardMarkup replyKeyboardMarkup = new ReplyKeyboardMarkup();
        replyKeyboardMarkup.setSelective(true);
        replyKeyboardMarkup.setResizeKeyboard(true);
        replyKeyboardMarkup.setOneTimeKeyboard(false);
        return replyKeyboardMarkup;
    }

    private InlineKeyboardMarkup getInlineKeyboardTemplate() {
        return new InlineKeyboardMarkup();
    }

    protected ReplyKeyboardMarkup getReplyKeyboard() {
        ReplyKeyboardMarkup replyKeyboardMarkup = getReplyKeyboardTemplate();
        List<KeyboardRow> keyboard = new ArrayList<>();

        KeyboardRow keyboardFirstRow = new KeyboardRow();
        keyboardFirstRow.add(new KeyboardButton("Уведомления"));

        KeyboardRow keyboardSecondRow = new KeyboardRow();
        keyboardSecondRow.add(new KeyboardButton("Настройки"));
        keyboardSecondRow.add(new KeyboardButton("Помощь"));


        keyboard.add(keyboardFirstRow);
        keyboard.add(keyboardSecondRow);
        replyKeyboardMarkup.setKeyboard(keyboard);
        return replyKeyboardMarkup;
    }

    protected InlineKeyboardMarkup getInlineKeyboard() {
        InlineKeyboardMarkup inlineKeyboardMarkup = getInlineKeyboardTemplate();
        List<List<InlineKeyboardButton>> keyboard = new ArrayList<>();

        List<InlineKeyboardButton> keyboardFirstRow = new ArrayList<>();
        keyboardFirstRow.add(new InlineKeyboardButton("Уведомления"));

        List<InlineKeyboardButton> keyboardSecondRow = new ArrayList<>();
        keyboardSecondRow.add(new InlineKeyboardButton("Настройки"));
        keyboardSecondRow.add(new InlineKeyboardButton("Помощь"));


        keyboard.add(keyboardFirstRow);
        keyboard.add(keyboardSecondRow);
        inlineKeyboardMarkup.setKeyboard(keyboard);
        return inlineKeyboardMarkup;
    }

    protected ReplyKeyboardMarkup getRegisterKeyboard() {
        ReplyKeyboardMarkup replyKeyboardMarkup = getReplyKeyboardTemplate();
        List<KeyboardRow> keyboard = new ArrayList<>();

        KeyboardRow keyboardFirstRow = new KeyboardRow();
        KeyboardButton btn = new KeyboardButton("Регистрация");
        btn.setRequestContact(true);
        keyboardFirstRow.add(btn);

        keyboardFirstRow.add(new KeyboardButton("Помощь"));
        keyboard.add(keyboardFirstRow);

        replyKeyboardMarkup.setKeyboard(keyboard);
        return replyKeyboardMarkup;
    }
}