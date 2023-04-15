package me.grom.mtsellerMain;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class CommandSell implements CommandExecutor  {

	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String alias, String[] args) {
		if (cmd.getName().equalsIgnoreCase("sell")) {
			Player p = (Player) sender;
			ListenerGUISell.openGui(p);
			return true;
		}
		return false;
	}

}
