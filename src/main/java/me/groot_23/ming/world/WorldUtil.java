package me.groot_23.ming.world;

import java.io.File;
import java.io.IOException;

import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.craftbukkit.libs.org.apache.commons.io.FileUtils;
import org.bukkit.entity.Player;

public class WorldUtil {
	
	public static File getWorldFolder(String name) {
		return new File(Bukkit.getWorldContainer(), name);
	}
	
	public static boolean worldExists(String name) {
		return getWorldFolder(name).exists();
	}
	
	public static void unloadWorld(World world) {
		for(Player player : world.getPlayers()) {
			player.teleport(Bukkit.getWorlds().get(0).getSpawnLocation());
		}
		Bukkit.unloadWorld(world, false);
	}
	
	public static void deleteWorld(String name) {
		if(worldExists(name)) {
			World world = Bukkit.getWorld(name);
			if(world != null) {
				unloadWorld(world);
			}
			try {
				FileUtils.deleteDirectory(getWorldFolder(name));
			} catch (IOException e) {
				System.err.println("[MinG] ERROR: unable to delete world folder: " + name);
				e.printStackTrace();
			}
		}
	}
	
	public static void copyWorld(String src, String dst) {
		if(worldExists(src)) {
			try {
				FileUtils.copyDirectory(getWorldFolder(src), getWorldFolder(dst));
				new File(getWorldFolder(dst), "uid.dat").delete();
			} catch (IOException e) {
				System.err.println("[MinG] ERROR: unable to copy world from '" + src + "' to " + dst);
				e.printStackTrace();
			}
		} else {
			System.err.println("[MinG] ERROR: world to copy does not exist: " + src);
		}
	}
	
	public static void replaceWorld(String src, String dst) {
		deleteWorld(dst);
		copyWorld(src, dst);
	}
}
