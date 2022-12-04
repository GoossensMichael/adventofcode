package git.goossensmichael;
import java.util.Arrays;

public class Day2 {

    public static void main(String[] args) {
        {
            final int sum = Arrays.stream(INPUT.split("\n"))
                    .map(Day2::toRound)
                    .mapToInt(Round::calculate)
                    .sum();

            System.out.println("Part 1: " + sum);
        }

        {
            final int sum = Arrays.stream(INPUT.split("\n"))
                    .map(Day2::toRound2)
                    .mapToInt(Round::calculate)
                    .sum();

            System.out.println("Part 2: " + sum);
        }
    }

    private static Round toRound(final String round) {
        final String[] states = round.split(" ");

        return new Round(State.toState(states[0]), State.toState(states[1]));
    }

    private static Round toRound2(final String round) {
        final String[] states = round.split(" ");

        final State left = State.toState(states[0]);
        final State right = stateToReachDesiredGoal(left, states[1]);
        return new Round(left, right);
    }

    private static State stateToReachDesiredGoal(final State opponent, final String desiredState) {
        return switch (desiredState) {
            case "X" -> State.valueOf(opponent.beats);
            case "Y" -> opponent;
            case "Z" -> State.valueOf(State.valueOf(opponent.beats).beats);
            default -> throw new IllegalArgumentException("Unknown desired state " + desiredState);
        };
    }

    private enum State {
        ROCK(1, "SISSORS"),
        PAPER(2, "ROCK"),
        SISSORS(3, "PAPER");

        private final int value;
        private final String beats;

        State(final int value, final String beats) {
            this.value = value;
            this.beats = beats;
        }

        public int getValue() {
            return value;
        }

        public int outcome(final State other) {
            if (other == this) {
                return 3;
            } else if (other == State.valueOf(beats)) {
                return 6;
            } else {
                return 0;
            }
        }

        public static State toState(final String state) {
            return switch (state) {
                case "A", "X" -> ROCK;
                case "B", "Y" -> PAPER;
                case "C", "Z" -> SISSORS;
                default -> throw new IllegalArgumentException("Not expected state: " + state);
            };
        }

    }

    private record Round(State left, State right) {

        public int calculate() {
            return right.getValue() + outcome();
        }

        public int outcome() {
            return right.outcome(left);
        }
    }

    ;


    private static final String TST_INPUT = "A Y\n" +
            "B X\n" +
            "C Z";

    private static final String INPUT = "A X\n" +
            "A Z\n" +
            "A Z\n" +
            "A X\n" +
            "A X\n" +
            "B X\n" +
            "C Y\n" +
            "B X\n" +
            "A Z\n" +
            "A X\n" +
            "A X\n" +
            "A X\n" +
            "A X\n" +
            "B X\n" +
            "A X\n" +
            "B X\n" +
            "A Z\n" +
            "A Z\n" +
            "A X\n" +
            "A X\n" +
            "A Z\n" +
            "A Z\n" +
            "A Z\n" +
            "A X\n" +
            "A Z\n" +
            "A X\n" +
            "C Y\n" +
            "C Z\n" +
            "A X\n" +
            "B X\n" +
            "A X\n" +
            "A X\n" +
            "A X\n" +
            "A Z\n" +
            "B X\n" +
            "A X\n" +
            "B X\n" +
            "C Y\n" +
            "A X\n" +
            "A Z\n" +
            "A Y\n" +
            "A X\n" +
            "A Z\n" +
            "A X\n" +
            "A Z\n" +
            "B X\n" +
            "A X\n" +
            "A Z\n" +
            "B X\n" +
            "A X\n" +
            "A Z\n" +
            "A X\n" +
            "A Z\n" +
            "A Z\n" +
            "A X\n" +
            "B X\n" +
            "B X\n" +
            "B X\n" +
            "B Z\n" +
            "A Z\n" +
            "B Z\n" +
            "A X\n" +
            "A X\n" +
            "A Z\n" +
            "A X\n" +
            "B Z\n" +
            "B Y\n" +
            "A Z\n" +
            "B X\n" +
            "A Y\n" +
            "B X\n" +
            "C X\n" +
            "B Z\n" +
            "B X\n" +
            "C Y\n" +
            "A Z\n" +
            "A X\n" +
            "A X\n" +
            "C Y\n" +
            "B X\n" +
            "A X\n" +
            "A Y\n" +
            "A X\n" +
            "A X\n" +
            "A Y\n" +
            "A Z\n" +
            "C Z\n" +
            "A Z\n" +
            "B X\n" +
            "B X\n" +
            "A X\n" +
            "B X\n" +
            "A X\n" +
            "A X\n" +
            "B Y\n" +
            "B X\n" +
            "A Z\n" +
            "B X\n" +
            "B X\n" +
            "A X\n" +
            "B Z\n" +
            "C Z\n" +
            "A Z\n" +
            "C Z\n" +
            "B X\n" +
            "B X\n" +
            "A Z\n" +
            "A X\n" +
            "A X\n" +
            "B X\n" +
            "A Z\n" +
            "B X\n" +
            "A Z\n" +
            "A X\n" +
            "A X\n" +
            "A X\n" +
            "B X\n" +
            "B X\n" +
            "B X\n" +
            "B X\n" +
            "A X\n" +
            "A Y\n" +
            "A X\n" +
            "B X\n" +
            "A Z\n" +
            "A X\n" +
            "A X\n" +
            "A X\n" +
            "C Y\n" +
            "A X\n" +
            "B X\n" +
            "A X\n" +
            "A Z\n" +
            "A X\n" +
            "B X\n" +
            "A Y\n" +
            "B X\n" +
            "C Z\n" +
            "A Y\n" +
            "A X\n" +
            "B Y\n" +
            "A X\n" +
            "B X\n" +
            "A X\n" +
            "A X\n" +
            "A X\n" +
            "B X\n" +
            "A X\n" +
            "A X\n" +
            "A Z\n" +
            "A Z\n" +
            "A X\n" +
            "C Y\n" +
            "B X\n" +
            "B X\n" +
            "A Z\n" +
            "B X\n" +
            "B Y\n" +
            "C Y\n" +
            "A X\n" +
            "A X\n" +
            "B Y\n" +
            "A X\n" +
            "B Y\n" +
            "B X\n" +
            "B X\n" +
            "A X\n" +
            "A Z\n" +
            "B X\n" +
            "B X\n" +
            "A Y\n" +
            "A X\n" +
            "C Y\n" +
            "C Y\n" +
            "C Y\n" +
            "A X\n" +
            "B X\n" +
            "A X\n" +
            "A X\n" +
            "A X\n" +
            "B X\n" +
            "B X\n" +
            "C Y\n" +
            "A X\n" +
            "A Z\n" +
            "A Z\n" +
            "A X\n" +
            "B X\n" +
            "A Z\n" +
            "A Z\n" +
            "A X\n" +
            "B X\n" +
            "A X\n" +
            "A Z\n" +
            "A X\n" +
            "C Z\n" +
            "B X\n" +
            "A X\n" +
            "C Y\n" +
            "C Y\n" +
            "A Z\n" +
            "B X\n" +
            "B Z\n" +
            "C Y\n" +
            "B X\n" +
            "A Y\n" +
            "B Y\n" +
            "A Z\n" +
            "A X\n" +
            "A X\n" +
            "B X\n" +
            "B Y\n" +
            "A Z\n" +
            "A X\n" +
            "B X\n" +
            "A X\n" +
            "C X\n" +
            "C Y\n" +
            "A X\n" +
            "B X\n" +
            "A Z\n" +
            "A X\n" +
            "B X\n" +
            "A X\n" +
            "C Y\n" +
            "C Y\n" +
            "C Y\n" +
            "A X\n" +
            "A X\n" +
            "A Z\n" +
            "A X\n" +
            "C Y\n" +
            "A Z\n" +
            "B Y\n" +
            "A X\n" +
            "C Y\n" +
            "B Z\n" +
            "B X\n" +
            "B X\n" +
            "A Z\n" +
            "B X\n" +
            "A Y\n" +
            "C X\n" +
            "A X\n" +
            "B X\n" +
            "A Z\n" +
            "C Y\n" +
            "A X\n" +
            "A Z\n" +
            "C Z\n" +
            "A X\n" +
            "C Y\n" +
            "B X\n" +
            "C Y\n" +
            "C Z\n" +
            "A Y\n" +
            "A Z\n" +
            "C Y\n" +
            "A X\n" +
            "A X\n" +
            "A X\n" +
            "A Y\n" +
            "B X\n" +
            "B X\n" +
            "C Y\n" +
            "C Y\n" +
            "A X\n" +
            "A Z\n" +
            "C Y\n" +
            "B Z\n" +
            "B X\n" +
            "C Z\n" +
            "B X\n" +
            "B X\n" +
            "C Z\n" +
            "A X\n" +
            "A X\n" +
            "B X\n" +
            "B X\n" +
            "A X\n" +
            "A X\n" +
            "B X\n" +
            "B X\n" +
            "B X\n" +
            "C Y\n" +
            "A X\n" +
            "A X\n" +
            "A X\n" +
            "A X\n" +
            "B X\n" +
            "A X\n" +
            "A X\n" +
            "B X\n" +
            "B Z\n" +
            "A X\n" +
            "B X\n" +
            "A X\n" +
            "C Y\n" +
            "B Y\n" +
            "A X\n" +
            "B X\n" +
            "A Y\n" +
            "B Z\n" +
            "B X\n" +
            "A X\n" +
            "B Z\n" +
            "B X\n" +
            "A Y\n" +
            "A X\n" +
            "B X\n" +
            "A X\n" +
            "A Z\n" +
            "B X\n" +
            "A X\n" +
            "A X\n" +
            "C Z\n" +
            "A X\n" +
            "B X\n" +
            "A X\n" +
            "B Y\n" +
            "A Z\n" +
            "A X\n" +
            "B X\n" +
            "A X\n" +
            "A Z\n" +
            "B X\n" +
            "B X\n" +
            "B Y\n" +
            "B X\n" +
            "A X\n" +
            "C Y\n" +
            "A X\n" +
            "A X\n" +
            "B X\n" +
            "B Y\n" +
            "A X\n" +
            "A X\n" +
            "A X\n" +
            "C Z\n" +
            "C Z\n" +
            "B Y\n" +
            "B X\n" +
            "B Z\n" +
            "A X\n" +
            "A Y\n" +
            "A X\n" +
            "C Y\n" +
            "B X\n" +
            "A X\n" +
            "A X\n" +
            "A X\n" +
            "A Z\n" +
            "C Y\n" +
            "A X\n" +
            "A X\n" +
            "C Y\n" +
            "A X\n" +
            "A X\n" +
            "A X\n" +
            "A Z\n" +
            "A Y\n" +
            "A X\n" +
            "A Y\n" +
            "C Z\n" +
            "A Z\n" +
            "A X\n" +
            "B X\n" +
            "A Z\n" +
            "C Z\n" +
            "C Y\n" +
            "A Z\n" +
            "A Z\n" +
            "C Y\n" +
            "C Z\n" +
            "C Z\n" +
            "A X\n" +
            "B X\n" +
            "B X\n" +
            "B Z\n" +
            "C Y\n" +
            "A X\n" +
            "A X\n" +
            "A X\n" +
            "A Z\n" +
            "A X\n" +
            "B X\n" +
            "B X\n" +
            "C X\n" +
            "A X\n" +
            "A X\n" +
            "B X\n" +
            "A Y\n" +
            "A X\n" +
            "A X\n" +
            "B Y\n" +
            "C Z\n" +
            "A X\n" +
            "A X\n" +
            "A Y\n" +
            "B X\n" +
            "B X\n" +
            "B X\n" +
            "C Z\n" +
            "A Z\n" +
            "A Y\n" +
            "B Y\n" +
            "A X\n" +
            "B X\n" +
            "B X\n" +
            "B X\n" +
            "B Z\n" +
            "A Y\n" +
            "B Y\n" +
            "A Z\n" +
            "A Z\n" +
            "C Z\n" +
            "B X\n" +
            "A X\n" +
            "A Y\n" +
            "A Y\n" +
            "C Y\n" +
            "B Y\n" +
            "A X\n" +
            "A X\n" +
            "A X\n" +
            "A X\n" +
            "B Y\n" +
            "A X\n" +
            "A Y\n" +
            "A X\n" +
            "A Z\n" +
            "B X\n" +
            "B X\n" +
            "A Y\n" +
            "A X\n" +
            "A Y\n" +
            "A Y\n" +
            "A Z\n" +
            "B X\n" +
            "B X\n" +
            "A Z\n" +
            "B X\n" +
            "A X\n" +
            "A Z\n" +
            "A Z\n" +
            "B X\n" +
            "A Z\n" +
            "A X\n" +
            "A Y\n" +
            "A Z\n" +
            "A X\n" +
            "A Z\n" +
            "B Y\n" +
            "A X\n" +
            "C Y\n" +
            "B X\n" +
            "C X\n" +
            "C Y\n" +
            "B Z\n" +
            "C Y\n" +
            "C Y\n" +
            "A X\n" +
            "A X\n" +
            "A Y\n" +
            "B X\n" +
            "C Z\n" +
            "B X\n" +
            "C Y\n" +
            "A X\n" +
            "A Z\n" +
            "C Y\n" +
            "A X\n" +
            "A Z\n" +
            "A Z\n" +
            "B Y\n" +
            "A X\n" +
            "A X\n" +
            "B X\n" +
            "B Y\n" +
            "A X\n" +
            "A X\n" +
            "B X\n" +
            "A X\n" +
            "B X\n" +
            "A X\n" +
            "B X\n" +
            "A X\n" +
            "B Y\n" +
            "A Z\n" +
            "B X\n" +
            "B X\n" +
            "B X\n" +
            "B X\n" +
            "A Y\n" +
            "A X\n" +
            "A Y\n" +
            "A Y\n" +
            "C Z\n" +
            "B Z\n" +
            "A X\n" +
            "B Y\n" +
            "A X\n" +
            "C Z\n" +
            "B X\n" +
            "A X\n" +
            "A X\n" +
            "A X\n" +
            "A X\n" +
            "A X\n" +
            "C Z\n" +
            "C Z\n" +
            "C Z\n" +
            "A X\n" +
            "C Y\n" +
            "A X\n" +
            "C X\n" +
            "C Z\n" +
            "A X\n" +
            "B X\n" +
            "A Z\n" +
            "A Z\n" +
            "A Z\n" +
            "A X\n" +
            "A Z\n" +
            "A Z\n" +
            "B Z\n" +
            "A X\n" +
            "B X\n" +
            "A Z\n" +
            "C Y\n" +
            "A X\n" +
            "A Z\n" +
            "A Z\n" +
            "A Z\n" +
            "B Z\n" +
            "B X\n" +
            "A X\n" +
            "B X\n" +
            "B X\n" +
            "B X\n" +
            "B X\n";
}
