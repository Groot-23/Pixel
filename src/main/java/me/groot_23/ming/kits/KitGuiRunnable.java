package me.groot_23.ming.kits;

import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.metadata.FixedMetadataValue;

import de.tr7zw.changeme.nbtapi.NBTEntity;
import de.tr7zw.changeme.nbtapi.NBTItem;
import me.groot_23.ming.MiniGame;
import me.groot_23.ming.gui.GuiRunnable;

public class KitGuiRunnable implements GuiRunnable {

	public static String getSelectedSuffix(Player player, MiniGame game) {
		return ChatColor.RESET + "    " + ChatColor.GRAY + "(" + ChatColor.GREEN
				+ game.getTranslation(player, "kit.selected") + ChatColor.GRAY + ")";
	}
	
	@Override
	public void run(Player player, ItemStack item, Inventory inv, MiniGame game) {
		System.out.println("KIT Runnable");
		NBTItem nbt = new NBTItem(item);
		String kit = nbt.getString("ming_kit");
		if(kit != null && game.kitExists(kit)) {
			// set kit
			player.setMetadata("ming_kit", new FixedMetadataValue(game.getPlugin(), kit));
			// update suffixes
			String suffix = getSelectedSuffix(player, game);
			for(ItemStack stack : inv) {
				if(stack != null) {
					ItemMeta meta = stack.getItemMeta();
					String name = meta.getDisplayName();
					if(name.contains(suffix)) {
						meta.setDisplayName(name.substring(0, name.indexOf(suffix)));
						stack.setItemMeta(meta);
						break;
					}
				}
			}
			
			ItemMeta meta = item.getItemMeta();
			meta.setDisplayName(meta.getDisplayName() + suffix);
			item.setItemMeta(meta);
		}
	}

}
