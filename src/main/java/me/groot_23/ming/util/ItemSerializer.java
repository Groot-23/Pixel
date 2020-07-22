package me.groot_23.ming.util;

import java.util.Iterator;
import java.util.Map;

import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.PotionMeta;
import org.bukkit.potion.PotionData;
import org.bukkit.potion.PotionType;

public class ItemSerializer {
	
	public static void serialize(ItemStack item, ConfigurationSection section) {
		section.set("type", item.getType().toString());
		section.set("amount", item.getAmount());
		
		if(!item.getEnchantments().isEmpty()) {
			ConfigurationSection enchants = section.createSection("enchants");
			for(Map.Entry<Enchantment, Integer> entry : item.getEnchantments().entrySet()) {
				enchants.set(entry.getKey().getKey().getKey().toString(), entry.getValue());
			}
		}
		
		if(item.getItemMeta() instanceof PotionMeta) {
			PotionMeta meta = (PotionMeta) item.getItemMeta();
			String effect = meta.getBasePotionData().getType().name();
			section.set("effect", effect);
		}
	}
	
	
	public static String asString(ItemStack item) {
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
			result += " {" + pm.getBasePotionData().getType().name().toLowerCase() + "}";
		}
		
		return result;
	}
	
	public static ItemStack deserialize(ConfigurationSection section) {
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
		
		if(section.contains("effect")) {
			if(item.getItemMeta() instanceof PotionMeta) {
				PotionMeta meta = (PotionMeta) item.getItemMeta();
				String effect = section.getString("effect");
				PotionType e = PotionType.valueOf(effect.toUpperCase());
				if(e == null) {
					throw new RuntimeException("Error parsing Kits: " + "Given effect '" + effect + "' does not exist. at:" + section.getCurrentPath());
				}
				meta.setBasePotionData(new PotionData(e));
				item.setItemMeta(meta);
			} else {
				System.out.println("[MinG] [WARNING] item can't have an effect type: " + section.getCurrentPath());
			}
		}

		return item;
	}
}
