package com.overloup.squidgame.game;

import java.util.ArrayList;
import java.util.Random;

import org.bukkit.Bukkit;
import org.bukkit.Color;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.inventory.ItemStack;

import com.overloup.squidgame.Main;
import com.overloup.squidgame.data.GameManager;
import com.overloup.squidgame.data.GameManager.Game;
import com.overloup.squidgame.utilities.CustomItem;
import com.overloup.squidgame.utilities.Elimination;
import com.overloup.squidgame.utilities.NPC;
import com.overloup.squidgame.utilities.ScoreBoard;

public class TugofWar {

	private static Location spawn = new Location(Main.world, 53, 25, -62, -0.3f, 1);

	private static ArrayList<Player> redAlive = new ArrayList<>();
	private static ArrayList<Player> redDead = new ArrayList<>();

	private static ArrayList<Player> blueAlive = new ArrayList<>();
	private static ArrayList<Player> blueDead = new ArrayList<>();

	public static int miniumplayers = 20;

	public static void Setup() {
		GameManager.setGame(Game.TUGOFWAR);
		if (Main.participants.size() < miniumplayers) {
			endGame("NOTENOUGHPPL");
			return;
		}

		for (Player p : Main.participants) {
			p.teleport(spawn);
		}

		Random r = new Random();
		ArrayList<Player> players = new ArrayList<>();
		for (Player p : Main.participants) {
			players.add(p);
		}

		Location adminspawn = new Location(Main.world, 53, 45, -39, 179, 37);
		for (Player p : Bukkit.getOnlinePlayers()) {
			ScoreBoard.set(p, "§fThug of War", "§fPunch your Enemies off the Bridge", "§for Fall to your own Death",
					false);
			if (Main.guards.contains(p)) {
				p.teleport(adminspawn);
			}
		}

		if (players.size() % 2 != 0) {
			players.remove(r.nextInt(players.size()));
		}
		int teamSize = players.size() / 2;
		for (int i = 0; i < teamSize; i++) {
			redAlive.add(players.get(r.nextInt(players.size())));
			blueAlive.add(players.get(r.nextInt(players.size())));
		}

		for (Player p : redAlive) {
			p.teleport(new Location(Main.world, 71, 38, -32, 90, 0));
			p.getInventory().setHelmet(CustomItem.coloredArmor(Material.LEATHER_HELMET, Color.RED));
			p.getInventory().setChestplate(CustomItem.coloredArmor(Material.LEATHER_CHESTPLATE, Color.RED));
			p.getInventory().setLeggings(CustomItem.coloredArmor(Material.LEATHER_LEGGINGS, Color.RED));
			p.getInventory().setBoots(CustomItem.coloredArmor(Material.LEATHER_BOOTS, Color.RED));
		}
		for (Player p : blueAlive) {
			p.teleport(new Location(Main.world, 34, 38, -32, -90, 0));
			p.getInventory().setHelmet(CustomItem.coloredArmor(Material.LEATHER_HELMET, Color.BLUE));
			p.getInventory().setChestplate(CustomItem.coloredArmor(Material.LEATHER_CHESTPLATE, Color.BLUE));
			p.getInventory().setLeggings(CustomItem.coloredArmor(Material.LEATHER_LEGGINGS, Color.BLUE));
			p.getInventory().setBoots(CustomItem.coloredArmor(Material.LEATHER_BOOTS, Color.BLUE));
		}

		NPC.createNPC(new Location(Main.world, 49, 24, -62, 0f, 0f));
		NPC.createNPC(new Location(Main.world, 56, 24, -62, 0f, 0f));

		Main.world.getBlockAt(52, 38, -31).setType(Material.DARK_OAK_FENCE);
		Main.world.getBlockAt(53, 38, -31).setType(Material.DARK_OAK_FENCE);
		Main.world.getBlockAt(52, 38, -32).setType(Material.DARK_OAK_FENCE);
		Main.world.getBlockAt(53, 38, -32).setType(Material.DARK_OAK_FENCE);
		Main.world.getBlockAt(52, 38, -33).setType(Material.DARK_OAK_FENCE);
		Main.world.getBlockAt(53, 38, -33).setType(Material.DARK_OAK_FENCE);

		NPC.createNPC(new Location(Main.world, 49.5, 25, -63.5, 0f, 0f));
		NPC.createNPC(new Location(Main.world, 46.5, 25, -63.5, 0f, 0f));

		GameManager.setGame(Game.TUGOFWAR);
		Main.frontman.getInventory().setItem(4, new ItemStack(Material.GREEN_CONCRETE));
		Main.frontman.sendMessage("§7----------------------------");
		Main.frontman.sendMessage("§aGame finished Setup");
		Main.frontman.sendMessage("§bOnce you are ready, you can Click the §agreen §bConcrete");
		Main.frontman.sendMessage("§bGame will then Immediately Start meaning Players are allowed to Fight!");
		Main.frontman.sendMessage("§7----------------------------");
	}

	public static void StartGame() {
		Bukkit.broadcastMessage("§aThe Round has Started!");
		Main.frontman.getInventory().setItem(4, new ItemStack(Material.AIR));

		Main.world.getBlockAt(52, 38, -31).breakNaturally();
		Main.world.getBlockAt(53, 38, -31).breakNaturally();
		Main.world.getBlockAt(52, 38, -32).breakNaturally();
		Main.world.getBlockAt(53, 38, -32).breakNaturally();
		Main.world.getBlockAt(52, 38, -33).breakNaturally();
		Main.world.getBlockAt(53, 38, -33).breakNaturally();
	}

	public static void PvPAction(EntityDamageByEntityEvent event) {
		if (!(event.getDamager() instanceof Player) || !(event.getEntity() instanceof Player))
			return;

		Player player = (Player) event.getEntity();
		Player damager = (Player) event.getDamager();
		if (redAlive.contains(damager) && redAlive.contains(player)
				|| blueAlive.contains(damager) && blueAlive.contains(player)) {
			event.setCancelled(true);
		}
		event.setDamage(0);
	}

	public static void onDeath(PlayerDeathEvent event) {
		if (redAlive.contains(event.getEntity())) {
			redAlive.remove(event.getEntity());
			redDead.add(event.getEntity());
			event.setDeathMessage("");
			if (redAlive.isEmpty()) {
				endGame("BLUEWIN");
			}
		} else if (blueAlive.contains(event.getEntity())) {
			blueAlive.remove(event.getEntity());
			blueDead.add(event.getEntity());
			event.setDeathMessage("");
			if (blueAlive.isEmpty()) {
				endGame("REDWIN");
			}
		} else
			return;

		Player p = event.getEntity();
		p.spigot().respawn();
		p.setInvisible(true);
		p.teleport(spawn);
		p.sendMessage(
				"§4You have been eliminated but there is still hope! If your teammates end up winning, you will be revived!");
	}

	@SuppressWarnings("deprecation")
	public static void endGame(String reason) {
		if (reason.equalsIgnoreCase("NOTENOUGHPPL")) {
			Bukkit.broadcastMessage("§cWe dont have enough Players! This Game will be Skipped!");
			return;
		}

		if (reason.equalsIgnoreCase("REDWIN")) {
			for (Player p : Main.participants)
				p.sendTitle("§4RED WINS", "§eBlue team is Eliminated");
			for (Player p : redDead) {
				p.setInvisible(false);
				p.sendMessage("§eYou have been revived because your teammates won!");
			}
			for (Player p : blueAlive) {
				Main.async.execute(() -> {
					Main.participants.remove(p);
					Elimination.Eliminate(p);
				});
			}
			for (Player p : blueDead) {
				Main.async.execute(() -> {
					Main.participants.remove(p);
					Elimination.Eliminate(p);
				});
			}
		}
		if (reason.equalsIgnoreCase("BLUEWIN")) {
			for (Player p : Main.participants)
				p.sendTitle("§9BLUE WINS", "§eRed team is Eliminated");
			for (Player p : blueDead) {
				p.setInvisible(false);
				p.sendMessage("§eYou have been revived because your teammates won!");
			}
			for (Player p : redAlive) {
				Main.async.execute(() -> {
					Main.participants.remove(p);
					Elimination.Eliminate(p);
				});
			}
			for (Player p : redDead) {
				Main.async.execute(() -> {
					Main.participants.remove(p);
					Elimination.Eliminate(p);
				});
			}
		}

		for (Player p : Main.participants) {
			p.teleport(Main.spawn);
			p.playSound(p.getLocation(), Sound.UI_TOAST_CHALLENGE_COMPLETE, 1, 1);
			p.getInventory().clear();
		}

		for (int i = 0; i < 9; i++) {
			Main.frontman.getInventory().setItem(i, new ItemStack(Material.AIR));
		}
	}

}
