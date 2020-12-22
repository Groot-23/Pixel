package me.groot_23.pixel.listener;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockCanBuildEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityPickupItemEvent;
import org.bukkit.event.entity.EntityPotionEffectEvent;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerPickupArrowEvent;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.util.BoundingBox;

import me.groot_23.pixel.Pixel;

public class SpectatorListener implements Listener {

	@EventHandler
	public void onJoin(PlayerJoinEvent event) {
		for(Player other : Bukkit.getOnlinePlayers()) {
			if(Pixel.isSpectator(other)) {
				event.getPlayer().hidePlayer(Pixel.getPlugin(), other);
			}
		}
	}
	
	@EventHandler(priority=EventPriority.HIGHEST)
	public void onDamage(EntityDamageByEntityEvent event) {
		if(event.getDamager() instanceof Player) {
			Player player = (Player)event.getDamager();
			if(Pixel.isSpectator(player)) {
				event.setCancelled(true);
			}
		}
	}
	
	@EventHandler(priority=EventPriority.HIGHEST)
	public void onBlockBreak(BlockBreakEvent event) {
		if(Pixel.isSpectator(event.getPlayer())) {
			event.setCancelled(true);
		}
	}
	
	@EventHandler(priority=EventPriority.HIGHEST)
	public void onBlockPlace(BlockPlaceEvent event) {
		if(Pixel.isSpectator(event.getPlayer())) {
			event.setCancelled(true);
		}
	}
	
	@EventHandler(priority=EventPriority.HIGHEST)
	public void onItemPickup(EntityPickupItemEvent event) {
		if(event.getEntity() instanceof Player) {
			if(Pixel.isSpectator((Player)event.getEntity())) {
				event.setCancelled(true);
			}
		}
	}
	
	@EventHandler(priority=EventPriority.HIGHEST)
	public void onArrowPickUp(PlayerPickupArrowEvent event) {
		if(Pixel.isSpectator(event.getPlayer())) {
			// deprecated because it extends the deprecated PlayerPickUpItemEvent. There seems to be no alternative
			event.setCancelled(true);
		}
	}
	
	@EventHandler(priority=EventPriority.HIGHEST)
	public void onInteract(PlayerInteractEvent event) {
		if(Pixel.isSpectator(event.getPlayer())) {
			event.setCancelled(true);
		}
	}
	@EventHandler(priority=EventPriority.HIGHEST)
	public void onInteractEntity(PlayerInteractEntityEvent event) {
		if(Pixel.isSpectator(event.getPlayer())) {
			event.setCancelled(true);
		}
	}
	
	@EventHandler(priority=EventPriority.HIGHEST)
	public void onEffect(EntityPotionEffectEvent event) {
		if(event.getEntity() instanceof Player) {
			Player p = (Player) event.getEntity();
			if(Pixel.isSpectator(p) && !event.getModifiedType().equals(PotionEffectType.INVISIBILITY)) {
				event.setCancelled(true);
			}
		}
	}
	
	@EventHandler(priority=EventPriority.HIGHEST)
	public void onBlockCanBuild(BlockCanBuildEvent event) {
		if(!event.isBuildable()) {
			Location l = event.getBlock().getLocation();
			boolean allow = false;
			for(Player p : l.getWorld().getPlayers()) {
				BoundingBox bb = p.getBoundingBox();
				if(bb.overlaps(new BoundingBox(l.getBlockX(), l.getBlockY(), l.getBlockZ(), l.getBlockX() + 1, l.getBlockY() + 1,
				l.getBlockZ() + 1)))
				{
					if(Pixel.isSpectator(p)) {
						allow = true;
					}
					else {
						allow = false;
						break;
					}
				}
			}
			event.setBuildable(allow);
		}
	}
	
}
