package git.goossensmichael;

public class Day10 {

    public static void main(final String[] args) {
        // Parsing input
        final String[] instructions = INPUT.split("\n");

        {
            int x = 1;
            int cycle = 0;
            int total = 0;

            int i = 0;
            String[] currentOperation = instructions[i].split(" ");
            int cyclesNeeded = cyclesNeeded(currentOperation[0]);
            char[][] screen = new char[6][40];
            while (i < instructions.length) {
                cycle++;

                draw(cycle, x, screen);
                if (cycleCalculationTriggered(cycle)) {
                    total += cycle * x;
                    System.out.println(String.format("Cycle %d has x value %d which gives %d. Total is %d.", cycle, x, cycle * x, total));
                }
                if (cyclesNeeded <= 1) {

                    switch (currentOperation[0]) {
                        case "noop":
                            break;
                        case "addx":
                            x += Integer.parseInt(currentOperation[1]);
                            break;
                        default:
                            throw new IllegalArgumentException("Did not expect instruction: " + instructions[i]);
                    }

                    i++;
                    if (i < instructions.length) {
                        currentOperation = instructions[i].split(" ");
                        cyclesNeeded = cyclesNeeded(currentOperation[0]);
                    }
                } else {
                    cyclesNeeded--;
                }

            }

            System.out.println("Total is " + total);

            for (int a = 0; a < screen.length; a++) {
                for (int b = 0; b < screen[0].length; b++) {
                    System.out.print(screen[a][b]);
                }
                System.out.println();
            }
        }

    }

    private static void draw(final int cycle, final int x, final char[][] screen) {
        final int i = (cycle - 1) / 40;
        final int j = (cycle - 1) % 40;

        if (j == x - 1 ||  j == x + 1 || j == x) {
            screen[i][j] = '█';
        } else {
            screen[i][j] = ' ';
        }
    }

    private static int cyclesNeeded(final String operation) {
        return switch(operation) {
            case "noop" -> 1;
            case "addx" -> 2;
            default -> throw new IllegalArgumentException("Did not expect operation " + operation);
        };
    }

    private static boolean cycleCalculationTriggered(final int cycle) {
        return (cycle - 20) % 40 == 0;
    }

    private static final String TST_INPUT = """
            noop
            addx 3
            addx -5
            """;

    private static final String TST2_INPUT = """
            addx 15
            addx -11
            addx 6
            addx -3
            addx 5
            addx -1
            addx -8
            addx 13
            addx 4
            noop
            addx -1
            addx 5
            addx -1
            addx 5
            addx -1
            addx 5
            addx -1
            addx 5
            addx -1
            addx -35
            addx 1
            addx 24
            addx -19
            addx 1
            addx 16
            addx -11
            noop
            noop
            addx 21
            addx -15
            noop
            noop
            addx -3
            addx 9
            addx 1
            addx -3
            addx 8
            addx 1
            addx 5
            noop
            noop
            noop
            noop
            noop
            addx -36
            noop
            addx 1
            addx 7
            noop
            noop
            noop
            addx 2
            addx 6
            noop
            noop
            noop
            noop
            noop
            addx 1
            noop
            noop
            addx 7
            addx 1
            noop
            addx -13
            addx 13
            addx 7
            noop
            addx 1
            addx -33
            noop
            noop
            noop
            addx 2
            noop
            noop
            noop
            addx 8
            noop
            addx -1
            addx 2
            addx 1
            noop
            addx 17
            addx -9
            addx 1
            addx 1
            addx -3
            addx 11
            noop
            noop
            addx 1
            noop
            addx 1
            noop
            noop
            addx -13
            addx -19
            addx 1
            addx 3
            addx 26
            addx -30
            addx 12
            addx -1
            addx 3
            addx 1
            noop
            noop
            noop
            addx -9
            addx 18
            addx 1
            addx 2
            noop
            noop
            addx 9
            noop
            noop
            noop
            addx -1
            addx 2
            addx -37
            addx 1
            addx 3
            noop
            addx 15
            addx -21
            addx 22
            addx -6
            addx 1
            noop
            addx 2
            addx 1
            noop
            addx -10
            noop
            noop
            addx 20
            addx 1
            addx 2
            addx 2
            addx -6
            addx -11
            noop
            noop
            noop
            """;

    private static final String INPUT = """
            noop
            noop
            noop
            addx 4
            addx 1
            addx 5
            addx 1
            addx 5
            noop
            addx -1
            addx -6
            addx 11
            noop
            noop
            noop
            noop
            addx 6
            addx 5
            noop
            noop
            noop
            addx -30
            addx 34
            addx 2
            addx -39
            noop
            addx 5
            addx 2
            addx 19
            addx -18
            addx 2
            addx 5
            addx 2
            addx 3
            noop
            addx 2
            addx 3
            noop
            addx 2
            addx 3
            noop
            addx 2
            addx 3
            noop
            addx 2
            addx -15
            addx -22
            noop
            noop
            addx 5
            addx 2
            noop
            noop
            addx 14
            addx -11
            addx 5
            addx 2
            addx 3
            noop
            addx 2
            addx -16
            addx 17
            addx 2
            addx 5
            addx 2
            addx -6
            addx -25
            addx 35
            addx 1
            addx -36
            addx 1
            addx 22
            addx -19
            addx 5
            addx 2
            noop
            noop
            addx 5
            noop
            noop
            noop
            addx 1
            addx 4
            noop
            noop
            noop
            addx 5
            noop
            addx 1
            addx 2
            addx 3
            addx 4
            addx -34
            addx 21
            addx -24
            addx 2
            addx 5
            addx 7
            addx -6
            addx 2
            addx 30
            addx -23
            addx 10
            addx -9
            addx 2
            addx 2
            addx 5
            addx -12
            addx 13
            addx 2
            addx 5
            addx 2
            addx -12
            addx -24
            addx -1
            noop
            addx 3
            addx 3
            addx 1
            addx 5
            addx 21
            addx -16
            noop
            addx 19
            addx -18
            addx 2
            addx 5
            addx 2
            addx 3
            noop
            addx 3
            addx -1
            addx 1
            addx 2
            addx -18
            addx 1
            noop
            """;
}
