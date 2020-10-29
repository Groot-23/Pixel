package me.groot_23.pixel.kits;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;

import me.groot_23.pixel.gui.GuiItem;
import me.groot_23.pixel.gui.PixelGuiRunnables;
import me.groot_23.pixel.language.LanguageApi;
import me.groot_23.pixel.language.PixelLangKeys;
import me.groot_23.pixel.player.DataManager;
import me.groot_23.pixel.util.Utf8Config;

public class KitApi {
	
	private static Map<String, List<Kit>> kits = new HashMap<String, List<Kit>>();
	private static Map<UUID, Map<String, String>> selectedKits = new HashMap<UUID, Map<String,String>>();
	private static Map<String, String> kitUnlockId = new HashMap<String, String>();
	
	public static void registerKit(Kit kit, String kitGroup) {
		List<Kit> list = kits.get(kitGroup);
		if(list == null) {
			list = new ArrayList<Kit>();
			kits.put(kitGroup, list);
		}
		list.add(kit);
	}
	public static Kit getKit(String group, String name) {
		List<Kit> list = kits.get(group);
		if(list != null) {
			for(Kit kit : list) {
				if(kit.getName().equals(name)) {
					return kit;
				}
			}
		}
		return null;
	}
	public static String getSelectedKitName(Player player, String group) {
		Map<String, String> playerSection = selectedKits.get(player.getUniqueId());
		if(playerSection != null) {
			String name = playerSection.get(group);
			if(name != null) {
				return name;
			}
		}
		return null;
	}
	public static Kit getSelectedKit(Player player, String group) {
		String name = getSelectedKitName(player, group);
		return name != null ? getKit(group, name) : null;
	}
	public static void setSelectedKit(Player player, String group, String kit) {
		Map<String, String> playerSection = selectedKits.get(player.getUniqueId());
		if(playerSection == null) {
			playerSection = new HashMap<String, String>();
			selectedKits.put(player.getUniqueId(), playerSection);
		}
		playerSection.put(group, kit);
	}
	public static void loadKits(File kitFile, String kitGroup) {
		Utf8Config cfg = new Utf8Config();
		try {
			cfg.load(kitFile);
			for(String key : cfg.getKeys(false)) {
				ConfigurationSection kitSec = cfg.getConfigurationSection(key);
				if(kitSec != null) {
					Kit.loadKit(kitGroup, kitSec);
				}
			}
		} catch (IOException | InvalidConfigurationException e) {
			e.printStackTrace();
		}
	}
	public static List<Kit> getKits(String group) {
		return kits.get(group);
	}
	
	public static void setUnlockedDataId(String group, String id) {
		kitUnlockId.put(group, id);
	}
	public static boolean isUnlocked(String group, String kit, Player player) {
		String id = kitUnlockId.get(group);
		if(id == null) return true;
		ConfigurationSection sec = DataManager.getData(player, id);
		return sec.getBoolean(group + "." + kit, false);
	}
	public static void setUnlocked(String group, String kit, Player player, boolean val) {
		String id = kitUnlockId.get(group);
		if(id != null) {
			ConfigurationSection sec = DataManager.getData(player, id);
			sec.set(group + "." + kit, val);
			DataManager.saveData(id);
		}
	}
	
	public static Set<String> getGroups() {
		return kits.keySet();
	}
	
	public static void openGui(Player player, String kitGroup) {
		Inventory inv = Bukkit.createInventory(player, 45,
				LanguageApi.getTranslation(player, PixelLangKeys.KIT_SELECTOR));

		for (int y = 0; y <= 4; y += 4) {
			for (int x = 0; x < 9; x++) {
				inv.setItem(9 * y + x, new GuiItem(Material.BLACK_STAINED_GLASS_PANE, " ").getItem());
			}
		}
		for (int y = 1; y < 4; y++) {
			for (int x = 0; x <= 8; x += 8) {
				inv.setItem(9 * y + x, new GuiItem(Material.WHITE_STAINED_GLASS_PANE, " ").getItem());
			}
		}

		GuiItem leaveItem = new GuiItem(Material.BARRIER,  LanguageApi.getTranslation(player, PixelLangKeys.KIT_EXIT));
		leaveItem.addActionClickRunnable(PixelGuiRunnables.GUI_CLOSE);
		inv.setItem(9 * 4 + 4, leaveItem.getItem());

		int i = 0;
		for (int y = 1; y < 4; y++) {
			for (int x = 1; x < 8; x++) {
				
				List<Kit> kits = KitApi.getKits(kitGroup);
				if(i < kits.size()) {
					if(KitApi.isUnlocked(kitGroup, kits.get(i).getName(), player)) {			
						inv.setItem(9 * y + x, kits.get(i).getDisplayItem(player));
					}
					else {
						inv.setItem(9 * y + x, new GuiItem(Material.GRAY_DYE, LanguageApi.
								getTranslation(player, PixelLangKeys.KIT_LOCKED)).getItem());
					}
					i++;
				} else
					break;
			}
		}
		
		player.openInventory(inv);
	}
}
