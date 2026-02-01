package de.nq.nqcol.config;

import me.shedaniel.autoconfig.AutoConfig;
import me.shedaniel.autoconfig.ConfigData;
import me.shedaniel.autoconfig.annotation.Config;
import me.shedaniel.autoconfig.annotation.ConfigEntry;
import me.shedaniel.autoconfig.serializer.GsonConfigSerializer;
import org.lwjgl.glfw.GLFW;

@Config(name = "nqcol")
public class NqcolConfig implements ConfigData {
    @ConfigEntry.Category("autoHit")
    @ConfigEntry.Gui.TransitiveObject
    public AutoHitConfig autoHit = new AutoHitConfig();

    @ConfigEntry.Category("autoPress")
    @ConfigEntry.Gui.TransitiveObject
    public AutoPressConfig autoPress = new AutoPressConfig();

    @ConfigEntry.Category("general")
    public boolean onlyRunInGame = true;

    @ConfigEntry.Category("general")
    @ConfigEntry.Gui.Tooltip
    public boolean onlyAtNightAndDusk = false;

    public static void load() {
        AutoConfig.register(NqcolConfig.class, GsonConfigSerializer::new);
    }

    public static NqcolConfig get() {
        return AutoConfig.getConfigHolder(NqcolConfig.class).getConfig();
    }

    public static class AutoHitConfig {
        @ConfigEntry.Gui.Tooltip
        public boolean enabled = false;

        @ConfigEntry.Gui.Tooltip
        public int minDelayMs = 5000;

        @ConfigEntry.Gui.Tooltip
        public int maxDelayMs = 5500;

        @ConfigEntry.Gui.Tooltip
        public int jitterMs = 80;
    }

    public static class AutoPressConfig {
        @ConfigEntry.Gui.Tooltip
        public boolean enabled = false;

        @ConfigEntry.Gui.Tooltip
        public int minDelayMs = 30000;

        @ConfigEntry.Gui.Tooltip
        public int maxDelayMs = 30500;

        @ConfigEntry.Gui.Tooltip
        public int keyToPress = GLFW.GLFW_KEY_O;
    }
}

