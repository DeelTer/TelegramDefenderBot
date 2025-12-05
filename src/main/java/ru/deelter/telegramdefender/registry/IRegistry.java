package ru.deelter.telegramdefender.registry;

public interface IRegistry {

	default void load() {
		onLoad();
	}

	default void unload() {
		onUnload();
	}

	default void reload() {
		unload();
		load();
	}

	default void save() {
		onSave();
	}

	void onLoad();

	void onUnload();

	default void onSave() {
	}
}

