package me.groot_23.pixel.gui;

import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

public interface GuiRunnable {

	public abstract void run(Player player, ItemStack item, Inventory inv);
}
