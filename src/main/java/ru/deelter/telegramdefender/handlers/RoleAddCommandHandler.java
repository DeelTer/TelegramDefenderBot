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

	@SneakyThrows
	@Override
	public void execute(@NotNull TelegramBot bot, @NotNull Update update) {
		if (!update.hasMessage()) return;

		Message message = update.getMessage();
		if (!message.hasText()) return;

		String text = message.getText().trim();
		String[] parts = text.split("\\s+");
		if (parts.length < 4) return;

		String command = parts[0].toLowerCase();
		String userId = parts[1];
		String channelId = parts[2];
		RoleLevel roleLevel = RoleLevel.LEVELS.get(parts[3].toUpperCase());

		if (roleLevel == null) {
			bot.executeAsync(SendMessage.builder()
					.chatId(message.getChatId())
					.text(String.format("Роль %s не найдена.", parts[3]))
					.build());
			return;
		}

		if (!command.equalsIgnoreCase("/setrole")) return;
		if (!checkPermissions(bot, channelId, message, roleLevel)) return;

		setRole(update, bot, userId, channelId, roleLevel);
	}

	@SneakyThrows
	private boolean checkPermissions(TelegramBot bot, String channelId, @NotNull Message message, RoleLevel level) {
		GetChatMember getExecutorMember = new GetChatMember();
		getExecutorMember.setChatId(channelId);
		getExecutorMember.setUserId(message.getFrom().getId());
		ChatMember executorMember;
		try {
			executorMember = bot.execute(getExecutorMember);
		} catch (Exception e) {
			bot.executeAsync(SendMessage.builder()
					.chatId(message.getChatId())
					.text("Не удалось получить информацию об участнике (исполнитель). Проверьте, верно ли указан канал.")
					.build());
			return false;
		}
		if (executorMember == null || !isValidAdmin(executorMember, level)) {
			bot.executeAsync(SendMessage.builder()
					.chatId(message.getChatId())
					.text("У вас нет права назначать админов в указанном канале.")
					.build());
			return false;
		}

		Long botId = bot.getMe().getId();
		GetChatMember getBotMember = new GetChatMember();
		getBotMember.setChatId(channelId);
		getBotMember.setUserId(botId);
		ChatMember botMember;
		try {
			botMember = bot.execute(getBotMember);
		} catch (Exception e) {
			bot.executeAsync(SendMessage.builder()
					.chatId(message.getChatId())
					.text("Не удалось получить информацию о боте в указанном канале.")
					.build());
			return false;
		}

		if (botMember == null || !isAdminWithPromote(botMember)) {
			bot.executeAsync(SendMessage.builder()
					.chatId(message.getChatId())
					.text("Боту необходимо быть администратором канала с правом назначать админов (can_promote_members).")
					.build());
			return false;
		}
		return true;
	}

	@SneakyThrows
	private void setRole(@NotNull Update update, @NotNull TelegramBot bot, String userToken, String channelToken, @NotNull RoleLevel level) {
		Message message = update.getMessage();
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
						level.getFormattedPermissions()))
				.build());
	}

	private @NotNull PromoteChatMember createPromoteChatMemberRequest(String channelToken, @NotNull RoleLevel level, Long targetUserId) {
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

	private boolean isValidAdmin(@NotNull ChatMember member, RoleLevel level) {
		if (member instanceof ChatMemberAdministrator admin) {
			if (Boolean.FALSE.equals(admin.getCanPromoteMembers())) {
				return false;
			}
			return level.isLowerThan(admin);
		}
		return member instanceof ChatMemberOwner;
	}

	private boolean isAdminWithPromote(@NotNull ChatMember member) {
		if (member instanceof ChatMemberAdministrator admin) {
			return admin.getCanPromoteMembers();
		}
		return member instanceof ChatMemberOwner;
	}
}
