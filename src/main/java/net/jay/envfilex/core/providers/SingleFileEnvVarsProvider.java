package net.jay.envfilex.core.providers;

import java.io.File;
import java.util.Map;
import java.util.function.Consumer;
import java.util.stream.Collectors;

import lombok.Builder;
import lombok.val;
import net.jay.envfilex.core.EnvVarsProvider;
import net.jay.envfilex.core.exceptions.InvalidEnvFileException;
import org.jetbrains.annotations.NotNull;

@Builder
public class SingleFileEnvVarsProvider implements EnvVarsProvider {

    @NotNull
    private final EnvFileExecutor executor;

    @NotNull
    private final EnvFileReader reader;

    @NotNull
    private final EnvFileParser parser;

    private final Consumer<String> logger;

    @Override
    public Map<String, String> getEnvVars(
            final File file,
            final boolean isExecutable,
            final Map<String, String> context
    ) throws InvalidEnvFileException {

        val content = isExecutable
                ? execute(file.getAbsolutePath(), context)
                : reader.read(file);

        return parser.parse(content);
    }

    private String execute(String cmd, Map<String, String> context) throws InvalidEnvFileException {
        val output = executor.execute(cmd, context);

        if (logger != null) {
            logger.accept(
                    output.getStderr()
                            .map(line -> "STDERR: " + line)
                            .collect(Collectors.joining("\n"))
            );
        }
        return output.getStdout()
                .collect(Collectors.joining("\n"));
    }
}
