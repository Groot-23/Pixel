package me.groot_23.pixel.util;

import java.util.Iterator;
import java.util.Map;

import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.PotionMeta;
import org.bukkit.inventory.meta.SkullMeta;
import org.bukkit.potion.PotionData;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.potion.PotionType;

import de.tr7zw.nbtapi.NBTContainer;
import de.tr7zw.nbtapi.NBTItem;
import me.groot_23.pixel.Pixel;
import me.groot_23.pixel.language.LanguageApi;

public class ItemSerializer {

	public static void serialize(ItemStack item, ConfigurationSection section) {
		section.set("type", item.getType().toString());
		section.set("amount", item.getAmount());

		ItemMeta imeta = item.getItemMeta();
		if (imeta.hasDisplayName()) {
			section.set("name", imeta.getDisplayName());
		}
		if(imeta.hasLore()) {
			section.set("lore", imeta.getLore());
		}

		if (!item.getEnchantments().isEmpty()) {
			ConfigurationSection enchants = section.createSection("enchants");
			for (Map.Entry<Enchantment, Integer> entry : item.getEnchantments().entrySet()) {
				enchants.set(entry.getKey().getKey().getKey().toString(), entry.getValue());
			}
		}

		if (imeta instanceof PotionMeta) {
			PotionMeta pmeta = (PotionMeta) imeta;
			String effect = pmeta.getBasePotionData().getType().name();
			section.set("effect", effect);
			section.set("effect_data.extended", pmeta.getBasePotionData().isExtended());
			section.set("effect_data.upgraded", pmeta.getBasePotionData().isUpgraded());
			if(pmeta.hasCustomEffects()) {
				ConfigurationSection custom = section.createSection("custom_effects");
				for(int i = 0; i < pmeta.getCustomEffects().size(); ++i) {
					PotionEffect e = pmeta.getCustomEffects().get(i);
					custom.set(Integer.toString(i) + ".type",      e.getType().getName());
					custom.set(Integer.toString(i) + ".duration",  e.getDuration());
					custom.set(Integer.toString(i) + ".amplifier", e.getAmplifier());
				}
			}
		}
		
		if (imeta instanceof SkullMeta) {
//			SkullMeta smeta = (SkullMeta) imeta;
			NBTItem nbt = new NBTItem(item);
			System.out.println(nbt);
			section.set("skull_nbt", nbt.getCompound("SkullOwner").toString());
//			section.set("head_owner", smeta.getOwningPlayer().getUniqueId().toString());
		}
	}

	public static String asString(ItemStack item, String language) {
		String result = LanguageApi.translateMaterial(language, item.getType());
		ItemMeta imeta = item.getItemMeta();
		if(imeta.hasDisplayName()) {
			result = imeta.getDisplayName();
		}
		if (item.getAmount() != 1) {
			result += " x" + item.getAmount();
		}
		if (!item.getEnchantments().isEmpty()) {
			result += " {";
			Iterator<Map.Entry<Enchantment, Integer>> it = item.getEnchantments().entrySet().iterator();
			while (it.hasNext()) {
				Map.Entry<Enchantment, Integer> entry = it.next();
				result += LanguageApi.translateEnchant(language, entry.getKey()) + " " + entry.getValue();
				if (it.hasNext()) {
					result += ", ";
				}
			}
			result += "}";
		}

		if (imeta instanceof PotionMeta) {
			PotionMeta pmeta = (PotionMeta) imeta;
			if(!pmeta.hasCustomEffects())
				result += " {" + pmeta.getBasePotionData().getType().name().toLowerCase() + "}";
		}

		return result;
	}

	public static ItemStack deserialize(ConfigurationSection section) {
		String materialStr = section.getString("type");
		if (materialStr == null) {
			throw new NullPointerException("Error parsing kits: " + "No given type at: " + section.getCurrentPath());
		}
		Material material = Material.matchMaterial(materialStr);
		if (material == null) {
			throw new RuntimeException("Error parsing Kits: " + "Given type '" + materialStr + "' does not exist. at:"
					+ section.getCurrentPath());
		}
		ItemStack item = new ItemStack(material);
		item.setAmount(section.getInt("amount", 1));
		ItemMeta imeta = item.getItemMeta();
		
		if(section.contains("name")) {
			imeta.setDisplayName(section.getString("name"));
		}
		if(section.contains("lore")) {
			imeta.setLore(section.getStringList("lore"));
		}

		if (section.contains("effect")) {
			if (imeta instanceof PotionMeta) {
				PotionMeta pmeta = (PotionMeta) imeta;
				String effect = section.getString("effect");
				PotionType e = PotionType.valueOf(effect.toUpperCase());
				boolean extended = section.getBoolean("effect_data.extended");
				boolean upgraded = section.getBoolean("effect_data.upgraded");
				if (e == null) {
					throw new RuntimeException("Error parsing Kits: " + "Given effect '" + effect
							+ "' does not exist. at:" + section.getCurrentPath());
				}
				pmeta.setBasePotionData(new PotionData(e, extended, upgraded));
				ConfigurationSection custom = section.getConfigurationSection("custom_effects");
				if(custom != null) {
					for(String key : custom.getKeys(false)) {
						PotionEffectType type = PotionEffectType.getByName(custom.getString(key + ".type").toUpperCase());
						int dur = custom.getInt(key + ".duration");
						int amp = custom.getInt(key + ".amplifier");
						pmeta.addCustomEffect(new PotionEffect(type, dur, amp), true);
					}
				}
			} else {
				Pixel.getPlugin().getLogger().warning("[ItemSerializer] item can't have an effect type: " + section.getCurrentPath());
			}
		}
		
		item.setItemMeta(imeta);
		
		if (section.contains("enchants")) {
			ConfigurationSection enchants = section.getConfigurationSection("enchants");
			for (String key : enchants.getKeys(false)) {
				Enchantment e = Enchantment.getByKey(NamespacedKey.minecraft(key));
				if (e == null) {
					throw new RuntimeException("Error parsing Kits: " + "Given enchant '" + key
							+ "' does not exist. at:" + enchants.getCurrentPath());
				}
				item.addUnsafeEnchantment(e, enchants.getInt(key, 1));
			}
		}
		
		if(section.contains("skull_nbt")) {
			if(imeta instanceof SkullMeta) {
				NBTItem nbt = new NBTItem(item);
				NBTContainer con = new NBTContainer("{SkullOwner:" + section.getString("skull_nbt") + "}");
				nbt.mergeCompound(con);
				item = nbt.getItem();
			} else {
				Pixel.getPlugin().getLogger().warning("[ItemSerializer] item can't have a skull owner: " + section.getCurrentPath());
			}
		}

		
		return item;
	}
}
