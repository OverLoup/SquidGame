package com.overloup.squidgame.utilities;


import org.bukkit.Color;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.LeatherArmorMeta;

public class CustomItem {

	public static ItemStack item(Material mat, String name, int count) {
		ItemStack item = new ItemStack(mat, count);
		ItemMeta meta = item.getItemMeta();
		meta.setDisplayName(name);
		item.setItemMeta(meta);
		return item;
	}

	public static ItemStack unbreakableItem(Material mat, String name, int count) {
		ItemStack item = new ItemStack(mat, count);
		ItemMeta meta = item.getItemMeta();
		meta.setDisplayName(name);
		meta.addItemFlags(ItemFlag.HIDE_UNBREAKABLE);
		meta.setUnbreakable(true);
		item.setItemMeta(meta);
		return item;
	}

	public static ItemStack coloredArmor(Material mat, Color color) {
		ItemStack item = new ItemStack(mat);
		LeatherArmorMeta meta = (LeatherArmorMeta) item.getItemMeta();
		meta.setColor(null);
		meta.addEnchant(Enchantment.BINDING_CURSE, 1, false);
		item.setItemMeta(meta);
		return item;
	}
}
