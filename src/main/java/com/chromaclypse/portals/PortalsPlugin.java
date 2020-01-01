package com.chromaclypse.portals;

import org.bukkit.ChatColor;
import org.bukkit.command.TabExecutor;
import org.bukkit.event.HandlerList;
import org.bukkit.plugin.java.JavaPlugin;

import com.chromaclypse.api.command.CommandBase;
import com.chromaclypse.api.command.Context;

public class PortalsPlugin extends JavaPlugin {
	private final PortalsConfig config = new PortalsConfig();
	
	@Override
	public void onEnable() {
		config.init(this);

		TabExecutor ch = new CommandBase()
				.calls(this::helpCommand)
				.with().arg("reload").calls(this::reloadCommand)
				.with().arg("version").calls(CommandBase::pluginVersion)
				.getCommand();
		
		getCommand("portals").setExecutor(ch);
		getCommand("portals").setTabCompleter(ch);
		getServer().getPluginManager().registerEvents(new TravelPlanner(config), this);
	}
	
	@Override
	public void onDisable() {
		HandlerList.unregisterAll(this);
	}
	
	private boolean helpCommand(Context context) {
		context.Sender().sendMessage(ChatColor.RED + "Usage: /"+context.Alias()+" reload");
		return true;
	}
	
	private boolean reloadCommand(Context context) {
		config.init(this);
		context.Sender().sendMessage(ChatColor.GREEN + "Reloaded config");
		return true;
	}
}
