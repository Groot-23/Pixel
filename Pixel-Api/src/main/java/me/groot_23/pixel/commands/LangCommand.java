package me.groot_23.pixel.commands;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.craftbukkit.libs.org.apache.commons.io.FilenameUtils;
import org.bukkit.plugin.java.JavaPlugin;

import me.groot_23.pixel.language.LanguageFolder;
import me.groot_23.pixel.util.Utf8Config;

public class LangCommand extends CommandBase {

	private LanguageFolder lang;
	
	public LangCommand(JavaPlugin plugin, LanguageFolder lang, String cmdName, String permission) {
		super(plugin, cmdName, permission);
		this.lang = lang;
	}
	

	@Override
	public List<String> tabComplete(CommandSender sender, Command cmd, String label, String[] args) {
		List<String> list = new ArrayList<String>();
		if(args.length == 1) {
			for(String s : lang.getFolder().list()) {
				s = FilenameUtils.removeExtension(s);
				if(s.startsWith(args[0])) list.add(s);
			}
		}
		else if(args.length == 2) {
			Utf8Config cfg = lang.getConfig(args[0]);
			if(cfg != null) {
				for(String s : cfg.getKeys(true)) {
					if(s.startsWith(args[1]) && !cfg.isConfigurationSection(s)) list.add(s);
				}
			}
		}
		return list;
	}
	
	@Override
	public boolean execute(CommandSender sender, Command cmd, String label, String[] args) {
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
						return true;
					} catch (IOException e) {
						e.printStackTrace();
					}
				}
			}
		}
		return false;
	}
}
