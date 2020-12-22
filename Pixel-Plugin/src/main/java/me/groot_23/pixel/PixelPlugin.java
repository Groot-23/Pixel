package me.groot_23.pixel;

import java.io.File;

import org.bukkit.plugin.java.JavaPlugin;

import me.groot_23.pixel.commands.DataCommand;
import me.groot_23.pixel.commands.JoinCommand;
import me.groot_23.pixel.commands.KitUnlockCommand;
import me.groot_23.pixel.commands.SignCommand;
import me.groot_23.pixel.commands.ToggleSpectator;
import me.groot_23.pixel.listener.GameListener;
import me.groot_23.pixel.listener.GuiListener;
import me.groot_23.pixel.listener.SignListener;
import me.groot_23.pixel.listener.SpectatorListener;
import me.groot_23.pixel.util.ResourceExtractor;

public class PixelPlugin extends JavaPlugin {
	
	@Override
	public void onEnable() {
		saveDefaultConfig();
		extractResources();
		
		Pixel.init(this);
		registerListeners();
		registerCommands();
	}
	
	private void extractResources() {
		File file = getDataFolder();
		file.mkdirs();
		String path = "resources";
		ResourceExtractor.extractResources(path, file.toPath(), false, this.getClass());
	}
	
	private void registerListeners() {
		getServer().getPluginManager().registerEvents(new GameListener(), this);
		getServer().getPluginManager().registerEvents(new GuiListener(), this);
		getServer().getPluginManager().registerEvents(new SpectatorListener(), this);
		getServer().getPluginManager().registerEvents(new SignListener(), this);
	}
	
	private void registerCommands() {
		getCommand("toggle_spectator").setExecutor(new ToggleSpectator());
		getCommand("toggle_spectator").setPermission("pixel.toggle_spectator");
		getCommand("pjoin").setExecutor(new JoinCommand());
		getCommand("pjoin").setTabCompleter(new JoinCommand());
		getCommand("pdata").setExecutor(new DataCommand());
		getCommand("pdata").setTabCompleter(new DataCommand());
		getCommand("pdata").setPermission("pixel.data");
		getCommand("pkit_unlock").setExecutor(new KitUnlockCommand());
		getCommand("pkit_unlock").setTabCompleter(new KitUnlockCommand());
		getCommand("pkit_unlock").setPermission("pixel.pkit_unlock");
		
		new SignCommand(this, "psign", "pixel.psign");
	}
}
