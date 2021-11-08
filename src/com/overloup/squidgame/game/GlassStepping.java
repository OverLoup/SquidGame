package com.overloup.squidgame.game;

import java.util.ArrayList;
import java.util.Collections;

import org.bukkit.Bukkit;
import org.bukkit.GameMode;
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
				breakable.add(leftside.get(i).getRelative(BlockFace.UP).getRelative(BlockFace.WEST));
				breakable.add(leftside.get(i).getRelative(BlockFace.UP).getRelative(BlockFace.EAST));
				breakable.add(leftside.get(i).getRelative(BlockFace.UP).getRelative(BlockFace.UP));
				breakable.add(leftside.get(i).getRelative(BlockFace.UP));
				breakable.add(leftside.get(i));
			} else {
				breakable.add(rightside.get(i).getRelative(BlockFace.UP).getRelative(BlockFace.WEST));
				breakable.add(rightside.get(i).getRelative(BlockFace.UP).getRelative(BlockFace.EAST));
				breakable.add(rightside.get(i).getRelative(BlockFace.UP).getRelative(BlockFace.UP));
				breakable.add(rightside.get(i).getRelative(BlockFace.UP));
				breakable.add(rightside.get(i));
			}
		}
		
		for (Player p : Bukkit.getOnlinePlayers()) {
			// Filter out spectators
			if (!Main.participants.contains(p) && !Main.guards.contains(p) && Main.frontman.equals(p))
				p.teleport(new Location(Main.world, 201, 64, 99, 90f, 1.7f));
		}

		Collections.shuffle(Main.participants);
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
		Main.frontman.sendMessage("§7----------------------------");
		Main.frontman.sendMessage("§aGame finished Setup");
		Main.frontman.sendMessage("§bWhen you are Ready Click the Green Concrete");
		Main.frontman.sendMessage("§7----------------------------");
		Main.frontman.teleport(loc);
	}

	public static void StartGame() {
		Bukkit.broadcastMessage("§aThe Round has Started!");

		final Runnable timer = new Runnable() {
			@Override
			public void run() {
				time--;
				if (time < 0 || Main.participants.isEmpty()) {
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
			Location loc = p.getLocation();
			loc.setY(p.getLocation().getY() - 1);
			p.teleport(loc);
			b.getRelative(BlockFace.WEST).getRelative(BlockFace.DOWN).setType(Material.AIR);
			b.getRelative(BlockFace.UP).getRelative(BlockFace.WEST).setType(Material.AIR);
			b.getRelative(BlockFace.UP).getRelative(BlockFace.EAST).setType(Material.AIR);
			b.getRelative(BlockFace.DOWN).getRelative(BlockFace.DOWN).setType(Material.AIR);
			b.getRelative(BlockFace.DOWN).setType(Material.AIR);
			b.setType(Material.AIR);
			for (Player player : Bukkit.getOnlinePlayers()) {
				player.playSound(p.getLocation(), Sound.BLOCK_GLASS_BREAK, 2, 1);
			}
		}

		if (b.getRelative(BlockFace.DOWN).getType().equals(Material.RED_CONCRETE)) {
			players.remove(p);
			Bukkit.broadcastMessage("§e" + p.getName() + " §aPassed! §7They can Proceed to the Final Game");
		}
	}

	public static void onPlayerDeath(PlayerDeathEvent event) {
		Player p = event.getEntity();

		Main.participants.remove(p);
		Bukkit.broadcastMessage("§e" + p.getName() + " §chas been Eliminated");
		p.spigot().respawn();
		event.setDeathMessage("");
		p.setPlayerListName(p.getName());
		p.setGameMode(GameMode.SPECTATOR);
		p.teleport(new Location(Main.world, 174, 54, 56, 0f, -10f));

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
			Main.frontman.sendMessage("§aSquid Game Ended. §7No one won :(");
		}

		for (int i = 0; i < 9; i++) {
			Main.frontman.getInventory().setItem(i, new ItemStack(Material.AIR));
		}

		for (Player p : Main.participants) {
			p.teleport(Main.spawn);
		}

		Main.frontman.sendMessage("§eTo Start the next Game use §a/startsquid");
		Bukkit.getScheduler().cancelTasks(Main.plugin);
	}

}