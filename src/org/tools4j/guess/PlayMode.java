package org.tools4j.guess;

import static java.util.Objects.requireNonNull;

public enum PlayMode {
    TWO_PLAYERS("2 players"),
    PLAYER_GUESSES("Computer picks number"),
    COMPUTER_GUESSES("Computer guesses number");

    public final String displayString;

    PlayMode(final String displayString) {
        this.displayString = requireNonNull(displayString);
    }

    @Override
    public String toString() {
        return displayString;
    }
}
