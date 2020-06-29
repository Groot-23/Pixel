package me.groot_23.ming.commands;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;

import me.groot_23.ming.MiniGame;
import me.groot_23.ming.config.Utf8Config;
import me.groot_23.ming.kits.Kit;

public class KitCommands {
	
	public static List<String> tabComplete(MiniGame game, String[] args) {
		List<String> list = new ArrayList<String>();
		if(args.length == 1) {
			String[] modes = new String[] {"list", "create", "delete", "set"};
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
	
	public static void saveKitConfig(Utf8Config cfg, MiniGame game) {
		try {
			cfg.save(game.getKitFile());
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
