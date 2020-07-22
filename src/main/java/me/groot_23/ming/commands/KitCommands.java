package me.groot_23.ming.commands;

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
import org.bukkit.craftbukkit.libs.org.apache.commons.io.FilenameUtils;
import org.bukkit.entity.Player;

import me.groot_23.ming.MiniGame;
import me.groot_23.ming.kits.Kit;
import me.groot_23.ming.util.Utf8Config;

public class KitCommands implements CommandExecutor, TabCompleter {
	
	private MiniGame game;
	private String permission;
	
	public KitCommands(MiniGame game, String cmdName, String permission) {
		game.getPlugin().getCommand(cmdName).setExecutor(this);
		game.getPlugin().getCommand(cmdName).setTabCompleter(this);
		this.permission = permission;
		this.game = game;
	}
	
	@Override
	public List<String> onTabComplete(CommandSender arg0, Command arg1, String arg2, String[] args) {
		return tabComplete(game, args);
	}

	@Override
	public boolean onCommand(CommandSender sender, Command arg1, String arg2, String[] args) {
		if(permission == null || sender.hasPermission(permission)) {
			execute(game, sender, args);
		} else {
			sender.sendMessage(ChatColor.RED + "You don't have the required permission to execute this command: " + permission);
		}

		return true;
	}
	
	
	public static List<String> tabComplete(MiniGame game, String[] args) {
		List<String> list = new ArrayList<String>();
		if(args.length == 1) {
			String[] modes = new String[] {"list", "create", "delete", "set", "get", "name", "description"};
			for(String s : modes) {
				if(s.startsWith(args[0])) list.add(s);
			}
		} else if (args.length == 2) {
			Utf8Config cfg = game.getKitConfig();
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
				for(String s : game.getLangManager().getLanguageHolder(0).getFolder().list()) {
					s = FilenameUtils.removeExtension(s);
					if(s.startsWith(args[2])) list.add(s);
				}
			}
		} else if(args.length == 4) {
			if(args[0].equals("name") || args[0].equals("description")) {
				Utf8Config cfg = game.getLangManager().getLanguageHolder(0).getConfig(args[2]);
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
	
	public static void execute(MiniGame game, CommandSender sender, String[] args) {
		if(args[0].equals("list")) {
			list(game, sender);
		} else if(args[0].equals("create")) {
			create(game, sender, args[1], args[2]);
		} else if(args[0].equals("delete")) {
			delete(game, sender, args[1]);
		} else if(args[0].equals("set")) {
			set(game, (Player)sender, args[1]);
		} else if(args[0].equals("get")) {
			get(game, (Player)sender, args[1]);
		} else if(args[0].equals("name")) {
			String val = args[3];
			for(int i = 4; i < args.length; i++) {
				val += " " + args[i];
			}
			name(game, args[1], args[2], val);
		} else if(args[0].equals("description")) {
			String val = args[3];
			for(int i = 4; i < args.length; i++) {
				val += " " + args[i];
			}
			description(game, args[1], args[2], val);
		}
	}

	public static void get(MiniGame game, Player sender, String name) {
		Kit kit = game.getKit(name);
		if(kit != null) {
			kit.applyToPlayer(sender);
		}
	}
	
	public static void set(MiniGame game, Player sender, String name) {
		Utf8Config cfg = game.getKitConfig();
		cfg.set(name + ".items", null);
		ConfigurationSection section = cfg.createSection(name + ".items");
		Kit.serializeInventory(sender, section);
		saveKitConfig(cfg, game);
	}
	
	public static void delete(MiniGame game, CommandSender sender, String name) {
		Utf8Config cfg = game.getKitConfig();
		cfg.set(name, null);
		saveKitConfig(cfg, game);
	}
	
	public static void create(MiniGame game, CommandSender sender, String name, String material) {
		Utf8Config cfg = game.getKitConfig();
		ConfigurationSection section = cfg.createSection(name);
		Material mat = Material.matchMaterial(material);
		if(mat == null) {
			sender.sendMessage(ChatColor.RED + "The Material '" + material + "' does NOT exist!");
		} else {
			section.set("material", material);
		}
		saveKitConfig(cfg, game);
	}
	
	public static void list(MiniGame game, CommandSender sender) {
		Utf8Config cfg = game.getKitConfig();
		for(String key : cfg.getKeys(false)) {
			sender.sendMessage(key);
		}
	}
	
	public static void name(MiniGame game, String kit, String language, String name) {
		setLangValue(game, kit, language, "name", name);
	}
	public static void description(MiniGame game, String kit, String language, String description) {
		setLangValue(game, kit, language, "description", description);
	}
	
	public static void setLangValue(MiniGame game, String kit, String language, String key, String value) {
		Utf8Config cfg = game.getLangManager().getLanguageHolder(0).getConfig(language);
		if(cfg != null) {
			cfg.set("kits." + kit + "." + key, value);
			game.getLangManager().getLanguageHolder(0).saveConfig(cfg, language);
		}
	}
	
	public static void saveKitConfig(Utf8Config cfg, MiniGame game) {
		try {
			cfg.save(game.getKitFile());
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

}
