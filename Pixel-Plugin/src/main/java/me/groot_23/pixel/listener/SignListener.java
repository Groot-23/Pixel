package me.groot_23.pixel.listener;

import org.bukkit.GameMode;
import org.bukkit.block.Sign;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.scheduler.BukkitRunnable;

import me.groot_23.pixel.Pixel;
import me.groot_23.pixel.display.JoinSignApi;

public class SignListener implements Listener {
	
	@EventHandler
	public void onClick(PlayerInteractEvent event) {
		if(event.getClickedBlock() != null) {
			if(event.getPlayer().getGameMode() == GameMode.CREATIVE && event.getAction() == Action.LEFT_CLICK_BLOCK) return;
			JoinSignApi.join(event.getClickedBlock().getLocation(), event.getPlayer());
		}
	}
	
	@EventHandler
	public void onPlace(BlockPlaceEvent event) {
		if(event.getBlock().getState() instanceof Sign) {
			if(JoinSignApi.placeSign(event.getItemInHand(), event.getBlock().getLocation())) {
				new BukkitRunnable() {
					@Override
					public void run() {
						event.getPlayer().closeInventory();
						Sign sign = (Sign)event.getBlock().getState();
						sign.setEditable(false);
						sign.update(true);
					}
				}.runTaskLater(Pixel.getPlugin(), 1);

			}
		}
	}
	
	@EventHandler
	public void onBreak(BlockBreakEvent event) {
		if(event.getBlock().getState() instanceof Sign) {
			JoinSignApi.removeSign(event.getBlock().getLocation());
		}
	}
}
