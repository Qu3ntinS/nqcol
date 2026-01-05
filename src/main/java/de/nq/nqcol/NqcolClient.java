package de.nq.nqcol;

import de.nq.nqcol.config.NqcolConfig;
import de.nq.nqcol.logic.AutoScheduler;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.client.util.InputUtil;
import org.lwjgl.glfw.GLFW;

public class NqcolClient implements ClientModInitializer {
    private static KeyBinding toggleAutoHitKey;
    private static KeyBinding toggleAutoPressKey;

    @Override
    public void onInitializeClient() {
        // Initialize config
        NqcolConfig.load();

        // Register keybinds
        toggleAutoHitKey = KeyBindingHelper.registerKeyBinding(new KeyBinding(
            "key.nqcol.toggleAutoHit",
            InputUtil.Type.KEYSYM,
            GLFW.GLFW_KEY_H,
            "key.categories.nqcol"
        ));

        toggleAutoPressKey = KeyBindingHelper.registerKeyBinding(new KeyBinding(
            "key.nqcol.toggleAutoPress",
            InputUtil.Type.KEYSYM,
            GLFW.GLFW_KEY_P,
            "key.categories.nqcol"
        ));

        // Register tick handlers
        // START tick: Set keys pressed so they get processed during the tick
        ClientTickEvents.START_CLIENT_TICK.register(client -> {
            AutoScheduler.tickStart(client);
        });
        
        // END tick: Handle toggles, process keybindings, and release keys
        ClientTickEvents.END_CLIENT_TICK.register(client -> {
            // Handle keybind toggles
            while (toggleAutoHitKey.wasPressed()) {
                AutoScheduler.toggleAutoHit(client);
            }
            while (toggleAutoPressKey.wasPressed()) {
                AutoScheduler.toggleAutoPress(client);
            }

            // Process any keybindings that were pressed by AutoPress
            AutoScheduler.processPressedKeybindings(client);

            // Release keys and check for next actions
            AutoScheduler.tickEnd(client);
        });
    }
}

