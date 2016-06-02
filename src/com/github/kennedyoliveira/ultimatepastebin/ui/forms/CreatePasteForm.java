package com.github.kennedyoliveira.ultimatepastebin.ui.forms;

import com.github.kennedyoliveira.pastebin4j.Paste;
import com.github.kennedyoliveira.pastebin4j.PasteExpiration;
import com.github.kennedyoliveira.pastebin4j.PasteHighLight;
import com.github.kennedyoliveira.pastebin4j.PasteVisibility;
import com.github.kennedyoliveira.ultimatepastebin.service.PasteBinService;
import com.github.kennedyoliveira.ultimatepastebin.service.ToolWindowService;
import com.github.kennedyoliveira.ultimatepastebin.utils.ClipboardUtils;
import com.intellij.notification.Notification;
import com.intellij.notification.NotificationListener;
import com.intellij.notification.NotificationType;
import com.intellij.notification.Notifications;
import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.components.ServiceManager;
import com.intellij.openapi.editor.Document;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.editor.EditorFactory;
import com.intellij.openapi.editor.EditorSettings;
import com.intellij.openapi.editor.ex.EditorEx;
import com.intellij.openapi.fileTypes.FileType;
import com.intellij.openapi.progress.ProgressIndicator;
import com.intellij.openapi.progress.Task;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.DialogWrapper;
import com.intellij.openapi.util.AsyncResult;
import com.intellij.ui.EditorTextField;
import com.intellij.uiDesigner.core.GridConstraints;
import com.intellij.util.Consumer;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;
import java.awt.*;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

import static com.github.kennedyoliveira.ultimatepastebin.i18n.MessageBundle.getMessage;

/**
 * Created by kennedy on 11/7/15.
 */
public class CreatePasteForm extends DialogWrapper {

    private final static Map<Integer, PasteVisibility> pasteVisibilityMap;
    private final static Map<Integer, PasteExpiration> pasteExpirationMap;

    static {
        pasteVisibilityMap = new HashMap<>(6);

        pasteVisibilityMap.put(0, PasteVisibility.PUBLIC);
        pasteVisibilityMap.put(1, PasteVisibility.PRIVATE);
        pasteVisibilityMap.put(2, PasteVisibility.UNLISTED);

        pasteExpirationMap = new HashMap<>(14);

        pasteExpirationMap.put(0, PasteExpiration.NEVER);
        pasteExpirationMap.put(1, PasteExpiration.TEN_MINUTES);
        pasteExpirationMap.put(2, PasteExpiration.ONE_HOUR);
        pasteExpirationMap.put(3, PasteExpiration.ONE_DAY);
        pasteExpirationMap.put(4, PasteExpiration.ONE_WEEK);
        pasteExpirationMap.put(5, PasteExpiration.TWO_WEEKS);
        pasteExpirationMap.put(6, PasteExpiration.ONE_MONTH);
    }

    private JPanel principalPanel;
    private JTextField pasteTitle;
    private JComboBox pasteExpiration;
    private JComboBox pasteVisibility;
    private JComboBox pasteHighlight;
    private JPanel pasteContentPanel;
    private EditorTextField codeEditor;
    private Editor customizedEditor;
    /**
     * Paste that will be created.
     */
    private Paste paste;

    public CreatePasteForm(@Nullable Project project, Paste paste, FileType fileType) {
        super(project);

        Objects.requireNonNull(paste);

        this.paste = paste;

        setTitle(getMessage("ultimatepastebin.createpaste.form.title"));
        setAutoAdjustable(true);
        setModal(false);

        Arrays.stream(PasteHighLight.values()).forEach(pasteHighlight::addItem);

        if (paste.getHighLight() != null) {
            setSelectedPasteHighlight(paste.getHighLight());
        } else {
            setSelectedPasteHighlight(PasteHighLight.TEXT);
        }

        pasteExpiration.addItem(getMessage("ultimatepastebin.paste.expiration.never"));
        pasteExpiration.addItem(getMessage("ultimatepastebin.paste.expiration.tenminutes"));
        pasteExpiration.addItem(getMessage("ultimatepastebin.paste.expiration.onehour"));
        pasteExpiration.addItem(getMessage("ultimatepastebin.paste.expiration.oneday"));
        pasteExpiration.addItem(getMessage("ultimatepastebin.paste.expiration.oneweek"));
        pasteExpiration.addItem(getMessage("ultimatepastebin.paste.expiration.twoweeks"));
        pasteExpiration.addItem(getMessage("ultimatepastebin.paste.expiration.onemonth"));

        pasteVisibility.addItem(getMessage("ultimatepastebin.paste.visibility.public"));
        pasteVisibility.addItem(getMessage("ultimatepastebin.paste.visibility.private"));
        pasteVisibility.addItem(getMessage("ultimatepastebin.paste.visibility.unlisted"));

        init();

        String pasteContent = paste.getContent() != null ? paste.getContent() : "";

        final Document content = createDocument(pasteContent);

        codeEditor = new EditorTextField(content, project, fileType, false, false) {
            @Override
            protected EditorEx createEditor() {
                EditorEx editor = (EditorEx) EditorFactory.getInstance().createEditor(content, project, fileType, false);

                editor.setHorizontalScrollbarVisible(true);
                editor.setVerticalScrollbarVisible(true);

                EditorSettings settings = editor.getSettings();
                settings.setLineNumbersShown(true);

                customizedEditor = editor;

                return editor;
            }
        };

        // Copied from the generated code
        pasteContentPanel.add(codeEditor, new GridConstraints(0, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, null, new Dimension(600, 325), null, 0, false));
        pasteContentPanel.setMinimumSize(new Dimension(600, 325));

        pasteTitle.requestFocusInWindow();
    }

    @NotNull
    private Document createDocument(String content) {
        Document document;
        try {
            document = EditorFactory.getInstance().createDocument(content);
        } catch (Exception | AssertionError e) {
            document = EditorFactory.getInstance().createDocument("");
        }

        return document;
    }


    /**
     * Show the create paste forms populated with the Paste info
     *
     * @param paste    Paste to be saved
     * @param project  Current project
     * @param fileType Type of the file for SyntaxHighLight
     */
    public static void createAndShowForm(final Paste paste, Project project, FileType fileType) {
        AsyncResult<Boolean> booleanAsyncResult = new CreatePasteForm(project, paste, fileType).showAndGetOk();

        booleanAsyncResult.doWhenDone((Consumer<Boolean>) result -> {
            if (result) {
                new Task.Backgroundable(project, getMessage("ultimatepastebin.actions.createpaste.task.title"), false) {
                    @Override
                    public void run(@NotNull ProgressIndicator indicator) {
                        try {
                            PasteBinService service = ServiceManager.getService(PasteBinService.class);
                            String url = service.getPasteBin().createPaste(paste);

                            String message = getMessage("ultimatepastebin.actions.createpaste.ok.notification.message", url);

                            ClipboardUtils.copyToClipboard(url);

                            Notifications.Bus.notify(new Notification("Paste created",
                                    "Ultimate PasteBin",
                                    message,
                                    NotificationType.INFORMATION,
                                    NotificationListener.URL_OPENING_LISTENER), project);

                            // Updates the pastes...
                            ApplicationManager.getApplication().invokeLater(() -> {
                                ServiceManager.getService(ToolWindowService.class).fetchUserPastes();
                            });
                        } catch (Exception e1) {
                            Notifications.Bus.notify(new Notification("Error creating a paste",
                                    "Ultimate PasteBin",
                                    getMessage("ultimatepastebin.actions.createpaste.error.notification.message", e1.getMessage()),
                                    NotificationType.ERROR));
                        }
                    }
                }.queue();
            }
        });
    }

    @Nullable
    @Override
    protected JComponent createCenterPanel() {
        return principalPanel;
    }

    public void setSelectedPasteHighlight(PasteHighLight pasteHighlight) {
        this.pasteHighlight.setSelectedItem(pasteHighlight);
    }

    @Override
    protected void doOKAction() {
        // Updates the paste
        paste.setTitle(pasteTitle.getText());
        paste.setContent(codeEditor.getText());
        paste.setHighLight((PasteHighLight) pasteHighlight.getSelectedItem());
        paste.setExpiration(pasteExpirationMap.get(pasteExpiration.getSelectedIndex()));
        paste.setVisibility(pasteVisibilityMap.get(pasteVisibility.getSelectedIndex()));

        releaseEditor();

        super.doOKAction();
    }

    @Override
    public void doCancelAction() {
        releaseEditor();

        super.doCancelAction();
    }

    /**
     * Releases the allocated editor.
     */
    private void releaseEditor() {
        if (customizedEditor != null)
            EditorFactory.getInstance().releaseEditor(customizedEditor);
    }
}
