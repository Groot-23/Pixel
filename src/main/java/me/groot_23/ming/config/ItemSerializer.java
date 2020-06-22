package me.groot_23.ming.config;

import java.util.Iterator;
import java.util.Map;

import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemStack;

import de.tr7zw.changeme.nbtapi.NBTItem;

public class ItemSerializer {
	
	public static void serialize(ItemStack item, ConfigurationSection section) {
		section.set("type", item.getType());
		section.set("amount", item.getAmount());
		
		if(!item.getEnchantments().isEmpty()) {
			ConfigurationSection enchants = section.createSection("enchantments");
			for(Map.Entry<Enchantment, Integer> entry : item.getEnchantments().entrySet()) {
				enchants.set(entry.getKey().toString().toLowerCase(), entry.getValue());
			}
		}
	}
	
	public static String asString(ItemStack item) {
		String result = item.getType().toString().toLowerCase();
		if(item.getAmount() != 1) {
			result += " x" + item.getAmount();
		}
		if(!item.getEnchantments().isEmpty()) {
			result += " {";
			Iterator<Map.Entry<Enchantment, Integer>> it = item.getEnchantments().entrySet().iterator();
			while(it.hasNext()) {
				Map.Entry<Enchantment, Integer> entry = it.next();
				result += entry.getKey().getKey() + " " + entry.getValue();
				if(it.hasNext()) {
					result += ", ";
				}
			}
			result += "}";
		}
		
		System.out.println(result);
		
//		NBTItem nbt = new NBTItem(item);
//		result += nbt.toString();
//		System.out.println(nbt.toString());
		return result;
	}
}
