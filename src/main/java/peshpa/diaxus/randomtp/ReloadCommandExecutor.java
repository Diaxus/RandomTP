package peshpa.diaxus.randomtp;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class ReloadCommandExecutor implements CommandExecutor {

    private final RandomTP plugin;

    public ReloadCommandExecutor(RandomTP plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (sender instanceof Player) {
            Player player = (Player) sender;

            if (!player.hasPermission("randomtp.reload")) {
                player.sendMessage(plugin.getMessagesConfig().getMessage("reload.no-permission"));
                return true;
            }
        }

        plugin.reloadConfig();
        plugin.getMessagesConfig().reload();

        plugin.getCommand("rtp").setExecutor(new RTPCommandExecutor(
                plugin,
                plugin.getMessagesConfig(),
                plugin.getConfig().getInt("teleport.minX", -5000),
                plugin.getConfig().getInt("teleport.maxX", 5000),
                plugin.getConfig().getInt("teleport.minZ", -5000),
                plugin.getConfig().getInt("teleport.maxZ", 5000)
        ));

        sender.sendMessage(plugin.getMessagesConfig().getMessage("reload.success"));
        return true;
    }
}