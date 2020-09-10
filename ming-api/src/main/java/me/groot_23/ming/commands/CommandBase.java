package me.groot_23.ming.commands;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.plugin.java.JavaPlugin;

public abstract class CommandBase implements CommandExecutor, TabCompleter {

	public CommandBase(JavaPlugin plugin, String name, String permission) {
		plugin.getCommand(name).setExecutor(this);
		plugin.getCommand(name).setTabCompleter(this);
		plugin.getCommand(name).setPermission(permission);
	}
	
	@Override
	public List<String> onTabComplete(CommandSender sender, Command cmd, String alias, String[] args) {
		return cmd.testPermissionSilent(sender) ? tabComplete(sender, cmd, alias, args) : new ArrayList<String>();
	}

	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		if(cmd.testPermission(sender)) {
			return execute(sender, cmd, label, args);
		}
		return true;
	}
	
	public abstract List<String> tabComplete(CommandSender sender, Command cmd, String label, String[] args);
	public abstract boolean execute(CommandSender sender, Command cmd, String label, String[] args);

}
