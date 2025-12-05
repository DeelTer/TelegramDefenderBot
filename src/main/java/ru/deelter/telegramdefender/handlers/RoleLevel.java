package ru.deelter.telegramdefender.handlers;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.telegram.telegrambots.meta.api.objects.chatmember.ChatMemberAdministrator;

import java.util.HashMap;
import java.util.Map;

@Getter
@Builder
@AllArgsConstructor
public class RoleLevel {

	public static final Map<String, RoleLevel> LEVELS = new HashMap<>();

	private final String id;
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
	public @NotNull String getFormattedPermissions() {
		return "Изменять профиль канала: " + (canChangeInformation ? "Да" : "Нет") + "\n" +
				"Публиковать сообщения: " + (canPostMessages ? "Да" : "Нет") + "\n" +
				"Редактировать сообщения: " + (canEditMessages ? "Да" : "Нет") + "\n" +
				"Удалять сообщения: " + (canDeleteMessages ? "Да" : "Нет") + "\n" +
				"Приглашать пользователей: " + (canInviteUsers ? "Да" : "Нет") + "\n" +
				"Ограничивать участников: " + (canRestrictMembers ? "Да" : "Нет") + "\n" +
				"Закреплять сообщения: " + (canPinMessages ? "Да" : "Нет") + "\n" +
				"Назначать администраторов: " + (canPromoteMembers ? "Да" : "Нет");
	}

	public boolean isLowerThan(ChatMemberAdministrator admin) {
		if (admin == null) return true;

		Boolean adminCanChangeInfo = admin.getCanChangeInfo();
		Boolean adminCanPostMessages = admin.getCanPostMessages();
		Boolean adminCanEditMessages = admin.getCanEditMessages();
		Boolean adminCanDeleteMessages = admin.getCanDeleteMessages();
		Boolean adminCanInviteUsers = admin.getCanInviteUsers();
		Boolean adminCanRestrictMembers = admin.getCanRestrictMembers();
		Boolean adminCanPinMessages = admin.getCanPinMessages();
		Boolean adminCanPromoteMembers = admin.getCanPromoteMembers();

		/*
			Я честно не работал с ТГ API и не знаю, может ли тут прийти null.
			Некоторые миссают этот момент, но пусть лучше так.
		 */
		boolean aChangeInfo = Boolean.TRUE.equals(adminCanChangeInfo);
		boolean aPost = Boolean.TRUE.equals(adminCanPostMessages);
		boolean aEdit = Boolean.TRUE.equals(adminCanEditMessages);
		boolean aDelete = Boolean.TRUE.equals(adminCanDeleteMessages);
		boolean aInvite = Boolean.TRUE.equals(adminCanInviteUsers);
		boolean aRestrict = Boolean.TRUE.equals(adminCanRestrictMembers);
		boolean aPin = Boolean.TRUE.equals(adminCanPinMessages);
		boolean aPromote = Boolean.TRUE.equals(adminCanPromoteMembers);

		if (!canChangeInformation && aChangeInfo) return true;
		if (!canPostMessages && aPost) return true;
		if (!canEditMessages && aEdit) return true;
		if (!canDeleteMessages && aDelete) return true;
		if (!canInviteUsers && aInvite) return true;
		if (!canRestrictMembers && aRestrict) return true;
		if (!canPinMessages && aPin) return true;
		return !canPromoteMembers && aPromote;
	}

	public void register() {
		LEVELS.put(id.toUpperCase(), this);
	}
}
