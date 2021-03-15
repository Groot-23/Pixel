package me.groot_23.pixel.commands;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;

import me.groot_23.pixel.Pixel;
import me.groot_23.pixel.game.Lobby;

public class JoinCommand implements CommandExecutor, TabCompleter {

	@Override
	public List<String> onTabComplete(CommandSender sender, Command arg1, String arg2, String[] args) {
		List<String> list = new ArrayList<String>();
		if(args.length == 1) {
			for(String s : Pixel.LobbyProvider.currentlobbies.keySet()) {
				if(s.startsWith(args[0])) {
					list.add(s);
				}
			}
		}
		if(args.length == 2) {
			if(Pixel.LobbyProvider.currentlobbies.containsKey(args[0])) {
				for(String s : Pixel.LobbyProvider.currentlobbies.get(args[0]).keySet()) {
					if(s.startsWith(args[1])) {
						list.add(s);
					}
				}
			}
		}
		return list;
	}

	@Override
	public boolean onCommand(CommandSender sender, Command arg1, String arg2, String[] args) {
		if(!(sender instanceof Player)) {
			sender.sendMessage(ChatColor.RED + "You are not a player!");
			return true;
		}
		Player player = (Player)sender;
		Lobby lobby = null;
		if(args.length == 1) {
			lobby = Pixel.LobbyProvider.provideGame(args[0], Pixel.LobbyProvider.ProvideType.MOST_PLAYERS);
		}
		if(args.length == 2) {
			lobby = Pixel.LobbyProvider.provideLobby(args[0], args[1]);
		}
		if(lobby != null) {
			lobby.join(player);
			return true;
		}
		sender.sendMessage(ChatColor.RED + "Invalid Game!");
		return false;
	}

}
