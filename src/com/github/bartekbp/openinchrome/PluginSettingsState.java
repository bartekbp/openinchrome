package com.github.bartekbp.openinchrome;

import com.intellij.openapi.components.PersistentStateComponent;
import com.intellij.openapi.components.ServiceManager;
import com.intellij.openapi.components.State;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.util.SimpleModificationTracker;
import org.apache.commons.io.FilenameUtils;
import org.jetbrains.annotations.NotNull;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

@State(
    name = "OpenInChromeSettings"
)
public class PluginSettingsState extends SimpleModificationTracker implements PersistentStateComponent<PluginSettingsState.PluginSettings> {
    private static final String FILENAME_PARAMETER = "${FILE_NAME}";
    private static final String RELATIVE_PATH_PARAMETER = "${RELATIVE_PATH}";
    private static final String ABSOLUTE_PATH_PARAMETER = "${ABSOLUTE_PATH}";

    static class PluginSettings {
        boolean enabled = false;
        List<RewriteRule> rewriteRules = new ArrayList<>();
    }

    static class RewriteRule {
        boolean active;
        String pattern;
        String replacementRule;

        public RewriteRule copy() {
            RewriteRule rewriteRule = new RewriteRule();
            rewriteRule.active = this.active;
            rewriteRule.pattern = this.pattern;
            rewriteRule.replacementRule = this.replacementRule;
            return rewriteRule;
        }

        public void updateWith(RewriteRule rewriteRule) {
            this.active = rewriteRule.active;
            this.pattern = rewriteRule.pattern;
            this.replacementRule = rewriteRule.replacementRule;
        }

        public String rewrite(String contentRoot, Path filepath) {
            String filename = filepath.getFileName().toString();
            String absolutePath = filepath.toAbsolutePath()
                    .getParent()
                    .toString();

            String relativePath = Paths.get(contentRoot)
                    .toAbsolutePath()
                    .relativize(filepath.toAbsolutePath())
                    .getParent()
                    .toString();

            return replacementRule
                    .replace(FILENAME_PARAMETER, filename)
                    .replace(ABSOLUTE_PATH_PARAMETER, FilenameUtils.separatorsToUnix(absolutePath))
                    .replace(RELATIVE_PATH_PARAMETER, FilenameUtils.separatorsToUnix(relativePath));
        }
    }

    private PluginSettings state = new PluginSettings();

    @Override
    @NotNull
    public PluginSettings getState() {
        return this.state;
    }

    @Override
    public void loadState(@NotNull PluginSettings pluginSettings) {
        this.state = pluginSettings;
    }

    public void updateState(PluginSettings pluginSettings) {
        this.state = pluginSettings;
        incModificationCount();
    }

    public static PluginSettingsState getInstance(Project project) {
        return ServiceManager.getService(project, PluginSettingsState.class);
    }
}
