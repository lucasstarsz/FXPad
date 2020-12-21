package fxpadrtfx.utils;

import java.util.function.IntFunction;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.paint.Paint;
import javafx.scene.text.Font;
import javafx.scene.text.FontPosture;

import org.fxmisc.richtext.GenericStyledArea;
import org.reactfx.collection.LiveList;
import org.reactfx.value.Val;

public class PaddedLineNumberFactory implements IntFunction<Node> {

    private static final Insets DEFAULT_INSETS = new Insets(0.0, 15.0, 0.0, 5.0);
    //    private static final Paint DEFAULT_TEXT_FILL = Color.web("#666");
    private static final Font DEFAULT_FONT = Font.font("Consolas", FontPosture.REGULAR, 13);
    //    private static final Background DEFAULT_BACKGROUND = new Background(new BackgroundFill(Color.web("#ddd"), null, null));

    public static IntFunction<Node> get(GenericStyledArea<?, ?, ?> area) {
        return get(area, digits -> "%1$" + digits + "s");
    }

    public static IntFunction<Node> get(
            GenericStyledArea<?, ?, ?> area,
            IntFunction<String> format) {
        return new PaddedLineNumberFactory(area, format);
    }

    private final Val<Integer> nParagraphs;
    private final IntFunction<String> format;

    private PaddedLineNumberFactory(GenericStyledArea<?, ?, ?> area, IntFunction<String> format) {
        nParagraphs = LiveList.sizeOf(area.getParagraphs());
        this.format = format;
    }

    @Override
    public Node apply(int idx) {
        Val<String> formatted = nParagraphs.map(n -> format(idx + 1, n));

        Label paddedLineNo = new Label();
        paddedLineNo.setFont(DEFAULT_FONT);
//        paddedLineNo.setBackground(DEFAULT_BACKGROUND);
//        paddedLineNo.setTextFill(DEFAULT_TEXT_FILL);
        paddedLineNo.setPadding(DEFAULT_INSETS);
        paddedLineNo.setAlignment(Pos.TOP_LEFT);
        paddedLineNo.getStyleClass().add("padded-lineno");

        // bind label's text to a Val that stops observing area's paragraphs
        // when lineNo is removed from scene
        paddedLineNo.textProperty().bind(formatted.conditionOnShowing(paddedLineNo));

        return paddedLineNo;
    }

    private String format(int x, int max) {
        int digits = (int) Math.floor(Math.log10(max)) + 1;
        return String.format(format.apply(digits), x);
    }
}

