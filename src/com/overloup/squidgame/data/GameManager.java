package com.overloup.squidgame.data;

public class GameManager {

	public enum Game {
		LOBBY, REDLIGHTGREENLIGHT, HONEYCOME, TUGOFWAR, MARBLE, GLASSSTEPPING, SQUIDGAME
	}

	private static Game current;

	public static void setGame(Game game) {
		current = game;
	}

	public static Game getGame() {
		return current;
	}
}
