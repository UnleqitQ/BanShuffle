package me.unleqitq.banshuffle;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabExecutor;
import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class BanShuffleCommand implements TabExecutor {
	
	@Override
	public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label,
			@NotNull String[] args) {
		if (args.length != 4 || !args[0].equalsIgnoreCase("timer") ||
				!(args[1].equalsIgnoreCase("add") || args[1].equalsIgnoreCase("remove"))) {
			sender.sendMessage("§4Usage: /banshuffle timer add/remove <player> <duration>");
			return false;
		}
		char type = Character.toLowerCase(args[3].charAt(args[3].length() - 1));
		String timeS = args[3].substring(0, args[3].length() - 1);
		Player player = Bukkit.getPlayer(args[2]);
		if (player == null) {
			sender.sendMessage("§4That player is not online");
			return false;
		}
		if (timeS.length() == 0) {
			sender.sendMessage("§4Usage: /banshuffle timer add/remove <player> <duration>");
			return false;
		}
		try {
			int time = Integer.parseInt(timeS);
			int ticks;
			switch (type) {
				case 's' -> ticks = time * 20;
				case 'm' -> ticks = time * 60 * 20;
				case 'h' -> ticks = time * 3600 * 20;
				default -> {
					sender.sendMessage("§4Usage: /banshuffle timer add/remove <player> <duration>");
					return false;
				}
			}
			if (args[1].equalsIgnoreCase("remove")) {
				ticks *= -1;
			}
			BanShuffle.startTimes.put(player.getUniqueId(), BanShuffle.startTimes.get(player.getUniqueId()) + ticks);
			return true;
		}
		catch (Exception ignored) {
			sender.sendMessage("§4Usage: /banshuffle timer add/remove <player> <duration>");
			return false;
		}
	}
	
	@Override
	public @Nullable List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command command,
			@NotNull String label, @NotNull String[] args) {
		if (args.length == 1) {
			return List.of("timer");
		}
		if (args.length == 2) {
			if (args[0].equalsIgnoreCase("timer"))
				return List.of("add", "remove");
		}
		if (args.length == 3) {
			if (args[0].equalsIgnoreCase("timer"))
				return Bukkit.getOnlinePlayers().stream().map(HumanEntity::getName)
						.filter(s -> s.toLowerCase().startsWith(args[2].toLowerCase())).toList();
		}
		return null;
	}
	
}
