package org.tools4j.guess;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionListener;
import java.text.NumberFormat;

import static java.awt.event.KeyEvent.VK_ENTER;
import static java.util.Objects.requireNonNull;

public class GuessInputPanel extends JPanel {
    private static final String TEXT = "Guess a number between %s and %s: ";
    private static final String PLACE = "Place guess";

    public final JLabel label = new JLabel();
    public final JFormattedTextField guess = new JFormattedTextField(NumberFormat.getIntegerInstance());
    public final JButton place = new JButton(PLACE);
    public final JLabel answer = new JLabel("Guess your number and I'll give you an answer!");

    public GuessInputPanel(final GuessModel model, final int correctNumber) {
        super(new BorderLayout(0, 0));
        final JPanel top = new JPanel(new FlowLayout(5, 5, FlowLayout.LEFT));
        final JPanel buttom = new JPanel(new FlowLayout(5, 5, FlowLayout.CENTER));
        guess.setColumns(5);
        label.setText(String.format(TEXT, model.minValue(), model.maxValue()));
        add(top, BorderLayout.NORTH);
        add(buttom, BorderLayout.SOUTH);
        top.add(label);
        top.add(place);
        top.add(guess);
        buttom.add(answer);
        place.addActionListener(listener(correctNumber, model));
        place.setDefaultCapable(true);
        place.registerKeyboardAction(e -> place.doClick(), KeyStroke.getKeyStroke(VK_ENTER, 0), WHEN_IN_FOCUSED_WINDOW);
        model.addGuessPlacedListener((g, a) -> {
            if (a == GuessModel.Answer.CORRECT) {
                answer.setText(g + " is correct!");
                answer.setForeground(GuessFrame.GREEN);
            } else {
                answer.setText("Number is " + a + " than " + g);
                answer.setForeground(a == GuessModel.Answer.LOWER ? Color.RED : Color.BLUE);
            }
            answer.revalidate();
        });
    }

    private ActionListener listener(final int correctNumber, final GuessModel model) {
        requireNonNull(model);
        return e -> {
            final int guess = ((Number) this.guess.getValue()).intValue();
            final GuessModel.Answer answer =
                    guess == correctNumber ? GuessModel.Answer.CORRECT :
                            guess < correctNumber ? GuessModel.Answer.HIGHER : GuessModel.Answer.LOWER;
            model.answer(guess, answer, OptionPaneInconsistentAnswerCallback.INSTANCE);
            this.guess.requestFocus();
        };
    }

}
