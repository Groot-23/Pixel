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
		String result = item.getType().getKey().getKey();
		if(item.getAmount() != 1) {
			result += " x" + item.getAmount();
		}
		if(!item.getEnchantments().isEmpty()) {
			result += " {";
			Iterator<Map.Entry<Enchantment, Integer>> it = item.getEnchantments().entrySet().iterator();
			while(it.hasNext()) {
				Map.Entry<Enchantment, Integer> entry = it.next();
				result += entry.getKey().getKey().getKey() + " " + entry.getValue();
				if(it.hasNext()) {
					result += ", ";
				}
			}
			result += "}";
		}
		
		ItemMeta meta = item.getItemMeta();
		if(meta instanceof PotionMeta) {
			PotionMeta pm = (PotionMeta) meta;
			result += " {" + pm.getBasePotionData().getType().getEffectType().getName() + "}";
		}
		
		return result;
	}
	
	public static KitItem deserialize(ConfigurationSection section) {
		int slot = section.getInt("slot", -1);
		String materialStr = section.getString("type");
		if(materialStr == null) {
			throw new NullPointerException("Error parsing kits: " + "No given type at: " + section.getCurrentPath());
		}
		Material material = Material.matchMaterial(materialStr);
		if(material == null) {
			throw new RuntimeException("Error parsing Kits: " + "Given type '" + materialStr + "' does not exist. at:" + section.getCurrentPath());
		}
		ItemStack item = new ItemStack(material);
		item.setAmount(section.getInt("amount", 1));
		
		if(section.contains("enchants")) {
			ConfigurationSection enchants = section.getConfigurationSection("enchants");
			for(String key : enchants.getKeys(false)) {
				Enchantment e = Enchantment.getByKey(NamespacedKey.minecraft(key));
				if(e == null) {
					throw new RuntimeException("Error parsing Kits: " + "Given enchant '" + key + "' does not exist. at:" + enchants.getCurrentPath());
				}
				item.addUnsafeEnchantment(e, enchants.getInt(key, 1));
			}
		}
		
		if(section.contains("effects")) {
			if(item.getItemMeta() instanceof PotionMeta) {
				PotionMeta meta = (PotionMeta) item.getItemMeta();
				ConfigurationSection effects = section.getConfigurationSection("effects");
				for(String key : effects.getKeys(false)) {
					PotionType e = PotionType.valueOf(key.toUpperCase());
					if(e == null) {
						throw new RuntimeException("Error parsing Kits: " + "Given effect '" + key + "' does not exist. at:" + effects.getCurrentPath());
					}
					meta.setBasePotionData(new PotionData(e));
				}
			} else {
				System.out.println("[MinG] [WARNING] item can't have an effect type: " + section.getCurrentPath());
			}
		}

		return new KitItem(item, slot);
	}
}
