package com.github.kennedyoliveira.pastebin.service;

import com.github.kennedyoliveira.pastebin4j.AccountCredentials;
import com.github.kennedyoliveira.pastebin4j.PasteBin;
import com.github.kennedyoliveira.pastebin4j.UserInformation;
import org.jetbrains.annotations.NotNull;

/**
 * Created by kennedy on 11/7/15.
 */
public interface PasteBinService {
    void setCredentials(@NotNull String devKey, String userName, String password);

    /**
     * @return If the credentials are valid or not
     * @throws IllegalStateException if any credential is missing, like username or password
     */
    boolean isCredentialsValid();

    /**
     * <p>Initialize the service connecting to pastebin and fetching some info.</p>
     *
     * @throws IllegalStateException if account credentials {@link #setCredentials(String, String, String)} is null or {@link AccountCredentials#getDevKey()} is null.
     */
    void initialize();

    /**
     * @return {@link UserInformation} of the current logged user.
     */
    UserInformation getUserInformation();

    /**
     * @return {@link PasteBin} to interact with PasteBin.com
     */
    PasteBin getPasteBin();

    /**
     * Verify if the credentials are still valid.
     */
    void checkCredentials();
}
