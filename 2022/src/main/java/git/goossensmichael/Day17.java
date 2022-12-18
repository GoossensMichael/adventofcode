package git.goossensmichael;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.OptionalLong;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;

public class Day17 {

    private static final Logger LOGGER = Logger.getLogger(Day17.class.getName());

    private static long part1(final String jetstream) {
        return simulate(jetstream, 2022);
    }

    private static long simulate(final String jetstream, final long cycles) {
        long cycle = 0;

        final Set<Set<Coord>> towerState = new HashSet<>();

        final Set<Coord> tower = new HashSet<>();
        long highestPoint = -1;
        long fallenRocks = 0;
        Rock rock = createRock(fallenRocks, 2, highestPoint + 4);
        while (fallenRocks < cycles) {
            // push stream > or <
            final char direction = jetstream.charAt((int) cycle % jetstream.length());
            cycle++;
            rock.move(direction, tower);
//            visualise(highestPoint, rock, tower);

            // go down v
            if (!rock.move('v', tower)) {
                // Rock did not move
                // * Add rock structure to tower -> Must not contain any of the coordinates yet!
                // * Update highest point
                // * Spawn new rock
                final Collection<Coord> newStructure = rock.getStructure();
                tower.addAll(newStructure);
                cleanTower(newStructure, tower);
                final long rockHighestPoint = rock.getHighestPoint();
                if (highestPoint < rockHighestPoint) {
                    highestPoint = rockHighestPoint;
                }
                rock = createRock(++fallenRocks, 2, highestPoint + 4);
            }
//            visualise(highestPoint, rock, tower);
        }

        return highestPoint + 1;
    }

    private static void cleanTower(final Collection<Coord> newStructure, final Set<Coord> tower) {
        final long sizeBefore = tower.size();

        final Set<Coord> chain = new HashSet<>();
        final List<Coord> toProcess = new ArrayList<>(newStructure);

        while (!toProcess.isEmpty()) {
            final Coord current = toProcess.remove(0);

            if (!chain.contains(current)) {
                chain.add(current);

                for (int i = -1; i <= 1; i++) {
                    for (int j = -1; j <= 1; j++) {
                        if (!(i == 0 && j == 0)) {
                            final Coord possibility = new Coord(current.x() + i, current.y() + j);
                            if (!chain.contains(possibility) && tower.contains(possibility)) {
                                toProcess.add(possibility);
                            }
                        }
                    }
                }
            }
        }

        if (chain.size() >= 7 && chain.stream().anyMatch(c -> c.x() == 0) && chain.stream().anyMatch(c -> c.x() == 6)) {
            final long maxLeft = chain.stream().filter(c -> c.x() == 0).mapToLong(Coord::y).max().orElse(Long.MAX_VALUE);
            final long maxRight = chain.stream().filter(c -> c.x() == 6).mapToLong(Coord::y).max().orElse(Long.MAX_VALUE);
            final long min = Math.min(maxLeft, maxRight);

            tower.stream().filter(c -> c.y() < min).toList().forEach(tower::remove);
//            System.out.println("Tower size reduced: " + (sizeBefore - tower.size()));
        }

    }

    public static void visualise(final int highest, final Rock rock, final Set<Coord> tower) {
        final int yRange = highest + 4 + rock.height();
        final char[][] v = new char[7][yRange];

        for (int i = 0; i < 7; i++) {
            for (int j = 0; j < yRange; j++) {
                v[i][j] = '.';
            }
        }

        rock.getStructure().forEach(c -> v[(int) c.x()][(int) c.y()] = '@');
        tower.forEach(t -> v[(int) t.x()][(int) t.y()] = '#');

        for (int j = yRange - 1; j >= 0; j--) {
            for (int i = 0; i < 7; i++) {
                System.out.print(v[i][j] + " ");
            }
            System.out.println();
        }
        System.out.println();
        System.out.println();
    }

    public static Rock createRock(long cycle, long x, long y) {
        return switch ((int) cycle % 5) {
            case 0 -> new HBeam(x, y);
            case 1 -> new Cross(x, y);
            case 2 -> new LMirrored(x, y);
            case 3 -> new VBeam(x, y);
            case 4 -> new Square(x, y);
            default -> throw new IllegalArgumentException();
        };
    }

    private static long part2(final String jetstream) {
        return simulate(jetstream, 1000000000000L);
    }

    public static void main(final String[] args) {

        // Parsing input
        final var testInput = TST_INPUT;
        final var input = INPUT;

        {
            final var expectedResult = 3068;
            final var part1 = part1(testInput);
            LOGGER.log(Level.INFO, () -> String.format("Test part 1: %d", part1));

            if (expectedResult == part1) {
                final var result = part1(input);
                LOGGER.log(Level.INFO, () -> String.format("Part 1: %d", result));
            }
        }

        {
            final var expectedResult = 1514285714288L;
            final var testResult = part2(testInput);
            LOGGER.log(Level.INFO, () -> String.format("Test part 2: %d", testResult));

            if (expectedResult == testResult) {
                final var result = part2(input);
                LOGGER.log(Level.INFO, () -> String.format("Part 2: %d", result));
            }
        }
    }

    private static abstract sealed class Rock permits HBeam, Cross, LMirrored, VBeam, Square {

        private final int height;
        protected long x;
        protected long y;
        protected final int width;

        public Rock(long x, long y, int width, int height) {
            this.x = x;
            this.y = y;
            this.width = width;
            this.height = height;
        }

        public abstract long getHighestPoint();

        public Collection<Coord> getStructure() {
            return getStructure(x, y);
        }

        public abstract Collection<Coord> getStructure(long x, long y);

        public boolean isAgainstLeftWall() {
            return x == 0;
        }

        public boolean isAgainstRightWall() {
            return x + width == 7;
        }

        public boolean isAtBottom() {
            return y == 0;
        }

        public boolean move(final char direction, final Set<Coord> tower) {
            final boolean moved;
            if (direction == '>' && isAgainstRightWall()) {
                moved = false;
            } else if (direction == '<' && isAgainstLeftWall()) {
                moved = false;
            } else if (direction == 'v' && isAtBottom()) {
                moved = false;
            } else {
                final int[] movement = switch (direction) {
                    case 'v' -> new int[]{0, -1};
                    case '>' -> new int[]{1, 0};
                    case '<' -> new int[]{-1, 0};
                    default -> throw new IllegalArgumentException();
                };

                final Collection<Coord> hypotheticalStructure = getStructure(x + movement[0], y + movement[1]);
                final boolean canMove = tower.stream().noneMatch(hypotheticalStructure::contains);
                if (canMove) {
                    x += movement[0];
                    y += movement[1];
                }

                moved = canMove;
            }

            return moved;
        }

        public int height() {
            return height;
        }
    }

    private static final class HBeam extends Rock {

        public HBeam(final long x, final long y) {
            super(x, y, 4, 1);
        }

        @Override
        public long getHighestPoint() {
            return y;
        }

        @Override
        public Collection<Coord> getStructure(final long x, final long y) {
            return List.of(new Coord(x, y), new Coord(x + 1, y), new Coord(x + 2, y), new Coord(x + 3, y));
        }

    }

    private static final class Cross extends Rock {

        public Cross(final long x, final long y) {
            super(x, y, 3, 3);
        }

        @Override
        public long getHighestPoint() {
            return y + 2;
        }

        @Override
        public Collection<Coord> getStructure(final long x, final long y) {
            return List.of(new Coord(x + 1, y + 2),
                    new Coord(x, y + 1), new Coord(x + 1, y + 1), new Coord(x + 2, y + 1),
                    new Coord(x + 1, y));
        }
    }

    private static final class LMirrored extends Rock {

        public LMirrored(final long x, final long y) {
            super(x, y, 3, 3);
        }

        @Override
        public long getHighestPoint() {
            return y + 2;
        }

        @Override
        public Collection<Coord> getStructure(final long x, final long y) {
            return List.of(new Coord(x + 2, y + 2),
                    new Coord(x + 2, y + 1),
                    new Coord(x, y), new Coord(x + 1, y), new Coord(x + 2, y));
        }
    }

    private static final class VBeam extends Rock {

        public VBeam(final long x, final long y) {
            super(x, y, 1, 4);
        }

        @Override
        public long getHighestPoint() {
            return y + 3;
        }

        @Override
        public Collection<Coord> getStructure(final long x, final long y) {
            return List.of(new Coord(x, y + 3),
                    new Coord(x, y + 2),
                    new Coord(x, y + 1),
                    new Coord(x, y));
        }
    }

    private static final class Square extends Rock {

        public Square(final long x, final long y) {
            super(x, y, 2, 2);
        }

        @Override
        public long getHighestPoint() {
            return y + 1;
        }

        @Override
        public Collection<Coord> getStructure(final long x, final long y) {
            return List.of(new Coord(x, y + 1), new Coord(x + 1, y + 1),
                    new Coord(x, y), new Coord(x + 1, y));
        }
    }

    private record Coord(long x, long y) {
    }

    private static final String TST_INPUT = ">>><<><>><<<>><>>><<<>>><<<><<<>><>><<>>";

    private static final String INPUT = ">>>><<<<>>><<>>><<<<>><<>>>><><><><<<<><<>>>><<<<>>><><<><<>>>><<>>>><><<>>><>>><<<<>>><<<><<<<>>><<<<>>>><<<<>><<<<><<<<>><<>>><>>>><>>><<>>><<<<>>><>><<<><>>><>>>><><<<<>>><<>>><<><<<<>>><<<<>><>><<>>>><<><>>>><<>><<><>>><>>><<<<><<>><<<>><<>>><<<<>>>><>>>><<<>><<<><<<<>>><>><<<>>>><><<<<>><<><<>>>><<>><<><<>>><><<<><<>><><>>><<<<>><<<<>>>><<<<>>>><<<<>>>><<<<>>><>><<>>>><>>><<>>><<>>><<<>>>><<<<>><<<><>><<<><<<<><><<<>><<><>>>><<>>>><<<>>><<<>>>><>>><<><<<><<<<><<<>>><<<<>><<>>><<><>><>>><>>>><<>>><<<>>>><<<<>>>><>><<<<><>><<>>>><<<<>>><<<><<><<>>><<><>>><>>>><>>><<<>>>><<<<>>>><>>><<<>>>><<<<>>>><<<<><>>><>><<<<>>>><<<>>><<<<>><>>>><<<<><<<<>><<<><<<<>><>><<><<<<>>><>>>><<<<>>><>>><<<><<><<>><<<<>><<>><<><<<><<<<>><<<<>><<<>>><<<<>>>><<<<>>>><>><<<>>><<<>><<<<>>>><<>>><>>>><<<>>>><>><<<<>><>>><<<>><<<<>>>><>>><<><<>><<<>><<<<>>>><><<<<>>><>><>>><<<<>>><>><<<<>>>><>><<>><<<>>>><<>>><<<>>>><><>><<>><<<>>>><<<>><>><>>><<<<>>><<<<>>><><<>><<>>>><<<><<>>><>><>><<<>><<<<>><<<<>>>><<>><><<<>>>><<><><>>>><>><<>><<>><<>><<>><>>>><<>>><<<<><><<><<>><<><<>>><<<<>><<>>><<<<>>><<<<>>><>>><>>><><<<<><><>>>><>>>><<<>>><<<<>><<<<>><<<>>>><>>><<<<>><>>>><<>>>><<>>>><>>><<<<><>><<<<>><<<<>>>><>><><<<<>>><>>>><<>>><<>>><<>><<<>><<<<>>>><<<>><<><<>>><<<><<<<>>><<<<><>>>><>><>>>><<<<>>>><<>><<<<>>><<>><<<>><<><>><<>><><<<<>>><<<<>><>><<>><<>><<>><>><<><<>>>><<>>><>>><>>>><<<>><<><<<><>>><<<><><>>><<<<>>>><<<<>>>><<><<<<>><<<<><<><<<>>><<<<>>>><<>>>><<><<>>><<<<>>><<<<><<<><>>><<>><<<<>>><<<><<<<>>>><<>>><<<><<<<>>>><>>>><<<<><<>><<>>><<>>>><<>><<><<<<>>><<>>><<<>>><<<>><<<<>>>><<>>>><<>>>><>>><<<>>><<>><><<<>>>><<<<><<<>>>><><<>>><>><<>><>><>>>><><<<><><<>><>>>><<<<>><<<<>>><<<><<<<>>><><<<<>>>><<><<<<><<<<>>><<<<><<<>><<<>><>>><<>>>><<<>>><>>>><<><>>>><<<>>><>>>><<<>><<>>>><<<<>>><<<<>>>><<<><<<<>>><<<<>>>><>>>><<<<>><<<>>><<>><<<><<<>>>><<>><<<<>>><<<>>><<<<>>>><><<>><>><<<>><>>><<>><<<<>>>><<<<>><<>>>><<<><>><>><>>><<<>>>><<>>>><<>>><>>><<<<>>><>>><<>>><<<>>>><<>>>><>>><<>>><<<>>><<<><<<><<<<>>><>>>><<<>>><<<<><<<<>>><<<>>>><><<<<>><<>>>><<<><<<<>>><>>>><<<<><<<<><<<<><><<<<>>><<<>>><<<<>><>><>><<>>><<>><<<<>>>><<<<>>><<<><<>>><<<<>>>><><<<><<><<>><<<<><<>>><<>>><<><<<<>>><<<<>><>>>><<<>><<>>><>><<>>>><<><<<>>>><<>>>><>>><<>>>><<<<>>><<<<>>><<<>><<<><<<><<<>>>><<>><<<<>>>><>><><>>>><<<<>><<<><<<>><<>>>><<<<>>>><>>><<<>><<<>><<>><<<<>>>><>><<<<>>><><<>>>><<<>>><<>>>><<<><<>><>>>><<<>><<<<><<<<>>><<<>>><><><<<><>>><<<>>><<>>><<><<><>>><<<>><<>>><<<<>>>><<<<>><>>><>><>>><>>><>><<>>><<><<<>>><>>><<>><<<<>><>>>><<<><><<<<>>>><<<><<<>>><<<<>><<<>><>><<>><<<>>><<<<><<<<>><<<>><>>>><<<<>>><<><<<>>><<>><<><><<<<>>><<<<>>><<<>>><<<<>><<>><<>>>><<<<>>>><><<<<><<<<>>><<>>><<<<><<<>><<<>>><<>>><<<<>>><><<>>><<<<><<><<<<>>><<>>>><<<><<>>><><<>>>><<<<>>>><<<>>>><<<<>>><<<>>>><<<>>><<<><<<<>>><<>><<>>>><<<><<>>><<><<<>>>><>><<<>><<<<>>>><<>>><<<<><<<<>><<><>><<<<>>><<<>><>>>><<<<>><<>><<>>>><<<><>>>><<<><<<<><>>>><<>>><<>><<<>>>><<>>><<<>><<>>><>><<>><>>><<<<>>>><<<>>><>>><<>>><>>><<<><><<<>>><<>><<><>>>><<<>>><<<<><<>>><<<<>>><<<>>>><<><>>>><<>><>>><<>><>><<<>>>><>>><<<<>>><>>><<<>><<<>>>><<<>>><<<><<<><<<<><<<<>>><<<<>><<<<>><<<<>>>><<<<>>><><><<><<<<><>>>><<<<>>><<<><<<>><>>><><<>><>>>><<<<>><<<>>><<<<>>><<<>>><>>>><><<<<>>><<<>>>><<<>><<<<>>>><<<>><<<>><<<>>><<>>>><<>><>>>><<<<>>><<<<>><>>><<<>><><<<>><<<<>>>><>>>><<<><<><><<>><<><>><<<>>>><<><<<>><<>>><<><<>><<<>>><<><<<><<<>><<><>>>><><<<<><<<<>><<<>>><<<<>>><<<<>><<<><<<>><><<<>>><<<<>>><>>><<<<>>><<>>>><<<<>><<>>><<>><>>>><<<<>><<<<>><<<>>>><<<>>>><<><<<><<>>><<>><<<<>>>><<><>><>>><><<><<><>>>><<<<><<<>>>><<>>><<<>>>><<<><<>><<<<>>>><<<<>><<<<>>><<><<><<<<>>><<<<>>><<<>><<>>><<<<><><<<<><<><<<<>>><<>>><<<>>>><<><<<>>>><<<<>>>><<<<>><<<<>>>><<>>>><>>>><>><<<<>><>>>><>>>><>>>><<<>>><<<<>>><>><>>><<>>>><<<>>>><<<<><<<<><<<><<><<>>>><<><>>>><<<>>><<<<>>>><<>>>><<>>><>>>><<<>><<<>>>><<><<><<<>><>><<>>><<>><<>><>>><<<><><<<<>><>><>><>>><<<>>><<<><<<>><<>>>><><<>>>><<>>>><><<<><<>><<<><>>><>>><><<>><>>><><<>><<<<>><<>>>><>>><<<><>>>><>>>><>><>>>><<<<>>><<<>>><<><<>>>><<<<><<>>><<<>>><<<<>>>><>>><<<>>>><<<<><>>><<>><<<>>><<<<><>><<<><<>>>><<<>><><<<<>><><<>>><>>>><<>>><<<><<<<>>>><<<>>><<<<><<<<>>><>>>><<<<>><<>>><>><<>><<>>>><<<<>>><<><<>>>><<<>><<<<>><<<>><>>><<<>>><<><<<<><<>>><>><<<>><<<>>>><<<>><><<>>><<>>><<<<><<>>><<<>><<><<<>>><<<>>><><<<<>><<>>>><>>><<><<<>>>><<><<>>>><<<<>><><<<>>>><<>><>>><<<<><>><<<><<>>><<<><<<>>><>>>><<<>><>>>><<<>>><<>>>><<<<>><>>><<<<><<<<>>>><<<>>>><><<<<>>>><<<>>><<><<<<>><><<>>><<<>>>><<><<<>><<>>><<<>>>><<<><<<>>><<<><>>><<><><<<>>><<<<>><<>>><<<><<<><<><<<<><<<><<>>>><>><<<><<<<>><>><>><>>>><<<<>><<<<>>><>>><>><><>><<<<>><><<<><<><>>>><<>>><<<<>>><<>><<<>>><<<<><<<>>>><<<<>>>><>>>><<><<<<><<<>>>><<<><<<><<<<>>>><<<<>>><<<>><><>>><><<<<>>>><<<>>>><>><>><>>><<<<>>><<<>>><><<<>>>><<>>><>><<<>>><<<<>><<<<>>>><<<>>><<<>>>><<><<>><>>>><<<<>>><>><<<<>><<<>>><<<><>><<><<<>><<<>>>><<<<>>><<>>>><<<<><>>><>>>><<<<>>><<<>>><>>>><<<<>><<<>>>><<><>><<<<>>><<<><<><<>><<><><<<<>>><<><<<>><>>><<>>><<><<<>>><<<<>>><<<<>><<>><<<<>>><<<<>><<<>><<<><<<><<<<>><>><<<<>>>><>>><<<>>><<<<>><<<>>>><<>>>><<<<>>>><<<>>><<><<<><>>>><<><<<>>><<><>><<><><<<<>>>><<<<>><<<>>><<<<><>>>><<<<>><<<<>><<>><<<>><<>>>><<<<>>>><>>><<>>>><<<>>>><<>>>><<><<>>>><<>><<<<>><>>>><><<>>><>><<<>>>><<<<>>><<<<>>>><<<><<<>>>><<<<>>><<<<>><<<<>>><<<><<<<>><<<<><>>>><<<>>>><<>>>><<<<>>><<><<<<><<<><<<<>>>><>>><>>><<<<>>>><<><<<<>>><>>><<<>>>><>>>><>><<>>>><><<<>>><<<<>><<>>><<>>><<<<>>><>>><<<<><<>>><>>><>><<><<<>>>><<<>>>><<<>><>><<<><<<>>><<<<>>><<<>>><>>><<<><<<<>>>><<<<><<>>>><<<>><<<<>><<<>>>><<>>><<>>>><<>>><<<><<>>><><><><>>>><>>>><<><><<>><<>>><<<<>>>><<<<><>><<<<><<<>>><<>>>><<><<<<>>><>><<><>><>>>><>><<<<>>>><<<<>>><>>>><<<<>><<<<>><><>><<><>>><<<>>>><><>>>><><<<>><<<<>>><<<<>><<<>>>><<<>>><<<><<>><<<>><<<<>>><<>>><<>><>>><>><<<>>>><<<><<<<>><><<<<>>>><<<<>>><<<<>><>><<<>>><<<>><<<>>><<<<>>><<>><<<><<<<>>>><<<>>>><<><<<<>><<<><<<<>>><<<<><<>>>><<<><<<<>><<<>>><>>><><<<>>><<>>><<>><<<<>>><>><<<<>>><<<>>><><<<<>>>><<<>>><<>>><<>>><>><>>><<<><>>><<<<>>><<<>>><>>><<<>><<<>><><<<<>>><>>><<>><<>><>>><<>>>><>><>>>><<<>><<>>>><<<<>><<<>><<>><<>>>><<<>><<>><<<><<<>>><>><<<>>>><<>><>>>><<><<<>>>><<>>>><<>><<>>><<<>>><>>>><<<><>>>><<<>><<<<>>>><<<<><<>><<<>><<<>>>><<>>>><<<<><<<<>>><<<>>><>><<<>>><<<><>>>><<<<>><<<>><>>><<>>>><<<>>><<<<>>>><<><<>>><<>>><>><><<<><>>><<<<>>>><<<<>><<<>>>><<<>><<>>><<<>><<>><<<<>><<<>><<<<>><<>>>><>>><<<<>>><<<<>>><><><<<<><<<>>><<<<><<<>>>><<<<>>>><>>><>>><>>><<>>>><<<<>>><<<<>>><<<>>>><<<<>>>><<<<><><<<<>>>><<<>>>><><<>>><<<>>>><<>><<<<>>>><<<><<<<>>><<<>><<>>><<<>>><<>>>><<>><>><<><<<>>>><<>>>><<>><><<>><<<<>><>>><<<>>>><<<<><><<>>>><<>>>><<<><><<<<>><<<<>>>><<>>>><<>>>><>>><<<><<<<>>><<<>>><<<>><<<<>><<<<>><<<<>><<<>>><><<<>>><<>>>><<<>>>><<<>>>><><<<<>>>><<<<>>>><>><<<><<<<>>>><<<<><>>><>>>><><>>><><<<<>>><<<<>>>><<><<<<>>>><>>><>>>><>><<><<<<><<>>>><>><<<<><>>><<<>><<>>>><<>>>><<>>>><<<<>>>><>>>><<<>>>><><>><<<>>>><<<>><<<<>>>><<>>><<<>>><<<>><<<<>><<<<>>>><<<<>><><<<<>><<<<><<<>>>><<<<>><<>>>><>><<<>>><<<><>>><<<><>><>>>><<<<>>>><<<>><<>><>>>><<>>>><<<<>>>><<<<>><>><<<<>><>><<<<>>>><<>>><<>>>><<<<>>><<><<<>>>><<<>>>><<>>>><<>><>>><<<<><<<>>>><<<<>><<<<>>><<>>>><<<><<<<><<>><<<<>><<<<><<>><>>>><<<>><>>>><<<>>>><<>><<>>>><<<<><><<><<><<>><<<<>>>><>>><<<><<><>><<<>>><<<><>><<>>><<<>>><<>>><<>>><<<>>><<<<><>><<<>><<><>>><<<>>><<<<>>><<><>>>><<<<>><<>><<<>><<<><<>>><<>><>><>><<<>>><>><<>>>><<<<>>>><<<<>><<<>>><<>>>><<<<>>>><<>><><<<<>><<>><<>>><>><>>>><<>>><<>>>><<<>>><<<<>>><<>>>><<<<>>>><<>>><<<>><<<>><<>>><<>>><<<>>>><<<<><<>><<><<<><>>>><<<><<<<>>>><<<<><>>>><><<><<>>><<<><>>>><<<>><<<<>><<<>>><<><<>>><<<<>><<>>><<>><<<><<<>><>>><<>><<<<><><<<<>>><<<>>>><<<><<>>>><>><<<<>>>><<<<><<>>>><<<>><<<<>>><<><>>>><><<><>>><>>>><><<><<>><<<<>><<<<>><>><<<>><<><<>><<<>><>><<<>>>><<<>><<<<>><>>>><<<><>>><>>><<<<><<>>><><<<<>>>><<<>><>>>><<>><<<<><<<<>>><><<>><<<><>>>><<>>>><<>><<<<><>><<<<><<<<>>><<>>><<>><<<>>><<<<>>>><<<<><<><>><<<<>>><<><>>><><<<><<>>>><<<><<<><<<<><<<>>>><<<><>>>><>>><>>>><<>><<>>>><<<<>><<<>>><<<<>>>><<<>><><<<>>>><><<>>>><<<><>>>><>>>><<<<>>>><>>>><<><>><<<>>><<<<><>>><>>>><<><<<<><>>><<>>><<>>><<><<<<>>><<<>><<<<>><>>><<<>>>><>><<>>><<<<><<<>>><>><<<><>><<<<>><><>>><<<>>><<>><<<<>><<>>>><<<<><<<>><<><<<<>>>><<>>>><>>>><>><<<<>><<<><>>><<<><<<<><<>><><<<<>><<<><<>>><<><<<><<<><<<>><<<>>><>>><<<<>><<>>><<<>><>>>><<<>><>>>><<<>>><>>><<<>>>><<<<><<<<>>>><<>><<<<>>><<<<>>>><>>><>>><<<>>><<<><<>>><><<<>><<><><>><>><<><<<<><<<>>>><<<>>>><<>>>><<<<>>>><>>>><<<>>><>>>><<<><<<<>>>><<<<><<<<><>>>><<><>>><><<<<>>>><>><<<>>>><<>>><<<>><>>>><<>>><<>><>><<<>>><<<<><<>>>><<>>>><<<>><>><<>>>><<<<><<<>>>><<><<<<>>>><>><<<<><>>>><<>>><<<>>><><>>>><<>>>><<<>>><<>><<>>><>>><<<><>><>>><<<<><>>>><<<<><<<<>><<>>>><<>>><<<<><<<<>><<<>>>><<>>><<><<<<>>>><<<<>><<<>>>><>><<<><<<><<>><<<<>>><<<>>><<<<>>>><>><<>><><<<>>><<>>><<<><<><<<>>><<<>>><<<>>>><<<>>><<<<>><<>><<<><<<>>>><<>>>><>>>><<<<>>><<>><<<>>><<>>>><<<<>>><<<><<><<<<>><<<>><<<>>>><<>>>><>>>><<>><>>>><<<<><<>><<<<><<<<>><>>><<<>><<<>><<<<><<<<>>><<<>><<<>>>><>>>><<<<>>><><<<<><<><<<>>><<<<>><<>>><<<>>><<<>>><<<<>><<>>><<<<>>>><<>><<<>>><<><>><<<<>>>><<>>><>>><>>>><<<>>><<>><>>>><<<>>><>>><<<>>>><<<>>>><>>>><>>><<<<>>><<<>>><<>><>>><<<>><<<<><<<>>><<>>>><><<<<><>><<>>><<<<>><>>><<>>><><<<<>>><<<<>><><<><>><<<<>>><>><<>><<<<><<>>>><<>><><>><<<<>><<<<>>>><<<>>>><<>><<<><>>>><<><<><>>><><>>>><<<><<>>>><<<<>><<<<><<<<><<<>><<>><<<<>>><<>>>><<<><>>><>>>><>>>><<<>>>><<>>>><>>>><<><<>>>><<<<>>>><<<>><<>>><<<<>>>><<<<>>><>>><>>><<<>>>><<>>>><>><><<<>>><<<>>>><>>>><<<<>>><<<><<<>>><<>>>><><>>><<<<><<>><>>>><<>><>>>><<<<>>>><<<<>>><<>><<<><<<<>>><<>>><>>><<<>><>>><><<<>><<><<<<>>>><<>>><<<>>><<<<>><<<<>>>><<<<><<>><<<><<<>>><<<>><<<<>>><>><<>>>><>><<<><<>><<<<>><<<>><<>>><>>><<<>>>><>>>><<<<>><>>><><>>><<<>><<<<>>><<<<>><<<>>><<>>>><>><<<<>><>>>><<><<<><<>>><<<>>>><<<>><<>>><>><>><<<><<<<>>><<<<>><<<<>>><<>>>><<<<><<<>>><<<<>><<<>>>><<<>>>><>>><<><<<><>>><>>><<<>>><<<>>><<<<><<<<>><<<>>>><<<>><<<>>>><><>>>><<<>>>><<<>>>><>><<";
}
