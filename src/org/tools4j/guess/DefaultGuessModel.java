package org.tools4j.guess;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.OptionalInt;

public class DefaultGuessModel implements GuessModel {

    private final int minValue;
    private final int maxValue;
    private final List<GuessWithAnswer> answers = new ArrayList<>();

    private final List<GuessPlacedListener> listeners = new ArrayList<>();

    public DefaultGuessModel(final int minValue, final int maxValue) {
        if (minValue > maxValue) {
            throw new IllegalArgumentException("minValue is larger than max value: " + minValue + " > " + maxValue);
        }
        this.minValue = minValue;
        this.maxValue = maxValue;
    }
    @Override
    public int minValue() {
        return minValue;
    }

    @Override
    public int maxValue() {
        return maxValue;
    }

    @Override
    public OptionalInt correctValue() {
        return answers.stream().filter(gwa -> gwa.answer() == Answer.CORRECT)
                .mapToInt(GuessWithAnswer::guess).findFirst();
    }

    @Override
    public int lowValue() {
        final OptionalInt lowest = answers.stream().filter(gwa -> gwa.answer() != Answer.LOWER)
                .mapToInt(GuessWithAnswer::guess).max();
        return lowest.isPresent() ? lowest.getAsInt() : minValue() - 1;
    }

    @Override
    public int highValue() {
        final OptionalInt highest = answers.stream().filter(gwa -> gwa.answer() != Answer.HIGHER)
                .mapToInt(GuessWithAnswer::guess).min();
        return highest.isPresent() ? highest.getAsInt() : maxValue() + 1;
    }

    @Override
    public List<GuessWithAnswer> answers() {
        return Collections.unmodifiableList(answers);
    }


    @Override
    public boolean answer(final int guess, final Answer answer, InconsistentAnswerCallback callback) {
        if (inconsistent(guess, answer, callback)) {
            return false;
        }
        if (answer == Answer.CORRECT && correctValue().isPresent()) {
            return false;
        }
        final GuessWithAnswer gwa = GuessWithAnswer.create(guess, answer);
        answers.add(gwa);
        listeners.forEach(listener -> listener.onAnswer(gwa.guess(), gwa.answer()));
        return true;
    }

    @Override
    public void addGuessPlacedListener(final GuessPlacedListener listener) {
        listeners.add(listener);
    }

    private boolean inconsistent(final int guess, final Answer answer, final InconsistentAnswerCallback callback) {
        final OptionalInt correct = correctValue();
        if (correctValue().isPresent()) {
            if (guess != correct.getAsInt()) {
                callback.guessNotPossible(guess, guess + " is not possible, number was already correctly guessed as " + correct.getAsInt());
                return true;
            }
            if (answer != Answer.CORRECT) {
                callback.answerNotPossible(guess, answer, guess + " to be " + answer + " is not possible, number was answered to be correct");
                return true;
            }
            return false;
        }
        final int low = lowValue();
        if (guess <= low) {
            final String violation;
            if (guess < minValue()) {
                violation = guess + " is not possible, it is lower than the minimum value " + minValue();
            } else {
                final GuessWithAnswer lowest = answers.stream().filter(gwa -> gwa.answer() == Answer.HIGHER)
                        .max(Comparator.comparing(GuessWithAnswer::guess)).get();
                violation = guess + " is not possible, it is lower or equal to answer '" + lowest + "'";
            }
            callback.guessNotPossible(guess, violation);
            return true;
        }
        final int high  = highValue();
        if (guess >= high) {
            final String violation;
            if (guess > maxValue()) {
                violation = guess + " is not possible, it is higher than the maximum value " + maxValue();
                callback.guessNotPossible(guess, violation);
            } else {
                final GuessWithAnswer highest = answers.stream().filter(gwa -> gwa.answer() == Answer.LOWER)
                        .min(Comparator.comparing(GuessWithAnswer::guess)).get();
                violation = guess + " is not possible, it is higher or equal to answer '" + highest + "'";
            }
            callback.guessNotPossible(guess, violation);
            return true;
        }
        //low < guess < high
        if (guess - 1 == low && answer == Answer.LOWER) {
            final String violation;
            if (guess == minValue()) {
                violation = guess + " to be " + answer + " is not possible as this is the minimum value";
            } else {
                final GuessWithAnswer lowest = answers.stream().filter(gwa -> gwa.answer() == Answer.HIGHER)
                        .max(Comparator.comparing(GuessWithAnswer::guess)).get();
                violation = guess + " to be " + answer + " is not possible due to answer '" + lowest + "'";
            }
            callback.answerNotPossible(guess, answer, violation);
            return true;
        }
        if (guess + 1 == high && answer == Answer.HIGHER) {
            final String violation;
            if (guess == maxValue()) {
                violation = guess + " to be " + answer + " is not possible as this is the maximum value";
            } else {
                final GuessWithAnswer highest = answers.stream().filter(gwa -> gwa.answer() == Answer.LOWER)
                        .min(Comparator.comparing(GuessWithAnswer::guess)).get();
                violation = guess + " to be " + answer + " is not possible due to answer '" + highest + "'";
            }
            callback.answerNotPossible(guess, answer, violation);
            return true;
        }
        if (guess - 1 == low && guess + 1 == high && answer != Answer.CORRECT) {
            final GuessWithAnswer lowest = answers.stream().filter(gwa -> gwa.answer() == Answer.HIGHER)
                    .max(Comparator.comparing(GuessWithAnswer::guess)).get();
            final GuessWithAnswer highest = answers.stream().filter(gwa -> gwa.answer() == Answer.LOWER)
                    .min(Comparator.comparing(GuessWithAnswer::guess)).get();
            callback.answerNotPossible(guess, answer, guess + " has to be correct due to answers '" +
                    lowest + "'" + " and '" + highest + "'");
            return true;
        }
        return false;
    }
}
