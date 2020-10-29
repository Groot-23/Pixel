package me.groot_23.pixel.gui.runnable;

import org.bukkit.DyeColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.SkullMeta;

import de.tr7zw.nbtapi.NBTItem;
import me.groot_23.pixel.Pixel;
import me.groot_23.pixel.game.Game;
import me.groot_23.pixel.gui.GuiItem;
import me.groot_23.pixel.gui.GuiRunnable;
import me.groot_23.pixel.player.team.GameTeam;
import me.groot_23.pixel.player.team.TeamHandler;

public class TeamSelectorRunnable implements GuiRunnable{

	@Override
	public void run(Player player, ItemStack item, Inventory inv) {
		Game game = Pixel.getGame(player.getWorld().getUID());
		if(game != null) {
			DyeColor from = GameTeam.getTeamOfPlayer(player);
			TeamHandler th = game.teamHandler;
			NBTItem nbt = new NBTItem(item);
			if(nbt.hasKey("ming_team")) {
				DyeColor to = DyeColor.valueOf(nbt.getString("ming_team").toUpperCase());
				if(th.movePlayerToTeam(player, to)) {
					if(from != null) {
						updateColor(from, inv, game);
					}
					updateColor(to, inv, game);
				}
			}
		}
	}

	private void updateColor(DyeColor color, Inventory inv, Game game) {
		for(int i = 0; i < inv.getSize(); i++) {
			NBTItem nbt = new NBTItem(inv.getItem(i));
			if(nbt.hasKey("ming_team")) {
				if(nbt.getString("ming_team").equalsIgnoreCase(color.name())) {
					GameTeam team = game.teamHandler.getTeam(color);
					if(team != null) {
						for(int k = 0; k < game.teamHandler.teamSize; k++) {
							ItemStack stack = null;
							if(k < team.getPlayers().size()) {
								Player player = team.getPlayers().get(k);
								stack = new GuiItem(Material.PLAYER_HEAD).getItem();
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
