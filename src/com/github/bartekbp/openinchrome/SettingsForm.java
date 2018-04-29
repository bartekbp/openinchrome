package com.github.bartekbp.openinchrome;

import com.intellij.openapi.project.Project;
import com.intellij.uiDesigner.core.GridConstraints;
import com.intellij.uiDesigner.core.GridLayoutManager;
import com.intellij.util.Function;
import com.intellij.util.ui.ColumnInfo;
import com.intellij.util.ui.table.TableModelEditor;
import org.apache.commons.lang.ObjectUtils;
import org.apache.commons.lang.StringUtils;
import org.jetbrains.annotations.NotNull;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;

public class SettingsForm {
    private JPanel topLevelPanel;
    private JCheckBox enableCheckbox;
    private JPanel rewriteRulesPanel;
    private JPanel generalPanel;
    private JLabel hintLabel;
    private JComponent rewriteRulesTable;
    private TableModelEditor<PluginSettingsState.RewriteRule> editor;
    private Project project;

    public SettingsForm(Project project) {
        this.project = project;
    }

    public void setInitialValues() {
        PluginSettingsState.PluginSettings state = PluginSettingsState.getInstance(project).getState();
        this.enableCheckbox.getModel().setSelected(state.enabled);
        this.editor.getModel().setItems(new ArrayList<>(state.rewriteRules));
    }

    public boolean isModified() {
        return editor != null && editor.isModified() ||
                enableCheckbox.getModel().isSelected() != PluginSettingsState.getInstance(project).getState().enabled;
    }

    public void reset() {
        PluginSettingsState.PluginSettings state = PluginSettingsState.getInstance(project).getState();
        editor.reset(new ArrayList<>(state.rewriteRules));
        enableCheckbox.getModel().setSelected(state.enabled);
    }

    public PluginSettingsState.PluginSettings apply() {
        PluginSettingsState.PluginSettings pluginSettings = new PluginSettingsState.PluginSettings();
        pluginSettings.enabled = enableCheckbox.getModel().isSelected();
        pluginSettings.rewriteRules = editor.apply();
        return pluginSettings;
    }

    private final TableModelEditor.EditableColumnInfo<PluginSettingsState.RewriteRule, Boolean> ACTIVE_COLUMN = new TableModelEditor.EditableColumnInfo<PluginSettingsState.RewriteRule, Boolean>() {

        @Override
        public Class getColumnClass() {
            return Boolean.class;
        }

        @Override
        public Boolean valueOf(PluginSettingsState.RewriteRule item) {
            return item.active;
        }

        @Override
        public void setValue(PluginSettingsState.RewriteRule item, Boolean value) {
            item.active = value;
        }

    };

    private final TableModelEditor.EditableColumnInfo<PluginSettingsState.RewriteRule, String> PATTERN_COLUMN = new TableModelEditor.EditableColumnInfo<PluginSettingsState.RewriteRule, String>("Pattern") {
        @Override
        public String valueOf(PluginSettingsState.RewriteRule item) {
            return item.pattern;
        }

        @Override
        public void setValue(PluginSettingsState.RewriteRule item, String value) {
            if (StringUtils.isNotBlank(value)) {
                item.pattern = value;
            }
        }
    };

    private final TableModelEditor.EditableColumnInfo<PluginSettingsState.RewriteRule, String> REPLACEMENT_COLUMN = new TableModelEditor.EditableColumnInfo<PluginSettingsState.RewriteRule, String>("Replacement") {

        @Override
        public String valueOf(PluginSettingsState.RewriteRule item) {
            return item.replacementRule;
        }

        @Override
        public void setValue(PluginSettingsState.RewriteRule item, String value) {
            if (StringUtils.isEmpty(value)) {
                return;
            }

            item.replacementRule = value;
        }

    };

    private final ColumnInfo[] COLUMNS = {
            ACTIVE_COLUMN,
            PATTERN_COLUMN,
            REPLACEMENT_COLUMN
    };

    private void createUIComponents() {
        // TODO: place custom component creation code here
        TableModelEditor.DialogItemEditor<PluginSettingsState.RewriteRule> itemEditor = new TableModelEditor.DialogItemEditor<PluginSettingsState.RewriteRule>() {

            @NotNull
            @Override
            public Class<? extends PluginSettingsState.RewriteRule> getItemClass() {
                return PluginSettingsState.RewriteRule.class;
            }

            @Override
            public PluginSettingsState.RewriteRule clone(@NotNull PluginSettingsState.RewriteRule rewriteRule, boolean forInPlaceEditing) {
                return rewriteRule.copy();
            }

            @Override
            public void edit(@NotNull PluginSettingsState.RewriteRule rewriteRule, @NotNull Function<PluginSettingsState.RewriteRule, PluginSettingsState.RewriteRule> mutator, boolean isAdd) {
                PluginSettingsState.RewriteRule copy = rewriteRule.copy();
                mutator.fun(rewriteRule).updateWith(copy);
            }

            @Override
            public void applyEdited(@NotNull PluginSettingsState.RewriteRule oldItem, @NotNull PluginSettingsState.RewriteRule newItem) {
                oldItem.updateWith(newItem);
            }

            @Override
            public boolean isEditable(@NotNull PluginSettingsState.RewriteRule item) {
                return true;
            }
        };

        editor = new TableModelEditor<>(COLUMNS, itemEditor, "No rules configured");
        rewriteRulesTable = editor.createComponent();
    }

    {
// GUI initializer generated by IntelliJ IDEA GUI Designer
// >>> IMPORTANT!! <<<
// DO NOT EDIT OR ADD ANY CODE HERE!
        $$$setupUI$$$();
    }

    /**
     * Method generated by IntelliJ IDEA GUI Designer
     * >>> IMPORTANT!! <<<
     * DO NOT edit this method OR call it in your code!
     *
     * @noinspection ALL
     */
    private void $$$setupUI$$$() {
        createUIComponents();
        topLevelPanel = new JPanel();
        topLevelPanel.setLayout(new GridLayoutManager(3, 1, new Insets(0, 0, 0, 0), -1, -1));
        generalPanel = new JPanel();
        generalPanel.setLayout(new GridLayoutManager(1, 1, new Insets(0, 0, 0, 0), -1, -1));
        topLevelPanel.add(generalPanel, new GridConstraints(0, 0, 1, 1, GridConstraints.ANCHOR_NORTH, GridConstraints.FILL_HORIZONTAL, 1, 1, null, null, null, 0, false));
        generalPanel.setBorder(BorderFactory.createTitledBorder("General"));
        enableCheckbox = new JCheckBox();
        enableCheckbox.setSelected(true);
        enableCheckbox.setText("Enable plugin");
        generalPanel.add(enableCheckbox, new GridConstraints(0, 0, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        rewriteRulesPanel = new JPanel();
        rewriteRulesPanel.setLayout(new GridLayoutManager(1, 1, new Insets(0, 0, 0, 0), -1, -1));
        topLevelPanel.add(rewriteRulesPanel, new GridConstraints(1, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_WANT_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_WANT_GROW, null, null, null, 0, false));
        rewriteRulesPanel.setBorder(BorderFactory.createTitledBorder("Rewrite rules"));
        rewriteRulesPanel.add(rewriteRulesTable, new GridConstraints(0, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null, 0, false));
        hintLabel = new JLabel();
        hintLabel.setText("Hint: Use placeholders ${FILE_NAME}, ${RELATIVE_PATH} and ${ABSOLUTE_PATH}");
        topLevelPanel.add(hintLabel, new GridConstraints(2, 0, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
    }

    /**
     * @noinspection ALL
     */
    public JComponent $$$getRootComponent$$$() {
        return topLevelPanel;
    }
}
