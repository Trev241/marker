package io.github.trev.marker;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.Arrays;
import java.util.Set;
import java.util.stream.Collectors;

public class MarkCommand implements CommandExecutor {

    private final MarkStore storage;

    public MarkCommand(MarkStore storage) {
        this.storage = storage;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage("Only players can use this command.");
            return true;
        }

        Player player = (Player) sender;

        if (args.length == 0) {
            player.sendMessage("§cUsage: /mark <set|go|list|delete> [...]");
            return true;
        }

        String sub = args[0].toLowerCase();

        switch (sub) {
            case "set":
                if (args.length < 2) {
                    player.sendMessage("§cUsage: /mark set <label>");
                    return true;
                }

                String labelToSet = String.join(" ", Arrays.copyOfRange(args, 1, args.length)).toLowerCase();
                if (storage.getMarkData(labelToSet) != null) {
                    player.sendMessage("§cA mark with that label already exists.");
                    return true;
                }

                storage.setMark(player, labelToSet, player.getLocation());
                player.sendMessage("§aMark saved!");
                break;

            case "go":
                if (args.length < 2) {
                    player.sendMessage("§cUsage: /mark go <label>");
                    return true;
                }
                String labelToGo = String.join(" ", Arrays.copyOfRange(args, 1, args.length)).toLowerCase();
                Mark mark = storage.getMarkData(labelToGo);
                if (mark == null) {
                    player.sendMessage("§cNo such mark: " + labelToGo);
                    return true;
                }
                player.sendMessage("§aPosition: " + mark.x + ", " + mark.y + ", " + mark.z);
                break;

            case "list":
                Set<String> marks = storage.getMarks();
                if (marks.isEmpty()) {
                    player.sendMessage("§cNo marks saved.");
                } else {
                    player.sendMessage("§aMarks:\n" + String.join("\n", marks));
                }
                break;

            case "delete":
                if (args.length < 2) {
                    player.sendMessage("§cUsage: /mark delete <label>");
                    return true;
                }
                String labelToDelete = String.join(" ", Arrays.copyOfRange(args, 1, args.length));
                if (storage.deleteMark(labelToDelete)) {
                    player.sendMessage("§aDeleted mark '" + labelToDelete + "'.");
                } else {
                    player.sendMessage("§cNo such mark: " + labelToDelete);
                }
                break;

            default:
                player.sendMessage("§cUnknown subcommand: " + sub);
        }

        return true;
    }
}
