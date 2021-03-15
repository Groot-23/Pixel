package me.groot_23.pixel.world;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import org.bukkit.World;

public class GameplayModifier {
	
	private static Map<UUID, GameplayModifier> active = new HashMap<UUID, GameplayModifier>();
	
	public static GameplayModifier get(UUID id) {return active.get(id);}
	public static GameplayModifier get(World world) {return get(world.getUID());}
	
	public static GameplayModifier set(UUID id, GameplayModifier mod) {return active.put(id, mod);}
	public static GameplayModifier set(World world, GameplayModifier mod) {return set(world.getUID(), mod);}
	
	public static GameplayModifier remove(UUID id) {return active.remove(id);}
	public static GameplayModifier remove(World world) {return remove(world.getUID());}
	
	public boolean canBlock = false;
	public boolean cooldown = true;
	public boolean sweepAttack = true;
	
	public boolean canSwimWater = true;
	public boolean canSwimAir = true;

	public boolean projectileKnockback = false;
	public double projectileKnockbackSnowball = 1.0;
	public double projectileKnockbackEgg = 1.0;
	
	public boolean correctProjectiles = false;
	
	
	public static GameplayModifier old() {
		GameplayModifier mod = new GameplayModifier();
		mod.canBlock = true;
		mod.cooldown = false;
		mod.sweepAttack = false;
		mod.canSwimWater = false;
		mod.canSwimAir = false;
		mod.projectileKnockback = true;
		mod.correctProjectiles = true;
		return mod;
	}
}
