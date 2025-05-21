package net.jay.envfilex.platform.ui.table;

import java.awt.*;
import java.io.File;

import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.TableCellEditor;
import javax.swing.table.TableCellRenderer;

import com.intellij.openapi.project.Project;
import com.intellij.ui.JBColor;
import com.intellij.ui.components.JBTextField;
import com.intellij.util.ui.ColumnInfo;
import com.intellij.util.ui.UIUtil;
import lombok.Builder;
import lombok.NonNull;
import lombok.val;
import net.jay.envfilex.core.EnvVarsProviderFactory;
import net.jay.envfilex.core.providers.runconfig.RunConfigEnvVarsProvider;
import net.jay.envfilex.platform.EnvFileEntry;
import net.jay.envfilex.platform.EnvVarsProviderExtension;
import net.jay.envfilex.platform.ProjectFileResolver;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;


public class EnvFilePathColumnInfo extends ColumnInfo<EnvFileEntry, String> {

    @NonNull
    private final Project project;

    @Builder
    public EnvFilePathColumnInfo(@NotNull final Project project) {
        super("Path");
        this.project = project;
    }

    @Override
    public boolean isCellEditable(EnvFileEntry envFileEntry) {
        return EnvVarsProviderExtension.getParserFactoryById(envFileEntry.getParserId())
                .map(EnvVarsProviderFactory::isEditable)
                .map(value -> value && envFileEntry.isEnabled())
                .orElse(false);
    }

    @Nullable
    @Override
    public TableCellEditor getEditor(EnvFileEntry envFileEntry) {
        return new DefaultCellEditor(new JBTextField());
    }

    @Override
    public void setValue(EnvFileEntry envFileEntry, String value) {
        envFileEntry.setPath(value == null ? "" : value);
    }

    @Nullable
    @Override
    public String valueOf(EnvFileEntry envFileEntry) {
        return envFileEntry.getPath();
    }

    @Override
    public TableCellRenderer getRenderer(final EnvFileEntry entry) {
        return new DefaultTableCellRenderer() {
            @NotNull
            @Override
            public Component getTableCellRendererComponent(
                    @NotNull JTable table,
                    Object value,
                    boolean isSelected,
                    boolean hasFocus,
                    int row,
                    int column
            ) {
                final Component renderer = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
                setBorder(null);
                if (entry.getParserId().equals(RunConfigEnvVarsProvider.PARSER_ID)) { // TODO make generic
                    setText("<Run Configuration Env Vars>");
                    setForeground(UIUtil.getLabelDisabledForeground());
                } else {
                    setText(entry.getPath());

                    if (entry.isEnabled()) {
                        val validPath = ProjectFileResolver.DEFAULT.resolvePath(project, entry.getPath())
                                .map(File::exists)
                                .orElse(true);
                        if (!validPath) {
                            setForeground(JBColor.RED);
                            setToolTipText("File doesn't exist!");
                        }
                    } else {
                        setForeground(UIUtil.getLabelDisabledForeground());
                    }
                }

                return renderer;
            }
        };
    }
}
