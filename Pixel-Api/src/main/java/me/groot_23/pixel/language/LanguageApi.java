package me.groot_23.pixel.language;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import org.bukkit.DyeColor;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;

public class LanguageApi {
	
	private static List<LanguageFolder> langFolders = new ArrayList<LanguageFolder>();
	public static String defaultLanguage;
	
	public static void addLanguageFolder(LanguageFolder folder) {
		langFolders.add(folder);
	}
	public static LanguageFolder addLanguageFolder(File folder) {
		LanguageFolder langHolder = new LanguageFolder(defaultLanguage, folder);
		langFolders.add(langHolder);
		return langHolder;
	}
	
	/**
	 * 
	 * @param index Use 1 to get your LangHolder and 0 to get MinG's (if you don't change it in
	 *  {@link me.groot_23.ming.MiniGame#init() Minigame#init})
	 * @return
	 */
	public static LanguageFolder getLanguageHolder(int index) {
		return langFolders.get(index);
	}
	
	public static String getTranslation(String language, String key, String fallback) {
		for(int i = langFolders.size() - 1; i >= 0; --i) {
			String translation = langFolders.get(i).getTranslation(language, key, null);
			if(translation != null) return translation;
		}
		return fallback;
	}
	public static String getTranslation(String language, String key) {
		return getTranslation(language, key, key);
	}
	public static String getTranslation(Player player, String key, String fallback) {
		return getTranslation(player.getLocale(), key, fallback);
	}
	public static String getTranslation(Player player, String key) {
		return getTranslation(player, key, key);
	}
	
	public static String getDefault(String key, String fallback) {
		for(int i = langFolders.size() - 1; i >= 0; --i) {
			String translation = langFolders.get(i).getDefault(key, null);
			if(translation != null) return translation;
		}
		return fallback;
	}
	public static String getDefault(String key) {
		return getDefault(key, key);
	}
	
	public static String translateMaterial(String language, Material material) {
		return getTranslation(language, "material." + material.name().toLowerCase());
	}
	public static String translateMaterial(Player player, Material material) {
		return getTranslation(player, "material." + material.name().toLowerCase());
	}
	public static String translateMaterialDefault(Material material) {
		return getDefault("material." + material.name().toLowerCase());
	}
	
	public static String translateEnchant(String language, Enchantment enchant) {
		return getTranslation(language, "enchant." + enchant.getKey().getKey());
	}
	public static String translateEnchant(Player player, Enchantment enchant) {
		return getTranslation(player, "enchant." + enchant.getKey().getKey());
	}
	public static String translateEnchantDefault(Enchantment enchant) {
		return getDefault("enchant." + enchant.getKey().getKey());
	}
	
	public static String translateColor(String language, DyeColor color) {
		return getTranslation(language, "color." + color.name().toLowerCase(), color.name().toLowerCase());
	}
	public static String translateColor(Player player, DyeColor color) {
		return getTranslation(player, "color." + color.name().toLowerCase(), color.name().toLowerCase());
	}
	public static String translateColorDefault(DyeColor color) {
		return getDefault("color." + color.name().toLowerCase(), color.name().toLowerCase());
	}
}
