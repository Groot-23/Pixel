package me.groot_23.pixel.commands;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.craftbukkit.libs.org.apache.commons.io.FilenameUtils;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import me.groot_23.pixel.kits.Kit;
import me.groot_23.pixel.kits.KitApi;
import me.groot_23.pixel.language.LanguageFolder;
import me.groot_23.pixel.util.Utf8Config;

public class KitCommands extends CommandBase {
	
	private File kitFile;
	private Utf8Config cfg;
	private LanguageFolder lang;
	private String kitGroup;
	
	public KitCommands(JavaPlugin plugin, LanguageFolder lang, File kitFile,
			String kitGroup, String cmdName, String permission) {
		super(plugin, cmdName, permission);
		
		this.kitFile = kitFile;
		this.cfg = new Utf8Config();
		try {
			cfg.load(kitFile);
		} catch (IOException | InvalidConfigurationException e) {
			e.printStackTrace();
		}
		this.lang = lang;
		this.kitGroup = kitGroup;
	}
	
	@Override
	public List<String> tabComplete(CommandSender sender, Command cmd, String label, String[] args) {
		List<String> list = new ArrayList<String>();
		if(args.length == 1) {
			String[] modes = new String[] {"list", "create", "delete", "set", "get", "name", "description"};
			for(String s : modes) {
				if(s.startsWith(args[0])) list.add(s);
			}
		} else if (args.length == 2) {
			if(cfg != null) {
				for(String key : cfg.getKeys(false)) {
					if(key.startsWith(args[1])) list.add(key);
				}
			}
		} else if(args.length == 3) {
			if(args[0].equals("create")) {
				for(Material material : Material.values()) {
					String name = material.name().toLowerCase();
					if(name.startsWith(args[2])) list.add(name);
				}
			} else if(args[0].equals("name") || args[0].equals("description")) {
				for(String s : lang.getFolder().list()) {
					s = FilenameUtils.removeExtension(s);
					if(s.startsWith(args[2])) list.add(s);
				}
			}
		} else if(args.length == 4) {
			if(args[0].equals("name") || args[0].equals("description")) {
				Utf8Config cfg = lang.getConfig(args[2]);
				if(cfg != null) {
					String s = cfg.getString("kits." + args[1] + "." + args[0]);
					if(s != null && s.startsWith(args[3])) {
						list.add(s);
					}
				}
			}
		}
		return list;
	}

	@Override
	public boolean execute(CommandSender sender, Command cmd, String label, String[] args) {
		if(args.length == 0) return false;
		if(args[0].equals("list")) {
			list(sender);
			return true;
		} else if(args.length == 3 && args[0].equals("create")) {
			create(sender, args[1], args[2]);
			return true;
		} else if(args.length == 2 && args[0].equals("delete")) {
			delete(sender, args[1]);
			return true;
		} else if(args.length == 2 && args[0].equals("set")) {
			set((Player)sender, args[1]);
			return true;
		} else if(args.length == 2 && args[0].equals("get")) {
			get((Player)sender, args[1]);
			return true;
		} else if(args.length >= 4 && args[0].equals("name")) {
			String val = args[3];
			for(int i = 4; i < args.length; i++) {
				val += " " + args[i];
			}
			name(args[1], args[2], val);
			return true;
		} else if(args.length >= 4 && args[0].equals("description")) {
			String val = args[3];
			for(int i = 4; i < args.length; i++) {
				val += " " + args[i];
			}
			description(args[1], args[2], val);
			return true;
		}
		return false;
	}
	
	
	public void get(Player sender, String name) {
		Kit kit = KitApi.getKit(kitGroup, name);
		if(kit != null) {
			kit.applyToPlayer(sender);
		}
	}
	
	public void set(Player sender, String name) {
		cfg.set(name + ".items", null);
		ConfigurationSection section = cfg.createSection(name + ".items");
		Kit.serializeInventory(sender, section);
		saveKitConfig(cfg);
	}
	
	public void delete(CommandSender sender, String name) {
		cfg.set(name, null);
		saveKitConfig(cfg);
	}
	
	public void create(CommandSender sender, String name, String material) {
		ConfigurationSection section = cfg.createSection(name);
		Material mat = Material.matchMaterial(material);
		if(mat == null) {
			sender.sendMessage(ChatColor.RED + "The Material '" + material + "' does NOT exist!");
		} else {
			section.set("material", material);
		}
		saveKitConfig(cfg);
	}
	
	public void list(CommandSender sender) {
		for(String key : cfg.getKeys(false)) {
			sender.sendMessage(key);
		}
	}
	
	public void name(String kit, String language, String name) {
		setLangValue(kit, language, "name", name);
	}
	public void description(String kit, String language, String description) {
		setLangValue(kit, language, "description", description);
	}
	
	public void setLangValue(String kit, String language, String key, String value) {
		Utf8Config cfg = lang.getConfig(language);
		if(cfg != null) {
			cfg.set("kits." + kit + "." + key, value);
			lang.saveConfig(cfg, language);
		}
	}
	
	public void saveKitConfig(Utf8Config cfg) {
		try {
			cfg.save(kitFile);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

}
