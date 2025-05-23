package net.jay.envfilex.core;

import java.io.File;
import java.util.Map;

import net.jay.envfilex.core.exceptions.EnvFileException;


public interface EnvVarsProvider {

    /**
     * @param file         file to process
     * @param isExecutable if true, file should be executed and its output should be parsed
     * @param context      environment variables that were generated so far by other providers
     * @return environment variables defined by files, does not need to merge with context
     * @throws EnvFileException if something goes wrong
     */
    Map<String, String> getEnvVars(File file, boolean isExecutable, Map<String, String> context)
            throws EnvFileException;

}
