package me.unleqitq.banshuffle;

import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;
import org.bukkit.Statistic;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import java.io.IOException;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Date;

public class CheckTask extends BukkitRunnable {
	
	@Override
	public void run() {
		if (BanShuffle.config().getBoolean("instant-new-block", true)) {
			for (Player player : Bukkit.getOnlinePlayers()) {
				if (!BanShuffle.startTimes.containsKey(player.getUniqueId()))
					continue;
				if (player.getStatistic(Statistic.PLAY_ONE_MINUTE) >=
						BanShuffle.config().getLong("block-duration", 30) * 60 * 20 +
								BanShuffle.startTimes.get(player.getUniqueId())) {
					if (player.hasPermission("banshuffle.exempt")) {
						BanShuffle.applyNewBlock(player);
						continue;
					}
					fail(player);
					BanShuffle.applyNewBlock(player);
				}
			}
		}
		else {
			for (Player player : Bukkit.getOnlinePlayers()) {
				if (!BanShuffle.startTimes.containsKey(player.getUniqueId()))
					continue;
				if (player.getStatistic(Statistic.PLAY_ONE_MINUTE) >=
						BanShuffle.config().getLong("block-duration", 30) * 60 * 20 +
								BanShuffle.startTimes.get(player.getUniqueId())) {
					if (BanShuffle.achievedBlock.contains(player.getUniqueId())) {
						BanShuffle.applyNewBlock(player);
					}
					else {
						if (player.hasPermission("banshuffle.exempt")) {
							BanShuffle.applyNewBlock(player);
							continue;
						}
						fail(player);
						BanShuffle.applyNewBlock(player);
					}
				}
			}
		}
		try {
			BanShuffle.saveData();
		}
		catch (IOException e) {
			throw new RuntimeException(e);
		}
	}
	
	public static void fail(Player player) {
		BanShuffle.fails.put(player.getUniqueId(), BanShuffle.fails.getOrDefault(player.getUniqueId(), 0) + 1);
		Bukkit.broadcast(player.displayName().append(Component.text(" §cdidn't find "))
				.append(Component.translatable(BanShuffle.targetMaterials.get(player.getUniqueId()).translationKey()))
				.append(Component.text(" §cin time")));
		int left = BanShuffle.config().getInt("lives", 3) - BanShuffle.fails.get(player.getUniqueId());
		if (left <= 0) {
			if (BanShuffle.config().getBoolean("kill", false)) {
				player.damage(Math.abs(player.getHealth()) * 100);
				player.damage(1000000);
			}
			else if (BanShuffle.config().getDouble("ban-duration", 1) < 0)
				player.banPlayer("§cYou weren't fast enough to find " +
						BanShuffle.targetMaterials.get(player.getUniqueId()).getKey().getKey());
			else
				player.banPlayer("§cYou weren't fast enough to find " +
						BanShuffle.targetMaterials.get(player.getUniqueId()).getKey().getKey(), Date.from(Instant.now()
						.plus((long) (BanShuffle.config().getDouble("ban-duration", 1) * 60), ChronoUnit.MINUTES)));
			BanShuffle.fails.put(player.getUniqueId(), 0);
		}
		else {
			player.sendMessage(Component.text("§aYou have §6" + left + "§a lives left!"));
		}
	}
	
}
