package ru.deelter.telegramdefender.managers;

import lombok.Getter;
import lombok.NoArgsConstructor;
import org.jetbrains.annotations.NotNull;
import org.telegram.telegrambots.meta.api.objects.Update;
import ru.deelter.telegramdefender.TelegramBot;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

@NoArgsConstructor
@Getter
public class BotHandlerRegistry {

	private final List<IBotHandler> handlers = new ArrayList<>();

	public void register(IBotHandler handler) {
		handlers.add(handler);
	}

	public void registerAll(@NotNull Collection<IBotHandler> handlers) {
		handlers.forEach(this::register);
	}

	public void process(TelegramBot telegramBot, Update update) {
		handlers.forEach(handler -> handler.execute(telegramBot, update));
	}
}
