package me.groot_23.pixel.gui.runnable;

import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.SkullMeta;

import me.groot_23.pixel.gui.GuiRunnable;

public class TpToPlayerRunnable implements GuiRunnable {

	@Override
	public void run(Player player, ItemStack item, Inventory inv) {
		if(item.getType() == Material.PLAYER_HEAD) {
			SkullMeta meta = (SkullMeta) item.getItemMeta();
			OfflinePlayer other = meta.getOwningPlayer();
			if(other != null && other.isOnline()) {
				player.teleport(((Player)other).getLocation());
				player.closeInventory();
			}
		}
	}

}
