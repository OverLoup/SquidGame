package com.overloup.squidgame.utilities;

import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.entity.Player;

public class Elimination {

	public static void Eliminate(Player player) {
		Location loc = player.getLocation();

		player.playSound(loc, "squidgame.red_light_green_light.gunshot", 3, 1);

		try {
			Thread.sleep(500);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}

		player.damage(20);
		player.setGameMode(GameMode.SPECTATOR);
		player.setInvisible(true);
		Bukkit.broadcastMessage("§e" + player.getName() + "§c was Eliminated");

		try {
			Thread.sleep(500);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}

		player.spigot().respawn();
		player.teleport(loc);
	}

	public static void PlayShotforEveryone() {
		for (Player p : Bukkit.getOnlinePlayers()) {
			p.playSound(p.getLocation(), "squidgame.red_light_green_light.gunshot", 3, 1);
		}
	}

	public static void PlayShotforEveryone(Location loc) {
		for (Player p : Bukkit.getOnlinePlayers()) {
			p.playSound(loc, "squidgame.red_light_green_light.gunshot", 3, 1);
		}
	}

}
