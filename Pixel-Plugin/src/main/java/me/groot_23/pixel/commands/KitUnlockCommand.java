package me.groot_23.pixel.commands;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;

import me.groot_23.pixel.kits.Kit;
import me.groot_23.pixel.kits.KitApi;

public class KitUnlockCommand implements CommandExecutor, TabCompleter {

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
			for(String s : KitApi.getGroups()) {
				if(s.startsWith(args[1])) {
					list.add(s);
				}
			}
		}
		else if(args.length == 3) {
			for(Kit k : KitApi.getKits(args[1])) {
				if(k.getName().startsWith(args[2])) {
					list.add(k.getName());
				}
			}
		}
		else if(args.length == 4) {
			String[] modes = {"get", "set"};
			for(String s : modes) {
				if(s.startsWith(args[3])) {
					list.add(s);				
				}
			}
		}
		else if(args.length == 5) {
			if(args[3].contentEquals("set")) {
				String[] bools = {"true", "false"};
				for(String s : bools) {
					if(s.startsWith(args[4])) {
						list.add(s);				
					}
				}
			}
		}
		return list;
	}

	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		if(cmd.testPermission(sender)) {
			if(args.length < 4) return false;
			Player player = Bukkit.getPlayer(args[0]);
			String group = args[1];
			String kit = args[2];
			if(args[3].contentEquals("get")) {
				sender.sendMessage(KitApi.isUnlocked(group, kit, player) ? ChatColor.GREEN +
						"unlocked" : ChatColor.RED + "locked");
			}
			else if(args[3].contentEquals("set")) {
				if(args.length >= 5) {
					boolean val;
					if(args[4].contentEquals("true")) val = true;
					else if(args[4].contentEquals("false")) val = false;
					else {
						sender.sendMessage(ChatColor.RED + "Invalid argument: " + args[4]);
						return false;
					}
					KitApi.setUnlocked(group, kit, player, val);
					sender.sendMessage(kit + " -> " + (KitApi.isUnlocked(group, kit, player) ? ChatColor.GREEN +
							"unlocked" : ChatColor.RED + "locked"));
				} else {
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
