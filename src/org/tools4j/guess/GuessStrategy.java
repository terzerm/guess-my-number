package org.tools4j.guess;

import java.util.Random;

public enum GuessStrategy {
    BINARY {
        @Override
        public int guess(final GuessModel model) {
            return (model.highValue() + model.lowValue()) / 2;
        }

        @Override
        public String toString() {
            return "Binary";
        }
    },
    GAUSS {
        @Override
        public int guess(final GuessModel model) {
            final int high = model.highValue();
            final int low = model.lowValue();
            final int mid = (low + high) / 2;
            if (high - low < 3) {
                return mid;
            }
            final double rnd = RANDOM.nextGaussian();
            final int delta = Math.max(high - mid, mid - low);
            final int guess = mid + (int) (rnd * delta / 3.0);//end points at 3 std dev
            return Math.max(low + 1, Math.min(high - 1, guess));
        }

        @Override
        public String toString() {
            return "Gauss";
        }
    },
    UNIFORM {
        @Override
        public int guess(final GuessModel model) {
            final int high = model.highValue();
            final int low = model.lowValue();
            if (high - low < 3) {
                return (low + high) / 2;
            }
            return low + 1 + RANDOM.nextInt(high - low - 1);
        }

        @Override
        public String toString() {
            return "Uniform";
        }
    };

    private static final Random RANDOM = new Random();

    abstract public int guess(final GuessModel model);
}
