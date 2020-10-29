package me.groot_23.pixel.player;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.entity.Player;

import me.groot_23.pixel.util.Utf8Config;

public class DataManager {

	private static Map<String, Utf8Config> data = new HashMap<String, Utf8Config>();
	private static Map<String, File> paths = new HashMap<String, File>();
	
	public static void reload() {
		for(Map.Entry<String, File> e : paths.entrySet()) {
			Utf8Config cfg = new Utf8Config();
			try {
				cfg.load(e.getValue());
				data.put(e.getKey(), cfg);
			} catch (IOException | InvalidConfigurationException e1) {
				e1.printStackTrace();
			}
		}
	}
	
	public static void loadFile(File file, String id) {
		Utf8Config cfg = new Utf8Config();
		try {
			cfg.load(file);
			data.put(id, cfg);
			paths.put(id, file);
		} catch (IOException | InvalidConfigurationException e) {
			e.printStackTrace();
		}
	}
	
	public static ConfigurationSection getData(Player player, String id) {
		Utf8Config cfg = data.get(id);
		if(cfg == null) return null;
		ConfigurationSection sec = cfg.getConfigurationSection(player.getUniqueId().toString());
		if(sec == null) sec = cfg.createSection(player.getUniqueId().toString());
		return sec;
	}
	
	public static void saveData(String id) {
		Utf8Config cfg = data.get(id);
		if(cfg != null) {
			try {
				cfg.save(paths.get(id));
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
	
	public static Set<String> getIds() {
		return data.keySet();
	}
}
