package com.chromaclypse.portals;

import java.util.HashMap;

import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.TravelAgent;
import org.bukkit.World;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityPortalEnterEvent;
import org.bukkit.event.entity.EntityPortalEvent;
import org.bukkit.event.player.PlayerPortalEvent;
import org.bukkit.event.player.PlayerTeleportEvent.TeleportCause;
import org.bukkit.plugin.java.JavaPlugin;

public class PortalsPlugin extends JavaPlugin implements Listener {
	public PortalsConfig config = new PortalsConfig();
	public HashMap<Integer, TeleportCause> entityTransit = new HashMap<>();
	
	@Override
	public void onEnable() {
		config.init(this);
		getCommand("portals").setExecutor(this);
		getServer().getPluginManager().registerEvents(this, this);
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
	
	@EventHandler
	public void onEntityPortalEnter(EntityPortalEnterEvent event) {
		Material type = event.getLocation().getBlock().getType();
		if(type == Material.END_PORTAL) entityTransit.put(event.getEntity().getEntityId(), TeleportCause.END_PORTAL);
		else if(type == Material.NETHER_PORTAL)  entityTransit.put(event.getEntity().getEntityId(), TeleportCause.NETHER_PORTAL);
	}
	
	@EventHandler(ignoreCancelled=true)
	public void onEntityPortal(EntityPortalEvent event) {
		TeleportCause cause = entityTransit.remove(event.getEntity().getEntityId());
		Location to = getDestination(event.getEntity(), event.getFrom(),
				event.getTo(), event.getPortalTravelAgent(), cause);
		if(to != null)
			event.setTo(to);
	}
	
	@EventHandler(ignoreCancelled=true)
	public void onPlayerPortal(PlayerPortalEvent event) {
		Location to = getDestination(event.getPlayer(), event.getFrom(),
				event.getTo(), event.getPortalTravelAgent(), event.getCause());
		if(to != null)
			event.setTo(to);
	}
	
	public Location getDestination(Entity entity, Location from, Location to, TravelAgent agent, TeleportCause type) {
		if(type != TeleportCause.END_PORTAL && type != TeleportCause.NETHER_PORTAL)
			return to;
		
		PortalsConfig.WorldEntry entryFrom = config.worlds.getOrDefault(from.getWorld().getName(),
				new PortalsConfig.WorldEntry());
		World worldTo = getServer().getWorld(type == TeleportCause.END_PORTAL ? entryFrom.end : entryFrom.nether);
		if(entryFrom == null || worldTo == null)
			return null;

		double scale = config.worlds.get(worldTo.getName()).size / entryFrom.size;
		if(type == TeleportCause.NETHER_PORTAL)
			to = agent.findOrCreate(new Location(worldTo, from.getX() * scale, from.getY(), from.getZ() * scale));
		else if(worldTo.getEnvironment() == World.Environment.THE_END)
			to = agent.findOrCreate(new Location(worldTo, 100, 50, 0, 90, 0));
		else {
			Location spawn = entity instanceof Player ? ((Player)entity).getBedSpawnLocation() : null;
			to = spawn == null ? worldTo.getSpawnLocation() : spawn;
		}
		return to;
	}
}
