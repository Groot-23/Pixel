package me.groot_23.ming.world;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.EntityType;

public class WorldMarker {
	
	public static final String MARKER_PREFIX = "minG_marker_";
	
	public static void createMarker(Location location, String markerName, boolean spawnInvisible) {
		ArmorStand armorStand = (ArmorStand)location.getWorld().spawnEntity(location, EntityType.ARMOR_STAND);
		armorStand.setMarker(true);
		armorStand.setGravity(false);
		armorStand.setCustomName(MARKER_PREFIX + markerName);
		armorStand.setCustomNameVisible(false);
		armorStand.setVisible(!spawnInvisible);
	}
	
	public static List<Location> findMarkers(World world, String markerName) {
		List<Location> markers = new ArrayList<Location>();
		for(ArmorStand e : world.getEntitiesByClass(ArmorStand.class)) {
			if(e.getCustomName().equals(MARKER_PREFIX + markerName)) {
				markers.add(e.getLocation());
			}
		}
		return markers;
	}
	
	public static void setMarkersInvisible(World world, String markerName, boolean invisible) {
		for(ArmorStand e : world.getEntitiesByClass(ArmorStand.class)) {
			if(e.getCustomName().equals(MARKER_PREFIX + markerName)) {
				e.setVisible(!invisible);
			}
		}
	}
	
	public static void removeMarkers(World world, String markerName) {
		for(ArmorStand e : world.getEntitiesByClass(ArmorStand.class)) {
			if(e.getCustomName().equals(MARKER_PREFIX + markerName)) {
				e.remove();
			}
		}
	}
}
