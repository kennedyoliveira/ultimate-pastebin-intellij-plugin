package com.github.kennedyoliveira.pastebin.service;

import com.github.kennedyoliveira.pastebin.settings.PasteBinConfigurationService;
import com.github.kennedyoliveira.pastebin.settings.PasteBinSettings;
import com.github.kennedyoliveira.pastebin4j.AccountCredentials;
import com.github.kennedyoliveira.pastebin4j.PasteBin;
import com.github.kennedyoliveira.pastebin4j.UserInformation;
import com.intellij.openapi.components.ServiceManager;
import org.jetbrains.annotations.NotNull;

import static com.github.kennedyoliveira.pastebin.i18n.MessageBundle.getMessage;

/**
 * Created by kennedy on 11/7/15.
 */
public class PasteBinServiceImpl implements PasteBinService {

    private boolean initialized = false;

    private PasteBinConfigurationService configurationService;

    /**
     * Plugin settings
     */
    private PasteBinSettings pasteBinSettings;

    /**
     * Object to interact with PasteBin API.
     */
    private PasteBin pasteBin;

    /**
     * User information.
     */
    private UserInformation userInformation;

    public PasteBinServiceImpl() {
        this.configurationService = ServiceManager.getService(PasteBinConfigurationService.class);
        this.pasteBinSettings = this.configurationService.getPasteBinSettings();
    }

    @Override
    public void setCredentials(@NotNull String devKey, String userName, String password) {
        if (this.pasteBinSettings == null)
            this.pasteBinSettings = new PasteBinSettings();

        this.pasteBinSettings.setPasteBinAccountCredentials(new AccountCredentials(devKey, userName, password));
    }

    @Override
    public boolean isCredentialsValid() {
        if (!initialized)
            initialize();

        return true;
    }

    @Override
    public void initialize() {
        if (this.pasteBinSettings == null || this.pasteBinSettings.getPasteBinAccountCredentials() == null)
            throw new IllegalStateException(getMessage("ultimatepastebin.accountcredentials.null"));

        if (this.pasteBinSettings.getPasteBinAccountCredentials().getDevKey() == null)
            throw new IllegalStateException(getMessage("ultimatepastebin.accountcredentials.devkey.null"));

        this.pasteBin = new PasteBin(this.pasteBinSettings.getPasteBinAccountCredentials());
        this.userInformation = this.pasteBin.fetchUserInformation();

        configurationService.setValidCredentials(true);
    }

    @Override
    public UserInformation getUserInformation() {
        return userInformation;
    }

    @Override
    public PasteBin getPasteBin() {
        return pasteBin;
    }

    @Override
    public void checkCredentials() {
        initialized = false;
        initialize();
    }
}
