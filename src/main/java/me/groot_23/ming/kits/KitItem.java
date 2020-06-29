package me.groot_23.ming.kits;

import java.util.Iterator;
import java.util.Map;


import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.PotionMeta;
import org.bukkit.potion.PotionData;
import org.bukkit.potion.PotionType;

import me.groot_23.ming.config.ItemSerializer;

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
	
	@Override
	public String toString() {
		return ItemSerializer.asString(item);
	}
	
	public static KitItem deserialize(ConfigurationSection section) {
		int slot = section.getInt("slot", -1);
		ItemStack item = ItemSerializer.deserialize(section);
		return new KitItem(item, slot);
	}
}
