package me.groot_23.ming.commands;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.craftbukkit.libs.org.apache.commons.io.FilenameUtils;
import org.bukkit.entity.Player;

import me.groot_23.ming.MiniGame;
import me.groot_23.ming.config.Utf8Config;
import me.groot_23.ming.language.LanguageManager;

public class LangCommand implements CommandExecutor, TabCompleter{

	private MiniGame game;
	private String permission;
	
	public LangCommand(MiniGame game, String cmdName, String permission) {
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
			for(String s : game.getLangManager().getFolder().list()) {
				s = FilenameUtils.removeExtension(s);
				if(s.startsWith(args[0])) list.add(s);
			}
		}
		else if(args.length == 2) {
			Utf8Config cfg = game.getLangManager().getConfig(args[0]);
			if(cfg != null) {
				for(String s : cfg.getKeys(true)) {
					if(s.startsWith(args[1]) && !cfg.isConfigurationSection(s)) list.add(s);
				}
			}
		}
		return list;
	}
	
	public static void execute(MiniGame game, CommandSender sender, String[] args) {
		LanguageManager lang = game.getLangManager();
		Utf8Config cfg = lang.getConfig(args[0]);
		if(cfg != null) {
			if(args.length > 1) {
				if(args.length == 2) {
					sender.sendMessage(cfg.getString(args[1], ChatColor.RED + "Value NOT found!"));
				} else {
					String val = args[2];
					for(int i = 3; i < args.length; i++) {
						val += " " + args[i];
					}
					cfg.set(args[1], val);
					try {
						cfg.save(lang.getFile(args[0]));
						sender.sendMessage(ChatColor.GREEN + "Value of '" + args[1] + " was updated successfully!");
					} catch (IOException e) {
						e.printStackTrace();
					}
				}
			}
		}
	}
}
