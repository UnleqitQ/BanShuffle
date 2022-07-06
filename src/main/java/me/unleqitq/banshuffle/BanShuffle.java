package me.unleqitq.banshuffle;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.Statistic;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.*;
import java.util.stream.Stream;

public final class BanShuffle extends JavaPlugin {
	
	private static BanShuffle instance;
	
	public static Random rnd = new Random();
	public static CheckTask checkTask;
	public static JoinListener joinListener;
	public static StandListener standListener;
	
	public static Map<UUID, Integer> startTimes = new HashMap<>();
	public static Map<UUID, Material> targetMaterials = new HashMap<>();
	public static Set<UUID> achievedBlock = new HashSet<>();
	
	@Override
	public void onEnable() {
		instance = this;
		saveDefaultConfig();
		reloadConfig();
		try {
			loadData();
		}
		catch (IOException e) {
			throw new RuntimeException(e);
		}
		loadMaterials();
		checkTask = new CheckTask();
		checkTask.runTaskTimerAsynchronously(this, 20L, getConfig().getInt("check-interval", 20));
		joinListener = new JoinListener();
		standListener = new StandListener();
		Bukkit.getPluginManager().registerEvents(joinListener, this);
		Bukkit.getPluginManager().registerEvents(standListener, this);
	}
	
	@Override
	public void onDisable() {
		try {
			saveData();
		}
		catch (IOException e) {
			throw new RuntimeException(e);
		}
	}
	
	public static void loadData() throws IOException {
		File f = new File(getInstance().getDataFolder(), "data");
		if (!f.exists())
			return;
		ByteBuf buffer = Unpooled.wrappedBuffer(Files.readAllBytes(f.toPath()));
		{
			int size = buffer.readInt();
			for (int i = 0; i < size; i++) {
				UUID uuid = readUUID(buffer);
				int start = buffer.readInt();
				String targetS = readUTF(buffer);
				Material target = Material.getMaterial(targetS);
				startTimes.put(uuid, start);
				targetMaterials.put(uuid, target);
			}
		}
		{
			int size = buffer.readInt();
			for (int i = 0; i < size; i++) {
				UUID uuid = readUUID(buffer);
				achievedBlock.add(uuid);
			}
		}
	}
	
	public static void saveData() throws IOException {
		File f = new File(getInstance().getDataFolder(), "data");
		getInstance().getDataFolder().mkdirs();
		if (!f.exists())
			f.createNewFile();
		ByteBuf buffer = Unpooled.buffer();
		Map<UUID, Integer> _startTimes = new HashMap<>(startTimes);
		Map<UUID, Material> _targetMaterials = new HashMap<>(targetMaterials);
		Set<UUID> _achievedBlock = new HashSet<>(achievedBlock);
		if (_startTimes.size() != _targetMaterials.size())
			return;
		buffer.writeInt(_startTimes.size());
		for (UUID uuid : _startTimes.keySet()) {
			writeUUID(buffer, uuid);
			buffer.writeInt(_startTimes.get(uuid));
			writeUTF(buffer, _targetMaterials.get(uuid).name());
		}
		buffer.writeInt(_achievedBlock.size());
		for (UUID uuid : achievedBlock) {
			writeUUID(buffer, uuid);
		}
		Files.write(f.toPath(), buffer.array());
	}
	
	public static String readUTF(ByteBuf buffer) {
		int length = buffer.readInt();
		return String.valueOf(buffer.readCharSequence(length, StandardCharsets.US_ASCII));
	}
	
	public static void writeUTF(ByteBuf buffer, String data) {
		buffer.writeInt(data.length());
		buffer.writeCharSequence(data, StandardCharsets.US_ASCII);
	}
	
	public static UUID readUUID(ByteBuf buffer) {
		long mostSigBits = buffer.readLong();
		long leastSigBits = buffer.readLong();
		return new UUID(mostSigBits, leastSigBits);
	}
	
	public static void writeUUID(ByteBuf buffer, UUID data) {
		buffer.writeLong(data.getMostSignificantBits());
		buffer.writeLong(data.getLeastSignificantBits());
	}
	
	public static BanShuffle getInstance() {
		return instance;
	}
	
	
	public static @NotNull FileConfiguration config() {
		return getInstance().getConfig();
	}
	
	public static List<Material> materials = new ArrayList<>();
	
	public static void loadMaterials() {
		materials = new ArrayList<>();
		Stream<Material> stream = Arrays.stream(Material.values()).filter(Material::isBlock).filter(m -> !m.isAir())
				.filter(m -> !m.isLegacy());
		if (config().getBoolean("only-collidables", true))
			stream = stream.filter(Material::isCollidable);
		List<String> blocked = config().getStringList("blocked-blocks");
		stream.filter(m -> blocked.stream().noneMatch(b -> m.getKey().getKey().equalsIgnoreCase(b)));
		materials.addAll(stream.toList());
	}
	
	public static Material getBlock() {
		return materials.get(rnd.nextInt(materials.size()));
	}
	
	public static void applyNewBlock(Player player) {
		achievedBlock.remove(player.getUniqueId());
		Material target = getBlock();
		targetMaterials.put(player.getUniqueId(), target);
		startTimes.put(player.getUniqueId(), player.getStatistic(Statistic.PLAY_ONE_MINUTE));
		player.sendMessage(
				Component.text("§aYour new target block is: ").append(Component.translatable(target.translationKey())));
	}
	
}
