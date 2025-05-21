package net.jay.envfilex.core.providers.yaml;

import java.util.Map;
import java.util.function.Consumer;

import net.jay.envfilex.core.EnvVarsProvider;
import net.jay.envfilex.core.EnvVarsProviderFactory;
import net.jay.envfilex.core.providers.EnvFileExecutor;
import net.jay.envfilex.core.providers.EnvFileReader;
import net.jay.envfilex.core.providers.SingleFileEnvVarsProvider;
import org.jetbrains.annotations.NotNull;
import org.yaml.snakeyaml.Yaml;

public class YamlEnvFileParserFactory implements EnvVarsProviderFactory {
    private static final Yaml YAML = new Yaml();

    @Override
    public EnvVarsProvider createProvider(Map<String, String> baseEnvVars, Consumer<String> logger) {
        return SingleFileEnvVarsProvider.builder()
                .reader(EnvFileReader.DEFAULT)
                .executor(EnvFileExecutor.DEFAULT)
                .parser(
                        new YamlEnvFileParser(YAML)
                )
                .logger(logger)
                .build();
    }

    @NotNull
    public String getTitle() {
        return "JSON/YAML";
    }

    @Override
    public boolean isEditable() {
        return true;
    }

}
