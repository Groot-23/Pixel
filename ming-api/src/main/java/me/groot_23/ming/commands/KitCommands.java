package me.groot_23.ming.commands;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.craftbukkit.libs.org.apache.commons.io.FilenameUtils;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import me.groot_23.ming.MinG;
import me.groot_23.ming.MiniGame;
import me.groot_23.ming.kits.Kit;
import me.groot_23.ming.language.LanguageHolder;
import me.groot_23.ming.language.LanguageManager;
import me.groot_23.ming.util.Utf8Config;

public class KitCommands implements CommandExecutor, TabCompleter {
	
	private File kitFile;
	private Utf8Config cfg;
	private LanguageHolder lang;
	private String kitGroup;
	private String permission;
	
	public KitCommands(JavaPlugin plugin, LanguageHolder lang, File kitFile,
			String kitGroup, String cmdName, String permission) {
		plugin.getCommand(cmdName).setExecutor(this);
		plugin.getCommand(cmdName).setTabCompleter(this);
		
		this.permission = permission;
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
	public List<String> onTabComplete(CommandSender arg0, Command arg1, String arg2, String[] args) {
		return tabComplete(args);
	}

	@Override
	public boolean onCommand(CommandSender sender, Command arg1, String arg2, String[] args) {
		if(permission == null || sender.hasPermission(permission)) {
			execute(sender, args);
		} else {
			sender.sendMessage(ChatColor.RED + "You don't have the required permission to execute this command: " + permission);
		}

		return true;
	}
	
	
	public List<String> tabComplete(String[] args) {
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
	
	public void execute(CommandSender sender, String[] args) {
		if(args[0].equals("list")) {
			list(sender);
		} else if(args[0].equals("create")) {
			create(sender, args[1], args[2]);
		} else if(args[0].equals("delete")) {
			delete(sender, args[1]);
		} else if(args[0].equals("set")) {
			set((Player)sender, args[1]);
		} else if(args[0].equals("get")) {
			get((Player)sender, args[1]);
		} else if(args[0].equals("name")) {
			String val = args[3];
			for(int i = 4; i < args.length; i++) {
				val += " " + args[i];
			}
			name(args[1], args[2], val);
		} else if(args[0].equals("description")) {
			String val = args[3];
			for(int i = 4; i < args.length; i++) {
				val += " " + args[i];
			}
			description(args[1], args[2], val);
		}
	}

	public void get(Player sender, String name) {
		Kit kit = MinG.getKit(kitGroup, name);
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
