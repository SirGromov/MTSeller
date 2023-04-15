package me.grom.mtsellerMain;


import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import me.grom.sellUtils.CountUtil;
import me.grom.sellUtils.ItemUtil;


public class ListenerGUISell implements Listener{
	
	public static SimpleDateFormat format = new SimpleDateFormat("HH:mm:ss");
	
	private static ItemStack filler = ItemUtil.create(Material.getMaterial(MTSellerMain.instance.getConfig().getString("seller_settings.filler_material").toUpperCase()), 1, "   ", null);
	private static ItemStack closer = ItemUtil.create(Material.getMaterial(MTSellerMain.instance.getConfig().getString("seller_settings.close_material").toUpperCase()), 1, MTSellerMain.instance.getConfig().getString("seller_settings.close_name"), null);
	private static ItemStack updater;
	
	public static void openGui(Player p) {
		Inventory inv = Bukkit.createInventory(null, MTSellerMain.instance.getConfig().getInt("seller_settings.gui_size"), MTSellerMain.instance.getConfig().getString("seller_settings.title"));
		GUIConstructor(inv, p);
		p.openInventory(inv);
	}
	
	public static void GUIConstructor(Inventory inv, Player p) {
		int period = MTSellerMain.instance.getConfig().getInt("seller_settings.update_period");
		Long timeRemaings = (period * 1000) - (new Date().getTime() - MTSellerMain.now.getTime());
		int rhrs = (int) CountUtil.round(timeRemaings/1000/60/60, 0);
		int rmins = (int) CountUtil.round(timeRemaings/1000/60, 0) - (int) CountUtil.round(timeRemaings/1000/60/60, 0) * 60;
		int rsecs = (int) CountUtil.round(timeRemaings/1000, 0) - (int) CountUtil.round(timeRemaings/1000/60, 0) * 60;
		String timeRemaing = Integer.toString(rhrs) + ":" + Integer.toString(rmins) + ":" + Integer.toString(rsecs);
		Date rDate = new Date(MTSellerMain.now.getTime() + (period * 1000));
		List<String> updateLore = Arrays.asList(String.format(MTSellerMain.instance.getConfig().getString("seller_settings.update_lore"), timeRemaing, format.format(rDate)).split("\n"));
		updater = ItemUtil.create(Material.getMaterial(MTSellerMain.instance.getConfig().getString("seller_settings.update_material").toUpperCase()), 1, MTSellerMain.instance.getConfig().getString("seller_settings.update_name"), updateLore);
		ArrayList<Material> val = new ArrayList<>(MTSellerMain.sellerList.keySet());
		int j = 0;
		int i = 0;
		for (Character ch : MTSellerMain.instance.getConfig().getString("seller_settings.gui_schema").toCharArray()) {
			if (ch.toString().equals("E")) {
				Double price = MTSellerMain.sellerList.get(val.get(j));
				Double stack_price = price * 64;
				Integer num = CountUtil.CountMaterial(p.getInventory(), val.get(j));
				String cat = MTSellerMain.sellerCats.get(val.get(j));
				String rcat = MTSellerMain.sellerRawCats.get(val.get(j));
				Double num_price = CountUtil.round(price * num, 2);
				int catLimit = MTSellerMain.instance.getConfig().getInt("categories."+rcat+".limit");
				ItemStack item = new ItemStack(val.get(j), 1);
				ItemMeta itemMeta = item.getItemMeta();
				List<String> lore = Arrays.asList(String.format(MTSellerMain.instance.getConfig().getString("seller_settings.entry_lore"), 
						cat, 
						price.toString(),
						stack_price.toString(),
						num.toString(),
						num_price.toString(),
						MTSellerMain.playersData.get(p.getName()).get(rcat).toString(),
						Integer.toString(catLimit)).split("\n"));
				itemMeta.setLore(lore);
				item.setItemMeta(itemMeta);
				inv.setItem(i, item);
				j++;
			}
			else if (ch.toString().equals("F")) {
				inv.setItem(i, filler);
			}
			else if (ch.toString().equals("U")) {
				inv.setItem(i, updater);
			}
			else if (ch.toString().equals("C")) {
				inv.setItem(i, closer);
			}
		i++;
		}
	}
	
	@EventHandler
	public void onInventoryClick(InventoryClickEvent e) {
		Player p = (Player) e.getWhoClicked();
		ItemStack eventItem = e.getCurrentItem();
		if (e.getView().getTitle().equalsIgnoreCase(MTSellerMain.instance.getConfig().getString("seller_settings.title"))) {
			e.setCancelled(true);
			if (eventItem.isSimilar(closer)) {
				p.closeInventory();
			}
			else if (!(eventItem.isSimilar(filler) || eventItem.isSimilar(updater))){
				if (p.getInventory().contains(eventItem.getType())) {
					int num = 1;
					Double price = MTSellerMain.sellerList.get(eventItem.getType());
					ClickType click = e.getClick();
					if (click.name() == "MIDDLE") {
						num = CountUtil.CountMaterial(p.getInventory(), eventItem.getType());}
					else if ((click.name() == "RIGHT" || click.name() == "SHIFT_RIGHT") && p.getInventory().contains(new ItemStack(eventItem.getType(), 64))) {
						num = 64;
					}
					sellNum(p, num, eventItem.getType(), price, MTSellerMain.sellerCats.get(eventItem.getType()), e, MTSellerMain.sellerRawCats.get(eventItem.getType()));
				}
				else {
					p.sendMessage(MTSellerMain.instance.getConfig().getString("seller_settings.lack_items_message"));
				}
			}
		}
	}
	
	public void sellNum(Player p, int num, Material mat, Double price, String cat, InventoryClickEvent e, String rcat) {
		int catLimit = MTSellerMain.instance.getConfig().getInt("categories."+rcat+".limit");
		int pLimit = MTSellerMain.playersData.get(p.getName()).get(rcat);
		if (pLimit < catLimit) {
			Double stack_price = price * 64;
			int num1 = CountUtil.CountMaterial(p.getInventory(), mat);
			Double num_price = CountUtil.round(price * num1, 2);
			for (int i = 0; i < num; i++) {
				pLimit = MTSellerMain.playersData.get(p.getName()).get(rcat);
				if (pLimit < catLimit) {
					p.getInventory().removeItem(new ItemStack(mat, 1));
					MTSellerMain.playersData.get(p.getName()).put(rcat, pLimit+1);
				}
			}
			ItemStack newItem = new ItemStack(mat);
			ItemMeta newMeta = newItem.getItemMeta();
			List<String> newLore = Arrays.asList(String.format(MTSellerMain.instance.getConfig().getString("seller_settings.entry_lore"), 
					cat, 
					price.toString(),
					stack_price.toString(),
					Integer.toString(num),
					Double.toString(num_price),
					MTSellerMain.playersData.get(p.getName()).get(rcat).toString(),
					Integer.toString(catLimit)).split("\n"));
			newMeta.setLore(newLore);
			newItem.setItemMeta(newMeta);
			String cmd = String.format(MTSellerMain.instance.getConfig().getString("seller_settings.eco_command"), p.getName(), Double.toString(price));
			Bukkit.getServer().dispatchCommand(Bukkit.getConsoleSender(), cmd);
			p.sendMessage(String.format(MTSellerMain.instance.getConfig().getString("seller_settings.sell_items_message"), mat.name(), Integer.toString(num), Double.toString(num_price)));
			e.getInventory().setItem(e.getSlot(), newItem);
		}
		else {
			p.sendMessage(MTSellerMain.instance.getConfig().getString("seller_settings.limit_message"));
		}
	}
	
}
