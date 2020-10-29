package me.groot_23.pixel.listener;

import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.entity.Projectile;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerChangedWorldEvent;
import org.bukkit.event.player.PlayerInteractAtEntityEvent;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.player.PlayerRespawnEvent;
import org.bukkit.projectiles.ProjectileSource;

import me.groot_23.pixel.Pixel;
import me.groot_23.pixel.game.Game;
import me.groot_23.pixel.player.PlayerUtil;
import me.groot_23.pixel.world.Arena;

public class GameListener implements Listener{

	
	public Game getGame(World world) {
		Arena arena = Pixel.getArena(world.getUID());
		if(arena != null) {
			return arena.getGame();
		}
		return null;
	}
	
	@EventHandler
	public void onDeath(PlayerDeathEvent event) {
		 Game game = getGame(event.getEntity().getWorld());
		 if(game != null) {
			 game.onDeath(event);
		 }
	}
	
	@EventHandler
	public void onRespawn(PlayerRespawnEvent event) {
		 Game game = getGame(event.getPlayer().getWorld());
		 if(game != null) {
			 game.onRespawn(event);
		 }
	}
	
	@EventHandler
	public void onQuit(PlayerQuitEvent event) {
		 Game game = getGame(event.getPlayer().getWorld());
		 if(game != null) {
			 game.onPlayerLeave(event.getPlayer());
		 }
	}
	
	@EventHandler
	public void onLeave(PlayerChangedWorldEvent event) {
		Game from = getGame(event.getFrom());
		Game to = getGame(event.getPlayer().getWorld());
		if(from != null && from != to) {
			from.onPlayerLeave(event.getPlayer());
		}
	}
	
	@EventHandler
	public void onBlockPlace(BlockPlaceEvent event) {
		Game game = getGame(event.getPlayer().getWorld());
		if(game != null) {
			game.onBlockPlace(event);
		}
	}
	@EventHandler
	public void onBlockBreak(BlockBreakEvent event) {
		Game game = getGame(event.getPlayer().getWorld());
		if(game != null) {
			game.onBlockBreak(event);
		}
	}
	
	@EventHandler
	public void onEntityDamageByEntity(EntityDamageByEntityEvent event) {
		if(event.getEntity() instanceof Player) {
			if(event.getDamager() instanceof Player) {
				PlayerUtil.setLastAttacker((Player)event.getEntity(), (Player)event.getDamager());
			} else if(event.getDamager() instanceof Projectile) {
				ProjectileSource source = ((Projectile)event.getDamager()).getShooter();
				if(source != null && source instanceof Player) {
					PlayerUtil.setLastAttacker((Player)event.getEntity(), (Player)source);
				}
			}
		}
	}
	
	@EventHandler
	public void onEntityDamage(EntityDamageEvent event) {
		Game game = getGame(event.getEntity().getWorld());
		if(game != null) {
			game.onEntityDamage(event);
		}
	}
	@EventHandler
	public void onEntityDeath(EntityDeathEvent event) {
		Game game = getGame(event.getEntity().getWorld());
		if(game != null) {
			game.onEntityDeath(event);
		}
	}
	
	@EventHandler
	public void onInteract(PlayerInteractEvent event) {
		Game game = getGame(event.getPlayer().getWorld());
		if(game != null) {
			game.onInteract(event);
		}
	}
	@EventHandler
	public void onInteractEntity(PlayerInteractEntityEvent event) {
		Game game = getGame(event.getPlayer().getWorld());
		if(game != null) {
			game.onInteractEntity(event);
		}
	}
	@EventHandler
	public void onInteractAtEntity(PlayerInteractAtEntityEvent event) {
		Game game = getGame(event.getPlayer().getWorld());
		if(game != null) {
			game.onInteractAtEntity(event);
		}
	}
}
