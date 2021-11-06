package com.overloup.squidgame.data;

import org.bukkit.Location;
import org.bukkit.entity.Player;

public class HoneyCombs {

	public Player owner;
	public Location loc;
	public Location spawn;
	public int yawn;

	public HoneyCombs(Player owner, Location loc, Location spawn, int yawn) {
		this.owner = owner;
		this.loc = loc;
		this.spawn = spawn;
		this.yawn = yawn;
	}

}
