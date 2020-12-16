package me.groot_23.pixel.shop;

import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;

import me.groot_23.pixel.gui.GuiItem;
import me.groot_23.pixel.gui.runnables.GuiCloseRunnable;
import me.groot_23.pixel.language.LanguageApi;
import me.groot_23.pixel.language.PixelLangKeys;
import net.md_5.bungee.api.ChatColor;

public class Shop {
	
	public static void openShop(Player player, List<ShopItem> items, String title) {
		int rows = ((items.size() + 8) / 9) + 2;
		Inventory inv = Bukkit.createInventory(player, rows * 9, title);
		int i = 0;
		for(ShopItem item : items) {
			inv.setItem(i++, item.item);
		}
		for(i = 0; i < 18; ++i) {
			inv.setItem((rows-2) * 9 + i, new GuiItem(Material.GRAY_STAINED_GLASS_PANE).getItem());
		}
		inv.setItem((rows-1)*9, null);
		
		GuiItem leaveItem = new GuiItem(Material.BARRIER, ChatColor.RED + "" + ChatColor.BOLD + LanguageApi.getTranslation(player, PixelLangKeys.EXIT));
		leaveItem.addClickRunnable(new GuiCloseRunnable());
		inv.setItem((rows-1)*9+4, leaveItem.getItem());

		player.openInventory(inv);
	}
}
