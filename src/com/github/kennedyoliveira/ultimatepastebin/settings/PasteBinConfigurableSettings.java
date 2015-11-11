package com.github.kennedyoliveira.ultimatepastebin.settings;

import com.github.kennedyoliveira.ultimatepastebin.UltimatePasteBinConstants;
import com.github.kennedyoliveira.ultimatepastebin.i18n.MessageBundle;
import com.github.kennedyoliveira.ultimatepastebin.service.PasteBinService;
import com.github.kennedyoliveira.ultimatepastebin.service.ToolWindowService;
import com.intellij.openapi.components.ServiceManager;
import com.intellij.openapi.options.ConfigurationException;
import com.intellij.openapi.options.SearchableConfigurable;
import com.intellij.util.ui.UIUtil;
import org.jetbrains.annotations.Nls;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;
import java.util.Locale;

/**
 * Created by kennedy on 11/7/15.
 */
public class PasteBinConfigurableSettings implements SearchableConfigurable {

    private PasteBinConfigurationForm pasteBinConfigurationForm;
    private PasteBinConfigurationService pasteBinConfigurationService;

    public PasteBinConfigurableSettings() {
        UIUtil.invokeLaterIfNeeded(() -> {
            this.pasteBinConfigurationForm = new PasteBinConfigurationForm(true);
        });

        this.pasteBinConfigurationService = ServiceManager.getService(PasteBinConfigurationService.class);
    }

    @NotNull
    @Override
    public String getId() {
        return "ultimatepastebin.settings";
    }

    @Nullable
    @Override
    public Runnable enableSearch(String option) {
        return null;
    }

    @Nls
    @Override
    public String getDisplayName() {
        return "Ultimate PasteBin";
    }

    @Nullable
    @Override
    public String getHelpTopic() {
        return null;
    }

    @Nullable
    @Override
    public JComponent createComponent() {
        return pasteBinConfigurationForm.createCenterPanel();
    }

    @Override
    public boolean isModified() {
        return changedAccountCredentials() ||
                pasteBinConfigurationService.getTotalPastesToFetch() != ((int) pasteBinConfigurationForm.getPasteToFetch().getValue()) ||
                !pasteBinConfigurationForm.getLanguage().getSelectedItem().equals(pasteBinConfigurationService.getCurrentLanguage());
    }

    public boolean changedAccountCredentials() {
        return !pasteBinConfigurationForm.getConfiguration().equals(pasteBinConfigurationService.getPasteBinSettings());
    }

    @Override
    public void apply() throws ConfigurationException {
        boolean changedAccountCredentials = changedAccountCredentials();

        pasteBinConfigurationService.setPasteBinSettings(pasteBinConfigurationForm.getConfiguration());
        pasteBinConfigurationService.setTotalPastesToFetch((Integer) pasteBinConfigurationForm.getPasteToFetch().getValue());
        pasteBinConfigurationService.setCurrentLanguage((String) pasteBinConfigurationForm.getLanguage().getSelectedItem());

        // Updates the default locale so forms will get the language too
        Locale.setDefault(MessageBundle.getLanguageLocale());

        UIUtil.invokeLaterIfNeeded(() -> {
            if (changedAccountCredentials) {
                PasteBinService pasteBinService = ServiceManager.getService(PasteBinService.class);
                pasteBinService.invalidateCredentials();
            }

            ToolWindowService service = ServiceManager.getService(ToolWindowService.class);

            service.fetchPastes();
        });
    }

    @Override
    public void reset() {
        if (pasteBinConfigurationService.getPasteBinSettings() == null || pasteBinConfigurationService.getPasteBinSettings().getPasteBinAccountCredentials() == null) {
            pasteBinConfigurationForm.getUserName().setText(null);
            pasteBinConfigurationForm.getPassword().setText(null);
            pasteBinConfigurationForm.getDevkey().setText(null);
            pasteBinConfigurationForm.getPasteToFetch().setValue(UltimatePasteBinConstants.DEFAULT_TOTAL_PASTES_TO_FETCH);
            pasteBinConfigurationForm.getLanguage().setSelectedItem("English");
        } else {
            pasteBinConfigurationForm.getUserName().setText(pasteBinConfigurationService.getPasteBinSettings().getPasteBinAccountCredentials().getUserName().orElse(null));
            pasteBinConfigurationForm.getPassword().setText(pasteBinConfigurationService.getPasteBinSettings().getPasteBinAccountCredentials().getPassword().orElse(null));
            pasteBinConfigurationForm.getDevkey().setText(pasteBinConfigurationService.getPasteBinSettings().getPasteBinAccountCredentials().getDevKey());
            pasteBinConfigurationForm.getPasteToFetch().setValue(pasteBinConfigurationService.getTotalPastesToFetch());

            if (pasteBinConfigurationService.getCurrentLanguage() != null) {
                pasteBinConfigurationForm.getLanguage().setSelectedItem(pasteBinConfigurationService.getCurrentLanguage());
            } else {
                pasteBinConfigurationForm.getLanguage().setSelectedItem("English");
            }
        }
    }

    @Override
    public void disposeUIResources() {
    }
}
