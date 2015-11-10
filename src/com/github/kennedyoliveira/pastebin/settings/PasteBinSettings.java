package com.github.kennedyoliveira.pastebin.settings;

import com.github.kennedyoliveira.pastebin4j.AccountCredentials;

/**
 * Created by kennedy on 11/7/15.
 */
public class PasteBinSettings {

    /**
     * Holds the credentials to connect to PasteBin
     */
    private AccountCredentials pasteBinAccountCredentials;

    public AccountCredentials getPasteBinAccountCredentials() {
        return pasteBinAccountCredentials;
    }

    public void setPasteBinAccountCredentials(AccountCredentials pasteBinAccountCredentials) {
        this.pasteBinAccountCredentials = pasteBinAccountCredentials;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        PasteBinSettings that = (PasteBinSettings) o;

        if (that.getPasteBinAccountCredentials() == null && this.pasteBinAccountCredentials != null) return false;
        if (this.pasteBinAccountCredentials == null && that.getPasteBinAccountCredentials() == null) return false;

        AccountCredentials thatPasteBinAccountCredentials = that.getPasteBinAccountCredentials();

        if (!this.pasteBinAccountCredentials.getDevKey().equals(thatPasteBinAccountCredentials.getDevKey()))
            return false;

        if (!this.pasteBinAccountCredentials.getUserName().equals(that.pasteBinAccountCredentials.getUserName()))
            return false;

        if (!this.pasteBinAccountCredentials.getPassword().equals(that.pasteBinAccountCredentials.getPassword()))
            return false;

        return true;
    }

    @Override
    public int hashCode() {
        return pasteBinAccountCredentials != null ? pasteBinAccountCredentials.hashCode() : 0;
    }
}
