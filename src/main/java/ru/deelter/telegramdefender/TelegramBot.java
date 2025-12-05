package ru.deelter.telegram.dfolder.bot;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.jetbrains.annotations.NotNull;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.objects.Update;
import ru.deelter.telegram.dfolder.managers.BotHandlerManager;

import java.util.List;

@Getter
@Setter
@ToString
public class TelegramBot extends TelegramLongPollingBot {

	private final BotHandlerManager handlerManager = new BotHandlerManager();
	private final String userName;

	public TelegramBot(String userName, String botToken) {
		super(botToken);
		this.userName = userName;
	}

	@Override
	public void onUpdateReceived(@NotNull Update update) {
		handlerManager.process(this, update);
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

	public void save() {
	}
}
