package me.groot_23.ming.display;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.bukkit.boss.BossBar;
import org.bukkit.entity.Player;

public class BossBarManager {
	private static Map<UUID, List<BossBar>> bars;
	
	static {
		bars = new HashMap<UUID, List<BossBar>>();
	}
	
	public static void addPlayer(BossBar bar, Player player) {
		if(!bar.getPlayers().contains(player)) {
			bar.addPlayer(player);
			if(!bars.containsKey(player.getUniqueId())) {
				bars.put(player.getUniqueId(), new ArrayList<BossBar>());
			}
			bars.get(player.getUniqueId()).add(bar);
		}
	}
	
	public static void removePlayer(Player player) {
		List<BossBar> list = bars.get(player.getUniqueId());
		if(list != null) {
			for(BossBar bar : list) {
				bar.removePlayer(player);
			}
			list.clear();
		}
	}
}
