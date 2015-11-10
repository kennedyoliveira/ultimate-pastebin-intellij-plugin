package com.github.kennedyoliveira.pastebin.ui.forms;

import com.github.kennedyoliveira.pastebin4j.Paste;
import com.github.kennedyoliveira.pastebin4j.PasteExpiration;
import com.github.kennedyoliveira.pastebin4j.PasteHighLight;
import com.github.kennedyoliveira.pastebin4j.PasteVisibility;
import com.intellij.ide.highlighter.JavaFileType;
import com.intellij.openapi.editor.EditorFactory;
import com.intellij.openapi.editor.EditorSettings;
import com.intellij.openapi.editor.ex.EditorEx;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.project.ProjectManager;
import com.intellij.openapi.ui.DialogWrapper;
import com.intellij.openapi.util.Disposer;
import com.intellij.ui.*;
import com.intellij.uiDesigner.core.GridConstraints;
import com.intellij.uiDesigner.core.GridLayoutManager;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;
import java.awt.*;
import java.util.*;

import static com.github.kennedyoliveira.pastebin.i18n.MessageBundle.getMessage;

/**
 * Created by kennedy on 11/7/15.
 */
public class CreatePasteForm extends DialogWrapper {

    private final static Map<Integer, PasteVisibility> pasteVisibilityMap;
    private final static Map<Integer, PasteExpiration> pasteExpirationMap;

    static {
        pasteVisibilityMap = new HashMap<>(6);

        pasteVisibilityMap.put(1, PasteVisibility.PUBLIC);
        pasteVisibilityMap.put(2, PasteVisibility.PRIVATE);
        pasteVisibilityMap.put(3, PasteVisibility.UNLISTED);

        pasteExpirationMap = new HashMap<>(14);

        pasteExpirationMap.put(1, PasteExpiration.NEVER);
        pasteExpirationMap.put(2, PasteExpiration.TEN_MINUTES);
        pasteExpirationMap.put(3, PasteExpiration.ONE_HOUR);
        pasteExpirationMap.put(4, PasteExpiration.ONE_DAY);
        pasteExpirationMap.put(5, PasteExpiration.ONE_WEEK);
        pasteExpirationMap.put(6, PasteExpiration.TWO_WEEKS);
        pasteExpirationMap.put(7, PasteExpiration.ONE_MONTH);
    }

    private JPanel principalPanel;
    private JTextField pasteTitle;
    private JComboBox pasteExpiration;
    private JComboBox pasteVisibility;
    private JEditorPane pasteContent;
    private JComboBox pasteHighlight;
    private EditorTextField codeEditor;
    /**
     * Paste that will be created.
     */
    private Paste paste;

    public CreatePasteForm(@Nullable Project project, Paste paste) {
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

        if (paste.getContent() != null) {
            pasteContent.setText(paste.getContent());
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
        paste.setContent(pasteContent.getText());
        paste.setHighLight((PasteHighLight) pasteHighlight.getSelectedItem());
        paste.setExpiration(pasteExpirationMap.get(pasteExpiration.getSelectedIndex()));
        paste.setVisibility(pasteVisibilityMap.get(pasteHighlight.getSelectedIndex()));

        super.doOKAction();
    }

    private void createUIComponents() {
        codeEditor = new EditorTextField(EditorFactory.getInstance().createDocument("public class Zupa {\n\n public static void main(String[] args){\n\n}\n}"), ProjectManager.getInstance().getDefaultProject(), JavaFileType.INSTANCE, false, false) {
            @Override
            protected EditorEx createEditor() {
                EditorEx editor = super.createEditor();

                editor.setHorizontalScrollbarVisible(true);
                editor.setVerticalScrollbarVisible(true);

                EditorSettings settings = editor.getSettings();
                settings.setLineNumbersShown(true);

                return editor;
            }
        };
        codeEditor.setAutoscrolls(true);

    }
}
