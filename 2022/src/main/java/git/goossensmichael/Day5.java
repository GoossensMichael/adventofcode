package git.goossensmichael;

import java.util.Arrays;
import java.util.Stack;

public class Day5 {

    public static void main(final String[] args) {
        final int dimension = 9;
        final String[] instructions = INPUT.split("\n\n");

        {
            final String[] levels = instructions[0].split("\n");

            final Stack[] stacks = new Stack[dimension];
            for (int s = 0; s < dimension; s++) {
                stacks[s] = new Stack();
            }

            for (int i = levels.length - 2; i >= 0; i--) {
                for (int j = 0; j < levels[i].length(); j += 4) {
                    final String block = levels[i].substring(j + 1, j + 2);
                    if (!" ".equals(block)) {
                        stacks[j / 4].push(block);
                    }
                }
            }

            for (final String instruction : instructions[1].split("\n")) {
                final int amount = Integer.parseInt(getNumberBetween(instruction, "move ", " from"));

                final int from = Integer.parseInt(getNumberBetween(instruction, "from ", " to"));
                final int to = Integer.parseInt(getNumberBetween(instruction, "to ", null));

                for (int i = 0; i < amount; i++) {
                    stacks[to - 1].push(stacks[from - 1].pop());
                }
            }

            Arrays.stream(stacks).forEach(s -> System.out.print(s.peek()));
        }

        System.out.println();

        {
            final String[] levels = instructions[0].split("\n");

            final Stack[] stacks = new Stack[dimension];
            for (int s = 0; s < dimension; s++) {
                stacks[s] = new Stack();
            }

            for (int i = levels.length - 2; i >= 0; i--) {
                for (int j = 0; j < levels[i].length(); j += 4) {
                    final String block = levels[i].substring(j + 1, j + 2);
                    if (!" ".equals(block)) {
                        stacks[j / 4].push(block);
                    }
                }
            }

            for (final String instruction : instructions[1].split("\n")) {
                final int amount = Integer.parseInt(getNumberBetween(instruction, "move ", " from"));

                final int from = Integer.parseInt(getNumberBetween(instruction, "from ", " to"));
                final int to = Integer.parseInt(getNumberBetween(instruction, "to ", null));

                for (int i = 0; i < amount; i++) {
                    final int itemToMove = stacks[from - 1].size() - amount + i;
                    stacks[to - 1].push(stacks[from - 1].get(itemToMove));
                    stacks[from -1].remove(itemToMove);
                }
            }

            Arrays.stream(stacks).forEach(s -> System.out.print(s.peek()));
        }
    }

    private static String getNumberBetween(final String line, final String before, final String after) {
        final int beforeIndex = line.indexOf(before);
        final int afterIndex;
        if (after != null) {
            afterIndex = line.indexOf(after);
        } else {
            afterIndex = line.length();
        }

        return line.substring(beforeIndex + before.length(), afterIndex);
    }

    private static final String TST_INPUT =
            """
                [D]   \s
            [N] [C]   \s
            [Z] [M] [P]
             1   2   3\s
                        
            move 1 from 2 to 1
            move 3 from 1 to 3
            move 2 from 2 to 1
            move 1 from 1 to 2
            """;

    private static final String INPUT = """
                [G] [R]                 [P]   \s
                [H] [W]     [T] [P]     [H]   \s
                [F] [T] [P] [B] [D]     [N]   \s
            [L] [T] [M] [Q] [L] [C]     [Z]   \s
            [C] [C] [N] [V] [S] [H]     [V] [G]
            [G] [L] [F] [D] [M] [V] [T] [J] [H]
            [M] [D] [J] [F] [F] [N] [C] [S] [F]
            [Q] [R] [V] [J] [N] [R] [H] [G] [Z]
             1   2   3   4   5   6   7   8   9\s
                        
            move 5 from 8 to 2
            move 2 from 4 to 5
            move 3 from 3 to 9
            move 4 from 1 to 8
            move 5 from 9 to 1
            move 3 from 3 to 8
            move 2 from 4 to 7
            move 6 from 6 to 5
            move 5 from 2 to 4
            move 2 from 9 to 1
            move 1 from 7 to 1
            move 4 from 7 to 3
            move 5 from 1 to 5
            move 3 from 1 to 4
            move 8 from 5 to 3
            move 7 from 3 to 2
            move 10 from 4 to 7
            move 1 from 7 to 3
            move 1 from 6 to 2
            move 3 from 8 to 4
            move 4 from 3 to 2
            move 1 from 1 to 2
            move 4 from 3 to 1
            move 2 from 1 to 7
            move 3 from 5 to 1
            move 7 from 8 to 4
            move 9 from 5 to 1
            move 9 from 2 to 7
            move 6 from 4 to 9
            move 14 from 7 to 5
            move 2 from 1 to 4
            move 6 from 7 to 1
            move 4 from 4 to 9
            move 6 from 2 to 8
            move 2 from 4 to 9
            move 2 from 9 to 3
            move 3 from 8 to 3
            move 5 from 9 to 4
            move 1 from 2 to 9
            move 5 from 5 to 3
            move 3 from 2 to 7
            move 1 from 1 to 4
            move 3 from 7 to 5
            move 4 from 9 to 6
            move 2 from 9 to 3
            move 5 from 1 to 6
            move 7 from 6 to 5
            move 1 from 2 to 3
            move 10 from 1 to 5
            move 1 from 8 to 3
            move 14 from 3 to 7
            move 1 from 8 to 4
            move 2 from 6 to 1
            move 28 from 5 to 9
            move 1 from 2 to 1
            move 5 from 4 to 6
            move 2 from 4 to 3
            move 13 from 7 to 8
            move 1 from 3 to 5
            move 1 from 5 to 2
            move 1 from 3 to 6
            move 1 from 5 to 6
            move 22 from 9 to 1
            move 1 from 2 to 7
            move 3 from 9 to 5
            move 2 from 7 to 5
            move 18 from 1 to 4
            move 7 from 8 to 3
            move 4 from 6 to 8
            move 2 from 5 to 8
            move 5 from 3 to 9
            move 2 from 5 to 1
            move 3 from 6 to 8
            move 1 from 5 to 9
            move 2 from 3 to 6
            move 10 from 1 to 5
            move 15 from 8 to 6
            move 10 from 6 to 8
            move 1 from 9 to 4
            move 1 from 1 to 3
            move 4 from 4 to 3
            move 5 from 3 to 5
            move 9 from 5 to 6
            move 13 from 6 to 5
            move 8 from 5 to 7
            move 8 from 9 to 6
            move 2 from 6 to 4
            move 2 from 6 to 2
            move 3 from 7 to 4
            move 2 from 2 to 8
            move 1 from 5 to 4
            move 3 from 7 to 9
            move 1 from 5 to 9
            move 5 from 6 to 9
            move 10 from 8 to 3
            move 3 from 8 to 1
            move 5 from 9 to 2
            move 1 from 6 to 4
            move 4 from 5 to 6
            move 7 from 3 to 7
            move 5 from 6 to 5
            move 19 from 4 to 8
            move 15 from 8 to 3
            move 2 from 1 to 5
            move 7 from 5 to 9
            move 2 from 7 to 2
            move 3 from 3 to 8
            move 5 from 5 to 8
            move 10 from 9 to 3
            move 1 from 4 to 2
            move 10 from 8 to 3
            move 29 from 3 to 2
            move 2 from 3 to 4
            move 1 from 1 to 5
            move 2 from 8 to 4
            move 1 from 9 to 1
            move 1 from 3 to 9
            move 1 from 1 to 9
            move 2 from 3 to 4
            move 33 from 2 to 1
            move 2 from 2 to 4
            move 1 from 3 to 1
            move 22 from 1 to 2
            move 6 from 4 to 9
            move 4 from 7 to 1
            move 16 from 1 to 4
            move 3 from 7 to 6
            move 2 from 9 to 4
            move 1 from 5 to 2
            move 9 from 4 to 2
            move 1 from 6 to 5
            move 7 from 4 to 2
            move 6 from 9 to 8
            move 4 from 4 to 9
            move 4 from 8 to 3
            move 2 from 4 to 3
            move 2 from 2 to 5
            move 2 from 5 to 2
            move 1 from 5 to 6
            move 3 from 9 to 5
            move 1 from 6 to 8
            move 2 from 6 to 5
            move 1 from 3 to 2
            move 1 from 8 to 4
            move 2 from 8 to 2
            move 5 from 5 to 6
            move 44 from 2 to 8
            move 1 from 4 to 8
            move 3 from 6 to 8
            move 2 from 6 to 2
            move 37 from 8 to 3
            move 1 from 9 to 4
            move 1 from 2 to 5
            move 5 from 8 to 6
            move 1 from 4 to 6
            move 1 from 2 to 4
            move 16 from 3 to 2
            move 1 from 4 to 5
            move 1 from 8 to 3
            move 4 from 8 to 2
            move 1 from 8 to 7
            move 2 from 5 to 8
            move 15 from 2 to 4
            move 5 from 6 to 3
            move 1 from 7 to 4
            move 1 from 8 to 9
            move 1 from 6 to 7
            move 1 from 8 to 3
            move 2 from 2 to 8
            move 1 from 9 to 3
            move 2 from 8 to 4
            move 1 from 4 to 6
            move 33 from 3 to 7
            move 1 from 6 to 3
            move 1 from 4 to 8
            move 1 from 8 to 9
            move 4 from 4 to 3
            move 9 from 4 to 7
            move 3 from 4 to 8
            move 11 from 7 to 2
            move 14 from 7 to 6
            move 1 from 8 to 3
            move 1 from 9 to 5
            move 1 from 5 to 1
            move 8 from 2 to 9
            move 1 from 8 to 7
            move 6 from 3 to 6
            move 18 from 6 to 4
            move 1 from 2 to 7
            move 1 from 3 to 6
            move 14 from 4 to 2
            move 4 from 4 to 3
            move 3 from 6 to 3
            move 19 from 2 to 6
            move 16 from 6 to 8
            move 1 from 1 to 8
            move 16 from 8 to 7
            move 3 from 9 to 4
            move 3 from 6 to 2
            move 3 from 4 to 7
            move 4 from 3 to 2
            move 2 from 2 to 4
            move 4 from 9 to 8
            move 5 from 2 to 8
            move 29 from 7 to 5
            move 6 from 8 to 2
            move 2 from 3 to 4
            move 2 from 2 to 6
            move 1 from 3 to 5
            move 4 from 2 to 6
            move 8 from 7 to 5
            move 1 from 7 to 5
            move 2 from 8 to 6
            move 1 from 8 to 7
            move 6 from 6 to 1
            move 2 from 7 to 6
            move 1 from 9 to 7
            move 3 from 1 to 7
            move 3 from 6 to 1
            move 1 from 7 to 6
            move 3 from 1 to 6
            move 1 from 1 to 5
            move 4 from 6 to 3
            move 2 from 4 to 2
            move 38 from 5 to 6
            move 3 from 3 to 8
            move 4 from 8 to 6
            move 22 from 6 to 8
            move 1 from 7 to 8
            move 2 from 6 to 2
            move 2 from 5 to 2
            move 2 from 2 to 1
            move 2 from 4 to 6
            move 2 from 2 to 1
            move 1 from 1 to 9
            move 2 from 8 to 5
            move 2 from 2 to 8
            move 2 from 5 to 2
            move 2 from 7 to 2
            move 1 from 3 to 1
            move 4 from 1 to 8
            move 1 from 9 to 5
            move 1 from 1 to 7
            move 1 from 2 to 8
            move 29 from 8 to 3
            move 15 from 3 to 2
            move 12 from 2 to 5
            move 1 from 1 to 6
            move 3 from 2 to 1
            move 6 from 3 to 8
            move 2 from 3 to 9
            move 1 from 6 to 7
            move 12 from 5 to 8
            move 2 from 7 to 1
            move 2 from 1 to 4
            move 2 from 4 to 2
            move 1 from 5 to 8
            move 1 from 3 to 6
            move 2 from 3 to 4
            move 3 from 1 to 4
            move 5 from 8 to 9
            move 4 from 4 to 2
            move 5 from 9 to 6
            move 26 from 6 to 8
            move 7 from 2 to 8
            move 3 from 3 to 1
            move 1 from 6 to 4
            move 14 from 8 to 6
            move 2 from 1 to 2
            move 1 from 1 to 3
            move 18 from 8 to 5
            move 15 from 8 to 2
            move 5 from 6 to 8
            move 4 from 5 to 8
            move 7 from 2 to 5
            move 2 from 9 to 6
            move 1 from 2 to 1
            move 7 from 2 to 3
            move 7 from 8 to 1
            move 2 from 6 to 3
            move 1 from 4 to 6
            move 2 from 8 to 6
            move 10 from 3 to 9
            move 18 from 5 to 8
            move 1 from 4 to 6
            move 2 from 1 to 9
            move 12 from 6 to 9
            move 1 from 6 to 9
            move 9 from 8 to 4
            move 6 from 1 to 2
            move 3 from 8 to 9
            move 14 from 9 to 8
            move 5 from 4 to 9
            move 2 from 4 to 5
            move 16 from 8 to 5
            move 12 from 5 to 4
            move 7 from 5 to 1
            move 1 from 1 to 8
            move 1 from 5 to 8
            move 1 from 4 to 9
            move 8 from 2 to 7
            move 12 from 4 to 3
            move 2 from 2 to 5
            move 1 from 3 to 2
            move 3 from 5 to 4
            move 1 from 4 to 8
            move 3 from 4 to 9
            move 18 from 9 to 8
            move 8 from 3 to 1
            move 5 from 8 to 1
            move 1 from 2 to 5
            move 3 from 7 to 1
            move 3 from 7 to 5
            move 1 from 8 to 9
            move 5 from 9 to 7
            move 2 from 3 to 6
            move 16 from 1 to 4
            move 14 from 8 to 6
            move 2 from 5 to 6
            move 4 from 1 to 6
            move 3 from 4 to 9
            move 15 from 6 to 1
            move 5 from 4 to 3
            move 2 from 8 to 2
            move 6 from 4 to 3
            move 15 from 1 to 5
            move 14 from 5 to 3
            move 5 from 6 to 2
            move 2 from 4 to 7
            move 1 from 1 to 6
            move 2 from 3 to 4
            move 3 from 8 to 1
            move 1 from 5 to 1
            move 5 from 7 to 1
            move 7 from 1 to 3
            move 3 from 6 to 2
            move 4 from 9 to 5
            move 2 from 4 to 3
            move 4 from 7 to 9
            move 8 from 2 to 9
            move 1 from 9 to 1
            move 2 from 2 to 8
            move 11 from 9 to 1
            move 6 from 5 to 1
            move 21 from 3 to 2
            move 1 from 8 to 5
            move 5 from 1 to 7
            move 12 from 1 to 8
            move 1 from 5 to 2
            move 5 from 3 to 2
            move 4 from 7 to 2
            move 1 from 7 to 8
            move 13 from 2 to 5
            move 13 from 2 to 5
            move 2 from 2 to 1
            move 1 from 1 to 9
            move 26 from 5 to 4
            move 3 from 2 to 7
            move 2 from 3 to 9
            move 1 from 1 to 6
            move 5 from 3 to 2
            move 2 from 9 to 6
            move 1 from 1 to 8
            move 3 from 1 to 6
            move 24 from 4 to 9
            move 13 from 9 to 1
            move 2 from 6 to 2
            move 3 from 7 to 5
            move 2 from 9 to 7
            move 8 from 8 to 3
            move 4 from 8 to 5
            move 2 from 7 to 2
            move 8 from 9 to 4
            move 10 from 1 to 2
            move 1 from 9 to 1
            move 1 from 9 to 2
            move 4 from 3 to 2
            move 4 from 1 to 8
            move 3 from 4 to 8
            move 12 from 2 to 3
            move 3 from 4 to 6
            move 5 from 3 to 2
            move 9 from 3 to 9
            move 4 from 2 to 9
            move 1 from 3 to 7
            move 6 from 8 to 2
            move 4 from 6 to 8
            move 1 from 3 to 8
            move 6 from 9 to 1
            move 2 from 1 to 8
            move 5 from 5 to 8
            move 3 from 6 to 8
            move 1 from 5 to 1
            move 7 from 8 to 2
            move 1 from 1 to 4
            move 1 from 4 to 6
            move 1 from 9 to 4
            move 1 from 5 to 9
            move 1 from 4 to 7
            move 12 from 8 to 2
            move 4 from 4 to 3
            move 2 from 3 to 1
            move 1 from 7 to 2
            move 1 from 6 to 8
            move 1 from 8 to 6
            move 4 from 9 to 3
            move 1 from 9 to 3
            move 13 from 2 to 3
            move 3 from 1 to 7
            move 2 from 9 to 4
            move 2 from 1 to 9
            move 2 from 7 to 2
            move 1 from 4 to 1
            move 2 from 7 to 5
            move 14 from 3 to 8
            move 1 from 8 to 5
            move 2 from 1 to 4
            move 2 from 3 to 4
            move 2 from 3 to 4
            move 10 from 8 to 3
            move 2 from 4 to 8
            move 1 from 9 to 3
            move 3 from 2 to 3
            move 16 from 2 to 4
            move 1 from 8 to 5
            move 11 from 3 to 4
            move 2 from 3 to 7
            move 3 from 5 to 1
            move 1 from 1 to 2
            move 3 from 2 to 5
            move 1 from 1 to 9
            move 2 from 7 to 4
            move 8 from 4 to 3
            move 1 from 6 to 7
            move 1 from 8 to 6
            move 1 from 5 to 1
            move 6 from 3 to 5
            move 2 from 1 to 3
            move 5 from 5 to 7
            move 2 from 7 to 2
            move 2 from 3 to 4
            move 4 from 7 to 1
            move 1 from 6 to 8
            move 1 from 2 to 1
            move 3 from 1 to 6
            move 2 from 9 to 6
            move 8 from 2 to 1
            move 2 from 6 to 2
            move 2 from 6 to 3
            move 6 from 3 to 5
            move 2 from 4 to 6
            move 2 from 2 to 9
            move 1 from 8 to 6
            move 2 from 6 to 5
            move 1 from 9 to 1
            move 11 from 5 to 8
            move 7 from 8 to 6
            move 23 from 4 to 1
            move 1 from 5 to 9
            move 1 from 4 to 6
            move 2 from 4 to 8
            move 1 from 3 to 1
            move 6 from 8 to 3
            move 2 from 9 to 6
            move 3 from 6 to 1
            move 3 from 8 to 7
            move 1 from 3 to 6
            move 18 from 1 to 2
            move 5 from 3 to 8
            move 13 from 2 to 9
            move 5 from 9 to 7
            move 1 from 8 to 6
            move 5 from 2 to 6
            move 2 from 1 to 7
            move 9 from 7 to 8
            move 11 from 8 to 6
            move 2 from 9 to 4
            move 16 from 6 to 1
            move 2 from 4 to 6
            move 1 from 8 to 9
            move 1 from 7 to 6
            move 8 from 1 to 5
            move 3 from 6 to 5
            move 8 from 6 to 4
            move 7 from 9 to 5
            move 1 from 8 to 1
            move 6 from 5 to 1
            move 9 from 5 to 7
            move 4 from 7 to 9
            move 1 from 4 to 8
            move 1 from 8 to 3
            move 1 from 1 to 8
            move 1 from 8 to 7
            move 22 from 1 to 3
            move 1 from 6 to 7
            move 2 from 9 to 4
            move 1 from 9 to 6
            move 1 from 9 to 4
            move 10 from 4 to 3
            move 1 from 1 to 2
            move 2 from 5 to 4
            move 27 from 3 to 8
            move 5 from 3 to 9
            """;

}
