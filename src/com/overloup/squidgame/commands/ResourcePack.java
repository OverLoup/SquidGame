package com.overloup.squidgame.commands;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class ResourcePack implements CommandExecutor {

	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String arg, String[] args) {
		Player player = (Player) sender;

		player.sendMessage("§aLoading Texturepack...");
		player.sendMessage("§7If nothing happens, please rejoin the Server!");
		player.setResourcePack("https://beatingkids.club/images/squidpack.zip");
		return true;
	}

}
