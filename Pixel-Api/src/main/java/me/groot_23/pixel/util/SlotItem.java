package me.groot_23.pixel.util;

import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import me.groot_23.pixel.language.LanguageApi;

public class SlotItem {
	
	private ItemStack item;
	private int slot;
	
	public SlotItem(ItemStack item, int slot) {
		this.item = item;
		this.slot = slot;
	}
	
	public SlotItem(ItemStack item) {
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
	
	public String asString(String lang) {
		return ItemSerializer.asString(item, lang);
	}
	
	public static SlotItem deserialize(ConfigurationSection section) {
		int slot = section.getInt("slot", -1);
		ItemStack item = ItemSerializer.deserialize(section);
		return new SlotItem(item, slot);
	}
}
