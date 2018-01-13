package com.github.kennedyoliveira.ultimatepastebin.action;

import com.github.kennedyoliveira.pastebin4j.Paste;
import com.github.kennedyoliveira.ultimatepastebin.service.PasteBinService;
import com.github.kennedyoliveira.ultimatepastebin.service.ToolWindowService;
import com.github.kennedyoliveira.ultimatepastebin.utils.UltimatePasteBinUtils;
import com.intellij.notification.Notification;
import com.intellij.notification.NotificationType;
import com.intellij.notification.Notifications;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.application.Application;
import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.application.TransactionGuard;
import com.intellij.openapi.application.WriteAction;
import com.intellij.openapi.components.ServiceManager;
import com.intellij.openapi.diagnostic.Logger;
import com.intellij.openapi.fileEditor.FileEditorManager;
import com.intellij.openapi.fileEditor.OpenFileDescriptor;
import com.intellij.openapi.progress.PerformInBackgroundOption;
import com.intellij.openapi.progress.ProgressIndicator;
import com.intellij.openapi.progress.Task;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.project.ProjectManager;
import com.intellij.openapi.vfs.VfsUtil;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.util.ui.UIUtil;
import org.jetbrains.annotations.NotNull;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Optional;

import static com.github.kennedyoliveira.ultimatepastebin.i18n.MessageBundle.getMessage;

/**
 * Action that show the contents of a paste in the Editor.
 */
public class ShowInEditorAction extends AbstractPasteSelectedAction {

  private static final Logger logger = UltimatePasteBinUtils.logger;

  @Override
  public void actionPerformed(AnActionEvent e) {
    final Project currentProject = e.getProject() != null ? e.getProject() : ProjectManager.getInstance().getDefaultProject();

    new Task.Backgroundable(currentProject, getMessage("ultimatepastebin.paste.content.fetching"), false, PerformInBackgroundOption.DEAF) {
      @Override
      public void run(@NotNull ProgressIndicator indicator) {
        logger.info("Initializing Show Paste Content in Editor Task");
        try {
          ToolWindowService toolWindowService = ServiceManager.getService(ToolWindowService.class);

          Optional<Paste> selectedPaste = toolWindowService.getSelectedPaste();

          if (selectedPaste.isPresent()) {
            Paste paste = selectedPaste.get();

            PasteBinService service = ServiceManager.getService(PasteBinService.class);
            logger.info("Fetching paste content: " + paste);
            String pasteContent = service.getPasteBin().getPasteContent(paste);
            logger.info("Paste content fetched succesfully");

            if (pasteContent != null) {
              logger.debug("Saving content to temporary file");
              Path tmpFile = Files.createTempFile("ultimate_pastebin_", String.format("%08d.%s", System.currentTimeMillis(), paste.getHighLight().toString()));

              Files.write(tmpFile, pasteContent.getBytes());

              VirtualFile fileByIoFile = VfsUtil.findFileByIoFile(tmpFile.toFile(), true);

              if (fileByIoFile != null) {
                logger.info("Opening editor with paste contents");
                final Runnable openDocument = () -> FileEditorManager.getInstance(currentProject)
                                                                     .openEditor(new OpenFileDescriptor(currentProject,
                                                                                                        fileByIoFile),
                                                                                 true);

                final Application application = ApplicationManager.getApplication();
                application.invokeLater(() -> application.runWriteAction(openDocument));
              }
            }
          }
        } catch (Exception e1) {
          logger.error("Failed to get paste content to show in editor", e1);
          Notifications.Bus.notify(new Notification("Error opening paste in editor",
                                                    "Ultimate PasteBin",
                                                    getMessage("ultimatepastebin.actions.openpasteineditor.error.message", e1.getMessage()),
                                                    NotificationType.ERROR));
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
