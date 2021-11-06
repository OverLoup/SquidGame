package com.overloup.squidgame;

import java.util.ArrayList;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;

import com.overloup.squidgame.commands.ResourcePack;
import com.overloup.squidgame.commands.StartSquid;
import com.overloup.squidgame.data.GameManager;
import com.overloup.squidgame.data.GameManager.Game;
import com.overloup.squidgame.listener.PlayerListener;
import com.overloup.squidgame.utilities.NPC;

public class Main extends JavaPlugin {

	public static Plugin plugin;
	public static ExecutorService async = Executors.newFixedThreadPool(Integer.MAX_VALUE);

	public static Player frontman;
	public static ArrayList<Player> guards = new ArrayList<>();
	public static ArrayList<Player> participants = new ArrayList<>();
	public static World world = Bukkit.getWorld("world");
	public static Location spawn = new Location(Main.world, -37, 31, 31);

	@Override
	public void onEnable() {
		plugin = this;

		NPC.createNPC(new Location(Main.world, -36.5, 31, 29.5, 87f, -5f));
		NPC.createNPC(new Location(Main.world, -36.5, 31, 32.5, 87f, -5f));

		GameManager.setGame(Game.LOBBY);
		this.getCommand("startsquid").setExecutor(new StartSquid());
		this.getCommand("resourcepack").setExecutor(new ResourcePack());
		Bukkit.getPluginManager().registerEvents(new PlayerListener(), this);
	}

	@Override
	public void onDisable() {
		
	}
}
