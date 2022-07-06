package me.unleqitq.banshuffle;

import org.bukkit.Material;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerMoveEvent;

public class StandListener implements Listener {
	
	@EventHandler (priority = EventPriority.MONITOR, ignoreCancelled = true)
	public static void onMove(PlayerMoveEvent event) {
		if (!BanShuffle.startTimes.containsKey(event.getPlayer().getUniqueId()))
			return;
		if (event.hasChangedBlock()) {
			Material type = event.getTo().clone().add(0, -1, 0).getBlock().getType();
			if (BanShuffle.targetMaterials.get(event.getPlayer().getUniqueId()).equals(type)) {
				if (BanShuffle.config().getBoolean("instant-new-block", true)) {
					BanShuffle.applyNewBlock(event.getPlayer());
				}
				else {
					BanShuffle.achievedBlock.add(event.getPlayer().getUniqueId());
					event.getPlayer().sendMessage("§aYou found your target block");
				}
			}
		}
	}
	
}
