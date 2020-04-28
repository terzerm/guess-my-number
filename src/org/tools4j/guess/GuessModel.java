package org.tools4j.guess;

import java.util.List;
import java.util.OptionalInt;

import static java.util.Objects.requireNonNull;

public interface GuessModel {
    enum Answer {
        CORRECT, LOWER, HIGHER
    }
    int minValue();
    int maxValue();
    int lowValue();
    int highValue();
    OptionalInt correctValue();

    List<GuessWithAnswer> answers();

    boolean answer(int guess, Answer answer, InconsistentAnswerCallback callback);

    void addGuessPlacedListener(GuessPlacedListener listener);

    @FunctionalInterface
    interface GuessPlacedListener {
        void onAnswer(int guess, Answer answer);
    }

    interface InconsistentAnswerCallback {
        void guessNotPossible(int guess, String violation);
        void answerNotPossible(int guess, Answer answer, String violation);
    }

    interface GuessWithAnswer {
        int guess();
        Answer answer();

        static GuessWithAnswer create(final int guess, final Answer answer) {
            requireNonNull(answer);
            return new GuessWithAnswer() {
                @Override
                public int guess() {
                    return guess;
                }

                @Override
                public Answer answer() {
                    return answer;
                }

                @Override
                public String toString() {
                    if (answer == Answer.CORRECT) {
                        return "correct guess=" + guess;
                    }
                    return answer == Answer.HIGHER ?
                            "guess is " + guess + " but number is higher" :
                            "guess is " + guess + " but number is lower";
                }
            };
        }
    }


}
