package me.groot_23.pixel.commands;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import me.groot_23.pixel.Pixel;
import me.groot_23.pixel.display.JoinSignApi;
import me.groot_23.pixel.game.Lobby;

public class SignCommand extends PlayerCommand{

	public SignCommand(JavaPlugin plugin, String name, String permission) {
		super(plugin, name, permission);
	}

	@Override
	public List<String> tabComplete(CommandSender sender, Command cmd, String label, String[] args) {
		List<String> list = new ArrayList<String>();
		if(args.length == 1) {
			for(Material mat : Material.values()) {
				String s = mat.name().toLowerCase();
				if(s.contains("sign") && !s.contains("wall") && s.startsWith(args[0])) {
					list.add(s);
				}
			}
		}
		if(args.length == 2) {
			for(String s : Pixel.LobbyProvider.currentlobbies.keySet()) {
				if(s.startsWith(args[1])) {
					list.add(s);
				}
			}
		}
		if(args.length == 3) {
			Map<String, Lobby> m = Pixel.LobbyProvider.currentlobbies.get(args[1]);
			if(m != null) {
				for(String s : m.keySet()) {
					if(s.startsWith(args[2])) {
						list.add(s);
					}
				}
			}
		}
		return list;
	}

	@Override
	public boolean execute(CommandSender sender, Command cmd, String label, String[] args) {
		Player player = (Player) sender;
		if(args.length < 2) return false;
		String strMaterial = args[0].toUpperCase();
		Material material = Material.getMaterial(strMaterial);
		if(!strMaterial.contains("SIGN") || material == null) return false;
		String game = args[1];
		Map<String, Lobby> games = Pixel.LobbyProvider.currentlobbies.get(game);
		if(args.length < 3) {
			for(String s : games.keySet()) {
				player.getInventory().addItem(JoinSignApi.createItem(material, game, s));
			}
		}
		else {
			if(games.containsKey(args[3])) {
				player.getInventory().addItem(JoinSignApi.createItem(material, game, args[2]));	
			}
		}
		return true;
	}
	

}
