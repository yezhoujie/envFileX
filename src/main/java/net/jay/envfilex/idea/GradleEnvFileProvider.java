package net.jay.envfilex.idea;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import com.intellij.execution.ExecutionException;
import com.intellij.execution.Executor;
import com.intellij.execution.application.ApplicationConfiguration;
import com.intellij.execution.runners.ExecutionEnvironment;
import com.intellij.openapi.project.Project;
import com.intellij.task.ExecuteRunConfigurationTask;
import net.jay.envfilex.platform.EnvFileEnvironmentVariables;
import net.jay.envfilex.platform.ui.EnvFileConfigurationEditor;
import org.jetbrains.plugins.gradle.execution.build.GradleExecutionEnvironmentProvider;
import org.jetbrains.plugins.gradle.service.execution.GradleRunConfiguration;

public class GradleEnvFileProvider implements GradleExecutionEnvironmentProvider {

    @Override
    public boolean isApplicable(final ExecuteRunConfigurationTask executeRunConfigurationTask) {
        return executeRunConfigurationTask.getRunProfile() instanceof ApplicationConfiguration;
    }

    @Override
    public ExecutionEnvironment createExecutionEnvironment(
            final Project project,
            final ExecuteRunConfigurationTask executeRunConfigurationTask,
            final Executor executor
    ) {
        final ExecutionEnvironment environment = delegateProvider(executeRunConfigurationTask)
                .map(provider -> provider.createExecutionEnvironment(project, executeRunConfigurationTask, executor))
                .orElse(null);

        if (environment != null && environment.getRunProfile() instanceof GradleRunConfiguration targetConfig) {
            final ApplicationConfiguration sourceConfig = (ApplicationConfiguration) executeRunConfigurationTask.getRunProfile();
            applyEnvFile(sourceConfig, targetConfig);
        }

        return environment;
    }

    private void applyEnvFile(final ApplicationConfiguration sourceConfig, final GradleRunConfiguration targetConfig) {
        Map<String, String> newEnv;
        try {
            newEnv = new EnvFileEnvironmentVariables(
                    EnvFileConfigurationEditor.getEnvFileSetting(sourceConfig)
            )
                    .render(
                            sourceConfig.getProject(),
                            sourceConfig.getEnvs(),
                            sourceConfig.isPassParentEnvs()
                    );

        } catch (ExecutionException e) {
            throw new RuntimeException(e);
        }

        if (newEnv == null) {
            return;
        }

        targetConfig.getSettings().setEnv(new HashMap<>(newEnv));
    }

    private Optional<GradleExecutionEnvironmentProvider> delegateProvider(
            final ExecuteRunConfigurationTask executeRunConfigurationTask
    ) {
        return GradleExecutionEnvironmentProvider.EP_NAME.extensions()
                .filter(provider -> provider != this && provider.isApplicable(executeRunConfigurationTask))
                .findFirst();
    }
}
