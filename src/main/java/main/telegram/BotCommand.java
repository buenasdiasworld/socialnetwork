package main.telegram;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum BotCommand {
    START("/start"),
    HELP("Помощь"),
    NOTIFICATIONS("Уведомления"),
    SETTINGS("Настройки"),
    REGISTER("Регистрация");

    private final String name;
}
