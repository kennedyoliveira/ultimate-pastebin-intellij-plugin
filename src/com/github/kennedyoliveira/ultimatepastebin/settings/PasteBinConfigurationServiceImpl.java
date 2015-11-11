package com.github.kennedyoliveira.ultimatepastebin.settings;

import com.github.kennedyoliveira.ultimatepastebin.UltimatePasteBinConstants;
import com.github.kennedyoliveira.pastebin4j.AccountCredentials;
import com.intellij.openapi.components.*;
import org.jdom.Element;
import org.jetbrains.annotations.Nullable;

import static com.github.kennedyoliveira.ultimatepastebin.UltimatePasteBinConstants.MAX_PASTES_TO_FETCH;

/**
 * <p>Configuration service for saving and loading.</p>
 *
 * @author kennedy
 */
@State(name = "ultimatepastebin", storages = @Storage(id = "main", file = StoragePathMacros.APP_CONFIG + "/ultimatepastebin_settings.xml"))
public class PasteBinConfigurationServiceImpl implements PersistentStateComponent<Element>, PasteBinConfigurationService {

    /**
     * Pastebin configurations
     */
    private PasteBinSettings pasteBinSettings;

    /**
     * Flag setted after showing the welcome message, to preent reshowing it.
     */
    private boolean showedWelcomeMessage;

    /**
     * Flag setted after a valid credential is set
     */
    private boolean validCredentials;

    /**
     * Version that the configuration was saved
     */
    private String version;

    /**
     * Configuration for the total of pastes to fetch, default is 50 if not especified.
     */
    private int totalPastesToFetch = UltimatePasteBinConstants.DEFAULT_TOTAL_PASTES_TO_FETCH;

    /**
     * The current plugin language
     */
    private String currentLanguage;

    public static PasteBinConfigurationService getInstance() {
        return ServiceManager.getService(PasteBinConfigurationService.class);
    }

    @Nullable
    @Override
    public Element getState() {
        final Element element = new Element("UltimatePasteBinSettings");

        if (pasteBinSettings != null && pasteBinSettings.getPasteBinAccountCredentials() != null) {
            element.setAttribute("username", pasteBinSettings.getPasteBinAccountCredentials().getUserName().orElse(null));
            element.setAttribute("password", pasteBinSettings.getPasteBinAccountCredentials().getPassword().orElse(null));
            element.setAttribute("devkey", pasteBinSettings.getPasteBinAccountCredentials().getDevKey());
            element.setAttribute("showedWelcomeMessage", String.valueOf(showedWelcomeMessage));
            element.setAttribute("version", version);
            element.setAttribute("totalPastesToFetch", String.valueOf(totalPastesToFetch));

            if (currentLanguage != null)
                element.setAttribute("currentLanguage", currentLanguage);
        }

        return element;
    }

    @Override
    public void loadState(Element state) {
        this.pasteBinSettings = new PasteBinSettings();
        String devkey = state.getAttributeValue("devkey", "");
        String username = state.getAttributeValue("username", "");
        String password = state.getAttributeValue("password", "");
        this.pasteBinSettings.setPasteBinAccountCredentials(new AccountCredentials(devkey, username, password));

        this.showedWelcomeMessage = Boolean.valueOf(state.getAttributeValue("showedWelcomeMessage", "false"));
        this.version = state.getAttributeValue("version");

        try {
            this.totalPastesToFetch = Integer.parseInt(state.getAttributeValue("totalPastesToFetch"));
        } catch (NumberFormatException e) {
            // If fails to recover the value, sets the default
            this.totalPastesToFetch = UltimatePasteBinConstants.DEFAULT_TOTAL_PASTES_TO_FETCH;
        }

        String currentLanguage = state.getAttributeValue("currentLanguage");

        if (currentLanguage != null)
            this.currentLanguage = currentLanguage;
    }

    @Override
    public PasteBinSettings getPasteBinSettings() {
        return pasteBinSettings;
    }

    @Override
    public void setPasteBinSettings(PasteBinSettings pasteBinSettings) {
        this.pasteBinSettings = pasteBinSettings;
    }

    @Override
    public boolean isShowedWelcomeMessage() {
        return showedWelcomeMessage;
    }

    @Override
    public void setShowedWelcomeMessage(boolean showedWelcomeMessage) {
        this.showedWelcomeMessage = showedWelcomeMessage;
    }

    @Override
    public String getVersion() {
        return version;
    }

    @Override
    public void setVersion(String version) {
        this.version = version;
    }

    @Override
    public boolean isValidCredentials() {
        return validCredentials;
    }

    @Override
    public void setValidCredentials(boolean validCredentials) {
        this.validCredentials = validCredentials;
    }

    @Override
    public int getTotalPastesToFetch() {
        return totalPastesToFetch;
    }

    @Override
    public void setTotalPastesToFetch(int totalPastesToFetch) {
        this.totalPastesToFetch = totalPastesToFetch > MAX_PASTES_TO_FETCH ? MAX_PASTES_TO_FETCH : totalPastesToFetch < 1 ? 1 : totalPastesToFetch;
    }

    @Override
    @Nullable
    public String getCurrentLanguage() {
        return currentLanguage;
    }

    @Override
    public void setCurrentLanguage(@Nullable String currentLanguage) {
        this.currentLanguage = currentLanguage;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        PasteBinConfigurationServiceImpl that = (PasteBinConfigurationServiceImpl) o;

        if (showedWelcomeMessage != that.showedWelcomeMessage) return false;
        if (validCredentials != that.validCredentials) return false;
        if (totalPastesToFetch != that.totalPastesToFetch) return false;
        if (pasteBinSettings != null ? !pasteBinSettings.equals(that.pasteBinSettings) : that.pasteBinSettings != null)
            return false;
        if (version != null ? !version.equals(that.version) : that.version != null) return false;
        return !(currentLanguage != null ? !currentLanguage.equals(that.currentLanguage) : that.currentLanguage != null);

    }

    @Override
    public int hashCode() {
        int result = pasteBinSettings != null ? pasteBinSettings.hashCode() : 0;
        result = 31 * result + (showedWelcomeMessage ? 1 : 0);
        result = 31 * result + (validCredentials ? 1 : 0);
        result = 31 * result + (version != null ? version.hashCode() : 0);
        result = 31 * result + totalPastesToFetch;
        result = 31 * result + (currentLanguage != null ? currentLanguage.hashCode() : 0);
        return result;
    }
}
