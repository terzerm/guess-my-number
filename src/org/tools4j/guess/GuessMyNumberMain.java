package org.tools4j.guess;

import javax.swing.*;
import java.awt.*;

public class GuessMyNumberMain {
    public static void main(String... args) throws Exception {
        UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        KeyboardFocusManager.getCurrentKeyboardFocusManager()
                .addPropertyChangeListener("permanentFocusOwner", evt -> {
                    if (evt.getNewValue() instanceof JTextField) {
                        //  invokeLater needed for JFormattedTextField
                        SwingUtilities.invokeLater(() -> {
                            JTextField textField = (JTextField)evt.getNewValue();
                            textField.selectAll();
                        });
                    }});
        final GuessFrame frame = new GuessFrame();
        frame.pack();
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);
    }
}
