package com.overloup.squidgame.game;

import java.util.ArrayList;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.inventory.ItemStack;

import com.overloup.squidgame.Main;
import com.overloup.squidgame.data.GameManager;
import com.overloup.squidgame.data.GameManager.Game;
import com.overloup.squidgame.utilities.Elimination;
import com.overloup.squidgame.utilities.NPC;
import com.overloup.squidgame.utilities.ScoreBoard;

public class GlassStepping {

	private static String space = "§7----------------------------";
	private static ArrayList<Block> breakable = new ArrayList<>();
	private static ArrayList<Block> leftside = new ArrayList<>();
	private static ArrayList<Block> rightside = new ArrayList<>();
	private static ArrayList<Player> players = new ArrayList<>();
	private static int time = 60 * 6;

	public static void Setup() {
		GameManager.setGame(Game.GLASSSTEPPING);
		for (int i = 149; i < 170; i++) {
			Block lb = Main.world.getBlockAt(i, 63, 100);
			if (lb.getType().equals(Material.GLASS)) {
				leftside.add(lb);
			}
			Block rb = Main.world.getBlockAt(i, 63, 98);
			if (rb.getType().equals(Material.GLASS)) {
				rightside.add(rb);
			}
		}
		for (int i = 179; i < 196; i++) {
			Block lb = Main.world.getBlockAt(i, 63, 100);
			if (lb.getType().equals(Material.GLASS)) {
				leftside.add(lb);
			}
			Block rb = Main.world.getBlockAt(i, 63, 98);
			if (rb.getType().equals(Material.GLASS)) {
				rightside.add(rb);
			}
		}

		for (int i = 0; i < leftside.size(); i++) {
			if (Math.random() > 0.5) {
				breakable.add(leftside.get(i).getRelative(BlockFace.UP));
				breakable.add(leftside.get(i));
			} else {
				breakable.add(rightside.get(i).getRelative(BlockFace.UP));
				breakable.add(rightside.get(i));
			}
		}

		int i = 0;
		for (Player p : Main.participants) {
			i++;
			p.teleport(new Location(Main.world, 201, 64, 99, 90f, 1.7f));
			p.sendMessage("§aYou are Number §e" + i);
			p.setPlayerListName("§e" + i + " §f| " + p.getName());
			players.add(p);
		}

		Location loc = new Location(Main.world, 195, 66, 99, -95, 30);
		for (Player p : Bukkit.getOnlinePlayers()) {
			ScoreBoard.set(p, "§fGlass Stones", "§fJump on a Glass Stone, but Careful", "§fFake ones are Among us",
					true);
			if (Main.guards.contains(p)) {
				p.teleport(loc);
			}
		}

		NPC.createNPC(new Location(Main.world, 141.5, 64, 101.5, -90f, 0f));
		NPC.createNPC(new Location(Main.world, 141.5, 64, 97.5, -90f, 0f));
		NPC.createNPC(new Location(Main.world, 204.5, 64, 101.5, 90f, 0f));
		NPC.createNPC(new Location(Main.world, 204.5, 64, 97.5, 90f, 0f));

		Main.frontman.getInventory().setItem(4, new ItemStack(Material.GREEN_CONCRETE));
		Main.frontman.sendMessage(space);
		Main.frontman.sendMessage("§aGame finished Setup");
		Main.frontman.sendMessage("§bWhen you are Ready Click the Green Concrete");
		Main.frontman.sendMessage(space);
	}

	public static void StartGame() {
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
		Main.frontman.getInventory().setItem(4, new ItemStack(Material.AIR));
	}

	public static void PlayerMove(PlayerMoveEvent event) {
		Player p = event.getPlayer();
		Block b = p.getLocation().getBlock();

		if (!players.contains(p))
			return;

		if (breakable.contains(b)) {
			b.getRelative(BlockFace.DOWN).breakNaturally();
			b.breakNaturally();
			for (Player player : Bukkit.getOnlinePlayers()) {
				player.playSound(p.getLocation(), Sound.BLOCK_GLASS_BREAK, 2, 1);
			}
		}
	}

	public static void onPlayerDeath(PlayerDeathEvent event) {
		Player p = event.getEntity();

		Main.participants.remove(p);
		players.remove(p);
		Bukkit.broadcastMessage("§e" + p.getName() + " §chas been Eliminated");
		p.spigot().respawn();
		event.setDeathMessage("");
		p.setPlayerListName(p.getName());

		if (Main.participants.isEmpty()) {
			Bukkit.broadcastMessage("§cTHE GAMES ARE OVER! §7Nobody won :(");
		}
	}

	public static void endGame() {
		for (Player p : players) {
			Elimination.Eliminate(p);
			players.remove(p);
			Main.participants.remove(p);
		}

		if (Main.participants.isEmpty()) {
			Main.frontman.sendMessage(space);
			Main.frontman.sendMessage("§aSquid Game Ended");
			Main.frontman.sendMessage("§bNo one won");
			Main.frontman.sendMessage(space);
			return;
		}

		for (int i = 0; i < 9; i++) {
			Main.frontman.getInventory().setItem(i, new ItemStack(Material.AIR));
		}

		for (Player p : Main.participants) {
			p.teleport(Main.spawn);
		}
		Bukkit.getScheduler().cancelTasks(Main.plugin);
	}

}