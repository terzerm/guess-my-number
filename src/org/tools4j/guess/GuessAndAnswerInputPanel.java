package org.tools4j.guess;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionListener;
import java.text.NumberFormat;

import static java.util.Objects.requireNonNull;

public class GuessAndAnswerInputPanel extends JPanel {
    private static final String TEXT = "Guess a number between %s and %s: ";

    public final JLabel label = new JLabel();
    public final JFormattedTextField guess = new JFormattedTextField(NumberFormat.getIntegerInstance());
    public final JButton lower = new JButton("Lower");
    public final JButton higher = new JButton("Higher");
    public final JButton correct = new JButton("Correct");

    public GuessAndAnswerInputPanel(final GuessModel model) {
        super(new FlowLayout(5, 5, FlowLayout.LEFT));
        guess.setColumns(5);
        label.setText(String.format(TEXT, model.minValue(), model.maxValue()));
        add(label);
        add(guess);
        add(lower);
        add(higher);
        add(correct);
        lower.addActionListener(listener(GuessModel.Answer.LOWER, model));
        higher.addActionListener(listener(GuessModel.Answer.HIGHER, model));
        correct.addActionListener(listener(GuessModel.Answer.CORRECT, model));
    }

    private ActionListener listener(final GuessModel.Answer answer, final GuessModel model) {
        requireNonNull(answer);
        requireNonNull(model);
        return e -> {
            final int guess = ((Number) this.guess.getValue()).intValue();
            model.answer(guess, answer, OptionPaneInconsistentAnswerCallback.INSTANCE);
        };
    }

}
