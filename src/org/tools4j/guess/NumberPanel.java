package org.tools4j.guess;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionAdapter;
import java.util.Map;
import java.util.OptionalInt;
import java.util.stream.Collectors;

public class NumberPanel extends JPanel {

    private final Graph graph = new Graph();
    private final JPanel infoPanel = new JPanel(new FlowLayout(5, 5, FlowLayout.LEFT));

    public NumberPanel() {
        super(new BorderLayout(0, 0));
        add(graph, BorderLayout.CENTER);
        add(infoPanel, BorderLayout.NORTH);
    }

    public void setModel(final GuessModel model) {
        graph.setModel(model);
        model.addGuessPlacedListener((guess, answer) -> updateInfoPanel(model));
        updateInfoPanel(model);
    }

    private void updateInfoPanel(final GuessModel model) {
        final OptionalInt correct = model.correctValue();
        infoPanel.removeAll();
        final JLabel label = new JLabel();
        if (correct.isPresent()) {
            label.setText("Correct answer with " + model.answers().size() + " trials!");
            label.setForeground(GuessFrame.GREEN);
        } else {
            label.setText(model.answers().size() + " answers given so far");
        }
        infoPanel.add(label);
        infoPanel.revalidate();
        infoPanel.getParent().repaint();
    }

    private static class Graph extends JComponent {

        private GuessModel model;

        public Graph() {
            setOpaque(true);
            setBorder(BorderFactory.createEtchedBorder());
            addMouseMotionListener(new MouseMotionAdapter() {
                @Override
                public void mouseMoved(final MouseEvent e) {
                    if (model == null) {
                        return;
                    }
                    final int minValue = model.minValue();
                    final int maxValue = model.maxValue();
                    final int x = e.getX();
                    final int y = e.getY();
                    final Insets in = getInsets();
                    final int gp = getGap();
                    final int pw = getPointWidth();
                    final int ph = getPointHeight();
                    final int nx = getVerticalCount();
                    final int ny = getVerticalCount();

                    //int xoff = in.left + (i % nx) * (gp + pw);
                    //int yoff = in.top + (i / nx) * (gp + ph);
                    final int xi = (x - in.left) / (gp + pw);
                    final int yi = (y - in.top) / (gp + ph);
                    final int i = yi * ny + xi;
                    final int value = minValue + i;
                    if (0 <= xi && xi < nx && 0 <= yi && yi < ny &&
                            minValue <= value && value <= maxValue) {
                        setToolTipText(String.valueOf(value));
                    } else {
                        setToolTipText("");
                    }
                }
            });
        }

        public int getPointWidth() {
            final int n = getHorizontalCount();
            final Insets i = getInsets();
            final int w = getWidth() - i.left - i.right;
            return (w - (n-1) * getGap()) / n;
        }

        public int getPointHeight() {
            final int n = getVerticalCount();
            final Insets i = getInsets();
            final int h = getHeight() - i.top - i.bottom;
            return (h - (n-1) * getGap()) / n;
        }

        public int getGap() {
            return 3;
        }

        public int getCount() {
            if (model != null) {
                final int min = model.minValue();
                final int max = model.maxValue();
                return max - min + 1;
            }
            return 0;
        }

        public int getHorizontalCount() {
            return (int)(Math.ceil(Math.sqrt(getCount())));
        }

        public int getVerticalCount() {
            return (int)(Math.ceil(getCount() / (double)getHorizontalCount()));
        }

        public int getHorizontalSize() {
            final int h = getHorizontalCount();
            return h * getPointWidth() + (h-1) * getGap();
        }

        public int getVerticalSize() {
            final int h = getVerticalCount();
            return h * getPointHeight() + (h-1) * getGap();
        }

        public void setModel(final GuessModel model) {
            this.model = model;
            setPreferredSize(new Dimension(getHorizontalSize(), getVerticalSize()));
            model.addGuessPlacedListener((guess, answer) -> repaint());
        }

        @Override
        protected void paintComponent(final Graphics g) {
            if (model == null) {
                super.paintComponent(g);
                return;
            }
            final Map<Integer, GuessModel.Answer> answers = model.answers().stream().collect(
                    Collectors.toMap(GuessModel.GuessWithAnswer::guess, GuessModel.GuessWithAnswer::answer)
            );
            final Insets in = getInsets();
            final int low = model.lowValue();
            final int high = model.highValue();
            final int gp = getGap();
            final int w = getHorizontalSize();
            final int h = getVerticalSize();
            final int pw = getPointWidth();
            final int ph = getPointHeight();
            final int nx = getHorizontalCount();
            final int ny = getVerticalCount();
            final int n = getCount();
            g.setColor(Color.WHITE);
            g.fillRect(in.left, in.top, w, h);
            for (int i = 0; i < n; i++) {
                final int value = model.minValue() + i;
                int xoff = in.left + (i % nx) * (gp + pw);
                int yoff = in.top + (i / nx) * (gp + ph);
                g.setColor(colorFor(low, high, value, answers.get(value)));
                g.fillRect(xoff, yoff, pw, ph);
            }
            paintBorder(g);
        }

        private Color colorFor(final int low, final int high, final int value,
                               final GuessModel.Answer answer) {
            if (answer == null) {
                return value <= low || value >= high ? Color.LIGHT_GRAY : Color.DARK_GRAY;
            }
            switch (answer) {
                case CORRECT:
                    return GuessFrame.GREEN;
                case LOWER:
                    return Color.RED;
                case HIGHER:
                    return Color.BLUE;
                default:
                    throw new IllegalArgumentException("Invalid answer: " + answer);
            }
        }
    }
}
