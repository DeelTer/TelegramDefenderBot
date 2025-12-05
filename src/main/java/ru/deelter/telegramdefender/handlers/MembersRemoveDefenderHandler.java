package ru.deelter.telegramdefender.handlers;

import lombok.SneakyThrows;
import org.jetbrains.annotations.NotNull;
import org.telegram.telegrambots.meta.api.methods.groupadministration.PromoteChatMember;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;
import ru.deelter.telegramdefender.TelegramBot;
import ru.deelter.telegramdefender.registry.IBotHandler;

public class RoleAddCommandHandler implements IBotHandler {


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
			RoleLevel level = RoleLevel.valueOf(parts[3].toUpperCase()); // role

			if (!command.equalsIgnoreCase("/setrole")) return;

			setRole(update, bot, user, channel, level);

			bot.executeAsync(SendMessage.builder()
					.chatId(update.getMessage().getChatId())
					.text(String.format("Вы установили админом %s в чате %s", user, channel))
					.build());
		}
	}

	@SneakyThrows
	private void setRole(@NotNull Update update, @NotNull TelegramBot bot, String userToken, String channelToken, @NotNull RoleLevel level) {
		Long targetUserId = Long.parseLong(userToken);
		PromoteChatMember promote = createPromoteChatMemberRequest(channelToken, level, targetUserId);
		bot.execute(promote);
		bot.executeAsync(SendMessage.builder()
				.chatId(update.getMessage().getChatId())
				.text(String.format("Вы успешно назначили %s на роль %s в канале %s", userToken, level.name(), channelToken))
				.build());
	}

	private static @NotNull PromoteChatMember createPromoteChatMemberRequest(String channelToken, @NotNull RoleLevel level, Long targetUserId) {
		PromoteChatMember promote = new PromoteChatMember();
		promote.setChatId(channelToken);
		promote.setUserId(targetUserId);

		promote.setCanChangeInformation(level.isCanChangeInformation());
		promote.setCanPostMessages(level.isCanPostMessages());
		promote.setCanEditMessages(level.isCanEditMessages());
		promote.setCanDeleteMessages(level.isCanDeleteMessages());
		promote.setCanInviteUsers(level.isCanInviteUsers());
		promote.setCanRestrictMembers(level.isCanRestrictMembers());
		promote.setCanPinMessages(level.isCanPinMessages());
		promote.setCanPromoteMembers(level.isCanPromoteMembers());
		return promote;
	}
}
