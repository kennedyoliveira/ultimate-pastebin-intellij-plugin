package com.github.kennedyoliveira.ultimatepastebin.action;

import com.github.kennedyoliveira.ultimatepastebin.settings.PasteBinConfigurableSettings;
import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.options.ShowSettingsUtil;

import static com.github.kennedyoliveira.ultimatepastebin.i18n.MessageBundle.getMessage;

/**
 * Created by kennedy on 11/8/15.
 */
public class ShowSettingsAction extends AnAction {

    @Override
    public void actionPerformed(AnActionEvent e) {
        ShowSettingsUtil.getInstance().showSettingsDialog(e.getProject(), PasteBinConfigurableSettings.class);
    }

    @Override
    public void update(AnActionEvent e) {
        super.update(e);

        e.getPresentation().setText(getMessage("ultimatepastebin.actions.showsettings.text"));
        e.getPresentation().setDescription(getMessage("ultimatepastebin.actions.showsettings.description"));
    }
}
