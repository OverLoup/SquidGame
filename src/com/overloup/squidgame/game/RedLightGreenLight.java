package com.overloup.squidgame.game;

import java.util.ArrayList;

import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.inventory.ItemStack;

import com.overloup.squidgame.Main;
import com.overloup.squidgame.data.GameManager;
import com.overloup.squidgame.data.GameManager.Game;
import com.overloup.squidgame.utilities.Doll;
import com.overloup.squidgame.utilities.Elimination;
import com.overloup.squidgame.utilities.NPC;
import com.overloup.squidgame.utilities.ScoreBoard;

public class RedLightGreenLight {

	private static ArrayList<Player> winners = new ArrayList<>();
	private static ArrayList<Player> players = new ArrayList<>();
	private static boolean canmove = true;
	private static ArrayList<Block> changeableblocks = new ArrayList<>();
	private static int time = 60 * 4;

	public static void Setup() {
		for (int i = 11; i < 108; i++) {
			changeableblocks.add(Main.world.getBlockAt(i, 29, 3));
			changeableblocks.add(Main.world.getBlockAt(i, 29, 35));
		}
		for (int i = 4; i < 35; i++) {
			Main.world.getBlockAt(10, 30, i).setType(Material.RED_CONCRETE);
		}

		Location spawn = new Location(Main.world, 5, 30, 19, -89f, 19f);
		Location adminspawn = new Location(Main.world, 25, 38, 19, 89f, 19f);

		GameManager.setGame(Game.REDLIGHTGREENLIGHT);
		Doll.spawn();

		for (Player p : Bukkit.getOnlinePlayers()) {
			ScoreBoard.set(p, "§fRed/Green Light", "§fMove on §aGreen Light", "§fStop on §cRed Light", true);

			if (Main.participants.contains(p)) {
				p.teleport(spawn);
				players.add(p);
			}
			if (Main.guards.contains(p)) {
				p.teleport(adminspawn);
				p.setGameMode(GameMode.CREATIVE);
				p.setFlying(true);
			}
		}

		NPC.createNPC(new Location(Main.world, 115.5, 29, 6.5, 90f, 0f));
		NPC.createNPC(new Location(Main.world, 115.5, 29, 10.5, 90f, 0f));
		NPC.createNPC(new Location(Main.world, 115.5, 29, 14.5, 90f, 0f));
		NPC.createNPC(new Location(Main.world, 115.5, 29, 24.5, 90f, 0f));
		NPC.createNPC(new Location(Main.world, 115.5, 29, 28.5, 90f, 0f));
		NPC.createNPC(new Location(Main.world, 115.5, 29, 32.5, 90f, 0f));

		Main.frontman.teleport(adminspawn);
		Main.frontman.getInventory().setItem(4, new ItemStack(Material.GREEN_CONCRETE));
		Main.frontman.sendMessage("§7----------------------------");
		Main.frontman.sendMessage("§aGame finished Setup");
		Main.frontman.sendMessage("§bOnce you are ready, you can Click the §agreen §bConcrete");
		Main.frontman.sendMessage("§bGame will then Immediately Start meaning Players are allowed to Run!");
		Main.frontman.sendMessage("§6Use HotBar Slot 9 to Change the Lights");
		Main.frontman.sendMessage("§7----------------------------");
	}

	public static void StartGame() {
		Bukkit.broadcastMessage("§aThe Round has Started!");
		Main.frontman.getInventory().setItem(4, new ItemStack(Material.AIR));
		Main.frontman.getInventory().setItem(8, new ItemStack(Material.RED_DYE));
		switchlight(true);
		final Runnable timer = new Runnable() {
			@Override
			public void run() {
				time--;
				if (time < 0 || players.isEmpty()) {
					endGame();
				}
				String update = String.format("%02d:%02d", (time / 60), (time % 60));
				ScoreBoard.updateTime(update);
			}
		};
		Bukkit.getScheduler().scheduleSyncRepeatingTask(Main.plugin, timer, 0, 20);
		for (int i = 4; i < 35; i++) {
			Main.world.getBlockAt(10, 30, i).setType(Material.AIR);
		}
	}

	public static void switchlight(boolean state) {
		if (state) {
			for (Block b : changeableblocks) {
				b.setType(Material.GREEN_WOOL);
			}
			canmove = state;
			Bukkit.broadcastMessage("§aGREEN LIGHT! §7Run");
			for (Player p : Bukkit.getOnlinePlayers()) {
				p.playSound(p.getLocation(), "squidgame.red_light_green_light.start", 1, 1);
			}
			Doll.turn();
		} else {
			for (Block b : changeableblocks) {
				b.setType(Material.RED_WOOL);
			}
			Bukkit.broadcastMessage("§cRED LIGHT! §7Do not Move");
			for (Player p : Bukkit.getOnlinePlayers()) {
				p.stopSound("squidgame.red_light_green_light.start");
				p.playSound(p.getLocation(), "squidgame.red_light_green_light.end", 1, 1);
			}
			Main.async.execute(() -> {
				try {
					Thread.sleep(750);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
				canmove = state;
				Doll.turn();
			});
		}
	}

	@SuppressWarnings("deprecation")
	public static void PlayerMoves(PlayerMoveEvent event) {
		Player player = event.getPlayer();
		Location to = player.getLocation();
		Location from = event.getFrom();

		if (winners.contains(player)) {
			if (to.getX() < 109) {
				Location telto = new Location(Main.world, to.getX() + 2, to.getY(), to.getZ());
				telto.setYaw(from.getYaw());
				player.teleport(telto);
				return;
			}
		}

		if (!players.contains(player))
			return;

		if (to.getBlock().getRelative(BlockFace.DOWN).getRelative(BlockFace.DOWN).getType() == Material.SPONGE) {
			players.remove(player);
			winners.add(player);
			Bukkit.broadcastMessage("§e" + player.getName() + " §apassed! §7They can Proceed to the next Round");
			return;
		}

		if (canmove)
			return;

		if (to.getX() != from.getX() || to.getZ() != from.getZ() || !player.isOnGround()) {
			Main.participants.remove(player);
			players.remove(player);
			Elimination.Eliminate(player);
		}
	}

	public static void endGame() {
		changeableblocks.clear();

		for (Player p : Bukkit.getOnlinePlayers()) {
			p.stopSound("squidgame.red_light_green_light.start");
		}

		Bukkit.broadcastMessage("§cThe Game has Ended!");
		Bukkit.getScheduler().cancelTasks(Main.plugin);

		for (Player p : players) {
			Elimination.Eliminate(p);
			Elimination.PlayShotforEveryone();
			Main.participants.remove(p);
		}

		players.clear();
		changeableblocks.clear();
		winners.clear();
		Doll.destroy();

		for (Player p : Main.participants) {
			p.teleport(Main.spawn);
		}
		for (int i = 0; i < 9; i++) {
			Main.frontman.getInventory().setItem(i, new ItemStack(Material.AIR));
		}
		Main.frontman.sendMessage("§eUse the /startsquid command to start the next game!");
	}
}
