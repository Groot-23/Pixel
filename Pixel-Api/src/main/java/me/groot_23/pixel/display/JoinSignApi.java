package me.groot_23.pixel.display;

import java.io.File;
import java.io.IOException;
import java.util.HashSet;
import java.util.Set;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.BlockState;
import org.bukkit.block.Sign;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.metadata.FixedMetadataValue;

import de.tr7zw.nbtapi.NBTItem;
import me.groot_23.pixel.Pixel;
import me.groot_23.pixel.game.Lobby;
import me.groot_23.pixel.language.LanguageApi;
import me.groot_23.pixel.language.PixelLangKeys;
import me.groot_23.pixel.util.Utf8Config;

public class JoinSignApi {

	private static Utf8Config cfg;
	private static Set<Location> locations = new HashSet<Location>();
	
	private static void saveCfg() {
		try {
			cfg.save(new File(Pixel.getPlugin().getDataFolder(), "signs.yml"));
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public static void init() {
		cfg = new Utf8Config();
		try {
			cfg.load(new File(Pixel.getPlugin().getDataFolder(), "signs.yml"));
			loadSigns();
		} catch (IOException | InvalidConfigurationException e) {
			e.printStackTrace();
		}
	}
	
	private static void loadSigns() {
		for(String key : cfg.getKeys(false)) {
			ConfigurationSection sec = cfg.getConfigurationSection(key);
			String game = sec.getString("game");
			String map = sec.getString("map");
			Location loc = sec.getLocation("location");
			createSign(game, map, loc);
		}
	}
	
	public static void join(Location loc, Player player) {
		if(loc.getBlock().getState() instanceof Sign) {
			Sign sign = (Sign)loc.getBlock().getState();
			if(!sign.hasMetadata("pixel_game") || !sign.hasMetadata("pixel_map")) return;
			
			String game = sign.getMetadata("pixel_game").get(0).asString();
			String map = sign.getMetadata("pixel_map").get(0).asString();
			Lobby lobby = Pixel.LobbyProvider.provideLobby(game, map);
			lobby.join(player);
			updateSign(sign);
		}
	}
	
	public static ItemStack createItem(Material material, String game, String map) {
		ItemStack stack = new ItemStack(material);
		ItemMeta meta = stack.getItemMeta();
		meta.setDisplayName(game + "  -  " + map);
		stack.setItemMeta(meta);
		NBTItem nbt = new NBTItem(stack);
		nbt.setString("pixel_game", game);
		nbt.setString("pixel_map", map);
		return nbt.getItem();
	}
	
	/**
	 * Note: the sign has to be placed down already. This inits the join properties
	 */
	public static boolean placeSign(ItemStack stack, Location loc) {
		NBTItem nbt = new NBTItem(stack);
		if(nbt.hasKey("pixel_game") && nbt.hasKey("pixel_map")) {			
			String game = nbt.getString("pixel_game");
			String map = nbt.getString("pixel_map");
			addSign(game, map, loc);
			return true;
		}
		return false;
	}
	
	private static void createSign(String game, String map, Location loc) {
		Sign sign = (Sign)loc.getBlock().getState();
		loc.getBlock().setMetadata("pixel_game", new FixedMetadataValue(Pixel.getPlugin(), game));
		loc.getBlock().setMetadata("pixel_map", new FixedMetadataValue(Pixel.getPlugin(), map));
		locations.add(loc);
	}
	
	public static void addSign(String game, String map, Location loc) {
		BlockState state = loc.getBlock().getState();
		if(state instanceof Sign) {
			createSign(game, map, loc);
			updateSign((Sign)state);
			// add to config
			String hash = Integer.toString(loc.hashCode());
			ConfigurationSection sec = cfg.createSection(hash);
			sec.set("game", game);
			sec.set("map", map);
			sec.set("location", loc);
			saveCfg();
		}
	}
	
	public static void removeSign(Location loc) {
		BlockState state = loc.getBlock().getState();
		if(state instanceof Sign) {
			locations.remove(loc);
			loc.getBlock().removeMetadata("pixel_game", Pixel.getPlugin());
			loc.getBlock().removeMetadata("pixel_map", Pixel.getPlugin());
			// remove from config
			String hash = Integer.toString(loc.hashCode());
			cfg.set(hash, null);
			saveCfg();
		}
	}
	
	public static void updateSigns() {
		for(Location loc : locations) {
			if(loc.getBlock().getState() instanceof Sign) {
				updateSign((Sign)loc.getBlock().getState());
			}
		}
	}
	
	private static void updateSign(Sign sign) {
		if(!sign.hasMetadata("pixel_game") || !sign.hasMetadata("pixel_map")) return;
		
		String game = sign.getMetadata("pixel_game").get(0).asString();
		String map = sign.getMetadata("pixel_map").get(0).asString();
		sign.setLine(0, LanguageApi.getDefault("sign.game." + game));
		sign.setLine(1, "");
		sign.setLine(2, LanguageApi.getDefault("sign.map." + map));
		int cnt = Pixel.LobbyProvider.getPlayerCount(game, map);
		int max = Pixel.LobbyProvider.getMaxPlayers(game, map);
		sign.setLine(3, String.format("%d/%d " + LanguageApi.getDefault(PixelLangKeys.PLAYER), cnt, max));
		sign.update(true);
	}
}
