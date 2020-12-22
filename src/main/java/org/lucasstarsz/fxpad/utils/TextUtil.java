package org.lucasstarsz.fxpad.utils;

import org.lucasstarsz.fxpad.nodes.FormatArea;

import java.util.Map;

public class TextUtil {

    // TODO: This looks awful. Fix it. smh.
    public static void shift(FormatArea textArea, int shiftAmount) {
        if (textArea.selectedTextProperty().getValue().isEmpty()) {
            Map.Entry<String, int[]> currLine = getCurrentLine(textArea);
            if (currLine.getKey().startsWith("\t") || shiftAmount > 0) {
                if (shiftAmount > 0) {
                    textArea.replaceText(
                            currLine.getValue()[0],
                            currLine.getValue()[1],
                            (shiftAmount - 1 > 0 ? "\t".repeat(shiftAmount - 1) : '\t') + currLine.getKey()
                    );
                } else if (shiftAmount < 0) {
                    textArea.replaceText(
                            currLine.getValue()[0],
                            currLine.getValue()[1],
                            currLine.getKey().substring(Math.abs(shiftAmount))
                    );
                }
            }
        } else {
            int selectionStart = textArea.getSelection().getStart();
            int selectionEnd = textArea.getSelection().getEnd();

            int fullSelectionStart = textArea.getText().lastIndexOf('\n', selectionStart) + 1;
            int fullSelectionEnd = textArea.getText().indexOf('\n', selectionEnd);

            if (textArea.getSelectedText().startsWith("\n")) {
                fullSelectionStart = textArea.getText().lastIndexOf('\n', selectionStart + 1) + 1;
            }
            if (fullSelectionEnd == -1) fullSelectionEnd = textArea.getLength();

            String fullSelection = textArea.getText(fullSelectionStart, fullSelectionEnd);
            String[] lines = fullSelection.split("\n");

            int totalTabMovement = 0;
            int firstTabMovement = 0;
            for (int i = 0; i < lines.length; i++) {
                if (lines[i].startsWith("\t") || shiftAmount > 0) {
                    if (shiftAmount > 0) {
                        lines[i] = "\t".repeat(shiftAmount) + lines[i];
                        totalTabMovement += shiftAmount;
                    } else if (shiftAmount < 0) {
                        lines[i] = lines[i].substring(Math.abs(shiftAmount));
                        totalTabMovement += shiftAmount;
                    }
                }
            }
/*
            FXPad lets you control your indents.
            Ctrl/Command + Tab to indent selected content to the right.
                    Shift + Tab to indent selected content to the left.
*/

            textArea.replaceText(fullSelectionStart, Math.min(fullSelectionEnd, textArea.getText().length()), String.join("\n", lines));
            textArea.selectRange(selectionStart + firstTabMovement, selectionEnd + totalTabMovement);
        }
    }

    public static void enterWithTabs(FormatArea textArea) {
        Map.Entry<String, int[]> currLine = TextUtil.getCurrentLine(textArea);
        int caretPos = textArea.getCaretPosition() - 1;
        int tabCount = 0;

        for (char c : currLine.getKey().toCharArray()) {
            if (c == '\t') tabCount++;
            else break;
        }

        textArea.replaceText(caretPos, caretPos + 1, "\n" + "\t".repeat(tabCount));
    }

    private static Map.Entry<String, int[]> getCurrentLine(FormatArea tx) {
        String text = tx.getText();
        if (text.isEmpty()) return Map.entry("", new int[]{0, 0});

        int caretPos = tx.getCaretPosition() - 1;
        int first = text.lastIndexOf('\n', caretPos - 1);
        int last = text.indexOf('\n', caretPos);

        if (first < 0) first = 0;
        if (text.charAt(first) == '\n' && first != 0) first++;
        if (last < 0) last = text.length();

        return Map.entry(tx.getText(first, last), new int[]{first, last});
    }
}
