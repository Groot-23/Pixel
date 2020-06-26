package me.groot_23.ming.gui;

import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import me.groot_23.ming.MiniGame;

public class GuiCloseRunnable implements GuiRunnable {

	@Override
	public void run(Player player, ItemStack item, Inventory inv, MiniGame game) {
		player.closeInventory();
	}

}
