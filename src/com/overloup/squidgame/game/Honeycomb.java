package com.overloup.squidgame.game;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map.Entry;

import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;

import com.overloup.squidgame.Main;
import com.overloup.squidgame.data.GameManager;
import com.overloup.squidgame.data.GameManager.Game;
import com.overloup.squidgame.data.HoneyCombs;
import com.overloup.squidgame.utilities.CustomItem;
import com.overloup.squidgame.utilities.Elimination;
import com.overloup.squidgame.utilities.NPC;
import com.overloup.squidgame.utilities.ScoreBoard;

public class Honeycomb {

	private static HashMap<Player, HoneyCombs> ownedblocks = new HashMap<>();
	private static ArrayList<Player> immunity = new ArrayList<>();
	private static ArrayList<Player> players = new ArrayList<>();
	private static ArrayList<Location> locs = new ArrayList<>();

	private static int time = 60 * 6;

	public static void Setup() {
		for (int i = 8; i < 87; i = i + 13) {
			locs.add(new Location(Main.world, i, 29, 62));
			locs.add(new Location(Main.world, i, 29, 74));
			locs.add(new Location(Main.world, i, 29, 108));
			locs.add(new Location(Main.world, i, 29, 120));
			locs.add(new Location(Main.world, i, 29, 154));
			locs.add(new Location(Main.world, i, 29, 166));
		}

		Location spawn = new Location(Main.world, 40, 29, 91, 0f, 12f);
		for (Player p : Main.participants) {
			p.teleport(spawn);
			immunity.add(p);
		}

		ItemStack pic = CustomItem.unbreakableItem(Material.GOLDEN_PICKAXE, "§eNeedle", 1);
		ItemStack verify = CustomItem.unbreakableItem(Material.YELLOW_CONCRETE, "§eVerify Player", 1);

		for (int i = 0; i < Main.participants.size(); i++) {
			Player p = Main.participants.get(i);
			Location caluclationblock = locs.get(i);
			Location honeyspawn = new Location(Main.world, caluclationblock.getX(), caluclationblock.getY() + 3,
					caluclationblock.getZ());

			ownedblocks.put(p, new HoneyCombs(p, caluclationblock, honeyspawn, -180));
			immunity.remove(p);
			players.add(p);
			p.getInventory().setItem(0, pic);
		}

		NPC.createNPC(new Location(Main.world, 72.5, 29, 83.5, 90f, 0f));
		NPC.createNPC(new Location(Main.world, 72.5, 29, 98.5, 90f, 0f));
		NPC.createNPC(new Location(Main.world, 72.5, 29, 130.5, 90f, 0f));
		NPC.createNPC(new Location(Main.world, 72.5, 29, 144.5, 90f, 0f));
		NPC.createNPC(new Location(Main.world, 39.5, 29, 83.5, 180f, 0f));
		NPC.createNPC(new Location(Main.world, 39.5, 29, 98.5, 0f, 0f));
		NPC.createNPC(new Location(Main.world, 39.5, 29, 130.5, 180f, 0f));
		NPC.createNPC(new Location(Main.world, 39.5, 29, 144.5, -0f, 0f));
		NPC.createNPC(new Location(Main.world, 4.5, 29, 83.5, -90f, 0f));
		NPC.createNPC(new Location(Main.world, 4.5, 29, 98.5, -90f, 0f));
		NPC.createNPC(new Location(Main.world, 4.5, 29, 130.5, -90f, 0f));
		NPC.createNPC(new Location(Main.world, 4.5, 29, 144.5, -90f, 0f));

		Main.frontman.getInventory().setItem(4, CustomItem.unbreakableItem(Material.GREEN_CONCRETE, "§aStart Game", 1));
		Main.frontman.getInventory().setItem(6, verify);
		Main.frontman.sendMessage("§7----------------------------");
		Main.frontman.sendMessage("§aGame finished Setup");
		Main.frontman.sendMessage("§bClick the Green Concrete to Start the Timer");
		Main.frontman.sendMessage("§7----------------------------");

		Location adminspawn = new Location(Main.world, 39, 47, 109, 89f, 19f);
		Main.frontman.teleport(adminspawn);
		for (Player p : Bukkit.getOnlinePlayers()) {
			ScoreBoard.set(p, "§fHoney Comb", "§fCut out the Honey", "§fBreak the Honey, you ded", true);
			if (Main.guards.contains(p)) {
				p.getInventory().setItem(4, verify);
				p.sendMessage("§7----------------------------");
				p.sendMessage("§aUse the yellow concrete while standing");
				p.sendMessage("§ain a players plot to verify completion.");
				p.sendMessage("§7----------------------------");
				p.teleport(adminspawn);
				p.setGameMode(GameMode.CREATIVE);
				p.setFlying(true);
			}
			if (immunity.contains(p)) {
				p.sendMessage("§7You have been Granted Immunity for this Game! Lean back and Enjoy the Show.");
			}
		}
		GameManager.setGame(Game.HONEYCOME);
	}

	public static void StartGame() {
		Bukkit.broadcastMessage("§aThe Round has Started!");
		Main.frontman.getInventory().setItem(4, new ItemStack(Material.AIR));
		final Runnable timer = new Runnable() {
			@Override
			public void run() {
				time--;
				if (time < 0) {
					endGame();
				}
				String update = String.format("%02d:%02d", (time / 60), (time % 60));
				ScoreBoard.updateTime(update);
			}
		};
		Bukkit.getScheduler().scheduleSyncRepeatingTask(Main.plugin, timer, 0, 20);

		for (Player p : players) {
			HoneyCombs combs = ownedblocks.get(p);
			Location loc = combs.spawn;
			loc.setYaw(combs.yawn);
			p.teleport(loc);
		}
	}

	public static void VerifyPlot(PlayerInteractEvent event) {
		Location loc = event.getPlayer().getLocation();
		for (Entry<Player, HoneyCombs> plot : ownedblocks.entrySet()) {
			if (plot.getValue().loc.distance(loc) < 5) {
				Player p = plot.getKey();
				players.remove(p);
				event.getPlayer().sendMessage("§7You have verified " + p.getName());
				event.getPlayer().playSound(event.getPlayer().getLocation(), Sound.ENTITY_ARROW_HIT_PLAYER, 1f, 1);

				p.sendMessage("§6You have been verified! Great job!");
				p.playSound(event.getPlayer().getLocation(), Sound.UI_TOAST_CHALLENGE_COMPLETE, 1f, 1);
				p.getInventory().clear();

				ownedblocks.remove(plot.getKey());
				if (players.isEmpty()) {
					endGame();
				}
				
				event.setCancelled(true);
				return;
			}
		}

		event.getPlayer().sendMessage("§cA plot does not exist here or has already been verified");
		event.getPlayer().playSound(event.getPlayer().getLocation(), Sound.ENTITY_VILLAGER_NO, 1f, 1);
		event.setCancelled(true);
	}

	public static void CheckBrokenBlock(BlockBreakEvent event) {
		Player player = event.getPlayer();
		Block block = event.getBlock();

		if (!players.contains(player)) {
			event.setCancelled(true);
			return;
		}

		Location loc = ownedblocks.get(player).loc;
		double dis = block.getLocation().distance(loc);
		if (dis > 7) {
			event.setCancelled(true);
			player.sendMessage("§7Hey! You can't break that block!");
			return;
		}

		if (block.getType().equals(Material.HONEYCOMB_BLOCK)) {
			player.playSound(player.getLocation(), Sound.ENTITY_ARROW_HIT_PLAYER, 0.3f, 1);
			block.setType(Material.AIR);
			return;
		}

		if (block.getType().equals(Material.HONEY_BLOCK)) {
			Main.async.execute(() -> {
				Elimination.Eliminate(player);
				Elimination.PlayShotforEveryone(player.getLocation());
			});
			players.remove(player);
			Main.participants.remove(player);
			return;
		}

		event.setCancelled(true);
		player.sendMessage("§7Hey! You can't break that block!");
	}

	public static void endGame() {
		for (Player p : players) {
			Elimination.Eliminate(p);
			Main.participants.remove(p);
		}
		if (!players.isEmpty())
			Elimination.PlayShotforEveryone();

		ownedblocks.clear();
		immunity.clear();
		players.clear();
		locs.clear();

		Bukkit.broadcastMessage("§cThe Game Ended!");
		for (Player p : Main.participants) {
			p.teleport(Main.spawn);
		}
		for (int i = 0; i < 9; i++) {
			Main.frontman.getInventory().setItem(i, new ItemStack(Material.AIR));
		}
		Bukkit.getScheduler().cancelTasks(Main.plugin);

		Main.frontman.sendMessage("§eUse the /startsquid command to start the next game!");
	}
}
