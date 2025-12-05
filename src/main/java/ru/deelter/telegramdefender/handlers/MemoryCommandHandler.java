package ru.deelter.telegramdefender.handlers;

import lombok.SneakyThrows;
import org.jetbrains.annotations.NotNull;
import org.telegram.telegrambots.meta.api.methods.groupadministration.GetChat;
import org.telegram.telegrambots.meta.api.methods.groupadministration.GetChatMember;
import org.telegram.telegrambots.meta.api.methods.groupadministration.PromoteChatMember;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Chat;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.chatmember.ChatMember;
import org.telegram.telegrambots.meta.api.objects.chatmember.ChatMemberAdministrator;
import org.telegram.telegrambots.meta.api.objects.chatmember.ChatMemberOwner;
import ru.deelter.telegramdefender.TelegramBot;
import ru.deelter.telegramdefender.registry.IBotHandler;

public class RoleAddCommandHandler implements IBotHandler {


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
		}
	}

	@SneakyThrows
	private void setRole(@NotNull Update update, @NotNull TelegramBot bot, String userToken, String channelToken, @NotNull RoleLevel level) {
		Message message = update.getMessage();
		Long executorId = message.getFrom().getId();

		GetChatMember getExecutorMember = new GetChatMember();
		getExecutorMember.setChatId(channelToken);
		getExecutorMember.setUserId(executorId);
		ChatMember executorMember;
		try {
			executorMember = bot.execute(getExecutorMember);
		} catch (Exception e) {
			bot.executeAsync(SendMessage.builder()
					.chatId(message.getChatId())
					.text("Не удалось получить информацию об участнике (исполнитель). Проверьте, верно ли указан канал.")
					.build());
			return;
		}
		if (executorMember == null || !isAdminWithPromote(executorMember)) {
			bot.executeAsync(SendMessage.builder()
					.chatId(message.getChatId())
					.text("У вас нет права назначать админов в указанном канале.")
					.build());
			return;
		}

		Long botId = bot.getMe().getId();
		GetChatMember getBotMember = new GetChatMember();
		getBotMember.setChatId(channelToken);
		getBotMember.setUserId(botId);
		ChatMember botMember;
		try {
			botMember = bot.execute(getBotMember);
		} catch (Exception e) {
			bot.executeAsync(SendMessage.builder()
					.chatId(message.getChatId())
					.text("Не удалось получить информацию о боте в указанном канале.")
					.build());
			return;
		}

		if (botMember == null || !isAdminWithPromote(botMember)) {
			bot.executeAsync(SendMessage.builder()
					.chatId(message.getChatId())
					.text("Боту необходимо быть администратором канала с правом назначать админов (can_promote_members).")
					.build());
			return;
		}

		Long targetUserId = Long.parseLong(userToken);
		PromoteChatMember promote = createPromoteChatMemberRequest(channelToken, level, targetUserId);
		bot.execute(promote);

		GetChat getChat = new GetChat();
		getChat.setChatId(channelToken);
		Chat channelChat = bot.execute(getChat);

		GetChatMember getChatMember = new GetChatMember();
		getChatMember.setChatId(userToken);
		getChatMember.setUserId(Long.parseLong(userToken));
		ChatMember chatMember = bot.execute(getChatMember);

		bot.executeAsync(SendMessage.builder()
				.chatId(update.getMessage().getChatId())
				.text(String.format("Вы успешно назначили @%s на роль %s в канале @%s\n\nНовые права пользователя:\n\n%s",
						chatMember.getUser().getUserName(),
						level.getName(),
						channelChat.getUserName(),
						level.getFormatted()))
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

	private static boolean isAdminWithPromote(@NotNull ChatMember member) {
		if (member instanceof ChatMemberAdministrator admin) {
			return Boolean.TRUE.equals(admin.getCanPromoteMembers());
		}
		return member instanceof ChatMemberOwner;
	}
}
