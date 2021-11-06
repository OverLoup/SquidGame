package com.overloup.squidgame.utilities;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.LivingEntity;

public class Doll {

	private static World world = Bukkit.getWorld("world");
	private static Entity doll;

	public static void spawn() {
		Location loc = new Location(world, 111, 29, 19);
		loc.setYaw(90);
		doll = world.spawnEntity(loc, EntityType.ZOMBIE);

		doll.setInvulnerable(true);
		doll.setSilent(true);
		((LivingEntity) doll).setAI(false);
		doll.setFireTicks(0);
		((LivingEntity) doll).setRemoveWhenFarAway(false);
	}

	public static void turn() {
		Location loc = doll.getLocation();
		loc.setYaw(loc.getYaw() + 180f);
		doll.teleport(loc);
	}

	public static void destroy() {
		if (doll == null)
			return;
		doll.remove();
	}

}
