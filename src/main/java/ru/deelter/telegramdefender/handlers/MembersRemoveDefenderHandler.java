package ru.deelter.telegramdefender.handlers;

import lombok.SneakyThrows;
import org.jetbrains.annotations.NotNull;
import org.telegram.telegrambots.meta.api.methods.groupadministration.PromoteChatMember;
import org.telegram.telegrambots.meta.api.methods.groupadministration.UnbanChatMember;
import org.telegram.telegrambots.meta.api.objects.ChatMemberUpdated;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.User;
import org.telegram.telegrambots.meta.api.objects.chatmember.ChatMember;
import ru.deelter.telegramdefender.TelegramBot;
import ru.deelter.telegramdefender.registry.IBotHandler;

public class MembersRemoveDefenderHandler implements IBotHandler {

	@SneakyThrows
	@Override
	public void execute(@NotNull TelegramBot bot, @NotNull Update update) {
		if (!update.hasChatMember()) return;

		ChatMemberUpdated memberUpdated = update.getChatMember();
		if (memberUpdated == null) return;

		ChatMember newChatMember = memberUpdated.getNewChatMember();
		if (!newChatMember.getStatus().equalsIgnoreCase("kicked")) return;
//		if (newChatMember.getUser().getIsBot()) return;

		User initiatorUser = memberUpdated.getFrom();
		if (initiatorUser.getId().equals(bot.getMe().getId())) return;

		PromoteChatMember demote = new PromoteChatMember();
		demote.setChatId(memberUpdated.getChat().getId().toString());
		demote.setUserId(initiatorUser.getId());

		demote.setCanChangeInformation(false);
		demote.setCanPostMessages(false);
		demote.setCanEditMessages(false);
		demote.setCanDeleteMessages(false);
		demote.setCanInviteUsers(false);
		demote.setCanRestrictMembers(false);
		demote.setCanPinMessages(false);
		demote.setCanPromoteMembers(false);

		bot.executeAsync(demote);
		System.out.printf("Deleted %s from channel because raid kick%n", initiatorUser.getUserName());

		UnbanChatMember unban = UnbanChatMember.builder()
				.chatId(memberUpdated.getChat().getId())
				.userId(newChatMember.getUser().getId())
				.build();
		System.out.printf("Unban user %s", newChatMember.getUser().getUserName());
		bot.execute(unban);
	}
}