package peshpa.diaxus.randomtp.MessagesConfig;

import net.md_5.bungee.api.ChatColor;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import peshpa.diaxus.randomtp.RandomTP;

import java.io.File;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Java {

    private final RandomTP plugin;
    private FileConfiguration config;
    private final File file;

    public Java(RandomTP plugin) {
        this.plugin = plugin;
        this.file = new File(plugin.getDataFolder(), "messages.yml");
        if (!file.exists()) {
            plugin.saveResource("messages.yml", false);
        }
        reload();
    }

    public void reload() {
        config = YamlConfiguration.loadConfiguration(file);
    }

    public String getMessage(String key) {
        String message = config.getString(key, "Message not found");
        return colorize(message);
    }

    private String colorize(String text) {
        text = ChatColor.translateAlternateColorCodes('&', text);

        Pattern hexPattern = Pattern.compile("&#([0-9A-Fa-f]{6})");
        Matcher matcher = hexPattern.matcher(text);

        StringBuffer sb = new StringBuffer();
        while (matcher.find()) {
            String hexColor = matcher.group(1);
            String color = net.md_5.bungee.api.ChatColor.of("#" + hexColor).toString();
            matcher.appendReplacement(sb, color);
        }
        matcher.appendTail(sb);

        return sb.toString();
    }
}