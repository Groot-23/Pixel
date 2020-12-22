package me.groot_23.pixel.listener;

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
import me.groot_23.pixel.gui.GuiItem;
import me.groot_23.pixel.gui.GuiItem.UseAction;

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
			Player player = (Player) event.getWhoClicked();
			
			NBTItem nbt = new NBTItem(event.getCurrentItem());
			NBTList<String> cmds = nbt.getStringList(GuiItem.CLICK_COMMAND + event.getClick().name());
			for (String cmd : cmds) {
				player.performCommand(cmd);
			}
			NBTList<Integer> runs = nbt.getIntegerList(GuiItem.CLICK_RUNNABLE + event.getClick().name());
			for(int run : runs) {
				GuiItem.executeRunnable(run, player, event.getCurrentItem(), event.getClickedInventory());
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
				Player player = event.getPlayer();
				
				NBTList<String> cmds = nbt.getStringList(GuiItem.USE_COMMAND + action.name());
				for (String cmd : cmds) {
					player.performCommand(cmd);
				}
				NBTList<Integer> runs = nbt.getIntegerList(GuiItem.USE_RUNNABLE + action.name());
				for(int run : runs) {
					GuiItem.executeRunnable(run, player, event.getItem(), null);
				}
			}
		}
	}
}
