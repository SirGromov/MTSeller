package me.grom.mtsellerMain;

import org.bukkit.scheduler.BukkitRunnable;

public class SellerRunnable extends BukkitRunnable {
	
	@Override
	public void run() {
		MTSellerMain.reloadSeller();
	}

}
