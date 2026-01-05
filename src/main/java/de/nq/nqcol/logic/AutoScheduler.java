package de.nq.nqcol.logic;

import de.nq.nqcol.config.NqcolConfig;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.client.util.InputUtil;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import org.lwjgl.glfw.GLFW;

public class AutoScheduler {
    private static long nextAutoHitTimeMs = 0;
    private static long nextAutoPressTimeMs = 0;
    private static boolean autoHitActive = false;
    private static boolean autoPressActive = false;
    private static KeyBinding pressedAttackKey = null;
    private static KeyBinding pressedAutoPressKey = null;

    /**
     * Toggles AutoHit on/off and provides user feedback.
     */
    public static void toggleAutoHit(MinecraftClient client) {
        NqcolConfig config = NqcolConfig.get();
        autoHitActive = !autoHitActive;
        config.autoHit.enabled = autoHitActive;

        // Save config
        me.shedaniel.autoconfig.AutoConfig.getConfigHolder(NqcolConfig.class).save();

        String status = autoHitActive ? "ON" : "OFF";
        // Create decorated action bar message with purple [NQCOL] prefix
        Text messageText = Text.literal("[NQCOL] ")
            .formatted(Formatting.LIGHT_PURPLE)
            .append(Text.literal("AutoHit: ").formatted(Formatting.WHITE))
            .append(Text.literal(status).formatted(autoHitActive ? Formatting.GREEN : Formatting.RED));
        
        if (client.inGameHud != null) {
            // Only show action bar message (no chat message)
            client.inGameHud.setOverlayMessage(messageText, false);
        }

        if (autoHitActive) {
            scheduleNextAutoHit();
        }
    }

    /**
     * Toggles AutoPress on/off and provides user feedback.
     */
    public static void toggleAutoPress(MinecraftClient client) {
        NqcolConfig config = NqcolConfig.get();
        autoPressActive = !autoPressActive;
        config.autoPress.enabled = autoPressActive;

        // Save config
        me.shedaniel.autoconfig.AutoConfig.getConfigHolder(NqcolConfig.class).save();

        String status = autoPressActive ? "ON" : "OFF";
        // Create decorated action bar message with purple [NQCOL] prefix
        Text messageText = Text.literal("[NQCOL] ")
            .formatted(Formatting.LIGHT_PURPLE)
            .append(Text.literal("AutoPress: ").formatted(Formatting.WHITE))
            .append(Text.literal(status).formatted(autoPressActive ? Formatting.GREEN : Formatting.RED));
        
        if (client.inGameHud != null) {
            // Only show action bar message (no chat message)
            client.inGameHud.setOverlayMessage(messageText, false);
        }

        if (autoPressActive) {
            scheduleNextAutoPress();
        }
    }

    /**
     * Called at the START of each client tick to set keys pressed.
     */
    public static void tickStart(MinecraftClient client) {
        long currentTime = System.currentTimeMillis();
        NqcolConfig config = NqcolConfig.get();

        // Check if we should run (world/player exists, no screen if onlyRunInGame is true)
        if (!shouldRun(client, config)) {
            return;
        }

        // Update active states from config (in case config was changed externally)
        autoHitActive = config.autoHit.enabled;
        autoPressActive = config.autoPress.enabled;

        // Process AutoHit - set key pressed at START of tick so it gets processed
        if (autoHitActive && currentTime >= nextAutoHitTimeMs) {
            performAutoHit(client);
            scheduleNextAutoHit();
        }

        // Process AutoPress - set key pressed at START of tick so it gets processed
        if (autoPressActive && currentTime >= nextAutoPressTimeMs) {
            performAutoPress(client);
            scheduleNextAutoPress();
        }
    }

    /**
     * Called at the END of each client tick to release keys.
     */
    public static void tickEnd(MinecraftClient client) {
        // Release any keys that were pressed during this tick
        releasePressedKeys(client);
    }

    /**
     * Processes keybindings that were pressed by AutoPress.
     * This ensures the keybinding action is actually triggered.
     */
    public static void processPressedKeybindings(MinecraftClient client) {
        // Process the AutoPress keybinding if it was pressed
        // The keybinding action is triggered when wasPressed() returns true
        // This happens automatically when we set it from false to true
        // We just need to ensure the keybinding system processes it
        if (pressedAutoPressKey != null) {
            // Check wasPressed() which will trigger the keybinding's action handlers
            // This is how Minecraft processes keybinding actions
            boolean triggered = pressedAutoPressKey.wasPressed();
            // The wasPressed() check itself processes the keybinding action
        }
    }

    /**
     * Checks if automation should run based on game state.
     */
    private static boolean shouldRun(MinecraftClient client, NqcolConfig config) {
        // Check if world and player exist
        if (client.world == null || client.player == null) {
            return false;
        }

        // Check if screen is open and onlyRunInGame is enabled
        if (config.onlyRunInGame && client.currentScreen != null) {
            return false;
        }

        return true;
    }

    /**
     * Performs a single attack action.
     */
    private static void performAutoHit(MinecraftClient client) {
        ClientPlayerEntity player = client.player;
        if (player == null || client.interactionManager == null) {
            return;
        }

        // Use the interaction manager to perform an attack directly
        // This is the most reliable way to trigger an attack
        if (client.crosshairTarget != null) {
            if (client.crosshairTarget.getType() == net.minecraft.util.hit.HitResult.Type.ENTITY) {
                // Attack the entity we're looking at
                net.minecraft.util.hit.EntityHitResult entityHit = (net.minecraft.util.hit.EntityHitResult) client.crosshairTarget;
                client.interactionManager.attackEntity(player, entityHit.getEntity());
                // Trigger the hand swing animation to make it look natural
                player.swingHand(net.minecraft.util.Hand.MAIN_HAND);
            } else {
                // For blocks or air, simulate the attack key being pressed
                // We need to set it pressed and let Minecraft process it
                KeyBinding attackKey = client.options.attackKey;
                if (attackKey != null) {
                    attackKey.setPressed(true);
                    pressedAttackKey = attackKey;
                    // Also trigger the hand swing animation
                    player.swingHand(net.minecraft.util.Hand.MAIN_HAND);
                }
            }
        } else {
            // Fallback: use the attack keybinding
            KeyBinding attackKey = client.options.attackKey;
            if (attackKey != null) {
                attackKey.setPressed(true);
                pressedAttackKey = attackKey;
                // Also trigger the hand swing animation
                player.swingHand(net.minecraft.util.Hand.MAIN_HAND);
            }
        }
    }

    /**
     * Performs a single key press action.
     * This method injects a key press event that all mods (including Meteor Client) can detect.
     */
    private static void performAutoPress(MinecraftClient client) {
        NqcolConfig config = NqcolConfig.get();
        int keyCode = config.autoPress.keyToPress;
        InputUtil.Key targetKey = InputUtil.fromKeyCode(keyCode, GLFW.glfwGetKeyScancode(keyCode));

        // To trigger Meteor Client keybindings, we need to inject the key event
        // at the input level. We'll use Minecraft's keyboard input system.
        // First, set all matching keybindings as pressed
        for (KeyBinding binding : client.options.allKeys) {
            if (binding.getDefaultKey().getCode() == keyCode) {
                if (!binding.isPressed()) {
                    binding.setPressed(true);
                    pressedAutoPressKey = binding;
                }
                break;
            }
        }
        
        // Also inject the key event through Minecraft's keyboard system
        // This ensures mods like Meteor Client that listen to keyboard events can detect it
        if (client.keyboard != null) {
            // Use the keyboard's onKey method to inject the key press
            // This simulates an actual key press that all mods can detect
            long windowHandle = client.getWindow().getHandle();
            int scancode = targetKey.getCode();
            int action = GLFW.GLFW_PRESS;
            int mods = 0;
            
            // Call the keyboard's key callback directly
            // This will trigger all keyboard event handlers, including Meteor Client's
            client.keyboard.onKey(windowHandle, keyCode, scancode, action, mods);
        }
    }

    /**
     * Schedules the next AutoHit action.
     */
    private static void scheduleNextAutoHit() {
        NqcolConfig config = NqcolConfig.get();
        long delay = HumanDelay.calculateGaussianDelay(
            config.autoHit.minDelayMs,
            config.autoHit.maxDelayMs,
            config.autoHit.jitterMs
        );
        nextAutoHitTimeMs = System.currentTimeMillis() + delay;
    }

    /**
     * Schedules the next AutoPress action.
     */
    private static void scheduleNextAutoPress() {
        NqcolConfig config = NqcolConfig.get();
        long delay = HumanDelay.calculateUniformDelay(
            config.autoPress.minDelayMs,
            config.autoPress.maxDelayMs
        );
        nextAutoPressTimeMs = System.currentTimeMillis() + delay;
    }

    /**
     * Releases keys that were pressed by automation in the previous tick.
     * This ensures single-press behavior (not continuous holding).
     */
    private static void releasePressedKeys(MinecraftClient client) {
        // Release attack key if it was pressed by us
        if (pressedAttackKey != null) {
            pressedAttackKey.setPressed(false);
            pressedAttackKey = null;
        }

        // Release auto press key if it was pressed by us
        if (pressedAutoPressKey != null) {
            pressedAutoPressKey.setPressed(false);
            pressedAutoPressKey = null;
        }
        
        // Also release the key event for AutoPress through the keyboard system
        // This ensures we simulate a complete press/release cycle
        NqcolConfig config = NqcolConfig.get();
        if (config.autoPress.enabled && client.keyboard != null) {
            int keyCode = config.autoPress.keyToPress;
            long windowHandle = client.getWindow().getHandle();
            InputUtil.Key targetKey = InputUtil.fromKeyCode(keyCode, GLFW.glfwGetKeyScancode(keyCode));
            int scancode = targetKey.getCode();
            int action = GLFW.GLFW_RELEASE;
            int mods = 0;
            // Release the key through the keyboard system
            client.keyboard.onKey(windowHandle, keyCode, scancode, action, mods);
        }
    }
}

