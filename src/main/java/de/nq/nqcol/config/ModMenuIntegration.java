package de.nq.nqcol.config;

import com.terraformersmc.modmenu.api.ConfigScreenFactory;
import com.terraformersmc.modmenu.api.ModMenuApi;
import me.shedaniel.autoconfig.AutoConfig;

/**
 * ModMenu integration. This entrypoint is only registered if ModMenu is installed.
 * If ModMenu is not installed, this class will not be loaded and the entrypoint will be skipped.
 */
public class ModMenuIntegration implements ModMenuApi {
    @Override
    public ConfigScreenFactory<?> getModConfigScreenFactory() {
        return parent -> AutoConfig.getConfigScreen(NqcolConfig.class, parent).get();
    }
}

