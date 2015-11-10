package com.github.kennedyoliveira.pastebin;

/**
 * Created by kennedy on 11/9/15.
 */
public class UltimatePasteBinConstants {

    /**
     * @deprecated Constants only class
     */
    private UltimatePasteBinConstants() {}

    /**
     * URL For donating
     */
    public final static String DONATION_URL = "https://www.paypal.com/cgi-bin/webscr?cmd=_donations&business=CR4K3FDKKK5FA&lc=GB&item_name=Kennedy%20Oliveira&item_number=ultimate%2dpastebin&currency_code=USD&bn=PP%2dDonationsBF%3abtn_donate_LG%2egif%3aNonHosted";

    /**
     * Link with the documentation for the translations
     */
    public final static String TRANSLATION_CONTRIBUTION_URL = "";

    /**
     * Link for reporting issues or suggestions
     */
    public final static String ISSUE_URL = "";

    /**
     * Default total of user pastes to fetch
     */
    public final static int DEFAULT_TOTAL_PASTES_TO_FETCH = 50;

    /**
     * <p>Maximum user pastes that can be fetched, this value is provided be the API.</p>
     * <p>The currently public api supports a maximum of 1000 pastes.</p>
     */
    public final static int MAX_PASTES_TO_FETCH = 1000;
}
