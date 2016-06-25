package com.github.kennedyoliveira.ultimatepastebin.utils;

import com.github.kennedyoliveira.pastebin4j.PasteHighLight;
import com.intellij.openapi.diagnostic.Logger;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.DialogWrapper;
import com.intellij.openapi.ui.Messages;
import com.intellij.openapi.util.SystemInfo;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.util.ui.UIUtil;
import org.jetbrains.annotations.Nls;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;
import java.awt.*;
import java.io.IOException;
import java.util.Optional;

import static com.github.kennedyoliveira.ultimatepastebin.utils.SyntaxHighlighUtils.getHighlighByFileExtension;

/**
 * Utility methods for UltimatePasteBin
 */
public class UltimatePasteBinUtils {

  /**
   * General Logger for UltimatePasteBin
   */
  public final static Logger LOG = Logger.getInstance("ultimatepastebin");

  private UltimatePasteBinUtils() {}

  /**
   * <p>Shows a simple message info message box.</p>
   * <p>Sometimes in OS X the default message box {@link Messages#showIdeaMessageDialog(Project, String, String, String[], int, Icon, DialogWrapper.DoNotAskOption)} do not work,
   * so if you are on OS X this method use {@link Messages#showInfoMessage(Component, String, String)} that works.</p>
   *
   * @param project The project
   * @param title   Title of the message box.
   * @param message Message of the message box.
   */
  public static void showInfoMessageBox(@Nullable Project project, String title, String message) {
    // When on MacOsX for some reason intellij randomly shows or not the MessageBOX, so i'm showing with different messages that works everytime
    UIUtil.invokeLaterIfNeeded(() -> {
      if (SystemInfo.isMac) {
        Messages.showIdeaMessageDialog(project, message, title, new String[]{"OK"}, 0, null, null);
      } else {
        Messages.showInfoMessage(project, message, title);
      }
    });
  }

  /**
   * This method exists for the same reason as {@link #showInfoMessageBox(Project, String, String)}
   *
   * @param project The project
   * @param title   Title of the message box.
   * @param message Message of the message box.
   */
  public static void showErrorMessageBox(@Nullable Project project, String message, @Nls(capitalization = Nls.Capitalization.Title) String title) {
    UIUtil.invokeLaterIfNeeded(() -> {
      if (SystemInfo.isMac) {
        Messages.showIdeaMessageDialog(project, message, title, new String[]{"OK"}, 0, null, null);
      } else {
        Messages.showErrorDialog(message, title);
      }
    });
  }

  /**
   * Try to get a {@link PasteHighLight} from the default file extension for the {@link VirtualFile}, if not found try the current extension of the selected paste.
   *
   * @param file File to extract the {@link PasteHighLight}
   * @return An {@link Optional} that can have the {@link PasteHighLight}
   */
  public static Optional<PasteHighLight> getHighlighFromVirtualFile(@Nullable VirtualFile file) {
    if (file == null)
      return Optional.empty();

    String defaultFileExtension = file.getFileType().getDefaultExtension();

    Optional<PasteHighLight> byDefaultExtension = getHighlighByFileExtension(defaultFileExtension);

    if (byDefaultExtension.isPresent())
      return byDefaultExtension;

    String extension = file.getExtension();
    return getHighlighByFileExtension(extension);
  }

  /**
   * Get the content of the {@code virtualFile}, if there is any error, just return an {@link Optional#empty()}.
   *
   * @param virtualFile The virtual file to get the content
   * @return An {@link Optional} that can have the content of the file, or empty if there was any error.
   */
  public static Optional<String> getFileContent(VirtualFile virtualFile) {
    if (virtualFile == null)
      return Optional.empty();

    try {
      return Optional.of(new String(virtualFile.contentsToByteArray(), virtualFile.getCharset()));
    } catch (IOException e) {
      LOG.debug("Error while fetching the contents of a virtual file [" + virtualFile.getName() + "]", e);
      return Optional.empty();
    }
  }
}
