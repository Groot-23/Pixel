package me.groot_23.pixel.gui.runnables;

import org.bukkit.DyeColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.SkullMeta;

import me.groot_23.pixel.gui.GuiItem;
import me.groot_23.pixel.gui.GuiRunnable;
import me.groot_23.pixel.language.LanguageApi;
import me.groot_23.pixel.language.PixelLangKeys;
import me.groot_23.pixel.player.team.GameTeam;
import me.groot_23.pixel.player.team.TeamHandler;

public class TeamSelectorRunnable implements GuiRunnable {

	private DyeColor team;
	private TeamHandler th;

	public TeamSelectorRunnable(DyeColor team, TeamHandler teamHandler) {
		this.team = team;
		this.th = teamHandler;
	}

	@Override
	public void run(Player player, ItemStack item, Inventory inv) {
		DyeColor from = GameTeam.getTeamOfPlayer(player);
		if (th.movePlayerToTeam(player, team)) {
			if (from != null) {
				updateColor(from, inv, th);
			}
			updateColor(team, inv, th);
		}
		player.sendMessage(th.chatPrefix + String.format(LanguageApi.getTranslation(player, PixelLangKeys.SELECTED_TEAM), GameTeam.toChatColor(team) + LanguageApi.translateColor(player, team)));
	}

	private void updateColor(DyeColor color, Inventory inv, TeamHandler th) {
		int col = 0;
		for (; th.colors[col] != color; col++);

		GameTeam gteam = th.getTeam(color);
		for (int i = 0; i < th.teamSize; ++i) {
			ItemStack stack = null;
			if (i < gteam.getPlayers().size()) {
				Player player = gteam.getPlayers().get(i);
				stack = new GuiItem(Material.PLAYER_HEAD).getItem();
				SkullMeta meta = (SkullMeta) stack.getItemMeta();
				meta.setOwningPlayer(player);
				stack.setItemMeta(meta);
			}
			int slot = col + 9 * (i + 1);
			if (slot < inv.getSize()) {
				inv.setItem(slot, stack);
			}
		}
	}

}
