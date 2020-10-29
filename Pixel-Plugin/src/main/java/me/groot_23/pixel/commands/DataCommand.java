package me.groot_23.pixel.commands;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;

import me.groot_23.pixel.player.DataManager;

public class DataCommand implements CommandExecutor, TabCompleter{

	@Override
	public List<String> onTabComplete(CommandSender sender, Command cmd, String label, String[] args) {
		List<String> list = new ArrayList<String>();
		if(args.length == 1) {
			for(Player player : Bukkit.getServer().getOnlinePlayers()) {
				if(player.getName().startsWith(args[0])) {
					list.add(player.getName());
				}
			}
		}
		else if(args.length == 2) {
			for(String s : DataManager.getIds()) {
				if(s.startsWith(args[1])) {
					list.add(s);
				}
			}
		}
		else if(args.length == 3) {
			String[] modes = {"list", "get", "set"};
			for(String s : modes) {
				if(s.startsWith(args[2])) {
					list.add(s);				
				}
			}
		}
		else if(args.length == 4) {
			if(args[2].contentEquals("get") || args[2].contentEquals("set")) {
				Player player = Bukkit.getPlayer(args[0]);
				if(player != null) {
					ConfigurationSection sec = DataManager.getData(player, args[1]);
					for(String key : sec.getKeys(true)) {
						if(!sec.isConfigurationSection(key)) {
							if(key.startsWith(args[3])) {
								list.add(key);
							}
						}
					}
				}
			}
		}
		return list;
	}

	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		if(cmd.testPermission(sender)) {
			if(args.length < 3) return false;
			Player player = Bukkit.getPlayer(args[0]);
			String id = args[1];
			ConfigurationSection sec = DataManager.getData(player, id);
			if(sec == null) {
				sender.sendMessage(ChatColor.RED + "Data '" + id + "' is not registered!");
				return false;
			}
			String mode = args[2];
			if(mode.contentEquals("list")) {
				for(String key : sec.getKeys(true)) {
					if(!sec.isConfigurationSection(key)) {
						sender.sendMessage(ChatColor.GREEN + key + "  =  " + sec.getString(key));
					}
				}
			}
			else if(mode.contentEquals("get")) {
				if(args.length >= 4) {
					String key = args[3];
					sender.sendMessage(ChatColor.GREEN + key + "  =  " + sec.getString(key, "null"));
				}
				else {
					sender.sendMessage(ChatColor.RED + "Not enough arguments!");
				}
			}
			else if(mode.contentEquals("set")) {
				if(args.length >= 5) {
					String key = args[3];
					String value = args[4];
					
					sec.set(key, value);
					if(value.contentEquals("true"))
						sec.set(key, true);
					if(value.contentEquals("false"))
						sec.set(key, false);
					try {
						int x = Integer.parseInt(value);
						sec.set(key, x);
					} catch(NumberFormatException e) {
						try {
							double x = Double.parseDouble(value);
							sec.set(key, x);
						} catch(NumberFormatException e1) {}
					}
					
					DataManager.saveData(id);
					sender.sendMessage(ChatColor.GREEN + key + " -> " + value);
				}
				else {
					sender.sendMessage(ChatColor.RED + "Not enough arguments!");
				}
			}
			else {
				return false;
			}
		}
		return true;
	}

}
