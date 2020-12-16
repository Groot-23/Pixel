package me.groot_23.pixel.shop;

import java.util.HashMap;
import java.util.List;

import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import me.groot_23.pixel.Pixel;
import me.groot_23.pixel.gui.GuiItem;
import me.groot_23.pixel.gui.runnables.ShopItemRunnable;

public class ShopItem {

	private static final HashMap<Integer, ShopRunnable> i2run = new HashMap<Integer, ShopRunnable>();
	private static final HashMap<ShopRunnable, Integer> run2i = new HashMap<ShopRunnable, Integer>();
	
	public final boolean isBuyable;
	public final double cost;
	public final ItemStack item;
	public final ShopRunnable runnable;
	
	private int getRunInt(ShopRunnable runnable) {
		Integer i = run2i.get(runnable);
		if(i == null) {
			i = i2run.size();
			i2run.put(i, runnable);
			run2i.put(runnable, i);
		}
		return i;
	}
	
	/**
	 * Use this constructor for items which can be bought in the shop. Requires displayItem,
	 *  cost and runnable (Function that executes after transaction)
	 */
	public ShopItem(ItemStack item, double cost, ShopRunnable runnable) {
		int id = getRunInt(runnable);
		this.cost = cost;
		this.runnable = runnable;
		GuiItem gi = new GuiItem(item);
		gi.addClickRunnable(new ShopItemRunnable(id, cost), ClickType.LEFT);
		this.item = gi.getItem();
		ItemMeta meta = this.item.getItemMeta();
		List<String> lore = meta.getLore();
		lore.add(0, "Preis: " + Pixel.getEconomy().format(cost));
		meta.setLore(lore);
		this.item.setItemMeta(meta);
		isBuyable = true;
	}
	
	/**
	 * Use this constructor for non buyable items that are just decoration (or already bought)
	 */
	public ShopItem(ItemStack item) {
		this.item = new GuiItem(item).getItem();
		isBuyable = false;
		runnable = null;
		cost = 0;
	}
	
	public static void unlock(int id, Player player) {
		i2run.get(id).unlock(player);
	}
}
