package net.jay.envfilex.core.providers.runconfig;

import java.util.Map;
import java.util.function.Consumer;

import net.jay.envfilex.core.EnvVarsProvider;
import net.jay.envfilex.core.EnvVarsProviderFactory;
import org.jetbrains.annotations.NotNull;

public class RunConfigEnvVarsProviderFactory implements EnvVarsProviderFactory {

    @Override
    public EnvVarsProvider createProvider(Map<String, String> baseEnvVars, Consumer<String> logger) {
        return new RunConfigEnvVarsProvider(baseEnvVars);
    }

    @Override
    public @NotNull String getTitle() {
        return "Run Config";
    }

    @Override
    public boolean isEditable() {
        return false;
    }

}
