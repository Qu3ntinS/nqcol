package de.nq.nqcol.logic;

import de.nq.nqcol.config.NqcolConfig;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayNetworkHandler;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;

/**
 * Checks the player's position each tick and executes a configurable command
 * when the player is within a configurable radius of target coordinates.
 * The command is repeated at a configurable interval while the player remains in range.
 */
public class CoordinateTrigger {
    private static long nextExecutionTimeMs = 0;

    /**
     * Called each client tick to check position and execute command if conditions are met.
     */
    public static void tick(MinecraftClient client) {
        try {
            tickInternal(client);
        } catch (Exception e) {
            // Prevent any exception from crashing the tick loop
            if (client.inGameHud != null) {
                Text errorText = Text.literal("[NQCOL] ")
                    .formatted(Formatting.LIGHT_PURPLE)
                    .append(Text.literal("CoordTrigger error: " + e.getMessage()).formatted(Formatting.RED));
                client.inGameHud.setOverlayMessage(errorText, false);
            }
        }
    }

    private static void tickInternal(MinecraftClient client) {
        NqcolConfig config = NqcolConfig.get();
        if (config == null || config.coordinateTrigger == null) {
            return;
        }
        NqcolConfig.CoordinateTriggerConfig triggerConfig = config.coordinateTrigger;

        // Check if feature is enabled
        if (!triggerConfig.enabled) {
            return;
        }

        // Check if world and player exist
        ClientPlayerEntity player = client.player;
        if (player == null || client.world == null) {
            return;
        }

        // Respect onlyRunInGame setting
        if (config.onlyRunInGame && client.currentScreen != null) {
            return;
        }

        // Use block position comparison for reliable matching.
        // The player's block position is the block their feet are in.
        int playerBlockX = player.getBlockX();
        int playerBlockY = player.getBlockY();
        int playerBlockZ = player.getBlockZ();

        double dx = playerBlockX - triggerConfig.triggerX;
        double dy = playerBlockY - triggerConfig.triggerY;
        double dz = playerBlockZ - triggerConfig.triggerZ;
        double distance = Math.sqrt(dx * dx + dy * dy + dz * dz);

        // Check if player is within radius
        if (distance <= triggerConfig.radius) {
            long now = System.currentTimeMillis();
            if (now >= nextExecutionTimeMs) {
                executeCommand(client, triggerConfig.command);
                nextExecutionTimeMs = now + triggerConfig.intervalMs;
            }
        }
    }

    /**
     * Executes a command or chat message.
     * If the command starts with '/', it is sent as a slash command.
     * Otherwise, it is sent as a chat message.
     */
    private static void executeCommand(MinecraftClient client, String command) {
        if (command == null) {
            return;
        }

        String trimmed = command.trim();
        if (trimmed.isEmpty()) {
            return;
        }

        ClientPlayNetworkHandler networkHandler = client.getNetworkHandler();
        if (networkHandler == null) {
            return;
        }

        try {
            if (trimmed.startsWith("/")) {
                // Send as slash command (without the leading '/')
                String cmd = trimmed.substring(1).trim();
                if (!cmd.isEmpty()) {
                    networkHandler.sendCommand(cmd);
                }
            } else {
                // Send as chat message
                networkHandler.sendChatMessage(trimmed);
            }
        } catch (Exception e) {
            // Show error feedback on action bar if command execution fails
            if (client.inGameHud != null) {
                Text errorText = Text.literal("[NQCOL] ")
                    .formatted(Formatting.LIGHT_PURPLE)
                    .append(Text.literal("Command failed: " + e.getMessage()).formatted(Formatting.RED));
                client.inGameHud.setOverlayMessage(errorText, false);
            }
        }
    }
}
