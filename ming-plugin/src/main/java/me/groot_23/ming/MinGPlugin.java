package me.groot_23.ming;

import java.io.File;

import org.bukkit.plugin.ServicePriority;
import org.bukkit.plugin.java.JavaPlugin;

import me.groot_23.ming.gui.runnable.GuiCloseRunnable;
import me.groot_23.ming.gui.runnable.KitGuiRunnable;
import me.groot_23.ming.gui.runnable.TeamSelectorRunnable;
import me.groot_23.ming.listener.GameListener;
import me.groot_23.ming.listener.GuiListener;
import me.groot_23.ming.listener.SpectatorListener;
import me.groot_23.ming.util.ResourceExtractor;

public class MinGPlugin extends JavaPlugin {

//	private MinG ming;
	
	@Override
	public void onEnable() {
		saveDefaultConfig();
		extractResources();
		
		MinG.init(this);
//		ming = new MinG(this);
		registerListeners();
		registerGuiRunnables();
//		getServer().getServicesManager().register(MinGApi.class, ming, this, ServicePriority.Normal);
	}
	
	private void extractResources() {
		File file = getDataFolder();
		file.mkdirs();
		String path = "/resources";
		ResourceExtractor.extractResources(path, file.toPath(), false, this.getClass());
	}
	
	private void registerListeners() {
		getServer().getPluginManager().registerEvents(new GameListener(), this);
		getServer().getPluginManager().registerEvents(new GuiListener(), this);
		getServer().getPluginManager().registerEvents(new SpectatorListener(), this);
	}
	
	private void registerGuiRunnables() {
		MinG.registerGuiRunnable("ming_kit_selector", new KitGuiRunnable());
		MinG.registerGuiRunnable("ming_gui_close", new GuiCloseRunnable());
		MinG.registerGuiRunnable("ming_team_selector", new TeamSelectorRunnable());
	}
}
