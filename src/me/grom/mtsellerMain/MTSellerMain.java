package me.grom.mtsellerMain;

import java.io.File;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;
import java.util.logging.Logger;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;

import me.grom.sellUtils.RandomUtil;


public class MTSellerMain extends JavaPlugin implements Listener{
	
	static HashMap<Material, Double> sellerList = new HashMap<>();
	static HashMap<Material, String> sellerCats = new HashMap<>();
	static HashMap<Material, String> sellerRawCats = new HashMap<>();
	static HashMap<String, HashMap<String, Integer>> playersData = new HashMap<String, HashMap<String, Integer>>();
	Logger logger = getLogger();
	private BukkitRunnable runnable;
	public static Date now = new Date();
	
	static MTSellerMain instance;
	
	public void onEnable() {
		logger.info("MTSeller Started!");
        getServer().getPluginManager().registerEvents(this, (Plugin)this);
        
        this.getCommand("sell").setExecutor(new CommandSell());
        
        instance = this;
        
		Bukkit.getPluginManager().registerEvents(new ListenerGUISell(), this);
		
		File config = new File(getDataFolder() + File.separator + "config.yml");
        if(!config.exists()){
            getConfig().options().copyDefaults(true);
            saveDefaultConfig();
        }
        assembleSeller();
        runnable = new SellerRunnable();
        runnable.runTaskTimer(this, 1L, getConfig().getInt("seller_settings.update_period") * 20L);
	}
	
	public void onDisable() {
		logger.info("MTSeller Stopped!");
	}
	
	@EventHandler
	public void onPlayerJoin(PlayerJoinEvent e) {
		if (!playersData.containsKey(e.getPlayer().getName())) {
			playersData.put(e.getPlayer().getName(), new HashMap<String, Integer>());
			for (String cat: sellerRawCats.values()) {
				playersData.get(e.getPlayer().getName()).put(cat, 0);
			}
		}
	}
	
	public static void reloadSeller() {
		for (Player player : Bukkit.getOnlinePlayers()) {
			if (player.getOpenInventory().getTitle().equalsIgnoreCase(instance.getConfig().getString("seller_settings.title"))) {
				player.closeInventory();
			}
		}
		assembleSeller();
		now = new Date();
		Bukkit.broadcastMessage(instance.getConfig().getString("seller_settings.update_sell_message"));
	}
	
	
	public static void assembleSeller() {
		sellerList.clear();
		sellerCats.clear();
		playersData.clear();
		
		for (String key : instance.getConfig().getConfigurationSection("categories").getKeys(true)) {
			if (!key.contains(".")) {
				Set<String> entries = new HashSet<String>();
				for (String key1 : instance.getConfig().getConfigurationSection("categories."+key+".entries").getKeys(true)) {
					if (!key1.contains(".")) {
						entries.add(key1);
					}
				}
				for (int i=0; i<instance.getConfig().getInt("categories."+key+".slots"); i++) {
					int rnd = RandomUtil.RandInt(0, entries.size()-1);
					String entry = (String) entries.toArray()[rnd];
					sellerRawCats.put(Material.getMaterial(entry.toUpperCase()), key);
					sellerCats.put(Material.getMaterial(entry.toUpperCase()), instance.getConfig().getString("categories."+key+".name"));
					entries.remove(entry);
					Double start = instance.getConfig().getDouble("categories."+key+".entries."+entry+ ".price_start");
					Double end = instance.getConfig().getDouble("categories."+key+".entries."+entry+ ".price_end");
					int rnds = RandomUtil.RandInt((int) (start * 100), (int) (end * 100));
					sellerList.put(Material.getMaterial(entry.toUpperCase()), rnds / 100.00);
				}
				for (Player player : Bukkit.getOnlinePlayers()) {
					playersData.put(player.getName(), new HashMap<String, Integer>());
					for (String cat: sellerRawCats.values()) {
						playersData.get(player.getName()).put(cat, 0);
					}
				}
			}
		}
	}
}
