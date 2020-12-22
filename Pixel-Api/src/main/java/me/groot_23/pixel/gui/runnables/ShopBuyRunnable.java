package me.groot_23.pixel.gui.runnables;

import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import me.groot_23.pixel.Pixel;
import me.groot_23.pixel.gui.GuiRunnable;
import me.groot_23.pixel.shop.ShopItem;
import net.milkbowl.vault.economy.EconomyResponse.ResponseType;

public class ShopBuyRunnable implements GuiRunnable{

	private int hash;
	private double cost;
	
	public ShopBuyRunnable(int hash, double cost) {
		this.cost = cost;
		this.hash = hash;
	}
	
	@Override
	public void run(Player player, ItemStack item, Inventory inv) {
		if(Pixel.getEconomy().has(player, cost)) {
			if(Pixel.getEconomy().withdrawPlayer(player, cost).type == ResponseType.SUCCESS) {
				ShopItem.unlock(hash, player);
			}
		}
		player.closeInventory();
	}

}
