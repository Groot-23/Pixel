package me.groot_23.ming.commands;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.World;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import me.groot_23.ming.MiniGame;
import me.groot_23.ming.world.WorldMarker;

public class MarkerCommand implements CommandExecutor, TabCompleter {
	
	private String markerName;
	
	public MarkerCommand(JavaPlugin plugin, String commandName, String markerName) {
		plugin.getCommand(commandName).setExecutor(this);
		plugin.getCommand(commandName).setTabCompleter(this);
		this.markerName = markerName;
	}
	
	@Override
	public List<String> onTabComplete(CommandSender sender, Command cmd, String label, String[] args) {
		List<String> list = new ArrayList<String>();
		if(args.length == 1) {
			String[] modes = new String[] {"add", "show", "remove"};
			for(String s : modes) {
				if(s.startsWith(args[0])) list.add(s);
			}
		}
		if(args.length == 2 && args[0].equals("show")) {
			String[] options = new String[] {"true", "false"};
			for(String s : options) {
				if(s.startsWith(args[1])) list.add(s);
			}
		}
		return list;
	}
	
	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		if(sender instanceof Player) {
			Player player = (Player) sender;
			World world = player.getWorld();
			if(args.length > 0) {
				if(args[0].equals("add")) {
					WorldMarker.createMarker(player.getLocation(), markerName, false);
				} else if(args[0].equals("show")) {
					if(args.length == 2)
						WorldMarker.setMarkersInvisible(world, markerName, args[1].equals("true"));
				} else if(args[0].equals("remove")) {
					WorldMarker.removeMarkers(world, markerName);
				}
			}
		}
		return false;
	}
}
