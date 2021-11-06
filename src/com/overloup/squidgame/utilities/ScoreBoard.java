package com.overloup.squidgame.utilities;

import java.util.HashMap;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.scoreboard.DisplaySlot;
import org.bukkit.scoreboard.Objective;
import org.bukkit.scoreboard.Score;
import org.bukkit.scoreboard.Scoreboard;
import org.bukkit.scoreboard.Team;

public class ScoreBoard {

	private static HashMap<Scoreboard, Player> boards = new HashMap<>();

	@SuppressWarnings("deprecation")
	public static void set(Player player, String currentgame, String des1, String des2, boolean timer) {
		Scoreboard board = Bukkit.getScoreboardManager().getNewScoreboard();
		Objective obj = board.registerNewObjective("aaa", "bbb");

		obj.setDisplaySlot(DisplaySlot.SIDEBAR);
		obj.setDisplayName("§5Squid Game");

		Score spacer15 = obj.getScore(" ");
		spacer15.setScore(14);
		Score game = obj.getScore("§eGame: " + currentgame);
		game.setScore(13);
		Score spacer13 = obj.getScore("  ");
		spacer13.setScore(12);
		Score desline1 = obj.getScore(des1);
		desline1.setScore(11);
		Score desline2 = obj.getScore(des2);
		desline2.setScore(10);
		Score spacer10 = obj.getScore("   ");
		spacer10.setScore(9);

		if (timer) {
			Team time = board.registerNewTeam("timer");
			time.setPrefix("§7➡ Time Left: ");
			time.setSuffix("§c6:00");
			time.addEntry(ChatColor.AQUA.toString());
			obj.getScore(ChatColor.AQUA.toString()).setScore(0);
		}
		boards.put(board, player);
		player.setScoreboard(board);
	}

	public static void updateTime(String i) {
		for (Scoreboard board : boards.keySet()) {
			board.getTeam("timer").setSuffix("§c" + i);
		}
	}

}
