package net.jay.envfilex.platform;

import com.intellij.execution.configurations.ConfigurationType;
import com.intellij.execution.configurations.ConfigurationTypeBase;
import com.intellij.icons.AllIcons;
import org.jetbrains.annotations.NotNull;

/**
 * Configuration type for EnvFile
 */
public class EnvFileConfigurationType extends ConfigurationTypeBase {

    public static final String ID = "EnvFileConfigurationType";

    public EnvFileConfigurationType() {
        super(ID, "EnvFile Configuration", "EnvFile configuration type", AllIcons.General.Settings);
    }

    @NotNull
    public static ConfigurationType getInstance() {
        return ConfigurationType.CONFIGURATION_TYPE_EP.findExtensionOrFail(EnvFileConfigurationType.class);
    }
}
