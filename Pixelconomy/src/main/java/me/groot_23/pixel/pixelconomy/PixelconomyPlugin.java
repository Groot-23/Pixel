package me.groot_23.pixel.pixelconomy;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.ServicePriority;
import org.bukkit.plugin.java.JavaPlugin;

import net.milkbowl.vault.economy.Economy;
import net.milkbowl.vault.economy.EconomyResponse;
import net.milkbowl.vault.economy.EconomyResponse.ResponseType;

public class PixelconomyPlugin extends JavaPlugin {

	private Pixelconomy econ;

	@Override
	public void onEnable() {
		
		saveDefaultConfig();
		try {
			new File(getDataFolder(), "data.yml").createNewFile();
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		Plugin vault = Bukkit.getPluginManager().getPlugin("Vault");
		econ = new Pixelconomy(this);
		if (vault != null) {
			Bukkit.getServicesManager().register(Economy.class, econ, vault, ServicePriority.Normal);
			getLogger().info("Vault registration successfull!");
		} else {
			getLogger().warning("Could not perform Vault registration!");
			Bukkit.getPluginManager().disablePlugin(this);
			return;
		}
		
		Bukkit.getPluginManager().registerEvents(new EconListener(), this);
		
		PeconCommand cmd = new PeconCommand();
		getCommand("pecon").setExecutor(cmd);
		getCommand("pecon").setTabCompleter(cmd);
	}

	private class PeconCommand implements CommandExecutor, TabCompleter {

		@Override
		public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
			List<String> list = new ArrayList<String>();
			if (args.length == 1) {
				for (OfflinePlayer player : Bukkit.getOfflinePlayers()) {
					if (player.getName().startsWith(args[0])) {
						list.add(player.getName());
					}
				}
			}
			if (args.length == 2) {
				String[] modes = { "get", "set", "add", "sub" };
				for (String s : modes) {
					if(s.startsWith(args[0]))
						list.add(s);
				}
			}
			return list;
		}

		@Override
		public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
			if (!command.testPermission(sender)) {
				return true;
			}
			if (args.length < 2) {
				sender.sendMessage(ChatColor.RED + "Not enough arguments!");
				return false;
			}
			OfflinePlayer player = Bukkit.getOfflinePlayer(args[0]);
			if (args[1].contentEquals("get")) {
				sender.sendMessage(econ.format(econ.getBalance(player)));
				return true;
			} else {
				if (args.length < 3) {
					sender.sendMessage(ChatColor.RED + "Not enough arguments!");
					return false;
				}
				double amount;
				try {
					amount = Double.parseDouble(args[2]);
				} catch (NumberFormatException e) {
					sender.sendMessage(ChatColor.RED + "Invalid number: " + args[2]);
					return false;
				}
				EconomyResponse resp = null;
				if (args[1].contentEquals("set")) {
					resp = econ.setBalance(player, amount);
				} else if (args[1].contentEquals("add")) {
					resp = econ.depositPlayer(player, amount);
				} else if(args[1].contentEquals("sub")) {
					resp = econ.withdrawPlayer(player, amount);
				} else {
					sender.sendMessage(ChatColor.RED + "Invalid operation: " + args[1]);
					return false;
				}
				if(resp != null) {
					sender.sendMessage("old balance: " + resp.amount + "  ->   new balance " + resp.balance);
					if(resp.type == ResponseType.FAILURE && resp.errorMessage != null)
						sender.sendMessage(ChatColor.RED + "Error: " + resp.errorMessage);
				}
			}
			return true;
		}

	}

	private class EconListener implements Listener {

		@EventHandler
		public void onJoin(PlayerJoinEvent event) {
			if (!econ.hasAccount(event.getPlayer())) {
				econ.createPlayerAccount(event.getPlayer());
			}
		}
	}
}
