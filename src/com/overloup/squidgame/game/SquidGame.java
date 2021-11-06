package com.overloup.squidgame.game;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.inventory.ItemStack;

import com.overloup.squidgame.Main;
import com.overloup.squidgame.data.GameManager;
import com.overloup.squidgame.data.GameManager.Game;
import com.overloup.squidgame.utilities.ScoreBoard;

public class SquidGame {

	private static Location spawn = new Location(Main.world, -46, 29, 97, -1f, 3.4f);
	private static Location adminspawn = new Location(Main.world, -46, 39, 108);

	public static void Setup() {
		GameManager.setGame(Game.SQUIDGAME);
		for (Player p : Bukkit.getOnlinePlayers()) {
			if (Main.participants.contains(p)) {
				p.teleport(spawn);
			}
			if (Main.guards.contains(p)) {
				p.teleport(adminspawn);
			}
			ScoreBoard.set(p, "§fSquid Game", "§fFight to the end", "§fLast Standing Wins", false);
		}

		Main.frontman.teleport(adminspawn);
		Main.frontman.getInventory().setItem(4, new ItemStack(Material.GREEN_CONCRETE));
		Main.frontman.getInventory().setItem(6, new ItemStack(Material.YELLOW_CONCRETE));
		Main.frontman.sendMessage("§7----------------------------");
		Main.frontman.sendMessage("§aGame finished Setup");
		Main.frontman.sendMessage("§bOnce you are ready, you can Click the §agreen §bConcrete");
		Main.frontman.sendMessage("§bClick the §eYellow Concrete §bto give every Player a Sword");
		Main.frontman.sendMessage("§7----------------------------");
	}

	public static void StartGame() {
		Main.frontman.getInventory().setItem(6, new ItemStack(Material.AIR));
		Bukkit.broadcastMessage("§aThe Game Stardet");
	}

	public static void addSword() {
		Main.frontman.getInventory().setItem(4, new ItemStack(Material.AIR));
		for (Player p : Main.participants) {
			p.getInventory().setItem(0, new ItemStack(Material.WOODEN_SWORD));
			p.sendMessage("§eYou got a Sword. §7Use it to Spice up the Game");
		}
	}

	public static void onDeath(PlayerDeathEvent event) {
		Player p = event.getEntity();

		if (!Main.participants.contains(p))
			return;

		Main.participants.remove(p);

		Bukkit.broadcastMessage("§e" + p.getName() + " §cdied!");
		if (Main.participants.size() == 1) {
			endGame();
		}
	}

	public static void endGame() {
		Bukkit.broadcastMessage("§cTHE SQUID GAMES ENDED!");
		Bukkit.broadcastMessage("§cThe Grand Winner is §e" + Main.participants.get(0).getName());

		for (Player p : Bukkit.getOnlinePlayers()) {
			p.playSound(p.getLocation(), Sound.ENTITY_ENDER_DRAGON_DEATH, 1, 5);
			p.teleport(Main.spawn);
		}

		Main.frontman.sendMessage("§bIf you want to Play again, please Complete the Setup Steps to reset the Server");
	}
}
