package net.jay.envfilex.platform;

import javax.swing.*;

import com.intellij.icons.AllIcons;
import com.intellij.openapi.fileTypes.LanguageFileType;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class EnvFileType extends LanguageFileType {
    static final EnvFileType INSTANCE = new EnvFileType();

    private EnvFileType() {
        super(EnvFileLanguage.INSTANCE);
    }

    @NotNull
    @Override
    public String getName() {
        return "Env File";
    }

    @NotNull
    @Override
    public String getDescription() {
        return "Env File";
    }

    @NotNull
    @Override
    public String getDefaultExtension() {
        return "env";
    }

    @Nullable
    @Override
    public Icon getIcon() {
        return AllIcons.FileTypes.Config;
    }
}
