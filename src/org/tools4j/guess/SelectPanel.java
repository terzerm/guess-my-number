package org.tools4j.guess;

import javax.swing.*;
import java.awt.*;
import java.text.NumberFormat;

public class SelectPanel extends JPanel {

    public final JFormattedTextField min = new JFormattedTextField(NumberFormat.getIntegerInstance());
    public final JFormattedTextField max = new JFormattedTextField(NumberFormat.getIntegerInstance());
    public final JComboBox<PlayMode> playMode = new JComboBox<>(playModes());
    public final JComboBox<GuessStrategy> guessStrategy = new JComboBox<>(guessStrategies());
    public final JButton newGame = new JButton("New game");

    public SelectPanel() {
        super(new FlowLayout(5, 5, FlowLayout.LEFT));
        min.setValue(1);
        min.setColumns(5);
        max.setValue(100);
        max.setColumns(5);
        add(min);
        add(max);
        add(playMode);
        add(guessStrategy);
        add(newGame);
        playMode.addItemListener(e -> {
            guessStrategy.setEnabled(getSelectedMode() == PlayMode.COMPUTER_GUESSES);
        });
        guessStrategy.setEnabled(false);
    }

    public PlayMode getSelectedMode() {
        final int selectedIndex = playMode.getSelectedIndex();
        if (selectedIndex >= 0) {
            return playMode.getItemAt(selectedIndex);
        }
        return null;
    }

    public GuessStrategy getSelectedStrategy() {
        final int selectedIndex = guessStrategy.getSelectedIndex();
        if (selectedIndex >= 0) {
            return guessStrategy.getItemAt(selectedIndex);
        }
        return GuessStrategy.BINARY;
    }

    private static PlayMode[] playModes() {
        final PlayMode[] allModes = PlayMode.values();
        final PlayMode[] modes = new PlayMode[allModes.length + 1];
        System.arraycopy(allModes, 0, modes, 1, allModes.length);
        return modes;
    }

    private static GuessStrategy[] guessStrategies() {
        return GuessStrategy.values();
    }
}
