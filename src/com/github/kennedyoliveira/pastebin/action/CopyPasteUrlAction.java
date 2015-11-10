package com.github.kennedyoliveira.pastebin.action;

import com.github.kennedyoliveira.pastebin.i18n.MessageBundle;
import com.github.kennedyoliveira.pastebin.service.ToolWindowService;
import com.github.kennedyoliveira.pastebin.utils.ClipboardUtils;
import com.github.kennedyoliveira.pastebin4j.Paste;
import com.intellij.notification.Notification;
import com.intellij.notification.NotificationListener;
import com.intellij.notification.NotificationType;
import com.intellij.notification.Notifications;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.components.ServiceManager;
import com.intellij.openapi.fileEditor.FileEditorManager;
import com.intellij.openapi.fileEditor.OpenFileDescriptor;
import com.intellij.openapi.vfs.VfsUtil;
import com.intellij.openapi.vfs.VfsUtilCore;
import com.intellij.openapi.vfs.VirtualFile;

import javax.swing.*;
import java.io.File;
import java.net.URL;
import java.util.Optional;

import static com.github.kennedyoliveira.pastebin.i18n.MessageBundle.getMessage;

/**
 * <p>Copy the URL of a paste to clipboard</p>
 */
public class CopyPasteUrlAction extends AbstractPasteSelectedAction {

    @Override
    public void actionPerformed(AnActionEvent e) {
        ToolWindowService toolWindowService = ServiceManager.getService(ToolWindowService.class);

        Optional<Paste> selectedPaste = toolWindowService.getSelectedPaste();

        selectedPaste.ifPresent(this::copyToClipboardAndNotify);
    }

    private void copyToClipboardAndNotify(Paste paste) {
        String url = paste.getUrl();

        ClipboardUtils.copyToClipboard(url);

        Notifications.Bus.notify(new Notification("Paste URL Copied to Clipboard",
                                "Ultimate PasteBin",
                                getMessage("ultimatepastebin.actions.copypasteurl.ok.notification.message", url),
                                NotificationType.INFORMATION,
                                NotificationListener.URL_OPENING_LISTENER));
    }

    @Override
    public void update(AnActionEvent e) {
        super.update(e);

        e.getPresentation().setText(getMessage("ultimatepastebin.actions.copypasteurl.text"));
        e.getPresentation().setDescription(getMessage("ultimatepastebin.actions.copypasteurl.description"));
    }
}
