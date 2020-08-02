package me.groot_23.ming.player;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.SkullMeta;

import de.tr7zw.changeme.nbtapi.NBTItem;
import me.groot_23.ming.MiniGame;
import me.groot_23.ming.game.Game;
import me.groot_23.ming.gui.GuiRunnable;
import me.groot_23.ming.world.Arena;

public class TeamSelectorRunnable implements GuiRunnable{

	@Override
	public void run(Player player, ItemStack item, Inventory inv, MiniGame game) {
		ChatColor from = GameTeam.getTeamOfPlayer(player, game.getPlugin());
		
		Arena arena = game.worldProvider.getArena(player.getWorld().getUID());
		if(arena != null) {
			TeamHandler th = arena.getGame().teamHandler;
			NBTItem nbt = new NBTItem(item);
			if(nbt.hasKey("ming_team")) {
				ChatColor to = ChatColor.valueOf(nbt.getString("ming_team").toUpperCase());
				if(th.movePlayerToTeam(player, to)) {
					if(from != null) {
						updateColor(from, inv, arena.getGame());
					}
					updateColor(to, inv, arena.getGame());
				}
			}
		}
	}

	private void updateColor(ChatColor color, Inventory inv, Game game) {
		for(int i = 0; i < inv.getSize(); i++) {
			NBTItem nbt = new NBTItem(inv.getItem(i));
			if(nbt.hasKey("ming_team")) {
				if(nbt.getString("ming_team").equalsIgnoreCase(color.name())) {
					GameTeam team = game.teamHandler.getTeam(color);
					if(team != null) {
						for(int k = 0; k < game.mode.getPlayersPerTeam(); k++) {
							ItemStack stack = null;
							if(k < team.getPlayers().size()) {
								Player player = team.getPlayers().get(k);
								stack = game.miniGame.createGuiItem(Material.PLAYER_HEAD).getItem();
								SkullMeta meta = (SkullMeta) stack.getItemMeta();
								meta.setOwningPlayer(player);
								stack.setItemMeta(meta);
							}
							int slot = i + 9 * (k + 1);
							if(slot < inv.getSize()) {
								inv.setItem(slot, stack);
							}
						}
						break;
					}
				}
			}
		}
	}

}
