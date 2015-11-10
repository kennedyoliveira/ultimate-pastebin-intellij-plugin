package com.github.kennedyoliveira.pastebin.action;

import com.github.kennedyoliveira.pastebin.service.PasteBinService;
import com.github.kennedyoliveira.pastebin.service.ToolWindowService;
import com.github.kennedyoliveira.pastebin4j.Paste;
import com.github.kennedyoliveira.pastebin4j.PasteVisibility;
import com.intellij.notification.Notification;
import com.intellij.notification.NotificationType;
import com.intellij.notification.Notifications;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.components.ServiceManager;
import com.intellij.openapi.fileChooser.ex.FileTextFieldImpl;
import com.intellij.openapi.fileEditor.FileEditorManager;
import com.intellij.openapi.fileEditor.OpenFileDescriptor;
import com.intellij.openapi.progress.PerformInBackgroundOption;
import com.intellij.openapi.progress.ProgressIndicator;
import com.intellij.openapi.progress.Task;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.project.ProjectManager;
import com.intellij.openapi.vfs.VfsBundle;
import com.intellij.openapi.vfs.VfsUtil;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.util.ui.UIUtil;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Optional;
import java.util.Random;

import static com.github.kennedyoliveira.pastebin.i18n.MessageBundle.getMessage;

/**
 * Created by kennedy on 11/9/15.
 */
public class ShowInEditorAction extends AbstractPasteSelectedAction {

    @Override
    public void actionPerformed(AnActionEvent e) {

        final Project currentProject = e.getProject() != null ? e.getProject() : ProjectManager.getInstance().getDefaultProject();

        new Task.Backgroundable(currentProject, getMessage("ultimatepastebin.paste.content.fetching"), false, PerformInBackgroundOption.DEAF) {
            @Override
            public void run(@NotNull ProgressIndicator indicator) {
                try {
                    ToolWindowService toolWindowService = ServiceManager.getService(ToolWindowService.class);

                    Optional<Paste> selectedPaste = toolWindowService.getSelectedPaste();

                    if (selectedPaste.isPresent()) {
                        Paste paste = selectedPaste.get();

                        if (paste.getVisibility() == PasteVisibility.PRIVATE) {
                            Notifications.Bus.notify(new Notification("Can't fetch private paste contents",
                                    "Ultimate PasteBin",
                                    getMessage("ultimatepastebin.actions.copypastecontent.error.notification.message"),
                                    NotificationType.ERROR));
                            return;
                        }

                        PasteBinService service = ServiceManager.getService(PasteBinService.class);
                        String pasteContent = service.getPasteBin().getPasteContent(paste);

                        if (pasteContent != null) {
                            Path tmpFile = Files.createTempFile("ultimate_pastebin_", String.format("%08d.%s", System.currentTimeMillis(), paste.getHighLight().toString()));

                            Files.write(tmpFile, pasteContent.getBytes());

                            VirtualFile fileByIoFile = VfsUtil.findFileByIoFile(tmpFile.toFile(), true);

                            if (fileByIoFile != null) {
                                UIUtil.invokeLaterIfNeeded(() -> {
                                    FileEditorManager.getInstance(currentProject).openEditor(new OpenFileDescriptor(currentProject, fileByIoFile), true);
                                });
                            }
                        }
                    }
                } catch (IOException e1) {
                    Notifications.Bus.notify(new Notification("Error opening paste in editor", "Ultimate PasteBin", getMessage("ultimatepastebin.actions.openpasteineditor.error.message", e1.getMessage()), NotificationType.ERROR));
                }
            }
        }.queue();
    }

    @Override
    public void update(AnActionEvent e) {
        super.update(e);

        e.getPresentation().setText(getMessage("ultimatepastebin.actions.showpasteineditor.text"));
        e.getPresentation().setDescription(getMessage("ultimatepastebin.actions.showpasteineditor.description"));
    }
}
