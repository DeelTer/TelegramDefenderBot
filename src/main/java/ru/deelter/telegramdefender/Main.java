package ru.deelter.telegramdefender;

import com.google.gson.*;
import lombok.Getter;
import lombok.SneakyThrows;
import org.telegram.telegrambots.bots.DefaultBotOptions;
import org.telegram.telegrambots.meta.TelegramBotsApi;
import org.telegram.telegrambots.meta.api.methods.updates.AllowedUpdates;
import org.telegram.telegrambots.updatesreceivers.DefaultBotSession;
import ru.deelter.telegramdefender.handlers.*;
import ru.deelter.telegramdefender.utils.ResourceUtils;

import java.io.File;
import java.io.FileReader;
import java.nio.file.Path;
import java.util.List;

public class Main {

	@Getter
	private static TelegramBot telegramBot;
	private static TelegramBotsApi telegramBotsApi;
	@Getter
	private static JsonObject config;

	@SneakyThrows
	public static void main(String[] args) {
		setupConfig();
		setupTelegramBot();
	}


	@SneakyThrows
	private static void setupConfig() {
		Path configPath = ResourceUtils.saveResource("config.json", false);
		config = JsonParser.parseReader(new FileReader(new File(configPath.toUri()))).getAsJsonObject();

		JsonArray rolesArray = config.getAsJsonArray("roles");
		if (rolesArray != null) {

			Gson gson = new GsonBuilder().create();
			rolesArray.forEach(element -> {

				RoleLevel roleLevel = gson.fromJson(element, RoleLevel.class);
				roleLevel.register();
			});
		}
	}

	@SneakyThrows
	private static void setupTelegramBot() {
		DefaultBotOptions options = new DefaultBotOptions();
		options.setAllowedUpdates(List.of(AllowedUpdates.CHATMEMBER, AllowedUpdates.MYCHATMEMBER, AllowedUpdates.MESSAGE));

		String username = config.get("username").getAsString();
		String token = config.get("token").getAsString();

		telegramBot = new TelegramBot(options, username, token);
		telegramBotsApi = new TelegramBotsApi(DefaultBotSession.class);
		telegramBotsApi.registerBot(telegramBot);

		setupBotListeners();
	}

	private static void setupBotListeners() {
		telegramBot.getHandlerRegistry().registerAll(List.of(
				new RoleAddCommandHandler(),
				new MembersRemoveDefenderHandler(),
				new MemoryCommandHandler(),
				new RolesCheckCommandHandler()
		));
	}
}
