package me.groot_23.pixel.gui.runnables;

import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import me.groot_23.pixel.gui.GuiRunnable;
import me.groot_23.pixel.kits.Kit;
import me.groot_23.pixel.kits.KitApi;

public class KitGuiRunnable implements GuiRunnable {

	private String kit, group;
	
	public KitGuiRunnable(String kit, String group) {
		this.kit = kit;
		this.group = group;
	}
	
	@Override
	public void run(Player player, ItemStack item, Inventory inv) {
		if(kit != null && KitApi.getKit(group, kit) != null) {
			// set kit
			KitApi.setSelectedKit(player, group, kit);
			// update suffixes
			String suffix = Kit.getSelectedSuffix(player);
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
