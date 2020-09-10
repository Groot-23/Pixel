package me.groot_23.ming.gui.runnable;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.SkullMeta;

import me.groot_23.ming.MinG;
import me.groot_23.ming.game.Game;
import me.groot_23.ming.gui.GuiItem;
import me.groot_23.ming.gui.GuiRunnable;

public class SpectatorTpRunnable implements GuiRunnable{

	@Override
	public void run(Player player, ItemStack item, Inventory inv) {
		Game game = MinG.getGame(player.getWorld().getUID());
		List<ItemStack> heads = new ArrayList<ItemStack>();
		if(game != null) {
			for(Player other : game.players) {
				if(!MinG.isSpectator(other)) {
					GuiItem gui = new GuiItem(Material.PLAYER_HEAD);
					gui.addActionClickRunnable("ming_tp_to_player", ClickType.LEFT);
					ItemStack head = gui.getItem();
					SkullMeta meta = (SkullMeta) head.getItemMeta();
					meta.setOwningPlayer(other);
					head.setItemMeta(meta);
					heads.add(head);
				}
			}
			if(heads.size() > 0) {
				Inventory selector = Bukkit.createInventory(player, (heads.size() + 8) / 9 * 9);
				for(int i = 0; i < heads.size(); ++i) {
					selector.setItem(i, heads.get(i));
				}
				player.openInventory(selector);
			}
		}
	}

}
