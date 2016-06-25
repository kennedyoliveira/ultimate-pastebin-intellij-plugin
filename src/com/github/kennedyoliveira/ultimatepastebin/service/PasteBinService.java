package com.github.kennedyoliveira.ultimatepastebin.service;

import com.github.kennedyoliveira.pastebin4j.PasteBin;
import com.github.kennedyoliveira.pastebin4j.UserInformation;
import com.github.kennedyoliveira.ultimatepastebin.settings.PasteBinConfigurationService;


public interface PasteBinService {
  /**
   * @return If the credentials are valid or not
   * @throws IllegalStateException if any credential is missing, like username or password
   */
  boolean isCredentialsValid();

  /**
   * <p>Initialize the service connecting to pastebin and fetching some info.</p>
   *
   * @throws IllegalStateException if account credentials ({@link PasteBinConfigurationService#getUsername()}, {@link PasteBinConfigurationService#getDevkey()}) are null.
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

  /**
   * Sets the atual cached crendentials invalid, it means that next time that any connection will be made to pastebin
   * the account credentials will be checked (do a new login)
   */
  void invalidateCredentials();
}
