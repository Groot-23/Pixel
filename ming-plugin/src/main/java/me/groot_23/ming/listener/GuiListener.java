package me.groot_23.ming.listener;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;

import de.tr7zw.nbtapi.NBTItem;
import de.tr7zw.nbtapi.NBTList;
import me.groot_23.ming.MinG;
import me.groot_23.ming.gui.GuiItem.UseAction;

public class GuiListener implements Listener {
	
	public static boolean isGuiItem(ItemStack item) {
		if(item != null && item.getType() != Material.AIR) {
			NBTItem nbt = new NBTItem(item);
			return (nbt.hasKey("isGuiItem") && nbt.getBoolean("isGuiItem"));
		}
		return false;
	}
	

	@EventHandler
	public void onClick(InventoryClickEvent event) {
		if (isGuiItem(event.getCurrentItem())) {
			event.setCancelled(true);
			NBTItem nbt = new NBTItem(event.getCurrentItem());
			NBTList<String> cmds = nbt.getStringList("ClickType_" + event.getClick().name());
			for (String cmd : cmds) {
				Player player = (Player) event.getWhoClicked();
				MinG.guiExecute(cmd, player, event.getCurrentItem(), event.getInventory());
			}
		}
	}

	@EventHandler
	public void preventDrop(PlayerDropItemEvent event) {
		if (isGuiItem(event.getItemDrop().getItemStack())) {
			event.setCancelled(true);
		}
	}

	@EventHandler
	public void onUse(PlayerInteractEvent event) {
		if (event.hasItem() && isGuiItem(event.getItem())) {
			UseAction action = null;
			switch (event.getAction()) {
			case RIGHT_CLICK_AIR:
			case RIGHT_CLICK_BLOCK:
				action = UseAction.RIGHT_CLICK;
				break;
			case LEFT_CLICK_AIR:
			case LEFT_CLICK_BLOCK:
				action = UseAction.LEFT_CLICK;
			default:
				break;
			}
			if(action != null) {
				NBTItem nbt = new NBTItem(event.getItem());
				NBTList<String> cmds = nbt.getStringList("UseAction_" + action.name());
				for (String cmd : cmds) {
					Player player = event.getPlayer();
					MinG.guiExecute(cmd, player, event.getItem(), null);
				}
			}
		}
	}
}
