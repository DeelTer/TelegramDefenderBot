package ru.deelter.telegramdefender;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.jetbrains.annotations.NotNull;
import org.telegram.telegrambots.bots.DefaultBotOptions;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.objects.Update;
import ru.deelter.telegramdefender.registry.BotHandlerRegistry;

import java.util.List;

@Getter
@Setter
@ToString
public class TelegramBot extends TelegramLongPollingBot {

	private final BotHandlerRegistry handlerRegistry = new BotHandlerRegistry();
	private final String userName;

	public TelegramBot(DefaultBotOptions options, String userName, String botToken) {
		super(options, botToken);
		this.userName = userName;
	}

	@Override
	public void onUpdateReceived(@NotNull Update update) {
		handlerRegistry.process(this, update);
	}

	@Override
	public String getBotUsername() {
		return userName;
	}

	@Override
	public void onRegister() {
		super.onRegister();
		System.out.println("Bot registered");
	}

	@Override
	public void onUpdatesReceived(List<Update> updates) {
		super.onUpdatesReceived(updates);
	}
}
