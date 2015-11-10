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
     * Object to interact with PasteBin API.
     */
    private PasteBin pasteBin;

    /**
     * User information.
     */
    private UserInformation userInformation;

    public PasteBinServiceImpl() {
        this.configurationService = ServiceManager.getService(PasteBinConfigurationService.class);
    }

    @Override
    public boolean isCredentialsValid() {
        if (!initialized)
            initialize();

        return true;
    }

    @Override
    public void initialize() {
        PasteBinSettings pasteBinSettings = configurationService.getPasteBinSettings();
        if (pasteBinSettings == null || pasteBinSettings.getPasteBinAccountCredentials() == null)
            throw new IllegalStateException(getMessage("ultimatepastebin.accountcredentials.null"));

        if (pasteBinSettings.getPasteBinAccountCredentials().getDevKey() == null)
            throw new IllegalStateException(getMessage("ultimatepastebin.accountcredentials.devkey.null"));

        this.pasteBin = new PasteBin(pasteBinSettings.getPasteBinAccountCredentials());

        // Fetchs the user information to check if the account credentials is valid
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

    @Override
    public void invalidateCredentials() {
        this.initialized = false;
    }
}
