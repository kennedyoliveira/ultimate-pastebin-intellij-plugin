package com.github.kennedyoliveira.ultimatepastebin.ui;

import com.github.kennedyoliveira.pastebin4j.UserInformation;

import javax.swing.*;

import static com.github.kennedyoliveira.ultimatepastebin.i18n.MessageBundle.getMessage;

/**
 * <p>Wrapper for a {@link com.github.kennedyoliveira.pastebin4j.UserInformation}</p>
 *
 * @author kennedy
 */
public class UserNode implements IconVisitable {

    private UserInformation userInformation;

    public UserNode(UserInformation userInformation) {
        this.userInformation = userInformation;
    }

    public UserInformation getUserInformation() {
        return userInformation;
    }

    public void setUserInformation(UserInformation userInformation) {
        this.userInformation = userInformation;
    }

    @Override
    public String toString() {
        return userInformation != null ? getMessage("ultimatepastebin.user.loggedin", userInformation.getUsername()) : getMessage("ultimatepastebin.user.loggedout");
    }

    @Override
    public Icon visitNode(IconVisitor iconVisitor) {
        return iconVisitor.visit(this);
    }
}
