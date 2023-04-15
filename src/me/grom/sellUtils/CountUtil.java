package me.grom.sellUtils;

import org.bukkit.Material;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

public class CountUtil {
	
	public static int CountMaterial(Inventory inv, Material mat) {
		int res = 0;
		for (int i  = 0; i < inv.getSize(); i++) {
			ItemStack is = inv.getItem(i);
			if (is != null) {
				if (is.getType() == mat) {
					res = res + is.getAmount();
				}
			}
		}
		return res;
	}
	
	public static double round(double value, int places) {
		if (places < 0) throw new IllegalArgumentException();
		
		long factor = (long) Math.pow(10,  places);
		value = value * factor;
		long tmp = Math.round(value);
		return (double) tmp / factor;
		}
	
}
