package net.jay.envfilex.idea;

import java.util.HashMap;
import java.util.Map;

import com.intellij.execution.ExecutionException;
import com.intellij.execution.RunConfigurationExtension;
import com.intellij.execution.configurations.JavaParameters;
import com.intellij.execution.configurations.RunConfigurationBase;
import com.intellij.execution.configurations.RunnerSettings;
import com.intellij.openapi.externalSystem.service.execution.ExternalSystemRunConfiguration;
import com.intellij.openapi.options.SettingsEditor;
import com.intellij.openapi.util.InvalidDataException;
import com.intellij.openapi.util.WriteExternalException;
import net.jay.envfilex.core.ReadOnceMap;
import net.jay.envfilex.platform.EnvFileEnvironmentVariables;
import net.jay.envfilex.platform.ui.EnvFileConfigurationEditor;
import org.jdom.Element;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class IdeaRunConfigurationExtension extends RunConfigurationExtension {

    @Nullable
    @Override
    protected String getEditorTitle() {
        return EnvFileConfigurationEditor.getEditorTitle();
    }

    @Nullable
    @Override
    protected <P extends RunConfigurationBase<?>> SettingsEditor<P> createEditor(@NotNull P configuration) {
        return new EnvFileConfigurationEditor<P>(configuration);
    }

    @NotNull
    @Override
    protected String getSerializationId() {
        return EnvFileConfigurationEditor.getSerializationId();
    }

    @Override
    protected void writeExternal(@NotNull RunConfigurationBase runConfiguration, @NotNull Element element) throws WriteExternalException {
        EnvFileConfigurationEditor.writeExternal(runConfiguration, element);
    }

    @Override
    protected void readExternal(@NotNull RunConfigurationBase runConfiguration, @NotNull Element element) throws InvalidDataException {
        EnvFileConfigurationEditor.readExternal(runConfiguration, element);
    }

    @Override
    protected void validateConfiguration(@NotNull RunConfigurationBase configuration, boolean isExecution) throws Exception {
        EnvFileConfigurationEditor.validateConfiguration(configuration, isExecution);
    }

    /**
     * Unlike other extensions the IDEA extension
     * calls this method instead of RunConfigurationExtensionBase#patchCommandLine method
     * that we could have used to update environment variables.
     */
    @Override
    public <T extends RunConfigurationBase<?>> void updateJavaParameters(
            @NotNull final T configuration,
            @NotNull final JavaParameters params,
            final RunnerSettings runnerSettings
    ) throws ExecutionException {
        Map<String, String> newEnv = new EnvFileEnvironmentVariables(
                EnvFileConfigurationEditor.getEnvFileSetting(configuration)
        )
                .render(
                        configuration.getProject(),
                        params.getEnv(),
                        params.isPassParentEnvs()
                );

        if (newEnv == null) {
            return;
        }

        // there is a chance that env is an immutable map,
        // that is why it is safer to replace it instead of updating it
        params.setEnv(new HashMap<>(newEnv));

        // The code below works based on assumptions about internal implementation of
        // ExternalSystemExecuteTaskTask and ExternalSystemExecutionSettings and therefore may break any time may it change
        // It seems to be the only way to get things working for run configurations such as Gradle, at least for now
        if (EnvFileConfigurationEditor.isEnableExperimentalIntegrations(configuration)) {
            if (configuration instanceof ExternalSystemRunConfiguration) {
                ExternalSystemRunConfiguration ext = (ExternalSystemRunConfiguration) configuration;

                ext.getSettings().setEnv(new ReadOnceMap<>(newEnv, ext.getSettings().getEnv()));
            }
        }
    }

    //

    @Override
    public boolean isApplicableFor(@NotNull RunConfigurationBase configuration) {
        return true;
    }

    @Override
    public boolean isEnabledFor(@NotNull RunConfigurationBase applicableConfiguration, @Nullable RunnerSettings runnerSettings) {
        return true;
    }

}
