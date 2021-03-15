package me.groot_23.pixel.listener;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import org.bukkit.Material;
import org.bukkit.attribute.Attribute;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.entity.Projectile;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent.DamageCause;
import org.bukkit.event.entity.EntityToggleSwimEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.entity.ProjectileHitEvent;
import org.bukkit.event.entity.ProjectileLaunchEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryOpenEvent;
import org.bukkit.event.player.PlayerChangedWorldEvent;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerItemHeldEvent;
import org.bukkit.event.player.PlayerSwapHandItemsEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;

import me.groot_23.pixel.Pixel;
import me.groot_23.pixel.world.GameplayModifier;

public class GameplayListener implements Listener {

	private Map<UUID, BukkitRunnable> shieldTask = new HashMap<UUID, BukkitRunnable>();
	private Map<UUID, ItemStack> shieldItem = new HashMap<UUID, ItemStack>();
	
	public static Vector calcKnockback(Vector dir, double strength) {
		Vector v = new Vector(dir.getX(), 0, dir.getZ()).normalize();
		v = v.add(new Vector(0, 0.5, 0)).normalize();
		v = v.multiply(strength);
		return v;
	}

	
	@EventHandler
	public void onSwordBlock(PlayerInteractEvent event) {
		GameplayModifier mod = GameplayModifier.get(event.getPlayer().getWorld());
		if(mod == null) return;
		if(event.getItem() == null) return;
		
		if(mod.canBlock) {
			if(event.getAction() == Action.RIGHT_CLICK_AIR || event.getAction() == Action.RIGHT_CLICK_BLOCK) {
				if(event.getItem().getType().name().contains("SWORD") && event.getPlayer().getInventory().getItemInOffHand().getType() != Material.SHIELD) {
					giveShield(event.getPlayer());
				}
			}	
		}
	}
	
	@EventHandler
	public void onSwap(PlayerSwapHandItemsEvent event) {
		GameplayModifier mod = GameplayModifier.get(event.getPlayer().getWorld());
		if(mod == null) return;
		
		if(event.getOffHandItem().getType().name().contains("SWORD")) event.setCancelled(true);
	}
	
	@EventHandler
	public void onDrop(PlayerDropItemEvent event) {
		GameplayModifier mod = GameplayModifier.get(event.getPlayer().getWorld());
		if(mod == null) return;
		
		if(event.getPlayer().getInventory().getItemInOffHand().getType() == Material.SHIELD) resetShield(event.getPlayer());
	}
	
	@EventHandler
	public void onDeath(PlayerDeathEvent event) {
		GameplayModifier mod = GameplayModifier.get(event.getEntity().getWorld());
		if(mod == null) return;
		
		for(int i = 0; i < event.getDrops().size(); ++i) {
			if(event.getDrops().get(i).getType() == Material.SHIELD) {
				event.getDrops().set(i, shieldItem.get(event.getEntity().getUniqueId()));
				break;
			}
		}
		resetShield(event.getEntity());
	}
	
	@EventHandler
	public void onInvClick(InventoryClickEvent event) {
		GameplayModifier mod = GameplayModifier.get(event.getWhoClicked().getWorld());
		if(mod == null) return;
		
		ItemStack a = event.getCurrentItem();
		ItemStack b = event.getCursor();
		if(a != null && a.getType() == Material.SHIELD) event.setCancelled(true);
		if(b != null && b.getType() == Material.SHIELD) event.setCancelled(true);
	}
	
	@EventHandler
	public void onInvOpen(InventoryOpenEvent event) {
		GameplayModifier mod = GameplayModifier.get(event.getPlayer().getWorld());
		if(mod == null) return;
		
		if(!(event.getPlayer() instanceof Player)) return;
		if(event.getPlayer().getInventory().getItemInMainHand().getType().name().contains("SWORD") &&
				event.getPlayer().getInventory().getItemInOffHand().getType() == Material.SHIELD) {
			resetShield((Player)event.getPlayer());
		}
	}
	
	@EventHandler
	public void onHotbarChange(PlayerItemHeldEvent event) {
		GameplayModifier mod = GameplayModifier.get(event.getPlayer().getWorld());
		if(mod == null) return;
		
		if(event.getPlayer().isBlocking())
			resetShield(event.getPlayer());
	}
	
	@EventHandler
	public void onDamage(EntityDamageByEntityEvent event) {
		GameplayModifier mod = GameplayModifier.get(event.getEntity().getWorld());
		if(mod == null) return;
		
		if(!mod.sweepAttack && event.getCause() == DamageCause.ENTITY_SWEEP_ATTACK) event.setCancelled(true);
		
		if(event.getEntity() instanceof Player)  {
			Player player = (Player) event.getEntity();
			if(player.isBlocking()) {
				player.damage(event.getDamage() * 0.5);
				event.setDamage(0);
			}
//			EntityType type = event.getDamager().getType();
//			if(type == EntityType.SNOWBALL || type == EntityType.EGG) {
//				event.setDamage(0.1);
//			}
		}
	}
	
	@EventHandler
	public void onProjectileHit(ProjectileHitEvent event) {
		GameplayModifier mod = GameplayModifier.get(event.getEntity().getWorld());
		if(mod == null) return;
		
		if(mod.projectileKnockback) {
			if(event.getHitEntity() instanceof Player) {
				Player player = (Player) event.getHitEntity();
				if(event.getEntityType() == EntityType.SNOWBALL || event.getEntityType() == EntityType.EGG) {
					player.damage(0.001);
					double strength = (event.getEntityType() == EntityType.SNOWBALL) ? mod.projectileKnockbackSnowball : mod.projectileKnockbackEgg;
					player.setVelocity(calcKnockback(event.getEntity().getVelocity(), strength));
				}
			}
		}
	}
	
	@EventHandler
	public void noSwim(EntityToggleSwimEvent event) {
//		System.out.println("SWIM TOGGLE");
		GameplayModifier mod = GameplayModifier.get(event.getEntity().getWorld());
		if(mod == null) return;
		
//		System.out.println("APPLY EVENT");
//		System.out.println(event.isSwimming());
		
		boolean can = true;
		if(event.getEntity().isInWater()) can = can && mod.canSwimWater;
		else can = can && mod.canSwimAir;
		
		if(!can && !event.isSwimming()) event.setCancelled(true);
	}
	
	@EventHandler
	public void correctProjectiles(ProjectileLaunchEvent event) {
		GameplayModifier mod = GameplayModifier.get(event.getEntity().getWorld());
		if(mod == null) return;
		
		if(mod.correctProjectiles) {
			Projectile projectile = event.getEntity();
			if(projectile.getShooter() instanceof Player) {
				Player player = (Player)projectile.getShooter();
				Vector dir = player.getLocation().getDirection().normalize();
				double velocity = projectile.getVelocity().length();
//				if(projectile.getType() == EntityType.ENDER_PEARL) velocity *= 2;
				projectile.setVelocity(dir.multiply(velocity));
			}			
		}
	}
	
	@EventHandler
	public void onWorldChange(PlayerChangedWorldEvent event) {
		GameplayModifier mod = GameplayModifier.get(event.getPlayer().getWorld());
		if(mod == null) return;
		
		if(mod.cooldown) {
			event.getPlayer().getAttribute(Attribute.GENERIC_ATTACK_SPEED).setBaseValue(16);			
		} else {
			event.getPlayer().getAttribute(Attribute.GENERIC_ATTACK_SPEED).setBaseValue(4);
		}
	}
	
	
	private void giveShield(Player player) {
		// store old offhand item
		shieldItem.put(player.getUniqueId(), player.getInventory().getItemInOffHand());
		// set shield in offhand
		player.getInventory().setItemInOffHand(new ItemStack(Material.SHIELD));
		
		// remove shield when not used anymore
		BukkitRunnable run = new BukkitRunnable() {
			@Override
			public void run() {
				if(!player.isBlocking()) {
					resetShield(player);
				}
			}
		};
		run.runTaskTimer(Pixel.getPlugin(), 10, 2);
		shieldTask.put(player.getUniqueId(), run);
	}
	
	private void resetShield(Player player) {
		player.getInventory().setItemInOffHand(shieldItem.get(player.getUniqueId()));
		shieldItem.remove(player.getUniqueId());
		BukkitRunnable task = shieldTask.get(player.getUniqueId());
		if(task != null) task.cancel();
		shieldTask.remove(player.getUniqueId());
	}
	
}
