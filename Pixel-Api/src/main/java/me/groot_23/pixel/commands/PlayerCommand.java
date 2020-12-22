package me.groot_23.pixel.commands;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import me.groot_23.pixel.language.LanguageApi;
import me.groot_23.pixel.language.PixelLangKeys;

public abstract class PlayerCommand extends CommandBase {

	public PlayerCommand(JavaPlugin plugin, String name, String permission) {
		super(plugin, name, permission);
	}

	@Override
	public List<String> onTabComplete(CommandSender sender, Command cmd, String alias, String[] args) {
		if(sender instanceof Player) {			
			return super.onTabComplete(sender, cmd, alias, args);
		}
		return new ArrayList<String>();
	}
	
	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		if(sender instanceof Player) {			
			return super.onCommand(sender, cmd, label, args);
		}
		else {
			sender.sendMessage(LanguageApi.getDefault(PixelLangKeys.CMD_PLAYER_ONLY));
			return true;
		}
	}

}
