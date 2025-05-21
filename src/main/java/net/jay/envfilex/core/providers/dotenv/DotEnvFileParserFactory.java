package net.jay.envfilex.core.providers.dotenv;

import java.util.Map;
import java.util.function.Consumer;

import net.jay.envfilex.core.EnvVarsProvider;
import net.jay.envfilex.core.EnvVarsProviderFactory;
import net.jay.envfilex.core.providers.EnvFileExecutor;
import net.jay.envfilex.core.providers.EnvFileReader;
import net.jay.envfilex.core.providers.SingleFileEnvVarsProvider;
import org.jetbrains.annotations.NotNull;

public class DotEnvFileParserFactory implements EnvVarsProviderFactory {

    @Override
    public EnvVarsProvider createProvider(Map<String, String> baseEnvVars, Consumer<String> logger) {
        return SingleFileEnvVarsProvider.builder()
                .reader(EnvFileReader.DEFAULT)
                .executor(EnvFileExecutor.DEFAULT)
                .parser(DotEnvFileParser.INSTANCE)
                .logger(logger)
                .build();
    }


    @Override
    public @NotNull String getTitle() {
        return ".env";
    }

    @Override
    public boolean isEditable() {
        return true;
    }

}
