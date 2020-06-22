package me.groot_23.ming.gui;

import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import me.groot_23.ming.MiniGame;

public interface GuiRunnable {

	public abstract void run(Player player, ItemStack item, Inventory inv, MiniGame game);
}
