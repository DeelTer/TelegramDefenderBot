package ru.deelter.telegramdefender.listeners;

import lombok.SneakyThrows;
import org.jetbrains.annotations.NotNull;
import org.telegram.telegrambots.meta.api.methods.groupadministration.PromoteChatMember;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import ru.deelter.telegramdefender.TelegramBot;
import ru.deelter.telegramdefender.registry.IBotHandler;

public class CommandListener implements IBotHandler {


	@SneakyThrows
	@Override
	public void execute(@NotNull TelegramBot bot, @NotNull Update update) {
		if (update.hasMessage() && update.getMessage().hasText()) {
			Message message = update.getMessage();
			String text = message.getText().trim();

			String[] parts = text.split("\\s+");
			if (parts.length < 4) return;

			String command = parts[0].toLowerCase();
			String user = parts[1];    // userId
			String channel = parts[2]; // chatId
			AdminLevel level = AdminLevel.valueOf(parts[3].toUpperCase()); // role

			if (!command.equalsIgnoreCase("/setrole")) return;

			setRole(update, bot, user, channel, level);

			bot.executeAsync(SendMessage.builder()
				.chatId(update.getMessage().getChatId())
				.text(String.format("Вы установили админом %s в чате %s", user, channel))
				.build());
	}
		}

	private void setRole(Update update, TelegramBot bot, String userToken, String channelToken, AdminLevel level) {
		Long targetUserId = Long.parseLong(userToken);
		try {
			PromoteChatMember promote = new PromoteChatMember();
			promote.setChatId(channelToken);
			promote.setUserId(targetUserId);

			// Пример прав: даём все возможные права администратора (настраивайте по необходимости)
			promote.setCanChangeInformation(level.isCanChangeInformation());
			promote.setCanPostMessages(level.isCanPostMessages());
			promote.setCanEditMessages(level.isCanEditMessages());
			promote.setCanDeleteMessages(level.isCanDeleteMessages());
			promote.setCanInviteUsers(level.isCanInviteUsers());
			promote.setCanRestrictMembers(level.isCanRestrictMembers());
			promote.setCanPinMessages(level.isCanPinMessages());
			promote.setCanPromoteMembers(level.isCanPromoteMembers());

			bot.execute(promote);
			
		} catch (TelegramApiException e) {
			sendSimpleReply(fromMessage, "Ошибка при назначении: " + e.getMessage());
		}
	}
}
