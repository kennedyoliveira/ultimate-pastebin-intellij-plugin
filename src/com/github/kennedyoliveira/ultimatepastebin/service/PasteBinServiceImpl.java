package com.github.kennedyoliveira.ultimatepastebin.service;

import com.github.kennedyoliveira.pastebin4j.AccountCredentials;
import com.github.kennedyoliveira.pastebin4j.PasteBin;
import com.github.kennedyoliveira.pastebin4j.UserInformation;
import com.github.kennedyoliveira.ultimatepastebin.settings.PasteBinConfigurationService;
import com.github.kennedyoliveira.ultimatepastebin.utils.UltimatePasteBinUtils;
import com.intellij.openapi.components.ServiceManager;
import com.intellij.openapi.diagnostic.Logger;
import com.intellij.openapi.util.text.StringUtil;

import java.util.concurrent.atomic.AtomicBoolean;

import static com.github.kennedyoliveira.ultimatepastebin.i18n.MessageBundle.getMessage;

/**
 * Service to interact with pastebin
 */
public class PasteBinServiceImpl implements PasteBinService {

  private static final Logger logger = UltimatePasteBinUtils.logger;

  /**
   * Use atomic because this can be accesed concurrently
   */
  private final AtomicBoolean initialized = new AtomicBoolean(false);

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
    if (initialized.compareAndSet(false, true)) {
      logger.info("PasteBin service not initialized");
      initialize();
    }

    return true;
  }

  @Override
  public void initialize() {
    logger.info("Initializing PasteBin service");
    if (!configurationService.isAuthInfoPresent())
      throw new IllegalStateException(getMessage("ultimatepastebin.accountcredentials.null"));

    if (StringUtil.isEmpty(configurationService.getDevkey()))
      throw new IllegalStateException(getMessage("ultimatepastebin.accountcredentials.devkey.null"));

    this.pasteBin = new PasteBin(new AccountCredentials(configurationService.getDevkey(),
                                                        configurationService.getUsername(),
                                                        configurationService.getPassword()));

    // Fetchs the user information to check if the account credentials is valid
    try {
      logger.info("Fetching user information...");
      this.userInformation = this.pasteBin.fetchUserInformation();
      logger.info("Fetched user information succefully");
    } catch (Exception e) {
      logger.error("Failed to fetch user information", e);
      invalidateCredentials();
      throw e;
    }

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
    initialized.set(false);
    initialize();
  }

  @Override
  public void invalidateCredentials() {
    logger.debug("Invalidating pastebin client credentials");
    this.initialized.set(false);
    this.configurationService.setValidCredentials(false);
  }
}
