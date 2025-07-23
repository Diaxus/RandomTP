package peshpa.diaxus.randomtp;

import org.bukkit.Material;
import org.bukkit.plugin.java.JavaPlugin;
import peshpa.diaxus.randomtp.MessagesConfig.Java;

public final class RandomTP extends JavaPlugin {

    private Java messagesConfig;
    private int minX;
    private int maxX;
    private int minZ;
    private int maxZ;

    @Override
    public void onEnable() {
        this.saveDefaultConfig();
        messagesConfig = new Java(this);

        minX = getConfig().getInt("teleport.minX", -5000);
        maxX = getConfig().getInt("teleport.maxX", 5000);
        minZ = getConfig().getInt("teleport.minZ", -5000);
        maxZ = getConfig().getInt("teleport.maxZ", 5000);

        Material block1 = Material.getMaterial(getConfig().getString("platform.block1", "BARRIER").toUpperCase());
        Material block2 = Material.getMaterial(getConfig().getString("platform.block2", "DIAMOND_BLOCK").toUpperCase());
        Material block4 = Material.getMaterial(getConfig().getString("platform.block4", "IRON_BLOCK").toUpperCase());

        if (block1 == null || block2 == null || block4 == null) {
            getLogger().warning("One or more of the platform blocks are invalid.");
            return;
        }

        RTPCommandExecutor rtpCommandExecutor = new RTPCommandExecutor(this, messagesConfig, minX, maxX, minZ, maxZ);

        this.getCommand("rtp").setExecutor(rtpCommandExecutor);

        this.getCommand("rtp.reload").setExecutor(new ReloadCommandExecutor(this));

        getServer().getPluginManager().registerEvents(new GroupTP(this, block1, block2, block4, rtpCommandExecutor), this);

        System.out.println("Successfully Enabled");
    }

    @Override
    public void onDisable() {
        System.out.println("Successfully Disabled");
    }

    public Java getMessagesConfig() {
        return messagesConfig;
    }

    public void reloadMessagesConfig() {
        messagesConfig.reload();
    }

    public int getMinX() {
        return minX;
    }

    public int getMaxX() {
        return maxX;
    }

    public int getMinZ() {
        return minZ;
    }

    public int getMaxZ() {
        return maxZ;
    }
}