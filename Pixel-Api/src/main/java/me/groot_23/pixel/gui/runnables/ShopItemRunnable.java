package me.groot_23.pixel.gui.runnables;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import me.groot_23.pixel.Pixel;
import me.groot_23.pixel.gui.GuiItem;
import me.groot_23.pixel.gui.GuiRunnable;
import me.groot_23.pixel.language.LanguageApi;
import me.groot_23.pixel.language.PixelLangKeys;

public class ShopItemRunnable implements GuiRunnable {

	private int id;
	private double cost;
	
	public ShopItemRunnable(int id, double cost) {
		this.cost = cost;
		this.id = id;
	}
	
	@Override
	public void run(Player player, ItemStack item, Inventory inv) {
		inv.setItem(inv.getSize() - 9, item);
		Material material;
		ItemStack buy;
		if(Pixel.getEconomy().has(player, cost)) {
			material = Material.GREEN_WOOL;
		} else {
			material = Material.RED_WOOL;
		}
		GuiItem gi = new GuiItem(material, ChatColor.GOLD + "" + ChatColor.BOLD + LanguageApi.getTranslation(player, PixelLangKeys.BUY));
		gi.addClickRunnable(new ShopBuyRunnable(id, cost), ClickType.LEFT);
		buy = gi.getItem();
		ItemMeta meta = buy.getItemMeta();
		List<String> lore = new ArrayList<String>();
		lore.add(ChatColor.RED + "" + ChatColor.BOLD + LanguageApi.getTranslation(player, PixelLangKeys.PRICE) + ": " + Pixel.getEconomy().format(cost));
		lore.add(ChatColor.GREEN + "" + ChatColor.BOLD + LanguageApi.getTranslation(player, PixelLangKeys.CREDIT) + ": " + Pixel.getEconomy().format(Pixel.getEconomy().getBalance(player)));
		lore.add(ChatColor.WHITE + "" + ChatColor.BOLD + "-----------------------");
		lore.add(ChatColor.GOLD + "" + ChatColor.BOLD + LanguageApi.getTranslation(player, PixelLangKeys.REMAINING) + ": " + Pixel.getEconomy().format(Pixel.getEconomy().getBalance(player) - cost));
		meta.setLore(lore);
		buy.setItemMeta(meta);
		inv.setItem(inv.getSize() - 1, buy);
	}

}
