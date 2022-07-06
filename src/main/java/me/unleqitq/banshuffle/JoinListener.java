package me.unleqitq.banshuffle;

import net.kyori.adventure.text.Component;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

public class JoinListener implements Listener {
	
	@EventHandler (priority = EventPriority.MONITOR, ignoreCancelled = true)
	public void onJoin(PlayerJoinEvent event) {
		if (!BanShuffle.startTimes.containsKey(event.getPlayer().getUniqueId())) {
			BanShuffle.applyNewBlock(event.getPlayer());
		}
		else {
			event.getPlayer().sendMessage(Component.text("§aYour target block is: ").append(Component.translatable(
					BanShuffle.targetMaterials.get(event.getPlayer().getUniqueId()).translationKey())));
		}
	}
	
}
