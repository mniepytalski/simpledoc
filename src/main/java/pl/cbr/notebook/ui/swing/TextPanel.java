package pl.cbr.notebook.ui.swing;

import lombok.extern.slf4j.Slf4j;
import pl.cbr.notebook.NoteUrl;
import pl.cbr.notebook.utils.SelectedTextUtil;

import javax.swing.*;
import javax.swing.text.Element;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.net.URI;

@Slf4j
public class TextPanel extends JTextPane {

    private final NotebookUIModel model;
    private final Label positionLabel;

    public TextPanel(NotebookUIModel model, Label positionLabel) {
        this.model = model;
        this.positionLabel = positionLabel;
        createUI();
        model.setTextArea(this);
    }

    public void replaceSelection(String replacement) {
        String oldText = getText();
        super.replaceSelection(replacement);
        model.calculateDiff(oldText, getText());
    }

    private void createUI() {
        addKeyListener(new KeyListener() {
            @Override
            public void keyTyped(KeyEvent e) {
            }

            @Override
            public void keyPressed(KeyEvent e) {
                if (SelectedTextUtil.checkIsCursor(e)) {
                    model.calculateDiffWithUpdate();
                }
                if (e.isMetaDown() || e.isControlDown()) {
                    return;
                }
                String sel = getSelectedText();
                if (sel != null && !sel.isEmpty()) {
                    String oldText = model.getTextArea().getText();
                    String newText = SelectedTextUtil.modifyText(e, model.getTextArea());
                    model.calculateDiff(oldText, newText);
                } else {
                    if (e.getKeyCode() == KeyEvent.VK_ENTER) {
                        model.calculateDiffWithUpdate();
                    }
                }
            }

            @Override
            public void keyReleased(KeyEvent e) {
                if ( KeyEvent.VK_DOWN == e.getKeyCode() ) {
                    int totalLines = model.getTextArea().getDocument().getDefaultRootElement().getElementCount();
                    int caretPosition = model.getTextArea().getCaretPosition();
                    Element root = model.getTextArea().getDocument().getDefaultRootElement();
                    int line = root.getElementIndex(caretPosition) + 1; // +1, jeśli chcesz liczyć od 1
                    if (line == totalLines) {
                        log.info("Kursor jest w linii: {} wszystkich jest:{}", line, totalLines);
                    }
                }
            }
        });

        positionLabel.setText(getPositionText());
        addCaretListener(e -> {
            positionLabel.setText(getPositionText());
            int end = getSelectionEnd();
            String selected = getSelectedText();
            if (selected != null && !selected.isEmpty()) {
                MarkedDocument markedDocument = model.getNotebook().getMarkedDocument();
                markedDocument.set(getSelectionStart(), end, selected);
            }
        });

        addTextAreaMouseListener();

        // Custom COPY
        KeyStroke keyStrokeC = KeyStroke.getKeyStroke(KeyEvent.VK_C, Toolkit.getDefaultToolkit().getMenuShortcutKeyMaskEx());
        getInputMap().put(keyStrokeC, "copy");
        getActionMap().put("copy", new AbstractAction() {
            public void actionPerformed(ActionEvent e) {
                model.copyTextToClipboard();
            }
        });

        // Custom PASTE
        KeyStroke keyStrokeV = KeyStroke.getKeyStroke(KeyEvent.VK_V, Toolkit.getDefaultToolkit().getMenuShortcutKeyMaskEx());
        getInputMap().put(keyStrokeV, "paste");
        getActionMap().put("paste", new AbstractAction() {
            public void actionPerformed(ActionEvent e) {
                model.insertFromClipboard();
            }
        });

        DocumentArea.dynamicDocumentStyle(this, model.getSearchField(), model.getNotebook());
    }

    private void addTextAreaMouseListener() {
        addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                model.calculateDiffWithUpdate();
                int pos = viewToModel2D(e.getPoint());
                NoteUrl noteUrl = model.getNotebook().getActualNote().findUrl(pos);
                if (noteUrl != null) {
                    try {
                        switch(noteUrl.getType()) {
                            case EXTERNAL:
                                Desktop.getDesktop().browse(new URI(noteUrl.getUrl()));
                                break;
                            case INTERNAL:
                                model.setNoteFromInternalLink(noteUrl.getUrl());
                                break;
                        }
                    } catch (Exception ex) {
                        log.error(ex.getMessage());
                    }
                }
            }
        });

        addMouseMotionListener(new MouseAdapter() {
            @Override
            public void mouseMoved(MouseEvent e) {
                int pos = viewToModel2D(e.getPoint());
                NoteUrl url = model.getNotebook().getActualNote().findUrl(pos);
                if (url != null) {
                    model.getNotebook().getActualNote().getUrls().stream()
                            .filter(noteUrl -> noteUrl.getUrl().equals(url.getUrl())).findFirst()
                            .ifPresent(noteUrl -> {
                                getStyledDocument().setCharacterAttributes(noteUrl.getStart(), noteUrl.getLength(), TextStyle.urlUnderlineStyle(), true);
                                setCursor(new Cursor(Cursor.HAND_CURSOR));
                                model.setUrlStyleChanged(true);
                            });
                } else {
                    if (model.isUrlStyleChanged()) {
                        model.getNotebook().getActualNote().getUrls()
                                .forEach(noteUrl -> getStyledDocument().setCharacterAttributes(noteUrl.getStart(), noteUrl.getLength(), TextStyle.urlStyle(), true));
                        setCursor(new Cursor(Cursor.TEXT_CURSOR));
                        model.setUrlStyleChanged(false);
                    }
                }
            }
        });
    }

    private String getPositionText() {
        Element root = getDocument().getDefaultRootElement();
        int caretPos = getCaretPosition();
        int line = getDocument().getDefaultRootElement().getElementIndex(caretPos) + 1;
        Element lineElement = root.getElement(root.getElementIndex(caretPos));
        int column = caretPos - lineElement.getStartOffset() + 1;
        return "Ln:" + line + "  Col:" + column + "  Pos:" + caretPos;
    }
}
