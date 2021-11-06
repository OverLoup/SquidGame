package com.overloup.squidgame.utilities;

import java.util.ArrayList;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.craftbukkit.v1_16_R3.CraftServer;
import org.bukkit.craftbukkit.v1_16_R3.CraftWorld;
import org.bukkit.craftbukkit.v1_16_R3.entity.CraftPlayer;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import com.mojang.authlib.GameProfile;
import com.overloup.squidgame.Main;

import net.minecraft.server.v1_16_R3.EntityPlayer;
import net.minecraft.server.v1_16_R3.MinecraftServer;
import net.minecraft.server.v1_16_R3.PacketPlayOutEntityHeadRotation;
import net.minecraft.server.v1_16_R3.PacketPlayOutNamedEntitySpawn;
import net.minecraft.server.v1_16_R3.PacketPlayOutPlayerInfo;
import net.minecraft.server.v1_16_R3.PlayerConnection;
import net.minecraft.server.v1_16_R3.PlayerInteractManager;
import net.minecraft.server.v1_16_R3.WorldServer;

public class NPC {

	private static ArrayList<EntityPlayer> npcs = new ArrayList<>();

	public static void createNPC(Location loc) {
		MinecraftServer server = ((CraftServer) Bukkit.getServer()).getServer();
		WorldServer world = ((CraftWorld) Bukkit.getWorld("world")).getHandle();
		GameProfile profile = new GameProfile(UUID.randomUUID(), "");

		EntityPlayer npc = new EntityPlayer(server, world, profile, new PlayerInteractManager(world));

		npc.setLocation(loc.getX(), loc.getY(), loc.getZ(), loc.getYaw(), loc.getPitch());

		profile.getProperties().put("textures", ChangeSkin.Guard());

		AddPacket(npc);
		npcs.add(npc);
	}

	public static void AddPacket(EntityPlayer npc) {
		for (Player p : Bukkit.getOnlinePlayers()) {
			PlayerConnection conn = ((CraftPlayer) p).getHandle().playerConnection;
			conn.sendPacket(new PacketPlayOutPlayerInfo(PacketPlayOutPlayerInfo.EnumPlayerInfoAction.ADD_PLAYER, npc));
			conn.sendPacket(new PacketPlayOutNamedEntitySpawn(npc));
			conn.sendPacket(new PacketPlayOutEntityHeadRotation(npc, (byte) (npc.yaw * 256 / 360)));

			new BukkitRunnable() {
				@Override
				public void run() {
					conn.sendPacket(new PacketPlayOutPlayerInfo(
							PacketPlayOutPlayerInfo.EnumPlayerInfoAction.REMOVE_PLAYER, npc));
				}
			}.runTaskLater(Main.plugin, 5);
		}
	}

	public static void onJoin(Player p) {
		for (EntityPlayer npc : npcs) {
			PlayerConnection conn = ((CraftPlayer) p).getHandle().playerConnection;
			conn.sendPacket(new PacketPlayOutPlayerInfo(PacketPlayOutPlayerInfo.EnumPlayerInfoAction.ADD_PLAYER, npc));
			conn.sendPacket(new PacketPlayOutNamedEntitySpawn(npc));
			conn.sendPacket(new PacketPlayOutEntityHeadRotation(npc, (byte) (npc.yaw * 256 / 360)));

			new BukkitRunnable() {
				@Override
				public void run() {
					conn.sendPacket(new PacketPlayOutPlayerInfo(
							PacketPlayOutPlayerInfo.EnumPlayerInfoAction.REMOVE_PLAYER, npc));
				}
			}.runTaskLater(Main.plugin, 5);
		}
	}

}
