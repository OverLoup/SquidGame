package com.overloup.squidgame.listener;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.EntityCombustEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.FoodLevelChangeEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.AsyncPlayerPreLoginEvent;
import org.bukkit.event.player.AsyncPlayerPreLoginEvent.Result;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.inventory.ItemStack;

import com.overloup.squidgame.Main;
import com.overloup.squidgame.data.GameManager;
import com.overloup.squidgame.data.GameManager.Game;
import com.overloup.squidgame.game.GlassStepping;
import com.overloup.squidgame.game.Honeycomb;
import com.overloup.squidgame.game.RedLightGreenLight;
import com.overloup.squidgame.game.SquidGame;
import com.overloup.squidgame.game.TugofWar;
import com.overloup.squidgame.utilities.ChangeSkin;
import com.overloup.squidgame.utilities.Elimination;
import com.overloup.squidgame.utilities.NPC;

public class PlayerListener implements Listener {

	@EventHandler
	public void onPreConnect(AsyncPlayerPreLoginEvent event) {
		if (!GameManager.getGame().equals(Game.LOBBY)) {
			event.disallow(Result.KICK_FULL, "§cThe Game already Stardet!");
			Main.frontman.sendMessage("§e" + event.getName() + " §atried to join the Running Game");
		}
	}

	@EventHandler
	public void onFood(FoodLevelChangeEvent event) {
		event.setCancelled(true);
	}

	@EventHandler
	public void onJoin(PlayerJoinEvent event) {
		Player player = event.getPlayer();

		if (player.isOp()) {
			if (Main.frontman == null) {
				Main.frontman = player;
			} else {
				Main.guards.add(player);
			}
		} else {
			Main.participants.add(player);
		}

		NPC.onJoin(player);
		ChangeSkin.change(player);

		player.teleport(Main.spawn);
		player.setResourcePack("https://beatingkids.club/images/squidpack.zip");

		player.sendMessage("§7-----------------------------------");
		player.sendMessage("§cIt is incredibly Important that you load the Texture Pack!");
		player.sendMessage("§bUse §e/resourcepack §bif you denied the Download.");
		player.sendMessage("§cYou will not be able to play the Game without the Pack!");
		player.sendMessage("§7-----------------------------------");
		event.setJoinMessage(
				"§e" + player.getName() + " §ejoined the Game. [" + Main.participants.size() + "/" + 100 + "]");
	}

	@EventHandler
	public void onInteraction(PlayerInteractEvent event) {
		Player player = event.getPlayer();
		ItemStack item = player.getInventory().getItemInMainHand();

		if (item == null)
			return;

		if (GameManager.getGame() == Game.REDLIGHTGREENLIGHT && Main.frontman.equals(player)) {
			if (item.getType().equals(Material.GREEN_CONCRETE)) {
				RedLightGreenLight.StartGame();
			}
			if (item.getType().equals(Material.GREEN_DYE)) {
				player.getInventory().setItem(8, new ItemStack(Material.RED_DYE));
				RedLightGreenLight.switchlight(true);
			}
			if (item.getType().equals(Material.RED_DYE)) {
				player.getInventory().setItem(8, new ItemStack(Material.GREEN_DYE));
				RedLightGreenLight.switchlight(false);
			}
		}

		if (GameManager.getGame() == Game.HONEYCOME && Main.frontman.equals(player)) {
			if (item.getType().equals(Material.GREEN_CONCRETE)) {
				Honeycomb.StartGame();
			}
			if (item.getType().equals(Material.YELLOW_CONCRETE)) {
				Honeycomb.VerifyPlot(event);
			}
		}

		if (GameManager.getGame() == Game.TUGOFWAR && Main.frontman.equals(player)) {
			if (item.getType().equals(Material.GREEN_CONCRETE)) {
				TugofWar.StartGame();
			}
		}

		if (GameManager.getGame() == Game.GLASSSTEPPING && Main.frontman.equals(player)) {
			if (item.getType().equals(Material.GREEN_CONCRETE)) {
				GlassStepping.StartGame();
			}
		}

		if (GameManager.getGame() == Game.SQUIDGAME && Main.frontman.equals(player)) {
			if (item.getType().equals(Material.GREEN_CONCRETE)) {
				SquidGame.StartGame();
			}
			if (item.getType().equals(Material.YELLOW_CONCRETE)) {
				SquidGame.addSword();
			}
		}

		if (item.getType().equals(Material.IRON_HORSE_ARMOR) || item.getType().equals(Material.NETHERITE_HOE)) {
			Elimination.PlayShotforEveryone(player.getLocation());
		}
	}

	@EventHandler
	public void BlockBreak(BlockBreakEvent event) {
		if (GameManager.getGame() == Game.HONEYCOME) {
			Honeycomb.CheckBrokenBlock(event);
			return;
		}

		event.setCancelled(true);
	}

	@EventHandler
	public void BlockPlace(BlockPlaceEvent event) {
		event.setCancelled(true);
	}

	@EventHandler
	public void onLeave(PlayerQuitEvent event) {
		Player player = event.getPlayer();

		if (Main.participants.contains(player)) {
			Main.participants.remove(player);
		} else if (Main.frontman == player) {
			Main.frontman = null;
		} else if (Main.guards.contains(player)) {
			Main.guards.remove(player);
		}

		if (GameManager.getGame() != Game.LOBBY) {
			Bukkit.broadcastMessage("§e" + player.getName() + " §cwas Disqualified!");
			Main.async.execute(() -> {
				Elimination.PlayShotforEveryone();
			});
		}
	}

	@EventHandler
	public void onDeath(PlayerDeathEvent event) {
		if (GameManager.getGame() == Game.GLASSSTEPPING) {
			GlassStepping.onPlayerDeath(event);
		}
	}

	@EventHandler
	public void Combut(EntityCombustEvent event) {
		event.setCancelled(true);
	}

	@EventHandler
	public void onPlayerMove(PlayerMoveEvent event) {
		if (GameManager.getGame() == Game.REDLIGHTGREENLIGHT) {
			RedLightGreenLight.PlayerMoves(event);
		}
		if (GameManager.getGame() == Game.GLASSSTEPPING) {
			GlassStepping.PlayerMove(event);
		}
	}

	public void onPlayerDeath(PlayerDeathEvent event) {
		if (GameManager.getGame().equals(Game.TUGOFWAR)) {
			TugofWar.onDeath(event);
			return;
		}
	}

	public void onEntityDamageByEntity(EntityDamageByEntityEvent event) {
		if (GameManager.getGame().equals(Game.TUGOFWAR)) {
			TugofWar.PvPAction(event);
			return;
		}
		event.setCancelled(true);
	}

}
