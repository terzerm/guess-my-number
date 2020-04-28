package org.tools4j.guess;

import javax.swing.*;

public enum OptionPaneInconsistentAnswerCallback implements GuessModel.InconsistentAnswerCallback {
    INSTANCE;

    @Override
    public void guessNotPossible(final int guess, final String violation) {
        JOptionPane.showMessageDialog(null, violation);
    }

    @Override
    public void answerNotPossible(final int guess, final GuessModel.Answer answer, final String violation) {
        JOptionPane.showMessageDialog(null, violation);
    }
}
