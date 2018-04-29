package com.github.bartekbp.openinchrome;

import com.intellij.openapi.options.Configurable;
import com.intellij.openapi.options.ConfigurationException;
import com.intellij.openapi.options.SearchableConfigurable;
import com.intellij.openapi.project.Project;
import org.jetbrains.annotations.Nls;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;

public class SettingsPage implements SearchableConfigurable, Configurable.NoScroll {
    private SettingsForm settingsForm;
    private Project project;

    public SettingsPage(Project project) {
        this.project = project;
    }

    @NotNull
    @Override
    public String getId() {
        return Version.ID;
    }

    @Nls
    @Override
    public String getDisplayName() {
        return Version.NAME;
    }

    @Nullable
    @Override
    public JComponent createComponent() {
        settingsForm = new SettingsForm(project);
        settingsForm.setInitialValues();
        return settingsForm.$$$getRootComponent$$$();
    }

    @Override
    public boolean isModified() {
        return settingsForm != null && settingsForm.isModified();
    }

    @Override
    public void apply() throws ConfigurationException {
        PluginSettingsState.PluginSettings pluginSettings = settingsForm.apply();
        PluginSettingsState.getInstance(project).updateState(pluginSettings);
    }

    @Override
    public void reset() {
        settingsForm.reset();
    }

    @Override
    public void disposeUIResources() {
        settingsForm = null;
    }
}
