package ru.exlmoto.digest.bot.ability.keyboard;

public enum Keyboard {
	rate,
	chart,
	subscribe,
	digest,
	show,
	greeting;

	private static final String DELIMITER = "_";
	public static final String PAGE = "page" + DELIMITER;

	public String withName() {
		return name() + DELIMITER;
	}

	public static String chopKeyboardNameRight(String key) {
		int find = key.indexOf(DELIMITER);
		return (find != -1) ? key.substring(0, find) + DELIMITER : key;
	}

	public static String chopKeyboardNameLeft(String key) {
		int find = key.indexOf(DELIMITER);
		return (find != -1) ? key.substring(find + 1) : key;
	}

	public static String chopKeyboardNameLast(String key) {
		int find = key.lastIndexOf(DELIMITER);
		return (find != -1) ? key.substring(find + 1) : key;
	}
}
