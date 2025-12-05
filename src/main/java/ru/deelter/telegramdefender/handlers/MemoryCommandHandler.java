package ru.deelter.telegramdefender.handlers;

import lombok.SneakyThrows;
import org.jetbrains.annotations.NotNull;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;
import ru.deelter.telegramdefender.TelegramBot;
import ru.deelter.telegramdefender.registry.IBotHandler;

public class MemoryCommandHandler implements IBotHandler {


	private final Runtime RUNTIME = Runtime.getRuntime();

	@SneakyThrows
	@Override
	public void execute(@NotNull TelegramBot bot, @NotNull Update update) {
		if (!update.hasMessage()) return;

		Message message = update.getMessage();
		if (!message.getText().startsWith("/ram")) return;

		String memory = getRamStats();
		bot.execute(SendMessage.builder()
				.chatId(message.getChatId())
				.text("Оперативная память: " + memory)
				.build());
	}

	private long getTotal() {
		return RUNTIME.totalMemory() / 1024 / 1024;
	}

	private long getFree() {
		return RUNTIME.freeMemory() / 1024 / 1024;
	}

	private long getUsage() {
		return getTotal() - getFree();
	}

	@NotNull
	public String getRamStats() {
		return "[" + getUsage() + "Mb/" + getTotal() + "Mb]";
	}
}
