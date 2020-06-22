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


public class Kit {
	
	private MiniGame game;
	
	private List<KitItem> items;
	private String name;
	private Material material;
	
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
	
	public ItemStack getDisplayItem(Player player) {
		
		String displayName = getDisplayName(player);
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
			lore.add(ChatColor.RESET + " - " + item.toString());
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
}
