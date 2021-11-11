package com.overloup.squidgame.commands;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.overloup.squidgame.Main;
import com.overloup.squidgame.game.TugofWar;

public class TugofWarsSetting implements CommandExecutor {

	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String arg, String[] args) {
		Player player = (Player) sender;

		if (args.length == 2 && player.equals(Main.frontman)) {
			int minplayers = Integer.parseInt(args[1]);
			TugofWar.miniumplayers = minplayers;
			player.sendMessage("§aSet the Minium Players for TugofWar to §e" + minplayers);
		} else {
			player.sendMessage("§cPlease use §e/thugofwar minplayers <number>");
		}
		return true;
	}

}
