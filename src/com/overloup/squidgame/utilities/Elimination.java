package com.overloup.squidgame.utilities;

import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.entity.Player;

import com.overloup.squidgame.Main;

public class Elimination {

	public static void Eliminate(Player player) {
		player.playSound(player.getLocation(), "squidgame.red_light_green_light.gunshot", 3, 100);

		try {
			Thread.sleep(500);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}

		player.damage(20);
		player.setGameMode(GameMode.SPECTATOR);
		Bukkit.broadcastMessage("§e" + player.getName() + "§c was Eliminated");

		try {
			Thread.sleep(250);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}

		player.spigot().respawn();
		player.teleport(Main.spawn);
	}

	public static void PlayShotforEveryone() {
		for (Player p : Bukkit.getOnlinePlayers()) {
			p.playSound(p.getLocation(), "squidgame.red_light_green_light.gunshot", 3, 100);
		}
	}

	public static void PlayShotforEveryone(Location loc) {
		for (Player p : Bukkit.getOnlinePlayers()) {
			p.playSound(loc, "squidgame.red_light_green_light.gunshot", 3, 100);
		}
	}

}
