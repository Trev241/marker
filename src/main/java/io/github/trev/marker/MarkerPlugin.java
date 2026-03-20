package io.github.trev.marker;

import org.bukkit.plugin.java.JavaPlugin;

public class MarkerPlugin extends JavaPlugin {
    private MarkStore storage;

    @Override
    public void onEnable() {
        saveDefaultConfig();

        storage = new MarkStore(this);
        storage.load();

        String url = getConfig().getString("webhook-url");
        if (url != null && !url.isEmpty()) {
            DiscordWebhook discordWebhook = new DiscordWebhook(this, url);
            storage.setWebhook(discordWebhook);
            getLogger().info("Discord webhook enabled!");
        } else {
            getLogger().info("No Discord webhook configured.");
        }

        getCommand("mark").setExecutor(new MarkCommand(storage));
        getLogger().info("MarkPlugin enabled!");
    }

    @Override
    public void onDisable() {
        storage.save();
        getLogger().info("MarkPlugin disabled!");
    }
}

