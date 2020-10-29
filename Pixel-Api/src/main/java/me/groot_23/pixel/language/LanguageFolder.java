package me.groot_23.pixel.language;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import org.bukkit.ChatColor;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.craftbukkit.libs.org.apache.commons.io.FilenameUtils;
import org.bukkit.entity.Player;

import me.groot_23.pixel.util.Utf8Config;

public class LanguageFolder {
	private Map<String, Utf8Config> languages;
	private String defaultLanguage;
	private File langFolder;
	
	public LanguageFolder(String defaultLanguage, File langFolder) {
		this.defaultLanguage = defaultLanguage;
		this.langFolder = langFolder;
		this.languages = new HashMap<String, Utf8Config>();
		
		loadLanguages();
	}
	
	private void loadLanguages() {
		if(!langFolder.isDirectory()) {
			throw new IllegalArgumentException("The given file is not a directory!");
		}
		for(File f : langFolder.listFiles()) {
			if(f.getName().endsWith(".yml")) {
				Utf8Config cfg = new Utf8Config();
				try {
					cfg.load(f);
					languages.put(FilenameUtils.removeExtension(f.getName()), cfg);
					
				} catch (FileNotFoundException e) {
					e.printStackTrace();
				} catch (IOException e) {
					e.printStackTrace();
				} catch (InvalidConfigurationException e) {
					e.printStackTrace();
				}
			}
		}
	}
	
	
	public String getTranslation(String language, String key, String fallback) {
		String translated = null;
		ConfigurationSection section = languages.get(language);
		// check if languages is registered
		if(section != null) {
			translated = section.getString(key);
		}
		// fallback to default language
		if(translated == null) {
			section = languages.get(defaultLanguage);
			if(section != null) {
				translated = section.getString(key);
			}
		}
		// use fallback values if no value could be found
		if(translated == null) {
			translated = fallback;
		}
		if(translated != null)
			return ChatColor.translateAlternateColorCodes('&', translated);
		else
			return null;
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
		String translated = null;
		ConfigurationSection section = languages.get(defaultLanguage);
		section = languages.get(defaultLanguage);
		if(section != null) {
			translated = section.getString(key);
		}
		// use fallback values if no value could be found
		if(translated == null) {
			translated = fallback;
		}
		if(translated != null)
			return ChatColor.translateAlternateColorCodes('&', translated);
		else
			return null;
	}
	public String getDefault(String key) {
		return getDefault(key, key);
	}
	
	
	public Utf8Config getConfig(String language) {
		return languages.get(language);
	}
	
	public void saveConfig(Utf8Config config, String language) {
		try {
			config.save(getFile(language));
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public File getFile(String language) {
		return new File(langFolder, language + ".yml");
	}
	
	public File getFolder() {
		return langFolder;
	}
}
