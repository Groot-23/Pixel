package me.groot_23.pixel;

import java.io.File;

import org.bukkit.plugin.java.JavaPlugin;

import me.groot_23.pixel.Pixel;
import me.groot_23.pixel.commands.DataCommand;
import me.groot_23.pixel.commands.JoinCommand;
import me.groot_23.pixel.commands.KitUnlockCommand;
import me.groot_23.pixel.commands.ToggleSpectator;
import me.groot_23.pixel.gui.PixelGuiRunnables;
import me.groot_23.pixel.gui.runnable.GuiCloseRunnable;
import me.groot_23.pixel.gui.runnable.KitGuiRunnable;
import me.groot_23.pixel.gui.runnable.SpectatorTpRunnable;
import me.groot_23.pixel.gui.runnable.TeamSelectorRunnable;
import me.groot_23.pixel.gui.runnable.TpToPlayerRunnable;
import me.groot_23.pixel.listener.GameListener;
import me.groot_23.pixel.listener.GuiListener;
import me.groot_23.pixel.listener.SpectatorListener;
import me.groot_23.pixel.util.ResourceExtractor;

public class PixelPlugin extends JavaPlugin {

//	private MinG ming;
	
	@Override
	public void onEnable() {
		saveDefaultConfig();
		extractResources();
		
		Pixel.init(this);
//		ming = new MinG(this);
		registerListeners();
		registerGuiRunnables();
		registerCommands();
//		getServer().getServicesManager().register(MinGApi.class, ming, this, ServicePriority.Normal);
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
	}
	
	private void registerGuiRunnables() {
		Pixel.registerGuiRunnable(PixelGuiRunnables.KIT_SELECTOR, 	new KitGuiRunnable());
		Pixel.registerGuiRunnable(PixelGuiRunnables.GUI_CLOSE, 		new GuiCloseRunnable());
		Pixel.registerGuiRunnable(PixelGuiRunnables.TEAM_SELECTOR, 	new TeamSelectorRunnable());
		Pixel.registerGuiRunnable(PixelGuiRunnables.TP_TO_PLAYER, 	new TpToPlayerRunnable());
		Pixel.registerGuiRunnable(PixelGuiRunnables.SPECTATOR_TP,	 	new SpectatorTpRunnable());
	}
	
	private void registerCommands() {
		getCommand("toggle_spectator").setExecutor(new ToggleSpectator());
		getCommand("toggle_spectator").setPermission("ming.toggle_spectator");
		getCommand("mjoin").setExecutor(new JoinCommand());
		getCommand("mjoin").setTabCompleter(new JoinCommand());
		getCommand("mdata").setExecutor(new DataCommand());
		getCommand("mdata").setTabCompleter(new DataCommand());
		getCommand("mdata").setPermission("ming.data");
		getCommand("mkit_unlock").setExecutor(new KitUnlockCommand());
		getCommand("mkit_unlock").setTabCompleter(new KitUnlockCommand());
		getCommand("mkit_unlock").setPermission("ming.mkit_unlock");
	}
}
