package org.tools4j.guess;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionListener;
import java.text.NumberFormat;

import static java.util.Objects.requireNonNull;

public class AnswerInputPanel extends JPanel {
    private static final String TEXT = "Computer guess is: ";

    public final JLabel label = new JLabel();
    public final JFormattedTextField guess = new JFormattedTextField(NumberFormat.getIntegerInstance());
    public final JButton lower = new JButton("Lower");
    public final JButton higher = new JButton("Higher");
    public final JButton correct = new JButton("Correct");

    public AnswerInputPanel(final GuessModel model, final GuessStrategy guessStrategy) {
        super(new FlowLayout(5, 5, FlowLayout.LEFT));
        label.setText(TEXT);
        guess.setColumns(5);
        guess.setEditable(false);
        guess.setValue(guessStrategy.guess(model));
        add(label);
        add(guess);
        add(lower);
        add(higher);
        add(correct);
        lower.addActionListener(listener(GuessModel.Answer.LOWER, model, guessStrategy));
        higher.addActionListener(listener(GuessModel.Answer.HIGHER, model, guessStrategy));
        correct.addActionListener(listener(GuessModel.Answer.CORRECT, model, guessStrategy));
    }

    private ActionListener listener(final GuessModel.Answer answer,
                                    final GuessModel model,
                                    final GuessStrategy guessStrategy) {
        requireNonNull(answer);
        requireNonNull(model);
        requireNonNull(guessStrategy);
        return e -> {
            final int guess = ((Number) this.guess.getValue()).intValue();
            model.answer(guess, answer, OptionPaneInconsistentAnswerCallback.INSTANCE);
            final int nextGuess = guessStrategy.guess(model);
            this.guess.setValue(nextGuess);
        };
    }

}
