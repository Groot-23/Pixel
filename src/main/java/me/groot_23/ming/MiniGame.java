package me.groot_23.ming;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;

import me.groot_23.ming.config.Utf8Config;
import me.groot_23.ming.game.GameState;
import me.groot_23.ming.gui.GuiCloseRunnable;
import me.groot_23.ming.gui.GuiItem;
import me.groot_23.ming.gui.GuiRunnable;
import me.groot_23.ming.kits.Kit;
import me.groot_23.ming.kits.KitGuiRunnable;
import me.groot_23.ming.language.LanguageManager;
import me.groot_23.ming.world.Arena;
import me.groot_23.ming.world.ArenaProvider;

public abstract class MiniGame {
	
	protected JavaPlugin plugin;
	protected ArenaProvider arenaProvider;
	protected LanguageManager lang;
	
	protected Map<String, Kit> kitsByName;
	protected List<Kit> kitsByIndex;
	
	private Map<String, GuiRunnable> guiRunnables;
	
	public MiniGame(JavaPlugin plugin) {
		this.plugin = plugin;
		init();
	}
	
	public void init() {
		lang = new LanguageManager(getDefaultLanguage());
		lang.loadLanguages(new File(plugin.getDataFolder(), "lang"));
		arenaProvider = new ArenaProvider(this);
		
		guiRunnables = new HashMap<String, GuiRunnable>();
		registerGuiRunnables();
		
		MinGapi.registerMiniGame(this);
		
		kitsByName = new HashMap<String, Kit>();
		kitsByIndex = new ArrayList<Kit>();
		initKits();
	}
	
	public ArenaProvider getArenaProvider() {
		return arenaProvider;
	}
	
	public String getTranslation(String language, String key) {
		return lang.getTranslation(language, key);
	}
	public String getTranslation(Player player, String key) {
		return lang.getTranslation(player, key);
	}
	public String getDefualtTranslation(String key) {
		return lang.getDefault(key);
	}
	
	/**
	 * Register your GuiRunnables in this method. <br>
	 * Don't forget to execute super method!
	 */
	public void registerGuiRunnables() {
		registerGuiRunnable("ming_kit_selector", new KitGuiRunnable());
		registerGuiRunnable("ming_gui_close", new GuiCloseRunnable());
	}
	public final void registerGuiRunnable(String name, GuiRunnable runnable) {
		guiRunnables.put(name, runnable);
	}
	public void guiExecute(String cmd, Player player, ItemStack item, Inventory inv) {
		if(cmd.startsWith(GuiItem.GUI_RUNNABLE_PREFIX)) {
			cmd = cmd.substring(GuiItem.GUI_RUNNABLE_PREFIX.length());
			GuiRunnable runnable = guiRunnables.get(cmd);
			if(runnable == null) {
				throw new IllegalArgumentException("The guiRunnable: '" + cmd + "' is not registered for minigame: " + getName());
			} else {
				runnable.run(player, item, inv, this);
			}
		} else {
			player.performCommand(cmd);
		}
	}
	
	public GuiItem createGuiItem(Material material, String name, String... lore) {
		return new GuiItem(getName(), material, name, lore);
	}
	public GuiItem createGuiItem(Material material, String name, List<String> lore) {
		return new GuiItem(getName(), material, name, lore);
	}
	public GuiItem createGuiItem(Material material) {
		return new GuiItem(getName(), material);
	}
	
	public abstract GameState<?> getStartingState(Arena arena);
	
	public Arena createArena(World world, String map) {
		return new Arena(this, world, map);
	}
	
	public JavaPlugin getPlugin() {
		return plugin;
	}
	
	public abstract String getWorldPrefix();
	
	public String getDefaultLanguage() {
		return "en_us";
	}
	
	public abstract String getName();
	
	public void initKits() {
		Utf8Config cfg = new Utf8Config();
		try {
			cfg.load(new File(plugin.getDataFolder(), "kits.yml"));
			for(String key : cfg.getKeys(false)) {
				ConfigurationSection kitSec = cfg.getConfigurationSection(key);
				if(kitSec != null) {
					Kit kit = Kit.deserialize(this, kitSec);
					kitsByName.put(key, kit);
					kitsByIndex.add(kit);
				}
			}
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (InvalidConfigurationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}
	
	public List<Kit> getKits() {
		return kitsByIndex;
	}
	public Map<String, Kit> getKitsByName() {
		return kitsByName;
	}
	public Kit getKit(String name) {
		return kitsByName.get(name);
	}
	public Kit getDefaultKit() {
		return kitsByIndex.get(0);
	}
	public boolean kitExists(String name) {
		return kitsByName.containsKey(name);
	}
	public Kit getKit(Player player) {
		Kit kit = null;
		if(player.hasMetadata("ming_kit")) {
			kit = kitsByName.get(player.getMetadata("ming_kit").get(0).asString());
		}
		if(kit == null) {
			kit = getDefaultKit();
		}
		return kit;
	}
	public void applyKitToPlayer(Player player) {
		Kit kit = getKit(player);
		kit.applyToPlayer(player);
	}

}
