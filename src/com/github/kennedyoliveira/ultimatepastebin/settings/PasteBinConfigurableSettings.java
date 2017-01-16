package com.github.kennedyoliveira.ultimatepastebin.settings;

import com.github.kennedyoliveira.ultimatepastebin.UltimatePasteBinConstants;
import com.github.kennedyoliveira.ultimatepastebin.i18n.MessageBundle;
import com.github.kennedyoliveira.ultimatepastebin.service.PasteBinService;
import com.github.kennedyoliveira.ultimatepastebin.service.ToolWindowService;
import com.intellij.openapi.components.ServiceManager;
import com.intellij.openapi.options.ConfigurationException;
import com.intellij.openapi.options.SearchableConfigurable;
import com.intellij.openapi.util.text.StringUtil;
import com.intellij.util.ui.UIUtil;
import org.jetbrains.annotations.Nls;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;
import java.util.Locale;
import java.util.Objects;

import static com.github.kennedyoliveira.ultimatepastebin.utils.UltimatePasteBinUtils.log;
import static com.github.kennedyoliveira.ultimatepastebin.utils.UltimatePasteBinUtils.showErrorMessageBox;

/**
 * Plugin configuration.
 */
public class PasteBinConfigurableSettings implements SearchableConfigurable {

  private PasteBinConfigurationForm pasteBinConfigurationForm;
  private PasteBinConfigurationService pasteBinConfigurationService;

  public PasteBinConfigurableSettings() {
    UIUtil.invokeLaterIfNeeded(() -> this.pasteBinConfigurationForm = new PasteBinConfigurationForm(true));

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
    return !(
        Objects.equals(pasteBinConfigurationForm.getDevkey().getText(), pasteBinConfigurationService.getDevkey())
            && Objects.equals(pasteBinConfigurationForm.getUserName().getText(), pasteBinConfigurationService.getUsername())
            && Objects.equals(new String(pasteBinConfigurationForm.getPassword().getPassword()), pasteBinConfigurationService.getPassword())
    );
  }

  @Override
  public void apply() throws ConfigurationException {
    boolean changedAccountCredentials = changedAccountCredentials();

    pasteBinConfigurationService.setUsername(pasteBinConfigurationForm.getUserName().getText());
    pasteBinConfigurationService.setDevkey(pasteBinConfigurationForm.getDevkey().getText());
    pasteBinConfigurationService.setTotalPastesToFetch((Integer) pasteBinConfigurationForm.getPasteToFetch().getValue());
    pasteBinConfigurationService.setCurrentLanguage((String) pasteBinConfigurationForm.getLanguage().getSelectedItem());
    pasteBinConfigurationService.setPassword(new String(pasteBinConfigurationForm.getPassword().getPassword()));

    // Updates the default locale so forms will get the language too
    Locale.setDefault(MessageBundle.getLanguageLocale());

    ToolWindowService service = ServiceManager.getService(ToolWindowService.class);

    UIUtil.invokeLaterIfNeeded(() -> {
      // when the settings changed, i try to login on pastebin with the new credentials
      if (changedAccountCredentials || !pasteBinConfigurationService.isValidCredentials()) {
        PasteBinService pasteBinService = ServiceManager.getService(PasteBinService.class);
        pasteBinService.invalidateCredentials();

        try {
          pasteBinService.checkCredentials();
          service.fetchPastes();
        } catch (Exception e) {
          // if there is any error, show to user
          log.error("Logging into pastebin", e);
          showErrorMessageBox(null, e.getMessage(), "Error While Logging with the Credentials Provided.");
        }
      } else {
        service.fetchPastes();
      }
    });
  }

  @Override
  public void reset() {
    if (StringUtil.isEmpty(pasteBinConfigurationService.getDevkey()) || StringUtil.isEmpty(pasteBinConfigurationService.getUsername())) {
      pasteBinConfigurationForm.getUserName().setText(null);
      pasteBinConfigurationForm.getDevkey().setText(null);
      pasteBinConfigurationForm.getPasteToFetch().setValue(UltimatePasteBinConstants.DEFAULT_TOTAL_PASTES_TO_FETCH);
      pasteBinConfigurationForm.getLanguage().setSelectedItem("English");
      pasteBinConfigurationForm.getPassword().setText(null);
    } else {
      pasteBinConfigurationForm.getUserName().setText(pasteBinConfigurationService.getUsername());
      pasteBinConfigurationForm.getDevkey().setText(pasteBinConfigurationService.getDevkey());
      pasteBinConfigurationForm.getPasteToFetch().setValue(pasteBinConfigurationService.getTotalPastesToFetch());
      pasteBinConfigurationForm.getPassword().setText(pasteBinConfigurationService.getPassword());

      if (pasteBinConfigurationService.getCurrentLanguage() != null) {
        pasteBinConfigurationForm.getLanguage().setSelectedItem(pasteBinConfigurationService.getCurrentLanguage());
      } else {
        pasteBinConfigurationForm.getLanguage().setSelectedItem("English");
      }
    }
  }

  @Override
  public void disposeUIResources() {
    log.debug("Disposing Configuration Settings UI");
  }
}
