package ru.karepanov35.aurarandomtp;

import cn.nukkit.Player;
import cn.nukkit.command.Command;
import cn.nukkit.command.CommandSender;
import cn.nukkit.event.Listener;
import cn.nukkit.level.Level;
import cn.nukkit.level.Location;
import cn.nukkit.level.Position;
import cn.nukkit.math.Vector3;
import cn.nukkit.plugin.PluginBase;
import cn.nukkit.scheduler.Task;
import cn.nukkit.utils.Config;
import cn.nukkit.utils.TextFormat;
import cn.nukkit.block.Block;
import cn.nukkit.block.BlockID;
import java.io.File;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Random;
import java.util.Set;

public class AuraRandomTP extends PluginBase implements Listener {
    private final Random random = new Random();
    private Config config;
    private int minX;
    private int maxX;
    private int minZ;
    private int maxZ;
    private int maxAttempts;
    private int maxY;
    private int minY;
    private int teleportDelay;
    private String messageOnlyPlayers;
    private String messageTeleportSuccess;
    private String titleTeleport;
    private String subtitleTeleport;
    private String titleDelay;
    private String subtitleDelay;
    private final Set<Integer> safeBlocks = new HashSet<>(Arrays.asList(
        BlockID.GRASS, BlockID.DIRT, BlockID.STONE, BlockID.SAND,
        BlockID.GRAVEL, BlockID.COBBLESTONE, BlockID.PLANKS, BlockID.SANDSTONE
    ));

    @Override
    public void onEnable() {
        updateConfig();
        loadConfig();
        this.getServer().getPluginManager().registerEvents(this, this);
        this.getLogger().info("§fПлагин §aвключен.§f Разработчик:§b https://github.com/karepanov35");
    }

    private void updateConfig() {
        File configFile = new File(this.getDataFolder(), "config.yml");
        if (!configFile.exists()) {
            this.saveResource("config.yml", false);
            return;
        }
        Config currentConfig = new Config(configFile, Config.YAML);
        Config defaultConfig = new Config();
        defaultConfig.load(this.getResource("config.yml"));
        boolean updated = false;
        for (String key : defaultConfig.getAll().keySet()) {
            if (!currentConfig.exists(key)) {
                currentConfig.set(key, defaultConfig.get(key));
                updated = true;
            }
        }
        if (updated) {
            currentConfig.save();
        }
    }

    private void loadConfig() {
        config = new Config(new File(this.getDataFolder(), "config.yml"), Config.YAML);
        minX = config.getInt("min-x");
        maxX = config.getInt("max-x");
        minZ = config.getInt("min-z");
        maxZ = config.getInt("max-z");
        maxAttempts = config.getInt("max-attempts");
        minY = config.getInt("min-y");
        maxY = config.getInt("max-y");
        teleportDelay = config.getInt("teleport-delay");
        messageOnlyPlayers = config.getString("messages.only-players");
        messageTeleportSuccess = config.getString("messages.teleport-success");
        titleTeleport = config.getString("messages.title-teleport");
        subtitleTeleport = config.getString("messages.subtitle-teleport");
        titleDelay = config.getString("messages.title-delay");
        subtitleDelay = config.getString("messages.subtitle-delay");
    }

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        if (cmd.getName().equalsIgnoreCase("rtp")) {
            if (!(sender instanceof Player)) {
                sender.sendMessage(TextFormat.colorize(messageOnlyPlayers));
                return true;
            }
            Player player = (Player) sender;
            Location safeLocation = findSafeLocation(player.getLevel());
            if (safeLocation == null) {
                safeLocation = forceFindSafeLocation(player.getLevel());
                if (safeLocation == null) {
                    return true;
                }
            }
            if (teleportDelay > 0) {
                scheduleTeleportWithDelay(player, safeLocation);
            } else {
                player.teleport(safeLocation);
                sendTeleportMessages(player, safeLocation);
            }
            return true;
        }
        return false;
    }

    private void scheduleTeleportWithDelay(Player player, Location safeLocation) {
        getServer().getScheduler().scheduleRepeatingTask(this, new Task() {
            int secondsLeft = teleportDelay;
            @Override
            public void onRun(int currentTick) {
                if (!player.isOnline() || secondsLeft <= 0) {
                    if (player.isOnline() && secondsLeft == 0) {
                        player.teleport(safeLocation);
                        sendTeleportMessages(player, safeLocation);
                    }
                    cancel();
                    return;
                }
                String subtitle = subtitleDelay.replace("{seconds}", String.valueOf(secondsLeft));
                player.sendTitle(TextFormat.colorize(titleDelay), TextFormat.colorize(subtitle), 0, 20, 0);
                secondsLeft--;
            }
        }, 20);
    }

    private void sendTeleportMessages(Player player, Location safeLocation) {
        String subtitle = subtitleTeleport
            .replace("{x}", String.valueOf(safeLocation.getFloorX()))
            .replace("{y}", String.valueOf(safeLocation.getFloorY()))
            .replace("{z}", String.valueOf(safeLocation.getFloorZ()));
        String successMessage = messageTeleportSuccess
            .replace("{x}", String.valueOf(safeLocation.getFloorX()))
            .replace("{y}", String.valueOf(safeLocation.getFloorY()))
            .replace("{z}", String.valueOf(safeLocation.getFloorZ()));
        player.sendTitle(TextFormat.colorize(titleTeleport), TextFormat.colorize(subtitle), 10, 40, 10);
        player.sendMessage(TextFormat.colorize(successMessage));
    }

    private Location findSafeLocation(Level level) {
        for (int i = 0; i < maxAttempts; i++) {
            int x = random.nextInt(maxX - minX + 1) + minX;
            int z = random.nextInt(maxZ - minZ + 1) + minZ;
            level.loadChunk(x >> 4, z >> 4);
            Location loc = findSafeY(level, x, z);
            if (loc != null && isPositionSafe(loc)) {
                return loc;
            }
        }
        return null;
    }

    private Location forceFindSafeLocation(Level level) {
        int step = 16;
        int spiralRadius = 100;
        int maxSpiralRadius = 2000;
        int maxSpiralAttempts = 100;

        for (int i = 0; i < maxSpiralAttempts; i++) {
            for (int xOffset = -spiralRadius; xOffset <= spiralRadius; xOffset += step) {
                for (int zOffset = -spiralRadius; zOffset <= spiralRadius; zOffset += step) {
                    int x = xOffset;
                    int z = zOffset;
                    level.loadChunk(x >> 4, z >> 4);
                    Location loc = findSafeY(level, x, z);
                    if (loc != null && isPositionSafe(loc)) {
                        return loc;
                    }
                }
            }
            spiralRadius += step;
            if (spiralRadius > maxSpiralRadius) break;
        }
        Position pos = level.getSpawnLocation();
        return new Location(pos.x, pos.y, pos.z, level);
    }

    private Location findSafeY(Level level, int x, int z) {
        for (int y = maxY; y >= minY; y--) {
            Location loc = new Location(x + 0.5, y, z + 0.5, level);
            if (isPositionSafe(loc)) {
                return loc;
            }
        }
        return null;
    }

    private boolean isPositionSafe(Location loc) {
        int x = loc.getFloorX();
        int y = loc.getFloorY();
        int z = loc.getFloorZ();
        Level level = loc.getLevel();
        Block blockUnder = level.getBlock(x, y - 1, z);
        Block blockFeet = level.getBlock(x, y, z);
        Block blockHead = level.getBlock(x, y + 1, z);
        if (blockUnder == null || blockFeet == null || blockHead == null) {
            return false;
        }
        return safeBlocks.contains(blockUnder.getId()) &&
               blockFeet.getId() == BlockID.AIR &&
               blockHead.getId() == BlockID.AIR &&
               !isDangerousBlock(blockUnder.getId());
    }

    private boolean isDangerousBlock(int blockId) {
        return blockId == BlockID.LAVA || blockId == BlockID.WATER ||
               blockId == BlockID.FIRE || blockId == BlockID.CACTUS ||
               blockId == BlockID.MAGMA || blockId == BlockID.STILL_LAVA ||
               blockId == BlockID.STILL_WATER || blockId == BlockID.OBSIDIAN ||
               blockId == BlockID.BEDROCK;
    }
}
