package peshpa.diaxus.randomtp;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import peshpa.diaxus.randomtp.MessagesConfig.Java;

import java.util.*;

public class RTPCommandExecutor implements CommandExecutor {

    private final RandomTP plugin;
    private final Java messagesConfig;
    private final int minX;
    private final int maxX;
    private final int minZ;
    private final int maxZ;
    private final int cooldown;
    private final Set<Material> blockedBlocks = new HashSet<>();
    private final Map<Player, Long> cooldownMap;
    private final Random random = new Random();

    public RTPCommandExecutor(RandomTP plugin, Java messagesConfig, int minX, int maxX, int minZ, int maxZ) {
        this.plugin = plugin;
        this.messagesConfig = messagesConfig;
        this.cooldownMap = new HashMap<>();

        this.minX = Math.min(minX, maxX);
        this.maxX = Math.max(minX, maxX);
        this.minZ = Math.min(minZ, maxZ);
        this.maxZ = Math.max(minZ, maxZ);

        this.cooldown = plugin.getConfig().getInt("teleport.cooldown", 60) * 1000;

        List<String> blockedBlockNames = plugin.getConfig().getStringList("teleport.blocked-blocks");
        for (String blockName : blockedBlockNames) {
            try {
                Material material = Material.valueOf(blockName.toUpperCase());
                blockedBlocks.add(material);
            } catch (IllegalArgumentException e) {
                Bukkit.getLogger().warning("Invalid block name in config: " + blockName);
            }
        }
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (sender instanceof Player) {
            Player player = (Player) sender;

            if (!player.hasPermission("randomtp.rtp.use")) {
                player.sendMessage(messagesConfig.getMessage("teleport.no-permission"));
                return true;
            }

            if (player.hasPermission("randomtp.bypass.cooldown")) {
                teleportPlayer(player);
            } else {
                long currentTime = System.currentTimeMillis();
                Long lastUsed = cooldownMap.get(player);

                if (lastUsed != null && (currentTime - lastUsed < cooldown)) {
                    long timeLeft = (cooldown - (currentTime - lastUsed)) / 1000;
                    player.sendMessage(messagesConfig.getMessage("teleport.cooldown")
                            .replace("{time}", String.valueOf(timeLeft)));
                    return true;
                }

                cooldownMap.put(player, currentTime);

                teleportPlayer(player);
            }
        } else {
            Bukkit.getConsoleSender().sendMessage(messagesConfig.getMessage("no-console"));
        }
        return true;
    }

    private void teleportPlayer(Player player) {
        Location randomLocation = getValidLocation(player);

        if (randomLocation != null) {
            player.teleport(randomLocation);

            String message = messagesConfig.getMessage("teleport.success");
            player.sendMessage(message.replace("{x}", String.valueOf(randomLocation.getBlockX()))
                    .replace("{y}", String.valueOf(randomLocation.getBlockY()))
                    .replace("{z}", String.valueOf(randomLocation.getBlockZ())));
        } else {
            player.sendMessage(messagesConfig.getMessage("teleport.failed"));
        }
    }

    public Location getValidLocation(Player player) {
        int attempts = 0;
        final int maxAttempts = 100;
        final org.bukkit.World world = (player != null) ? player.getWorld() : Bukkit.getWorlds().get(0);

        while (attempts < maxAttempts) {
            int x = random.nextInt(maxX - minX + 1) + minX;
            int z = random.nextInt(maxZ - minZ + 1) + minZ;
            int y = world.getHighestBlockYAt(x, z);

            Location location = new Location(world, x, y, z);
            Material blockType = location.getBlock().getType();

            if (!blockedBlocks.contains(blockType)) {
                return location;
            }

            attempts++;
        }

        return null;
    }
}
