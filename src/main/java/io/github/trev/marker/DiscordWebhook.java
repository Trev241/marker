package io.github.trev.marker;

import org.bukkit.plugin.java.JavaPlugin;

import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URI;
import java.net.URL;

public class DiscordWebhook {
    private final JavaPlugin plugin;
    private final String webhookUrl;

    public DiscordWebhook(JavaPlugin plugin, String webhookUrl) {
        this.plugin = plugin;
        this.webhookUrl = webhookUrl;
    }

    public void sendMessage(String content) {
        try {
            URL url = URI.create(webhookUrl).toURL();
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("POST");
            conn.setDoOutput(true);
            conn.addRequestProperty("Content-Type", "application/json");

            // JSON payload
            content = content.replace("\\", "\\\\")    // escape backslashes first
                    .replace("\"", "\\\"")    // escape quotes
                    .replace("\n", "\\n");    // escape newlines
            String json = String.format("{\"content\":\"%s\"}", content);
            plugin.getLogger().info(json);

            try (OutputStream os = conn.getOutputStream()) {
                os.write(json.getBytes());
                os.flush();
            }

            int responseCode = conn.getResponseCode();
            if (responseCode >= 400) {
                plugin.getLogger().severe("Discord webhook error: " + responseCode);
            }

            conn.disconnect();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
