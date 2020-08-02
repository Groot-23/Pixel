package me.groot_23.ming.kits;

import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import me.groot_23.ming.language.LanguageManager;
import me.groot_23.ming.util.ItemSerializer;

public class KitItem {
	
	private ItemStack item;
	private int slot;
	
	public KitItem(ItemStack item, int slot) {
		this.item = item;
		this.slot = slot;
	}
	
	public KitItem(ItemStack item) {
		this(item, -1);
	}
	
	public void addToInventory(Inventory inv) {
		if(slot == -1) {
			inv.addItem(item);
		} else {
			inv.setItem(slot, item);
		}
	}
	
	public ItemStack getItem() {
		return item;
	}
	public int getSlot() {
		return slot;
	}
	
	public String asString(LanguageManager manager, String lang) {
		return ItemSerializer.asString(item, manager, lang);
	}
	
	public static KitItem deserialize(ConfigurationSection section) {
		int slot = section.getInt("slot", -1);
		ItemStack item = ItemSerializer.deserialize(section);
		return new KitItem(item, slot);
	}
}
