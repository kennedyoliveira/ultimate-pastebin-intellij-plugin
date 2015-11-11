package com.github.kennedyoliveira.ultimatepastebin.action;

import com.github.kennedyoliveira.ultimatepastebin.i18n.MessageBundle;
import com.github.kennedyoliveira.ultimatepastebin.service.PasteBinService;
import com.github.kennedyoliveira.ultimatepastebin.service.ToolWindowService;
import com.github.kennedyoliveira.pastebin4j.Paste;
import com.github.kennedyoliveira.pastebin4j.PasteVisibility;
import com.intellij.ide.actions.ShowFilePathAction;
import com.intellij.notification.Notification;
import com.intellij.notification.NotificationType;
import com.intellij.notification.Notifications;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.components.ServiceManager;
import com.intellij.openapi.fileChooser.*;
import com.intellij.openapi.progress.ProgressIndicator;
import com.intellij.openapi.progress.Task;
import com.intellij.openapi.util.SystemInfo;
import com.intellij.openapi.vfs.VirtualFileWrapper;
import org.jetbrains.annotations.NotNull;

import java.nio.file.Files;
import java.util.Optional;

import static com.github.kennedyoliveira.ultimatepastebin.i18n.MessageBundle.getMessage;

/**
 * Created by kennedy on 11/8/15.
 */
public class SavePasteContentToFileAction extends AbstractPasteSelectedAction {

    @Override
    public void actionPerformed(AnActionEvent e) {
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

            FileSaverDescriptor fileSaverDescriptor = new FileSaverDescriptor(MessageBundle.getMessage("ultimatepastebin.actions.copypastecontentstofile.savefiledialog.title"),
                                                                              MessageBundle.getMessage("ultimatepastebin.actions.copypastecontentstofile.savefiledialog.description"),
                                                                              "txt", paste.getHighLight().toString());

            FileSaverDialog saveFileDialog = FileChooserFactory.getInstance().createSaveFileDialog(fileSaverDescriptor, e.getProject());
            VirtualFileWrapper fileToSave = saveFileDialog.save(null, null);

            if (fileToSave != null) {
                new Task.Backgroundable(null, getMessage("ultimatepastebin.actions.copypastecontentstofile.task.title"), false) {
                    @Override
                    public void run(@NotNull ProgressIndicator indicator) {
                        try {
                            indicator.setText(getMessage("ultimatepastebin.tasks.fetchingcontent.message"));
                            PasteBinService service = ServiceManager.getService(PasteBinService.class);

                            String pasteContent = service.getPasteBin().getPasteContent(paste);

                            indicator.setText(getMessage("ultimatepastebin.tasks.savingtodisk.message"));
                            indicator.setText2(getMessage("ultimatepastebin.tasks.savingtodisk.file", fileToSave.getFile().getAbsolutePath()));

                            Files.write(fileToSave.getFile().toPath(), pasteContent.getBytes());

                            String fileManager = SystemInfo.isMac ? getMessage("ultimatepastebin.actions.showfile.macosx") : getMessage("ultimatepastebin.actions.showfile.other",
                                                                                                                                        ShowFilePathAction.getFileManagerName());

                            Notifications.Bus.notify(new Notification("Paste contents saved to file",
                                                                      "Ultimate PasteBin",
                                                                      String.format(getMessage("ultimatepastebin.actions.copypastecontentstofile.ok.notification.message",
                                                                                               fileManager)),
                                                                      NotificationType.INFORMATION,
                                                                      (notification, event) -> {
                                                                          ShowFilePathAction.openFile(fileToSave.getFile());
                                                                      }), e.getProject());
                        } catch (Exception e) {
                            Notifications.Bus.notify(new Notification("Error fetching paste contents",
                                                                      "Ultimate PasteBin",
                                                                      getMessage("ultimatepastebin.actions.copypastecontent.genericerror.notification.message", e.getMessage()),
                                                                      NotificationType.ERROR));
                        }
                    }
                }.queue();
            }
        }
    }

    @Override
    public void update(AnActionEvent e) {
        super.update(e);

        e.getPresentation().setText(getMessage("ultimatepastebin.actions.copypastecontentstofile.text"));
        e.getPresentation().setDescription(getMessage("ultimatepastebin.actions.copypastecontentstofile.description"));
    }
}
