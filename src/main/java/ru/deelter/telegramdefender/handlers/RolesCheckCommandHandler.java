package ru.deelter.telegramdefender.handlers;

import lombok.SneakyThrows;
import org.jetbrains.annotations.NotNull;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;
import ru.deelter.telegramdefender.TelegramBot;
import ru.deelter.telegramdefender.registry.IBotHandler;

public class RolesCheckCommandHandler implements IBotHandler {

	@SneakyThrows
	@Override
	public void execute(@NotNull TelegramBot bot, @NotNull Update update) {
		if (!update.hasMessage()) return;

		Message message = update.getMessage();
		if (!message.hasText()) return;

		String text = message.getText().trim();
		if (!text.equalsIgnoreCase("/roles")) return;

		StringBuilder s = new StringBuilder();
		RoleLevel.LEVELS.values().forEach(level -> {
			s.append("\n\n").append(level.getId()).append(" (").append(level.getName()).append(")")
					.append("\n\n").append(level.getFormattedPermissions());
		});
		bot.executeAsync(SendMessage.builder()
				.chatId(message.getChatId())
				.text(s.toString())
				.build());
	}
}
