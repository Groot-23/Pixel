package me.groot_23.pixel.gui.runnables;

import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import me.groot_23.pixel.gui.GuiRunnable;

public class GuiCloseRunnable implements GuiRunnable {

	@Override
	public void run(Player player, ItemStack item, Inventory inv) {
		player.closeInventory();
	}

}
