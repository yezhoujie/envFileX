package net.jay.envfilex.core.providers;

import java.util.Map;

import net.jay.envfilex.core.exceptions.InvalidEnvFileException;


@FunctionalInterface
public interface EnvFileParser {

    Map<String, String> parse(String content) throws InvalidEnvFileException;
}
