package com.github.bartekbp.openinchrome;

import com.intellij.ide.BrowserUtil;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.PlatformDataKeys;
import com.intellij.openapi.diagnostic.Logger;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.roots.ProjectFileIndex;
import com.intellij.openapi.vfs.VirtualFile;
import org.apache.commons.lang.StringUtils;
import org.apache.http.client.utils.URIBuilder;

import java.net.URISyntaxException;
import java.nio.file.FileSystems;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class OpenInDevtoolsAction extends com.intellij.openapi.actionSystem.AnAction {
    private static final Logger log = Logger.getInstance(OpenInDevtoolsAction.class);

    @Override
    public void actionPerformed(AnActionEvent anActionEvent) {

        VirtualFile file = PlatformDataKeys.VIRTUAL_FILE.getData(anActionEvent.getDataContext());
        Editor editor = PlatformDataKeys.EDITOR.getData(anActionEvent.getDataContext());
        int cursorLine = editor.getCaretModel()
                .getLogicalPosition()
                .line;

        Project project = anActionEvent.getProject();
        if(StringUtils.isNotBlank(file.getName())) {
            try {
                BrowserUtil.browse(getUrl(project, file, cursorLine));
            } catch (URISyntaxException e) {
                e.printStackTrace();
            }
        }
    }

    private String getUrl(Project project, VirtualFile file, int line) throws URISyntaxException {
        List<PluginSettingsState.RewriteRule> rewriteRules = PluginSettingsState.getInstance(project)
                .getState()
                .rewriteRules;

        String contentRoot = ProjectFileIndex.getInstance(project)
                .getContentRootForFile(file)
                .getCanonicalPath();

        return getUrl(contentRoot, rewriteRules, Paths.get(file.getCanonicalPath()), line);
    }

    private String getUrl(String contentRoot, List<PluginSettingsState.RewriteRule> rules, String path, int line) throws URISyntaxException {
        return getUrl(contentRoot, rules, Paths.get(path), line);
    }

    private String getUrl(String contentRoot, List<PluginSettingsState.RewriteRule> rules, Path path, int line) throws URISyntaxException {
        Optional<PluginSettingsState.RewriteRule> matchingRule = rules.stream()
                .filter(rule -> rule.active)
                .filter(rule -> rule.pattern != null)
                .filter(rule -> FileSystems.getDefault()
                        .getPathMatcher("glob:" + rule.pattern).matches(path))
                .findFirst();

        String rewrittenName = matchingRule
                .map(rule -> rule.rewrite(contentRoot, path))
                .orElse(Paths.get(contentRoot)
                    .toAbsolutePath()
                    .relativize(path.toAbsolutePath())
                    .toString());

        return createUrl(rewrittenName, line);
    }

    private String createUrl(String filepath, int lineNumber) throws URISyntaxException {
        return new URIBuilder()
                .setScheme("http")
                .setHost("localhost")
                .setPath("/openinintellij.extension")
                .setParameter("file", filepath)
                .setParameter("line", Integer.toString(lineNumber))
                .build()
                .toString();
    }

    @Override
    public void update(AnActionEvent anActionEvent) {
        super.update(anActionEvent);
        Project project = anActionEvent.getProject();
        PluginSettingsState.PluginSettings settings = PluginSettingsState.getInstance(project).getState();

        anActionEvent.getPresentation()
                .setEnabledAndVisible(settings.enabled);
    }

    public static void mainy(String... args) throws URISyntaxException {
        List<PluginSettingsState.RewriteRule> rewriteRules = new ArrayList<>();
        {
            PluginSettingsState.RewriteRule rewriteRule = new PluginSettingsState.RewriteRule();
            rewriteRule.active = true;
            rewriteRule.replacementRule = "";
            rewriteRule.pattern = null;

            rewriteRules.add(rewriteRule);
        }

        {
            PluginSettingsState.RewriteRule rewriteRule = new PluginSettingsState.RewriteRule();
            rewriteRule.active = false;
            rewriteRule.replacementRule = "${FILENAME}";
            rewriteRule.pattern = null;

            rewriteRules.add(rewriteRule);
        }

        {
            PluginSettingsState.RewriteRule rewriteRule = new PluginSettingsState.RewriteRule();
            rewriteRule.active = true;
            rewriteRule.replacementRule = "webpack:///src/style/common/${FILEPATH}/${FILENAME}";
            rewriteRule.pattern = "*.less";

            rewriteRules.add(rewriteRule);
        }

        {
            PluginSettingsState.RewriteRule rewriteRule = new PluginSettingsState.RewriteRule();
            rewriteRule.active = true;
            rewriteRule.replacementRule = "webpack:///${FILEPATH}/${FILENAME}";
            rewriteRule.pattern = "*\\.js";

            rewriteRules.add(rewriteRule);
        }

        System.out.println(new OpenInDevtoolsAction()
                .getUrl("c:\\users\\bartek", rewriteRules, "test", 1));

        System.out.println(new OpenInDevtoolsAction()
                .getUrl("c:\\users\\bartek", rewriteRules, "test.less", 1));

        System.out.println(new OpenInDevtoolsAction()
                .getUrl("c:\\users\\bartek", rewriteRules, "test.css", 1));

        System.out.println(new OpenInDevtoolsAction()
                .getUrl("c:\\users\\bartek", rewriteRules, "src/styles/global.less", 1));

        System.out.println(new OpenInDevtoolsAction()
                .getUrl("c:\\users\\bartek", rewriteRules, "test.js", 1));

        System.out.println(new OpenInDevtoolsAction()
                .getUrl("c:\\users\\bartek", rewriteRules, "src/common/test.js", 1));
    }
}
