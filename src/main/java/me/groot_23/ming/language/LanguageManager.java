package me.groot_23.ming.language;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import org.bukkit.ChatColor;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.craftbukkit.libs.org.apache.commons.io.FilenameUtils;
import org.bukkit.entity.Player;

import me.groot_23.ming.config.Utf8Config;

public class LanguageManager {
	
	private Map<String, Map<String, String>> langValues;
	
	private String defaultLanguage;
	private File langFolder;
	
	public LanguageManager(String defaultLanguage, File langFolder) {
		langValues = new HashMap<String, Map<String,String>>();
		this.defaultLanguage = defaultLanguage;
		this.langFolder = langFolder;
	}
	
	public void loadLanguages() {
		if(!langFolder.isDirectory()) {
			throw new IllegalArgumentException("The given file is not a directory!");
		}
		for(File f : langFolder.listFiles()) {
			Utf8Config config = getConfig(f);
			try {
				config.load(f);
				loadLang(FilenameUtils.removeExtension(f.getName()), config);
				
			} catch (FileNotFoundException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			} catch (InvalidConfigurationException e) {
				e.printStackTrace();
			}
		}
	}
	
	public void loadLang(String lang, Utf8Config cfg) {
		Map<String, String> values = new HashMap<String, String>();
		for(String key : cfg.getKeys(true)) {
			if(!cfg.isConfigurationSection(key)) {
				String val = cfg.getString(key);
				if(val != null) {
					values.put(key, val);
				}
			}
		}
		langValues.put(lang, values);
	}
	
	public String getTranslation(String language, String key) {
		String translated = null;
		Map<String, String> values = langValues.get(language);
		// check if languages is registered
		if(values != null) {
			translated = values.get(key);
		}
		// fallback to default language
		if(translated == null) {
			values = langValues.get(defaultLanguage);
			if(values != null) {
				translated = values.get(key);
			}
		}
		// use key if no value could be found
		if(translated == null) {
			translated = key;
		}
		return ChatColor.translateAlternateColorCodes('&', translated);
	}
	
	public String getDefault(String key) {
		String translated = null;
		Map<String, String> values = langValues.get(defaultLanguage);
		if(values != null) {
			translated = values.get(key);
		}
		// use key if no value could be found
		if(translated == null) {
			translated = key;
		}
		return ChatColor.translateAlternateColorCodes('&', translated);
	}
	
	public String getTranslation(Player player, String key) {
		return getTranslation(player.getLocale(), key);
	}
	
	public Utf8Config getConfig(File language) {
		Utf8Config config = new Utf8Config();
		try {
			config.load(language);
			return config;
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (InvalidConfigurationException e) {
			e.printStackTrace();
		}
		return null;
	}
	
	public Utf8Config getConfig(String language) {
		return getConfig(getFile(language));
	}
	
	public File getFile(String language) {
		return new File(langFolder, language + ".yml");
	}
	
	public File getFolder() {
		return langFolder;
	}
}
