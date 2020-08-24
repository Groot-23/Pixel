package me.groot_23.ming.gui.runnable;

import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.metadata.FixedMetadataValue;

import de.tr7zw.nbtapi.NBTItem;
import me.groot_23.ming.MinG;
import me.groot_23.ming.gui.GuiRunnable;
import me.groot_23.ming.kits.Kit;

public class KitGuiRunnable implements GuiRunnable {

	
	@Override
	public void run(Player player, ItemStack item, Inventory inv) {
		NBTItem nbt = new NBTItem(item);
		String group = nbt.getString("ming_kit_group");
		String kit = nbt.getString("ming_kit");
		if(kit != null && MinG.getKit(group, kit) != null) {
			// set kit
			MinG.setSelectedKit(player, group, kit);
			// update suffixes
			String suffix = Kit.getSelectedSuffix(player, MinG.getLanguageManager());
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
