package net.jay.envfilex.core.providers;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import net.jay.envfilex.core.exceptions.InvalidEnvFileException;


/**
 * Execute given command with given environment, and return standard output
 * Throw {@link InvalidEnvFileException} in case file cannot be executed.
 */
@FunctionalInterface
public interface EnvFileReader {
    EnvFileReader DEFAULT = new Utf8Reader();

    String read(File file) throws InvalidEnvFileException;

    @NoArgsConstructor(access = AccessLevel.PRIVATE)
    class Utf8Reader implements EnvFileReader {

        @Override
        public String read(File file) throws InvalidEnvFileException {
            if (file == null) {
                throw new InvalidEnvFileException("File is required!");
            }

            byte[] bytes;
            try {
                bytes = Files.readAllBytes(file.toPath());
            } catch (IOException e) {
                throw new InvalidEnvFileException(e);
            }

            return new String(bytes, StandardCharsets.UTF_8);
        }
    }
}
