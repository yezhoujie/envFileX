package net.jay.envfilex.platform.ui.table;

import java.awt.*;

import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.TableCellRenderer;

import com.intellij.ui.JBColor;
import com.intellij.util.ui.ColumnInfo;
import com.intellij.util.ui.UIUtil;
import lombok.val;
import net.jay.envfilex.core.EnvVarsProviderFactory;
import net.jay.envfilex.platform.EnvFileEntry;
import net.jay.envfilex.platform.EnvVarsProviderExtension;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class EnvFileTypeColumnInfo extends ColumnInfo<EnvFileEntry, EnvFileEntry> {
    public EnvFileTypeColumnInfo() {
        super("Type");
    }

    @Nullable
    @Override
    public EnvFileEntry valueOf(EnvFileEntry envFileEntry) {
        return envFileEntry;
    }

    @Override
    public TableCellRenderer getRenderer(final EnvFileEntry p0) {
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
                EnvFileEntry entry = (EnvFileEntry) value;
                String typeTitle = EnvVarsProviderExtension.getParserFactoryById(entry.getParserId())
                        .map(EnvVarsProviderFactory::getTitle)
                        .orElse(String.format("<%s>", entry.getParserId()));
                setText(typeTitle);
                setBorder(null);

                if (entry.isEnabled()) {
                    if (EnvVarsProviderExtension.getParserFactoryById(entry.getParserId()).isEmpty()) {
                        setForeground(JBColor.RED);
                        setToolTipText("Parser not found!");
                    }
                } else {
                    setForeground(UIUtil.getLabelDisabledForeground());
                }

                return renderer;
            }
        };
    }
}
