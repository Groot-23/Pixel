package me.groot_23.pixel.pixelconomy;

import java.io.File;
import java.io.IOException;
import java.util.List;

import org.bukkit.OfflinePlayer;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

import net.milkbowl.vault.economy.Economy;
import net.milkbowl.vault.economy.EconomyResponse;
import net.milkbowl.vault.economy.EconomyResponse.ResponseType;

public class Pixelconomy implements Economy{

	private JavaPlugin plugin;
	private File file;
	private YamlConfiguration data;
	
	public Pixelconomy(JavaPlugin plugin) {
		this.plugin = plugin;
		file = new File(plugin.getDataFolder(), "data.yml");
		data = new YamlConfiguration();
		try {
			data.load(file);
		} catch (IOException | InvalidConfigurationException e) {
			e.printStackTrace();
		}
	}
	
	private void saveData() {
		try {
			data.save(file);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public EconomyResponse setBalance(OfflinePlayer player, double amount) {
		double balance = getBalance(player);
		if(amount < 0) {
			return new EconomyResponse(balance, balance, ResponseType.FAILURE, "amount must not be negative!");
		} else { 
			double newBalance = amount;
			data.set(player.getUniqueId().toString(), newBalance);
			saveData();
			return new EconomyResponse(balance, newBalance, ResponseType.SUCCESS, null);
		}
	}

	@Override
	public boolean isEnabled() {
		return plugin.isEnabled();
	}

	@Override
	public String getName() {
		return plugin.getName();
	}

	@Override
	public boolean hasBankSupport() {
		return false;
	}

	@Override
	public int fractionalDigits() {
		return 0;
	}

	@Override
	public String format(double amount) {
		if(amount > 1)
			return Long.toString((long)amount) + " " + currencyNamePlural();
		else
			return Long.toString((long)amount) + " " + currencyNameSingular();
	}

	@Override
	public String currencyNamePlural() {
		return plugin.getConfig().getString("currencyNamePlural", "currencyNamePlural");
	}

	@Override
	public String currencyNameSingular() {
		return plugin.getConfig().getString("currencyNameSingular", "currencyNameSingular");
	}

	@Override 
	@Deprecated
	public boolean hasAccount(String playerName) {
		return false;
	}

	@Override
	public boolean hasAccount(OfflinePlayer player) {
		return data.contains(player.getUniqueId().toString());
	}

	@Override
	@Deprecated
	public boolean hasAccount(String playerName, String worldName) {
		return false;
	}

	@Override
	public boolean hasAccount(OfflinePlayer player, String worldName) {
		return hasAccount(player);
	}

	@Override
	@Deprecated
	public double getBalance(String playerName) {
		return 0;
	}

	@Override
	public double getBalance(OfflinePlayer player) {
		return data.getDouble(player.getUniqueId().toString(), 0);
	}

	@Override
	@Deprecated
	public double getBalance(String playerName, String world) {
		return 0;
	}

	@Override
	public double getBalance(OfflinePlayer player, String world) {
		return getBalance(player);
	}

	@Override
	@Deprecated
	public boolean has(String playerName, double amount) {
		return false;
	}

	@Override
	public boolean has(OfflinePlayer player, double amount) {
		return getBalance(player) >= amount;
	}

	@Override
	@Deprecated
	public boolean has(String playerName, String worldName, double amount) {
		return false;
	}

	@Override
	public boolean has(OfflinePlayer player, String worldName, double amount) {
		return getBalance(player, worldName) >= amount;
	}

	@Override
	@Deprecated
	public EconomyResponse withdrawPlayer(String playerName, double amount) {
		return null;
	}

	@Override
	public EconomyResponse withdrawPlayer(OfflinePlayer player, double amount) {
		double balance = getBalance(player);
		if(amount < 0) {
			return new EconomyResponse(balance, balance, ResponseType.FAILURE, "amount must not be negative!");
		} else if(balance < amount) {
			return new EconomyResponse(balance, balance, ResponseType.FAILURE, "not enough money!");
		} else { 
			double newBalance = balance - amount;
			data.set(player.getUniqueId().toString(), newBalance);
			saveData();
			return new EconomyResponse(balance, newBalance, ResponseType.SUCCESS, null);
		}
	}

	@Override
	@Deprecated
	public EconomyResponse withdrawPlayer(String playerName, String worldName, double amount) {
		return null;
	}

	@Override
	public EconomyResponse withdrawPlayer(OfflinePlayer player, String worldName, double amount) {
		return withdrawPlayer(player, amount);
	}

	@Override
	@Deprecated
	public EconomyResponse depositPlayer(String playerName, double amount) {
		return null;
	}

	@Override
	public EconomyResponse depositPlayer(OfflinePlayer player, double amount) {
		double balance = getBalance(player);
		if(amount < 0) {
			return new EconomyResponse(balance, balance, ResponseType.FAILURE, "amount must not be negative!");
		} else {
			double newBalance = balance + amount;
			data.set(player.getUniqueId().toString(), newBalance);
			saveData();
			return new EconomyResponse(balance, newBalance, ResponseType.SUCCESS, null);
		}
	}

	@Override
	@Deprecated
	public EconomyResponse depositPlayer(String playerName, String worldName, double amount) {
		return null;
	}

	@Override
	public EconomyResponse depositPlayer(OfflinePlayer player, String worldName, double amount) {
		return depositPlayer(player, amount);
	}

	@Override
	@Deprecated
	public EconomyResponse createBank(String name, String player) {
		return new EconomyResponse(0, 0, ResponseType.NOT_IMPLEMENTED, null);
	}

	@Override
	public EconomyResponse createBank(String name, OfflinePlayer player) {
		return new EconomyResponse(0, 0, ResponseType.NOT_IMPLEMENTED, null);
	}

	@Override
	public EconomyResponse deleteBank(String name) {
		return new EconomyResponse(0, 0, ResponseType.NOT_IMPLEMENTED, null);
	}

	@Override
	public EconomyResponse bankBalance(String name) {
		return new EconomyResponse(0, 0, ResponseType.NOT_IMPLEMENTED, null);
	}

	@Override
	public EconomyResponse bankHas(String name, double amount) {
		return new EconomyResponse(0, 0, ResponseType.NOT_IMPLEMENTED, null);
	}

	@Override
	public EconomyResponse bankWithdraw(String name, double amount) {
		return new EconomyResponse(0, 0, ResponseType.NOT_IMPLEMENTED, null);
	}

	@Override
	public EconomyResponse bankDeposit(String name, double amount) {
		return new EconomyResponse(0, 0, ResponseType.NOT_IMPLEMENTED, null);
	}

	@Override
	@Deprecated
	public EconomyResponse isBankOwner(String name, String playerName) {
		return new EconomyResponse(0, 0, ResponseType.NOT_IMPLEMENTED, null);
	}

	@Override
	public EconomyResponse isBankOwner(String name, OfflinePlayer player) {
		return new EconomyResponse(0, 0, ResponseType.NOT_IMPLEMENTED, null);
	}

	@Override
	@Deprecated
	public EconomyResponse isBankMember(String name, String playerName) {
		return new EconomyResponse(0, 0, ResponseType.NOT_IMPLEMENTED, null);
	}

	@Override
	public EconomyResponse isBankMember(String name, OfflinePlayer player) {
		return new EconomyResponse(0, 0, ResponseType.NOT_IMPLEMENTED, null);
	}

	@Override
	public List<String> getBanks() {
		return null;
	}

	@Override
	@Deprecated
	public boolean createPlayerAccount(String playerName) {
		return false;
	}

	@Override
	public boolean createPlayerAccount(OfflinePlayer player) {
		if(!hasAccount(player)) {	
			data.set(player.getUniqueId().toString(), 0.0);
			return true;
		}
		return false;
	}

	@Override
	@Deprecated
	public boolean createPlayerAccount(String playerName, String worldName) {
		return false;
	}

	@Override
	public boolean createPlayerAccount(OfflinePlayer player, String worldName) {
		return createPlayerAccount(player);
	}
}
