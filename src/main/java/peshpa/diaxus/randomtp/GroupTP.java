package peshpa.diaxus.randomtp;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.HashSet;
import java.util.Set;

public class GroupTP implements Listener {

    private final JavaPlugin plugin;
    private final Material block1;
    private final Material block2;
    private final Material block4;
    private final Set<Player> playersOnPlatform = new HashSet<>();
    private final RTPCommandExecutor rtpCommandExecutor;

    public GroupTP(JavaPlugin plugin, Material block1, Material block2, Material block4, RTPCommandExecutor rtpCommandExecutor) {
        this.plugin = plugin;
        this.block1 = block1;
        this.block2 = block2;
        this.block4 = block4;
        this.rtpCommandExecutor = rtpCommandExecutor;
    }

    @EventHandler
    public void onBlockPlace(BlockPlaceEvent event) {
        Block placedBlock = event.getBlock();
        Material placedBlockType = placedBlock.getType();

        if (placedBlockType == block2) {
            Block blockBelow = placedBlock.getRelative(BlockFace.DOWN);
            Material blockBelowType = blockBelow.getType();

            if (blockBelowType == block1) {
                if (isBlock4Above(placedBlock)) {
                }
            }
        }
    }

    @EventHandler
    public void onPlayerMove(PlayerMoveEvent event) {
        Player player = event.getPlayer();
        Block blockBelowPlayer = player.getLocation().getBlock().getRelative(BlockFace.DOWN);

        if (blockBelowPlayer.getType() == block2) {
            Block blockBelow = blockBelowPlayer.getRelative(BlockFace.DOWN);

            if (blockBelow.getType() == block1) {
                playersOnPlatform.add(player);

                if (playersOnPlatform.size() == 2) {
                    teleportPlayers(playersOnPlatform);
                    playersOnPlatform.clear();
                }
            } else {
                playersOnPlatform.remove(player);
            }
        } else {
            playersOnPlatform.remove(player);
        }
    }

    private boolean isBlock4Above(Block startBlock) {
        Block blockAbove1 = startBlock.getRelative(BlockFace.UP);
        Block blockAbove2 = blockAbove1.getRelative(BlockFace.UP);

        return blockAbove1.getType() == block4 || blockAbove2.getType() == block4;
    }

    private void teleportPlayers(Set<Player> players) {
        Location randomLocation = rtpCommandExecutor.getValidLocation(null);

        if (randomLocation != null) {
            for (Player player : players) {
                if (player != null && player.isOnline()) {
                    player.teleport(randomLocation);
                } else {
                    plugin.getLogger().warning("Player is null or offline, cannot teleport.");
                }
            }
        } else {
            for (Player player : players) {
                if (player != null && player.isOnline()) {
                }
            }
        }
    }
}