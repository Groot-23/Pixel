package me.groot_23.ming.kits;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import de.tr7zw.changeme.nbtapi.NBTItem;
import me.groot_23.ming.MiniGame;
import me.groot_23.ming.gui.GuiItem;
import me.groot_23.ming.util.ItemSerializer;


public class Kit {
	
	private MiniGame game;
	
	private List<KitItem> items;
	private String name;
	private Material material;
	
	public static String getSelectedSuffix(Player player, MiniGame game) {
		return ChatColor.RESET + "    " + ChatColor.GRAY + "(" + ChatColor.GREEN
				+ game.getTranslation(player, "kit.selected") + ChatColor.GRAY + ")";
	}
	
	public Kit(MiniGame game, String name, Material material, List<KitItem> items) {
		this.game = game;
		this.name = name;
		this.material = material;
		this.items = items;
	}
	
	public String getName() {
		return name;
	}
	
	public String getDisplayName(Player player) {

		return game.getTranslation(player, "kits." + name + ".name");
	}
	
	public String getSuffixedDisplayName(Player player) {
		String selected = game.getKit(player).getName();
		String suffix = selected.equals(name) ? getSelectedSuffix(player, game) : "";
		return getDisplayName(player) + suffix;
	}
	
	public ItemStack getDisplayItem(Player player) {
		
		String displayName = getSuffixedDisplayName(player);
		List<String> lore = initLore(player);
		GuiItem item = game.createGuiItem(material, displayName, lore);
		item.addActionClickRunnable("ming_kit_selector");
		
		NBTItem nbt = new NBTItem(item.getItem());
		nbt.setString("ming_kit", name);
		return nbt.getItem();
	}
	
	private List<String> initLore(Player player) {
		List<String> lore = new ArrayList<String>();
		String description = game.getTranslation(player, "kits." + name + ".description");
		String[] descriptions = description.split("\\n");
		lore.add(ChatColor.YELLOW + game.getTranslation(player, "kit.description") + ":");
		for(String s : descriptions) {
			lore.add(ChatColor.RESET + " - " + s);
		}
		lore.add(ChatColor.YELLOW + game.getTranslation(player, "kit.items"));
		for(KitItem item : items) {
			lore.add(ChatColor.RESET + " - " + item.asString(game.getLangManager(), player.getLocale()));
		}
		return lore;
	}

	public void applyToPlayer(Player player) {
		for(KitItem ki : items) {
			ki.addToInventory(player.getInventory());
		}
	}
	
	public static Kit deserialize(MiniGame game, ConfigurationSection section) {
		String materialStr = section.getString("material");
		if(materialStr == null) {
			throw new NullPointerException("Error parsing kits: " + "No given material at: " + section.getCurrentPath());
		}
		Material material = Material.matchMaterial(materialStr);
		if(material == null) {
			throw new RuntimeException("Error parsing Kits: " + "Given material '" + materialStr + "' does not exist. at:" + section.getCurrentPath());
		}
		
		List<KitItem> kitItems = new ArrayList<KitItem>();
		ConfigurationSection items = section.getConfigurationSection("items");
		if(items != null) {
			for(String key : items.getKeys(false)) {
				ConfigurationSection item = items.getConfigurationSection(key);
				if(item != null) {
					kitItems.add(KitItem.deserialize(item));
				}
			}
		}
		
		return new Kit(game, section.getName(), material, kitItems);
		
	}
	
	public static void serializeInventory(Player player, ConfigurationSection section) {
		for(int i = 0; i < player.getInventory().getSize(); i++) {
			ItemStack item = player.getInventory().getItem(i);
			if(item != null) {
				ConfigurationSection itemSec = section.createSection("item_" + i);
				itemSec.set("slot", i);
				ItemSerializer.serialize(item, itemSec);
			}
		}
	}
}
