package io.github.trev.marker;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.Collections;
import java.util.Set;
import java.util.stream.Collectors;

public class MarkStore {
    private final JavaPlugin plugin;
    private File file;
    private FileConfiguration config;
    private DiscordWebhook discordWebhook;

    public MarkStore(JavaPlugin plugin) {
        this.plugin = plugin;
    }

    public void load() {
        file = new File(plugin.getDataFolder(), "marks.yml");

        if (!file.exists()) {
            file.getParentFile().mkdirs();
            try {
                file.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        config = YamlConfiguration.loadConfiguration(file);
    }

    public void save() {
        try {
            config.save(file);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void setMark(Player player, String label, Location loc) {
        String path = label;

        int x = (int) Math.round(loc.getX());
        int y = (int) Math.round(loc.getY());
        int z = (int) Math.round(loc.getZ());
        String dimension = getDimension(loc.getWorld());
        String creator = player.getName();

        config.set(path + ".x", (int) Math.round(loc.getX()));
        config.set(path + ".y", (int) Math.round(loc.getY()));
        config.set(path + ".z", (int) Math.round(loc.getZ()));
        config.set(path + ".dimension", getDimension(loc.getWorld()));
        config.set(path + ".creator", player.getName());

        save();

        // POST to Discord
        if (discordWebhook != null) {
            String prettyDimension = Character.toUpperCase(dimension.charAt(0)) + dimension.substring(1).toLowerCase();
            String message = String.format(
                    "\n**%s** (%s)\n%d %d %d\n-# by %s",
                    Mark.getPrettyTitle(label), prettyDimension, x, y, z, creator
            );
            Bukkit.getScheduler().runTaskAsynchronously(plugin, () -> {
                discordWebhook.sendMessage(message);
            });
        }
    }

    private String getDimension(World world) {
        switch (world.getEnvironment()) {
            case NETHER:
                return "NETHER";
            case THE_END:
                return "END";
            default:
                return "OVERWORLD";
        }
    }

    public Set<String> getMarks() {
        ConfigurationSection section = config.getConfigurationSection("");

        if (section == null) {
            return Collections.emptySet();
        }

        return section.getKeys(false);
    }

    public Mark getMarkData(String label) {
        String path = label;

        if (!config.contains(path)) return null;

        int x = config.getInt(path + ".x");
        int y = config.getInt(path + ".y");
        int z = config.getInt(path + ".z");
        String dim = config.getString(path + ".dimension");
        String creator = config.getString(path + ".creator");

        return new Mark(label, x, y, z, dim, creator);
    }

    public void setWebhook(DiscordWebhook discordWebhook) {
        this.discordWebhook = discordWebhook;
    }

    public boolean deleteMark(String labelToDelete) {
        if (config.contains(labelToDelete)) {
            config.set(labelToDelete, null);
            save();
            return true;
        }

        return false;
    }
}
