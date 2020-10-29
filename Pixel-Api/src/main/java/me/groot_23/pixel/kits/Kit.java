package me.groot_23.pixel.kits;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import de.tr7zw.nbtapi.NBTItem;
import me.groot_23.pixel.Pixel;
import me.groot_23.pixel.gui.GuiItem;
import me.groot_23.pixel.gui.PixelGuiRunnables;
import me.groot_23.pixel.language.LanguageApi;
import me.groot_23.pixel.util.ItemSerializer;
import me.groot_23.pixel.util.SlotItem;


public class Kit {
	
	private List<SlotItem> items;
	private String group;
	private String name;
	private Material material;
	
	public static String getSelectedSuffix(Player player) {
		return ChatColor.RESET + "    " + ChatColor.GRAY + "(" + ChatColor.GREEN
				+ LanguageApi.getTranslation(player, "kit.selected") + ChatColor.GRAY + ")";
	}
	
	public Kit(String group, String name, Material material, List<SlotItem> items) {
		this.group = group;
		this.name = name;
		this.material = material;
		this.items = items;
	}
	
	public String getName() {
		return name;
	}
	
	public String getDisplayName(Player player) {
		return LanguageApi.getTranslation(player, "kits." + name + ".name");
	}
	
	public String getSuffixedDisplayName(Player player) {
		String selected = KitApi.getSelectedKitName(player, group);
		if(selected == null) {
			selected = KitApi.getKits(group).get(0).name;
		}
		String suffix = selected.equals(name) ? getSelectedSuffix(player) : "";
		return getDisplayName(player) + suffix;
	}
	
	public ItemStack getDisplayItem(Player player) {
		
		String displayName = getSuffixedDisplayName(player);
		List<String> lore = initLore(player);
		GuiItem item = new GuiItem(material, displayName, lore);
		item.addActionClickRunnable(PixelGuiRunnables.KIT_SELECTOR);
		
		NBTItem nbt = new NBTItem(item.getItem());
		nbt.setString("ming_kit", name);
		nbt.setString("ming_kit_group", group);
		return nbt.getItem();
	}
	
	private List<String> initLore(Player player) {
		List<String> lore = new ArrayList<String>();
		String description = LanguageApi.getTranslation(player, "kits." + name + ".description");
		String[] descriptions = description.split("\\n");
		lore.add(ChatColor.YELLOW + LanguageApi.getTranslation(player, "kit.description") + ":");
		for(String s : descriptions) {
			lore.add(ChatColor.RESET + " - " + s);
		}
		lore.add(ChatColor.YELLOW + LanguageApi.getTranslation(player, "kit.items"));
		for(SlotItem item : items) {
			lore.add(ChatColor.RESET + " - " + item.asString(player.getLocale()));
		}
		return lore;
	}

	public void applyToPlayer(Player player) {
		for(SlotItem ki : items) {
			ki.addToInventory(player.getInventory());
		}
	}
	
	public static Kit loadKit(String group, ConfigurationSection section) {
		String materialStr = section.getString("material");
		if(materialStr == null) {
			throw new NullPointerException("Error parsing kits: " + "No given material at: " + section.getCurrentPath());
		}
		Material material = Material.matchMaterial(materialStr);
		if(material == null) {
			throw new RuntimeException("Error parsing Kits: " + "Given material '" + materialStr + "' does not exist. at:" + section.getCurrentPath());
		}
		
		List<SlotItem> kitItems = new ArrayList<SlotItem>();
		ConfigurationSection items = section.getConfigurationSection("items");
		if(items != null) {
			for(String key : items.getKeys(false)) {
				ConfigurationSection item = items.getConfigurationSection(key);
				if(item != null) {
					kitItems.add(SlotItem.deserialize(item));
				}
			}
		}
		
		Kit kit = new Kit(group, section.getName(), material, kitItems);
		KitApi.registerKit(kit, group);
		return kit;
		
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
