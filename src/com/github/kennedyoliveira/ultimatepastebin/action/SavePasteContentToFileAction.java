package com.github.kennedyoliveira.ultimatepastebin.action;

import com.github.kennedyoliveira.pastebin4j.Paste;
import com.github.kennedyoliveira.ultimatepastebin.component.UltimatePasteBin;
import com.github.kennedyoliveira.ultimatepastebin.service.PasteBinService;
import com.github.kennedyoliveira.ultimatepastebin.service.ToolWindowService;
import com.github.kennedyoliveira.ultimatepastebin.utils.UltimatePasteBinUtils;
import com.intellij.ide.actions.ShowFilePathAction;
import com.intellij.notification.Notification;
import com.intellij.notification.NotificationType;
import com.intellij.notification.Notifications;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.components.ServiceManager;
import com.intellij.openapi.diagnostic.Logger;
import com.intellij.openapi.fileChooser.FileChooserFactory;
import com.intellij.openapi.fileChooser.FileSaverDescriptor;
import com.intellij.openapi.fileChooser.FileSaverDialog;
import com.intellij.openapi.progress.ProgressIndicator;
import com.intellij.openapi.progress.Task;
import com.intellij.openapi.util.SystemInfo;
import com.intellij.openapi.vfs.VirtualFileWrapper;
import org.jetbrains.annotations.NotNull;

import java.nio.file.Files;
import java.util.Optional;

import static com.github.kennedyoliveira.ultimatepastebin.i18n.MessageBundle.getMessage;

/**
 * Action that download paste content and save it to a file
 */
public class SavePasteContentToFileAction extends AbstractPasteSelectedAction {

  private static final Logger logger = UltimatePasteBinUtils.logger;

  @Override
  public void actionPerformed(AnActionEvent e) {
    logger.info("Initializing SavePasteContentToFileAction");
    final ToolWindowService toolWindowService = ServiceManager.getService(ToolWindowService.class);

    final Optional<Paste> selectedPaste = toolWindowService.getSelectedPaste();

    if (selectedPaste.isPresent()) {
      final Paste paste = selectedPaste.get();

      final FileSaverDescriptor fileSaverDescriptor = new FileSaverDescriptor(getMessage("ultimatepastebin.actions.copypastecontentstofile.savefiledialog.title"),
                                                                        getMessage("ultimatepastebin.actions.copypastecontentstofile.savefiledialog.description"),
                                                                        "txt",
                                                                        paste.getHighLight().toString());

      logger.debug("Showing save dialog");
      final FileSaverDialog saveFileDialog = FileChooserFactory.getInstance().createSaveFileDialog(fileSaverDescriptor, e.getProject());
      final VirtualFileWrapper fileToSave = saveFileDialog.save(null, null);

      logger.info("Save content to file: " + fileToSave);
      if (fileToSave != null) {
        new Task.Backgroundable(null, getMessage("ultimatepastebin.actions.copypastecontentstofile.task.title"), false) {
          @Override
          public void run(@NotNull ProgressIndicator indicator) {
            logger.info("Starting save paste contents to file task");

            try {
              indicator.setText(getMessage("ultimatepastebin.tasks.fetchingcontent.message"));
              final PasteBinService service = ServiceManager.getService(PasteBinService.class);

              logger.info("Fetching paste content");
              final String pasteContent = service.getPasteBin().getPasteContent(paste);

              indicator.setText(getMessage("ultimatepastebin.tasks.savingtodisk.message"));
              indicator.setText2(getMessage("ultimatepastebin.tasks.savingtodisk.file", fileToSave.getFile().getAbsolutePath()));

              logger.info("Writing paste content to disk");
              Files.write(fileToSave.getFile().toPath(), pasteContent.getBytes());

              final String fileManager = SystemInfo.isMac ? getMessage("ultimatepastebin.actions.showfile.macosx") : getMessage("ultimatepastebin.actions.showfile.other",
                                                                                                                          ShowFilePathAction.getFileManagerName());

              Notifications.Bus.notify(new Notification("Paste contents saved to file",
                                                        "Ultimate PasteBin",
                                                        getMessage("ultimatepastebin.actions.copypastecontentstofile.ok.notification.message", fileManager),
                                                        NotificationType.INFORMATION,
                                                        (notification, event) -> ShowFilePathAction.openFile(fileToSave.getFile())), e.getProject());
            } catch (Exception e) {
              logger.error("Failed to save paste contents to disk", e);
              Notifications.Bus.notify(new Notification("Error fetching paste contents",
                                                        "Ultimate PasteBin",
                                                        getMessage("ultimatepastebin.actions.copypastecontent.genericerror.notification.message", e.getMessage()),
                                                        NotificationType.ERROR));
            }
          }
        }.queue();
      }
    } else {
      logger.info("No paste selected to save content");
    }
  }

  @Override
  public void update(AnActionEvent e) {
    super.update(e);

    e.getPresentation().setText(getMessage("ultimatepastebin.actions.copypastecontentstofile.text"));
    e.getPresentation().setDescription(getMessage("ultimatepastebin.actions.copypastecontentstofile.description"));
  }
}
