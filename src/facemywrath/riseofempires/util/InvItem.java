package facemywrath.riseofempires.util;

import java.util.List;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

public class InvItem {

	public static ItemStack invItem(Material mat, String displayname, int amount, int data, List<String> lore)
	{
		ItemStack item = new ItemStack(mat, amount, (byte) data);
		ItemMeta meta = item.getItemMeta();
		meta.setDisplayName(ChatColor.translateAlternateColorCodes('&', displayname));
		meta.setLore(lore);
		item.setItemMeta(meta);
		return item;
	}
	
	public static ItemStack invItem(Material mat, int amount, int data, List<String> lore)
	{
		ItemStack item = new ItemStack(mat, amount, (byte) data);
		ItemMeta meta = item.getItemMeta();
		meta.setLore(lore);
		item.setItemMeta(meta);
		return item;
	}
	
	public static ItemStack invItem(Material mat, String displayname, int amount, List<String> lore)
	{
		ItemStack item = new ItemStack(mat, amount);
		ItemMeta meta = item.getItemMeta();
		meta.setDisplayName(ChatColor.translateAlternateColorCodes('&', displayname));
		meta.setLore(lore);
		item.setItemMeta(meta);
		return item;
	}
	
	public static ItemStack invItem(Material mat, String displayname, int amount)
	{
		ItemStack item = new ItemStack(mat, amount);
		ItemMeta meta = item.getItemMeta();
		meta.setDisplayName(ChatColor.translateAlternateColorCodes('&', displayname));
		item.setItemMeta(meta);
		return item;
	}

	public static ItemStack invItem(Material mat, String displayname, int amount, int data)
	{
		ItemStack item = new ItemStack(mat, amount, (byte) data);
		ItemMeta meta = item.getItemMeta();
		meta.setDisplayName(ChatColor.translateAlternateColorCodes('&', displayname));
		item.setItemMeta(meta);
		return item;
	}

	public static ItemStack invItem(Material mat, int amount, int data)
	{
		ItemStack item = new ItemStack(mat, amount, (byte) data);
		return item;
	}

	public static ItemStack invItem(Material mat, String displayname, List<String> lore)
	{
		ItemStack item = new ItemStack(mat, 1);
		ItemMeta meta = item.getItemMeta();
		meta.setDisplayName(ChatColor.translateAlternateColorCodes('&', displayname));
		meta.setLore(lore);
		item.setItemMeta(meta);
		return item;
	}

	public static ItemStack invItem(Material mat, String displayname)
	{
		ItemStack item = new ItemStack(mat, 1);
		ItemMeta meta = item.getItemMeta();
		meta.setDisplayName(ChatColor.translateAlternateColorCodes('&', displayname));
		item.setItemMeta(meta);
		return item;
	}
}
