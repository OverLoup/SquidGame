package com.overloup.squidgame.commands;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.overloup.squidgame.Main;
import com.overloup.squidgame.data.GameManager;
import com.overloup.squidgame.game.GlassStepping;
import com.overloup.squidgame.game.Honeycomb;
import com.overloup.squidgame.game.RedLightGreenLight;
import com.overloup.squidgame.game.SquidGame;
import com.overloup.squidgame.game.TugofWar;

public class StartSquid implements CommandExecutor {

	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String arg, String[] args) {
		Player player = (Player) sender;
		if (Main.frontman != player) {
			player.sendMessage("§cOnly the §eFrontMan §ccan Start the Game!");
			return true;
		}

		switch (GameManager.getGame()) {
		case LOBBY:
			player.sendMessage("§aThe game will now Start... §cLocking Server!");
			RedLightGreenLight.Setup();
			break;
		case REDLIGHTGREENLIGHT:
			Honeycomb.Setup();
			break;
		case HONEYCOME:
			TugofWar.Setup();
			break;
		case TUGOFWAR:
			GlassStepping.Setup();
			break;
		case GLASSSTEPPING:
			SquidGame.Setup();
			break;

		default:
			player.sendMessage("bruh theres no more games");
			break;
		}

		return true;
	}

}
