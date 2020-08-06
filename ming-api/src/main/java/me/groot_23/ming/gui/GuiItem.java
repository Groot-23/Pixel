package me.groot_23.ming.gui;

import java.util.Arrays;
import java.util.List;

import org.bukkit.Material;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import de.tr7zw.nbtapi.NBTItem;
import de.tr7zw.nbtapi.NBTList;

public class GuiItem {

	public static String GUI_RUNNABLE_PREFIX = "//";
	
	private ItemStack item;
	
	public ItemStack getItem() {
		return item;
	}
	
	public GuiItem(Material material) {
		item = new ItemStack(material);
		// set nbt (item should not be movable)
		NBTItem nbt = new NBTItem(item);
		nbt.setBoolean("isGuiItem", true);
		item = nbt.getItem();
	}

	public GuiItem(Material material, String name, List<String> lore) {
		item = new ItemStack(material);
		// set item meta (name,lore)
		ItemMeta meta = item.getItemMeta();
		meta.setDisplayName(name);
		meta.setLore(lore);
		item.setItemMeta(meta);
		// set nbt (item should not be movable)
		NBTItem nbt = new NBTItem(item);
		nbt.setBoolean("isGuiItem", true);
		item = nbt.getItem();
	}
	
	public GuiItem(Material material, String name, String... lore) {
		this(material, name, Arrays.asList(lore));
	}

	public void addActionClick(String command, ClickType... clickType) {
		NBTItem nbt = new NBTItem(item);
		if (clickType == null || clickType.length == 0) {
			clickType = ClickType.values();
		}
		for (ClickType c : clickType) {
			NBTList<String> cmds = nbt.getStringList("ClickType_" + c.name());
			cmds.add(command);
		}
		item = nbt.getItem();
	}
	
	public void clearActionClick(ClickType... clickType) {
		NBTItem nbt = new NBTItem(item);
		if (clickType == null || clickType.length == 0) {
			clickType = ClickType.values();
		}
		for (ClickType c : clickType) {
			NBTList<String> cmds = nbt.getStringList("ClickType_" + c.name());
			cmds.clear();
		}
		item = nbt.getItem();
	}
	
	public static enum UseAction {
		LEFT_CLICK,
		RIGHT_CLICK
	}
	
	public void addActionUse(String command, UseAction... action) {
		NBTItem nbt = new NBTItem(item);
		if (action == null || action.length == 0) {
			action = UseAction.values();
		}
		for (UseAction a : action) {
			NBTList<String> cmds = nbt.getStringList("UseAction_" + a.name());
			cmds.add(command);
		}
		item = nbt.getItem();
	}
	
	public void clearActionUse(String command, UseAction... action) {
		NBTItem nbt = new NBTItem(item);
		if (action == null || action.length == 0) {
			action = UseAction.values();
		}
		for (UseAction a : action) {
			NBTList<String> cmds = nbt.getStringList("UseAction_" + a.name());
			cmds.clear();
		}
		item = nbt.getItem();
	}
	
	
	public void addActionClickRunnable(String runnable, ClickType... clickType) {
		addActionClick(GUI_RUNNABLE_PREFIX + runnable, clickType);
	}
	public void addActionUseRunnable(String runnable, UseAction... action) {
		addActionUse(GUI_RUNNABLE_PREFIX + runnable, action);
	}
	
}
