package me.groot_23.ming.language;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;

public class LanguageManager {
	
	private List<LanguageHolder> langHolders;
	private String defaultLanguage;
	
	public LanguageManager(String defaultLanguage) {
		this.defaultLanguage = defaultLanguage;
		this.langHolders = new ArrayList<LanguageHolder>();
	}
	
	public void addLanguageHolder(LanguageHolder holder) {
		langHolders.add(holder);
	}
	public void addLanguageHolder(File languageFolder) {
		langHolders.add(new LanguageHolder(defaultLanguage, languageFolder));
	}
	
	/**
	 * 
	 * @param index Use 1 to get your LangHolder and 0 to get MinG's (if you don't change it in
	 *  {@link me.groot_23.ming.MiniGame#init() Minigame#init})
	 * @return
	 */
	public LanguageHolder getLanguageHolder(int index) {
		return langHolders.get(index);
	}
	
	public String getTranslation(String language, String key, String fallback) {
		for(int i = langHolders.size() - 1; i >= 0; --i) {
			String translation = langHolders.get(i).getTranslation(language, key, null);
			if(translation != null) return translation;
		}
		return fallback;
	}
	public String getTranslation(String language, String key) {
		return getTranslation(language, key, key);
	}
	public String getTranslation(Player player, String key, String fallback) {
		return getTranslation(player.getLocale(), key, fallback);
	}
	public String getTranslation(Player player, String key) {
		return getTranslation(player, key, key);
	}
	
	public String getDefault(String key, String fallback) {
		for(int i = langHolders.size() - 1; i >= 0; --i) {
			String translation = langHolders.get(i).getDefault(key, null);
			if(translation != null) return translation;
		}
		return fallback;
	}
	public String getDefault(String key) {
		return getDefault(key, key);
	}
	
	public String translateMaterial(String language, Material material) {
		return getTranslation(language, "material." + material.name().toLowerCase());
	}
	public String translateMaterial(Player player, Material material) {
		return getTranslation(player, "material." + material.name().toLowerCase());
	}
	public String translateMaterialDefault(Material material) {
		return getDefault("material." + material.name().toLowerCase());
	}
	
	public String translateEnchant(String language, Enchantment enchant) {
		return getTranslation(language, "enchant." + enchant.getKey().getKey());
	}
	public String translateEnchant(Player player, Enchantment enchant) {
		return getTranslation(player, "enchant." + enchant.getKey().getKey());
	}
	public String translateEnchantDefault(Enchantment enchant) {
		return getDefault("enchant." + enchant.getKey().getKey());
	}
}
