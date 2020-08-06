package me.groot_23.ming;

import java.io.File;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import org.bukkit.plugin.java.JavaPlugin;

import me.groot_23.ming.game.MiniGameMode;
import me.groot_23.ming.gui.GuiRunnable;
import me.groot_23.ming.language.LanguageManager;
import me.groot_23.ming.provider.WorldProvider;

public abstract class MiniGame {
	
	protected Map<String, MiniGameMode> gameModes;
	
	public final WorldProvider worldProvider;
	public final JavaPlugin plugin;
	
	protected LanguageManager lang;
	
	private Map<String, GuiRunnable> guiRunnables;
	
	public MiniGame(JavaPlugin plugin) {
		this.plugin = plugin;
		worldProvider = new WorldProvider(this);
		worldProvider.cleanWorldFolders();
		init();
	}
	
	public void init() {
		lang = new LanguageManager(getDefaultLanguage());
		lang.addLanguageHolder(new File(plugin.getDataFolder().getParent(), "MinG/lang"));
		lang.addLanguageHolder(new File(plugin.getDataFolder(), "lang"));
		
		guiRunnables = new HashMap<String, GuiRunnable>();
		
		gameModes = new HashMap<String, MiniGameMode>();
		registerModes();

	}
	
	public final void registerMode(MiniGameMode mode) {
		gameModes.put(mode.getName(), mode);
	}
	public abstract void registerModes();
	
	public MiniGameMode getMode(String name) {
		return gameModes.get(name);
	}
	public Collection<MiniGameMode> getModes() {
		return gameModes.values();
	}
	public abstract MiniGameMode getDefaultMode();
	
	public final void registerGuiRunnable(String name, GuiRunnable runnable) {
		guiRunnables.put(name, runnable);
	}
	
	public JavaPlugin getPlugin() {
		return plugin;
	}
	
	public abstract String getWorldPrefix();
	
	public String getDefaultLanguage() {
		return "en_us";
	}
	
	public abstract String getName();
}
