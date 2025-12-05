package ru.deelter.telegramdefender;

import lombok.SneakyThrows;
import org.telegram.telegrambots.bots.DefaultBotOptions;
import org.telegram.telegrambots.meta.TelegramBotsApi;
import org.telegram.telegrambots.meta.api.methods.updates.AllowedUpdates;
import org.telegram.telegrambots.updatesreceivers.DefaultBotSession;
import ru.deelter.telegramdefender.handlers.MembersRemoveDefenderHandler;
import ru.deelter.telegramdefender.handlers.MemoryCommandHandler;
import ru.deelter.telegramdefender.handlers.RoleAddCommandHandler;

import java.io.File;
import java.util.List;

public class Main {

	public static final String DIRECTORY = new File(".").getPath();
	public static TelegramBot telegramBot;

	@SneakyThrows
	public static void main(String[] args) {

		DefaultBotOptions options = new DefaultBotOptions();
		options.setAllowedUpdates(List.of(AllowedUpdates.CHATMEMBER, AllowedUpdates.MYCHATMEMBER, AllowedUpdates.MESSAGE));

		String userName = "channeldefendbot";
		String token = "8470589141:AAEDqwWoq_K-cYdhbieCFu2x2e7iX4CW8QE";

		telegramBot = new TelegramBot(options, userName, token);

		TelegramBotsApi api = new TelegramBotsApi(DefaultBotSession.class);
		api.registerBot(telegramBot);

		telegramBot.getHandlerRegistry().registerAll(List.of(
				new RoleAddCommandHandler(),
                new MembersRemoveDefenderHandler(),
				new MemoryCommandHandler()
		));
	}
}
