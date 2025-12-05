package ru.deelter.telegramdefender.managers;

import org.jetbrains.annotations.NotNull;
import org.telegram.telegrambots.meta.api.objects.Update;
import ru.deelter.telegramdefender.Main;
import ru.deelter.telegramdefender.TelegramBot;

public interface IBotHandler {

	void execute(@NotNull TelegramBot bot, @NotNull Update update);

	default void register() {
		Main.BOT.getHandlerManager().register(this);
	}
}
