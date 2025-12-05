package ru.deelter.telegramdefender.handlers;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.telegram.telegrambots.meta.api.objects.chatmember.ChatMember;
import org.telegram.telegrambots.meta.api.objects.chatmember.ChatMemberAdministrator;
import org.telegram.telegrambots.meta.api.objects.chatmember.ChatMemberOwner;

@Getter
@AllArgsConstructor
public enum RoleLevel {

	NONE("Никто", false, false, false, false, false, false, false, false),
	EDITOR("Редактор",false, true, true, false, true, false, true, false),
	CURATOR("Куратор", false, true, true, false, true, true, true, true),
	ADMIN("Админ", true, true, true, true, true, true, true, true);

	private final String name;
	private final boolean canChangeInformation;
	private final boolean canPostMessages;
	private final boolean canEditMessages;
	private final boolean canDeleteMessages;
	private final boolean canInviteUsers;
	private final boolean canRestrictMembers;
	private final boolean canPinMessages;
	private final boolean canPromoteMembers;

	@Contract(pure = true)
	public @NotNull String getFormatted() {
		return "Изменять профиль канала: " + (canChangeInformation ? "Да" : "Нет") + "\n" +
				"Публиковать сообщения: " + (canPostMessages ? "Да" : "Нет") + "\n" +
				"Редактировать сообщения: " + (canEditMessages ? "Да" : "Нет") + "\n" +
				"Удалять сообщения: " + (canDeleteMessages ? "Да" : "Нет") + "\n" +
				"Приглашать пользователей: " + (canInviteUsers ? "Да" : "Нет") + "\n" +
				"Ограничивать участников: " + (canRestrictMembers ? "Да" : "Нет") + "\n" +
				"Закреплять сообщения: " + (canPinMessages ? "Да" : "Нет") + "\n" +
				"Назначать администраторов: " + (canPromoteMembers ? "Да" : "Нет");
	}
}
