package me.groot_23.pixel.pixelconomy;

import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.ServicePriority;
import org.bukkit.plugin.java.JavaPlugin;

import net.milkbowl.vault.economy.Economy;

public class PixelconomyPlugin extends JavaPlugin {
	
	private Pixelconomy econ;
	
	@Override
	public void onEnable() {
		Plugin vault = Bukkit.getPluginManager().getPlugin("Vault");
		econ = new Pixelconomy(this);
		if(vault != null) {
			Bukkit.getServicesManager().register(Economy.class, econ, vault, ServicePriority.Normal);
			getLogger().info("Vault registration successfull!");
		} else {
			getLogger().warning("Could not perform Vault registration!");
			Bukkit.getPluginManager().disablePlugin(this);
			return;
		}
		
		Bukkit.getPluginManager().registerEvents(new EconListener(), this);
	}
	
	private class EconListener implements Listener {
		
		@EventHandler
		public void onJoin(PlayerJoinEvent event) {
			if(!econ.hasAccount(event.getPlayer())) {
				econ.createPlayerAccount(event.getPlayer());
			}
		}
	}
}
