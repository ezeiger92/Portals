package com.chromaclypse.portals;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityPortalEvent;
import org.bukkit.event.player.PlayerPortalEvent;
import org.bukkit.event.player.PlayerTeleportEvent.TeleportCause;

public class TravelPlanner implements Listener {
	private final PortalsConfig config;

	public TravelPlanner(PortalsConfig config) {
		this.config = config;
	}
	
	@EventHandler
	public void onEntityUsePortal(EntityPortalEvent event) {
		event.setCancelled(true);
	}
	
	@EventHandler
	public void onPlayerUsePortal(PlayerPortalEvent event) {
		Location to = getDestination(event.getPlayer(), event.getFrom(),
				event.getTo(), event.getCause());
		if(to != null)
			event.setTo(to);
	}
	
	private Location getDestination(Entity entity, Location from, Location to, TeleportCause type) {
		if(type != TeleportCause.END_PORTAL && type != TeleportCause.NETHER_PORTAL)
			return to;
		
		PortalsConfig.WorldEntry entryFrom = config.worlds.getOrDefault(from.getWorld().getName(),
				new PortalsConfig.WorldEntry());
		World worldTo = Bukkit.getWorld(type == TeleportCause.END_PORTAL ? entryFrom.end : entryFrom.nether);
		if(entryFrom == null || worldTo == null)
			return null;

		double scale = config.worlds.get(worldTo.getName()).size / entryFrom.size;
		if(type == TeleportCause.NETHER_PORTAL)
			to = new Location(worldTo, from.getX() * scale, from.getY(), from.getZ() * scale);
		else if(worldTo.getEnvironment() == World.Environment.THE_END)
			to = new Location(worldTo, 100, 50, 0, 90, 0);
		else {
			Location spawn = entity instanceof Player ? ((Player)entity).getBedSpawnLocation() : null;
			to = spawn == null ? worldTo.getSpawnLocation() : spawn;
		}
		return to;
	}
}
