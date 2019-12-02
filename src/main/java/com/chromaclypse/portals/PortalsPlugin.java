package com.chromaclypse.portals;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.event.HandlerList;
import org.bukkit.plugin.java.JavaPlugin;

public class PortalsPlugin extends JavaPlugin {
	private final PortalsConfig config = new PortalsConfig();
	
	@Override
	public void onEnable() {
		config.init(this);
		getCommand("portals").setExecutor(this);
		getServer().getPluginManager().registerEvents(new TravelPlanner(config), this);
	}
	
	@Override
	public void onDisable() {
		HandlerList.unregisterAll(this);
	}
	
	@Override
	public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
		if(args.length >= 1 && args[0].equalsIgnoreCase("reload")) {
			config.init(this);
			sender.sendMessage(ChatColor.GREEN + "Reloaded config");
		}
		else
			sender.sendMessage(ChatColor.RED + "Usage: /"+label+" reload");
		return true;
	}
}
