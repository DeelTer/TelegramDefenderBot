package ru.deelter.telegramdefender.listeners;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum AdminLevel {

	EDITOR(false, true, true, false, true, false, true, false),
	CURATOR(false, true, true, false, true, true, true, true),
	ADMIN(true, true, true, true, true, true, true, true);

	private final boolean canChangeInformation;
	private final boolean canPostMessages;
	private final boolean canEditMessages;
	private final boolean canDeleteMessages;
	private final boolean canInviteUsers;
	private final boolean canRestrictMembers;
	private final boolean canPinMessages;
	private final boolean canPromoteMembers;
}
