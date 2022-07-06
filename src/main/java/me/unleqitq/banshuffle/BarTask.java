package me.unleqitq.banshuffle;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.TextColor;
import org.bukkit.Bukkit;
import org.bukkit.Statistic;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

public class BarTask extends BukkitRunnable {
	
	@Override
	public void run() {
		if (BanShuffle.config().getBoolean("action-bar", false))
			for (Player player : Bukkit.getOnlinePlayers()) {
				if (!BanShuffle.startTimes.containsKey(player.getUniqueId()))
					continue;
				int d = (BanShuffle.config().getInt("block-duration", 30) * 60 * 20 -
						player.getStatistic(Statistic.PLAY_ONE_MINUTE) +
						BanShuffle.startTimes.get(player.getUniqueId())) / 20;
				String time = String.format("%02d:%02d:%02d", d / 3600, (d / 60) % 60, d % 60);
				player.sendActionBar(
						Component.translatable(BanShuffle.targetMaterials.get(player.getUniqueId()).translationKey())
								.color(TextColor.color(0x00FF00))
								.append(Component.text(" | " + time).color(TextColor.color(0x00FF00))));
			}
	}
	
}
