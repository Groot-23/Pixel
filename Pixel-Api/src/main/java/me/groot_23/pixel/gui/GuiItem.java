package me.groot_23.pixel.gui;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import de.tr7zw.nbtapi.NBTItem;
import de.tr7zw.nbtapi.NBTList;

public class GuiItem {

	private static final Map<GuiRunnable, Integer> run2i = new HashMap<GuiRunnable, Integer>();
	private static final Map<Integer, GuiRunnable> i2run = new HashMap<Integer, GuiRunnable>();
	
	public static String CLICK_RUNNABLE = "PixelClickRunnable_";
	public static String USE_RUNNABLE = "PixelUseRunnable_";
	public static String CLICK_COMMAND = "PixelClickCommand_";
	public static String USE_COMMAND = "PixelUseCommand_";
	
	public static String GUI_RUNNABLE_PREFIX = "//";
	
	private ItemStack item;
	
	public ItemStack getItem() {
		return item;
	}
	
	public GuiItem(ItemStack item) {
		NBTItem nbt = new NBTItem(item);
		nbt.setBoolean("isGuiItem", true);
		this.item = nbt.getItem();
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

	public void addClickCommand(String command, ClickType... clickType) {
		NBTItem nbt = new NBTItem(item);
		if (clickType == null || clickType.length == 0) {
			clickType = ClickType.values();
		}
		for (ClickType c : clickType) {
			NBTList<String> cmds = nbt.getStringList(CLICK_COMMAND + c.name());
			cmds.add(command);
		}
		item = nbt.getItem();
	}
	
	public static enum UseAction {
		LEFT_CLICK,
		RIGHT_CLICK
	}
	
	public void addUseCommand(String command, UseAction... action) {
		NBTItem nbt = new NBTItem(item);
		if (action == null || action.length == 0) {
			action = UseAction.values();
		}
		for (UseAction a : action) {
			NBTList<String> cmds = nbt.getStringList(USE_COMMAND + a.name());
			cmds.add(command);
		}
		item = nbt.getItem();
	}
	
	
	private int getRunInt(GuiRunnable runnable) {
		Integer i = run2i.get(runnable);
		if(i == null) {
			i = i2run.size();
			i2run.put(i, runnable);
			run2i.put(runnable, i);
		}
		return i;
	}
	
	public void addClickRunnable(GuiRunnable runnable, ClickType... clickType) {
		int i = getRunInt(runnable);
		NBTItem nbt = new NBTItem(item);
		if (clickType == null || clickType.length == 0) {
			clickType = ClickType.values();
		}
		for (ClickType c : clickType) {
			NBTList<Integer> cmds = nbt.getIntegerList(CLICK_RUNNABLE + c.name());
			cmds.add(i);
		}
		item = nbt.getItem();
	}
	
	public void addUseRunnable(GuiRunnable runnable, UseAction... action) {
		int i = getRunInt(runnable);
		NBTItem nbt = new NBTItem(item);
		if (action == null || action.length == 0) {
			action = UseAction.values();
		}
		for (UseAction a : action) {
			NBTList<Integer> cmds = nbt.getIntegerList(USE_RUNNABLE + a.name());
			cmds.add(i);
		}
		item = nbt.getItem();
	}
	
	public void clearClick(ClickType... clickType) {
		NBTItem nbt = new NBTItem(item);
		if (clickType == null || clickType.length == 0) {
			clickType = ClickType.values();
		}
		for (ClickType c : clickType) {
			NBTList<String> cmds = nbt.getStringList(CLICK_COMMAND + c.name());
			cmds.clear();
			NBTList<Integer> cmd2 = nbt.getIntegerList(CLICK_RUNNABLE + c.name());
			cmd2.clear();
		}
		item = nbt.getItem();
	}
	
	public void clearUse(UseAction... action) {
		NBTItem nbt = new NBTItem(item);
		if (action == null || action.length == 0) {
			action = UseAction.values();
		}
		for (UseAction a : action) {
			NBTList<String> cmds = nbt.getStringList(USE_COMMAND + a.name());
			cmds.clear();
			NBTList<Integer> cmd2 = nbt.getIntegerList(USE_RUNNABLE + a.name());
			cmd2.clear();
		}
		item = nbt.getItem();
	}
	
	public static void executeRunnable(int id, Player player, ItemStack item, Inventory inv) {
		GuiRunnable run = i2run.get(id);
		if(run != null) {
			run.run(player, item, inv);
		}
	}
	
}
