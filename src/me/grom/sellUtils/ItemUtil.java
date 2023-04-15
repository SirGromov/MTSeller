package me.grom.sellUtils;

import java.util.List;

import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

public class ItemUtil {
	public static ItemStack create(Material material, int amount, String name, List<String> lore) {
		ItemStack item = new ItemStack(material, amount);
		ItemMeta meta = item.getItemMeta();
		if (name != null) {
			meta.setDisplayName(name);
		}
		if (lore != null) {
			meta.setLore(lore);
		}
		item.setItemMeta(meta);
		return item;
	}
}

