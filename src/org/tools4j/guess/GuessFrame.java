package org.tools4j.guess;

import javax.swing.*;
import java.awt.*;
import java.util.Random;

public class GuessFrame extends JFrame {

    public static final Color GREEN = new Color(76, 153, 0);
    private static final String TITLE = "Guess my Number";
    private static final String INSTRUCTION = "Select play mode and press 'New Game' to start!";

    private final NumberPanel numberPanel = new NumberPanel();
    private final JPanel playPanel = new JPanel(new FlowLayout(0, 0, FlowLayout.CENTER));

    public GuessFrame() {
        super(TITLE);
        setPreferredSize(new Dimension(650, 450));
        initLayout();
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
    }

    private void initLayout() {
        getContentPane().add(mainPanel());
    }

    private JComponent mainPanel() {
        final JPanel panel = new JPanel(new BorderLayout(5, 5));
        final SelectPanel selectPanel = new SelectPanel();
        panel.add(selectPanel, BorderLayout.NORTH);
        panel.add(numberPanel, BorderLayout.CENTER);
        panel.add(playPanel, BorderLayout.SOUTH);
        selectPanel.playMode.addItemListener(e -> playModeChanged(selectPanel));
        selectPanel.newGame.addActionListener(e -> playModeChanged(selectPanel));
        playModeChanged(selectPanel);
        return panel;
    }

    private void playModeChanged(final SelectPanel selectPanel) {
        final int min = ((Number) selectPanel.min.getValue()).intValue();
        final int max = ((Number) selectPanel.max.getValue()).intValue();
        final GuessModel model = new DefaultGuessModel(min, max);
        numberPanel.setModel(model);
        playPanel.removeAll();
        playPanel.add(inputPanel(selectPanel, model));
        playPanel.revalidate();
    }

    private JComponent inputPanel(final SelectPanel selectPanel, final GuessModel model) {
        final PlayMode selectedMode = selectPanel.getSelectedMode();
        if (selectedMode != null) {
            switch (selectedMode) {
                case TWO_PLAYERS:
                    return new GuessAndAnswerInputPanel(model);
                case PLAYER_GUESSES:
                    final int number = model.minValue() + new Random().nextInt(model.maxValue() - model.minValue() + 1);
                    return new GuessInputPanel(model, number);
                case COMPUTER_GUESSES:
                    return new AnswerInputPanel(model, selectPanel.getSelectedStrategy());
            }
        }
        return new JPanel(new FlowLayout(5, 5, FlowLayout.LEFT)) {
            {
                add(new JLabel(INSTRUCTION));
            }
        };
    }

}
