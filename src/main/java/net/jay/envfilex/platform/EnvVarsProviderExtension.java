package net.jay.envfilex.platform;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import com.intellij.diagnostic.PluginException;
import com.intellij.openapi.diagnostic.Logger;
import com.intellij.openapi.extensions.AbstractExtensionPointBean;
import com.intellij.openapi.extensions.ExtensionPointName;
import com.intellij.openapi.extensions.PluginDescriptor;
import com.intellij.openapi.extensions.PluginId;
import com.intellij.openapi.util.LazyInstance;
import com.intellij.util.LazyInitializer;
import com.intellij.util.xmlb.annotations.Attribute;
import lombok.NonNull;
import net.jay.envfilex.core.EnvVarsProviderFactory;
import org.jetbrains.annotations.NotNull;

public class EnvVarsProviderExtension extends AbstractExtensionPointBean {
    @SuppressWarnings("WeakerAccess")
    public static final ExtensionPointName<EnvVarsProviderExtension> EP_NAME = ExtensionPointName
            .create("net.jay.envFileX.envVarsProvider");

    @Attribute("id")
    public String id;

    @Attribute("factory")
    public String factory;

    private final LazyInstance<EnvVarsProviderFactory> implementation = new LazyInstance<EnvVarsProviderFactory>() {
        protected Class<EnvVarsProviderFactory> getInstanceClass() throws ClassNotFoundException {
            return findClass(factory);
        }
    };

    public EnvVarsProviderFactory getFactory() {
        return implementation.getValue();
    }

    public static Optional<EnvVarsProviderFactory> getParserFactoryById(@NonNull String parserId) {
        Map<String, EnvVarsProviderExtension> parsers = new HashMap<>();
        for (EnvVarsProviderExtension extension : EP_NAME.getExtensionList()) {
            if (parsers.containsKey(extension.getId())) {
                Logger.getInstance(EnvVarsProviderExtension.class).error(String.format(
                        "Cannot load parser '%s' with implementation class '%s' since there is already parser '%s' " +
                                "registered with the same id. Skipping.",
                        extension.getId(), extension.factory, parsers.get(extension.getId())));
            } else {
                parsers.put(extension.getId(), extension);
            }
        }
        EnvVarsProviderExtension extension = parsers.get(parserId);
        return Optional.ofNullable(extension)
                .map(EnvVarsProviderExtension::getFactory);
    }

    public static List<EnvVarsProviderExtension> getParserExtensions() {
        List<EnvVarsProviderExtension> extensions = new ArrayList<>(EP_NAME.getExtensionList());
        extensions.sort((o1, o2) -> o1.toString().compareToIgnoreCase(o2.toString()));
        return extensions;
    }

    public String getId() {
        if (id == null) {
            String error = "No id specified for environment variable provider factory " + factory;
            PluginDescriptor descriptor = getPluginDescriptor();
            if (descriptor != null) {
                PluginId pluginId = descriptor.getPluginId();
                throw new PluginException(error, pluginId);
            }
            throw new IllegalArgumentException(error);
        }
        return id.toLowerCase();
    }

    @Override
    public String toString() {
        return getFactory().getTitle();
    }
}
