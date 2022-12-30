package git.goossensmichael;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;

public class Day23 {

    private static final Logger LOGGER = Logger.getLogger(Day23.class.getName());

    private static final Direction[] DIRECTIONS =
            new Direction[] { Direction.NORTH, Direction.SOUTH, Direction.WEST, Direction.EAST };

    private static long part1(final String[] input) {
        final int rounds = 10;

        Set<Elf> elves = parse(input);
        int r = 0;

        while (r < rounds) {
            // First half
            final Map<Elf, List<Elf>> propositions = makeProposals(elves, r);
            // Second half
            final Map<Elf, Elf> validPropositions = propositions.entrySet().stream()
                    .filter(p -> p.getValue().size() < 2)
                    .collect(Collectors.toMap(p -> p.getValue().get(0), Map.Entry::getKey));
            elves = elves.stream()
                    .map(elf -> validPropositions.getOrDefault(elf, elf))
                    .collect(Collectors.toSet());

            r++;
        }

        final int minX = elves.stream().mapToInt(elf -> elf.x).min().orElseThrow();
        final int maxX = elves.stream().mapToInt(elf -> elf.x).max().orElseThrow();
        final int minY = elves.stream().mapToInt(elf -> elf.y).min().orElseThrow();
        final int maxY = elves.stream().mapToInt(elf -> elf.y).max().orElseThrow();

        int emptyTiles = 0;
        for (int i = minX; i <= maxX; i++) {
            for (int j = minY; j <= maxY; j++) {
                if (!elves.contains(new Elf(i, j))) {
                    emptyTiles++;
                }
            }
        }

        return emptyTiles;
    }

    private static Map<Elf, List<Elf>> makeProposals(final Set<Elf> elves, final int r) {
        final int leadingDirection = r % DIRECTIONS.length;

        final Map<Elf, List<Elf>> proposals = new HashMap<>();
        for (final Elf elf : elves) {
            if (!isFree(elf, elves)) {
                final Elf proposal = makeProposal(elf, leadingDirection, elves);
                if (proposal != null) {
                    if (!proposals.containsKey(proposal)) {
                        proposals.put(proposal, new ArrayList<>());
                    }
                    proposals.get(proposal).add(elf);
                }
            }
        }

        return proposals;
    }

    private static boolean isFree(final Elf elf, final Set<Elf> elves) {
        return elves.stream().filter(e -> !e.equals(elf)).noneMatch(e -> {
            boolean matches = false;
            for (int x = -1; x <= 1 && !matches; x++) {
                for (int y = -1; y <= 1 && !matches; y++) {
                    matches = e.equals(new Elf(elf.x + x, elf.y + y));
                }
            }

            return matches;
        });
    }

    private static Elf makeProposal(final Elf elf, final int leadingDirection, final Set<Elf> elves) {
        int d = leadingDirection;

        Elf proposal = null;
        boolean makingProposal = true;
        while (makingProposal) {
            final Coord[] moves = DIRECTIONS[d % DIRECTIONS.length].coord;

            if (Arrays.stream(moves).anyMatch(m -> elves.contains(new Elf(elf.x + m.x, elf.y + m.y)))) {
                d++;
                makingProposal = d < leadingDirection + DIRECTIONS.length;
            } else {
                proposal = new Elf(elf.x + moves[1].x, elf.y + moves[1].y);
                makingProposal = false;
            }
        }

        return proposal;
    }

    private static Set<Elf> parse(final String[] input) {
        final Set<Elf> elves = new HashSet<>();

        for (int i = 0; i < input.length; i++) {
            for (int j = 0; j < input[i].length(); j++) {
                if (input[i].charAt(j) == '#') {
                    elves.add(new Elf(i, j));
                }
            }
        }

        return elves;
    }

    private static long part2(final String[] input) {
        Set<Elf> previousElves = Collections.emptySet();
        Set<Elf> elves = parse(input);
        int r = 0;

        while (!(previousElves.containsAll(elves) && elves.containsAll(previousElves))) {
            // First half
            final Map<Elf, List<Elf>> propositions = makeProposals(elves, r);
            // Second half
            final Map<Elf, Elf> validPropositions = propositions.entrySet().stream()
                    .filter(p -> p.getValue().size() < 2)
                    .collect(Collectors.toMap(p -> p.getValue().get(0), Map.Entry::getKey));
            previousElves = elves;
            elves = elves.stream()
                    .map(elf -> validPropositions.getOrDefault(elf, elf))
                    .collect(Collectors.toSet());

            r++;
        }

        return r;
    }

    private enum Direction {
        NORTH(new Coord[] { new Coord(-1, -1), new Coord(-1,  0), new Coord(-1, 1) }),
        EAST(new Coord[] { new Coord(-1, 1), new Coord(0, 1), new Coord(1, 1) }),
        SOUTH(new Coord[] { new Coord(1, -1), new Coord(1, 0), new Coord(1, 1) }),
        WEST(new Coord[] { new Coord(-1, -1), new Coord(0, -1), new Coord(1, -1) })
        ;

        private final Coord[] coord;

        Direction(final Coord[] coord) {
            this.coord = coord;
        }
    }

    private record Coord(int x, int y) {}

    private record Elf(int x, int y) {}

    public static void main(final String[] args) {

        // Parsing input
        final var tinyInput = TINY_INPUT.split("\n");
        final var testInput = TST_INPUT.split("\n");
        final var input = INPUT.split("\n");

        {
            final var tinyPart1 = part1(tinyInput);
            LOGGER.log(Level.INFO, () -> String.format("Tiny part 1 (expecting 25): %d", tinyPart1));

            final var expectedResult = 110;
            final var part1 = part1(testInput);
            LOGGER.log(Level.INFO, () -> String.format("Test part 1: %d", part1));

            if (expectedResult == part1) {
                final var result = part1(input);
                LOGGER.log(Level.INFO, () -> String.format("Part 1: %d", result));
            }
        }

        {
            final var expectedResult = 20;
            final var testResult = part2(testInput);
            LOGGER.log(Level.INFO, () -> String.format("Test part 2: %d", testResult));

            if (expectedResult == testResult) {
                final var result = part2(input);
                LOGGER.log(Level.INFO, () -> String.format("Part 2: %d", result));
            }
        }
    }
    private static final String TST_INPUT = """
            ..............
            ..............
            .......#......
            .....###.#....
            ...#...#.#....
            ....#...##....
            ...#.###......
            ...##.#.##....
            ....#..#......
            ..............
            ..............
            ..............
            """;

    private static final String TINY_INPUT = """
            .....
            ..##.
            ..#..
            .....
            ..##.
            .....
            """;

    private static final String INPUT = """
            .#..###.##..##.####.#.....##.##.#..#.##.#..#.#.#.####..#####.#......#...#..
            ###...#.#..##...#.##...#.###.###....##..##..######.#.#..#.#...#.##...#.#.#.
            .......#.###.....#.##.....##..#.#...##..#####.###.##......#.###.##.#####.##
            ##.##...##..####.##.........#.##.#.#..####.##....#.#####.####.####..##..##.
            #..#.###...###...#..#..#..#.#.##.######.###..##########.#..#.##.#.#.##....#
            .#..#..##..#.#.......#.#....#######..#.##.##..##.#.#.#..#.##....#..##.#####
            .#.##..##.##...#.##.##..##.##.##..##...###..###.#...##..##....#.##....#####
            .###.###.#.##.###....#..###..###..##.#.#.#.#....#####.###.######...###.###.
            #...####.###.....#.....###...#..##.##...########.#.#.######...#..#.###....#
            ..###.#.###..#.#.........#....#.#.#####.#.#.#....###.##..###...####.#.#...#
            ###.#.######.##.#.##....##.##..##.#.#..#.#..##..#..##.###..##.########.#.#.
            .#...##.....###...##..###.#.##.....#.##..#.#.#..##.#...#.#.#.###.#....#.##.
            ..####.#.#.###....###.#.....##...#.##.#...#..#.#..#####...######....#..##.#
            .##.#..##.###.###..#.#.#####.#.###......###..#.#..#..###..###..#.....#..##.
            #.#.#####..##.##...#...#...#..#...#.#.#..#........######.##.#...#..#.#..##.
            .#.####....#.#..##.##..#...##.##...##.....###.###.###..#.#....#.#.####.#..#
            ##.#..#.###..###........#...##.#..##...####.##..#..#.##.#....##..##..#####.
            ...#..##.#..##.####..###...#.#.#.......#####..######....###..#.#..###.###..
            .#.##...#.#.#........##..#.#####.#####...##......#.##.....#..####..#####...
            .#.#..#..#.#.#.####.#...###.###.#..###...###.##..##...##..####.#.....#.#.##
            ##...#.#...##..##..##..##.####..######...##..#...##..##.##....##...#.#.....
            ..####.#####..##.####...#...#...#.#.#..##.##..#...###.#..#...#...#.#...#.##
            ..###.#....#.########...##....#..#.#.#..#.##..#...#.##.....#.###..#..#..#.#
            #.##....###.#..##.#.##..####..###......####.###.##..###....#..#.#.#...####.
            .######.#....#....#####...#..#..###.#..#...#.###.#.##..#.###..#..###......#
            #..##.#.#####.#.#.##.#..###.#..#..#.#..#.#..#.#.....##########.#..#..#..#.#
            ###.##....#...####.###..##..#....#.#......#.#.#......#..##.#..####...#..#.#
            ..#.###..#.###.##.#..#....#..#####.#.#.####.#...#....#.##.#.#..###.########
            ..#.#.....##.##...#..##...#.#..##.####.###.#.#..#..####...#..##..##..#.###.
            .###..#.###....#.####.#..#...###.#....#...#....###.#.######.####..#.#..###.
            .#.##.##.....#.###.#...#..#.#.##.##..#.#..###.##..#.##....##....#.#...#.##.
            #.##.#..#.#.#....##...####.#..#.###..######.###.#..##.#.#.#####...##..#.##.
            #.###.##.#..#...#..##..#.##.....#...#....#...##....#..#..##..##.##....##.##
            ..###..#...#.#..##.##..#####.#..#.#..#...#...##.#.#..#..#.###....#.####.###
            ###.###...####..###..#..#.##..#.#.#.....#....#.#####.#..###.#..####....##.#
            #.###.#.#.##.###.##.........###.#..#######.####..........#.#.###..#.#.##.#.
            ####.##.#####.##.#.#.##......#...#.#.##..###..#...#...###..###.#####..##...
            .##...#####.##########.#....##.......#.#.#.#.###..#####....#..#.####..#####
            ####..#..####.###..###..#####.###....#.#.#.....#.####..#..###.#..##.##..##.
            ###.#.#....##..#.##.#.....##..##.##..##.###.##.#.#...####...#.##..####..##.
            #..#..##.#....###..##.#..#...#.#.#.#.######.##..#...##.#.#.....####..##.##.
            ....####..###.#.##..#...#..##..#####.....##.##..#.##..##...#####.....##..#.
            .##...##...######..##..#.##..##.#.#..##.##..###.#.####...#######..##....#..
            .###.##.#......###....#####.#.#.##...#####..#..##.###...##...###.#.##...##.
            .#....####..#..#..##...##....#.##...###..##.#..#.#.#.#.####.....###...#.###
            ##.###..#....##....#.##..#.#.....#...###..##..###....#.....###...##.....###
            ##......#.##.#.###...##..###.....#.##.#..#.###..###.#.##...#####..###..#.##
            ##.##.#####........#..##........#..##..##......#.###.#.#..##.######.#....##
            ##.....#.##...#.####.####...#.###.#....#.######.#....#.#.##....#...##..##.#
            #.##.....#####.###.###.##.#.#.#..#....#.....#.#..###.####.#####...#.......#
            ...#######......###.##.#.#.#.....######.#.##.#.#...#..#.#....#.#.#.##..###.
            #.###..#.##.#####..#.#...##.####.###.#####..#.....#...#.#..##.#.####.##.#..
            .##..##...##....#####.#...####..#....#..##.#...#####.#.##...#.####.###...##
            ..####.###...##.##.#..#.###.##..#....#.#....###.#..##.#.....#.###....##.#..
            #.##.#...#..##.####.######.##..#.#......#.###...#..##.#..##..###.#..#.#.#.#
            ..##.##..#..#.####.###.#...##.###.###.##..##.###....##...##.#..##.###...##.
            .####..#....#####..#..##....#.#.##.#####.#####..#.....###.#..###.####......
            ##...#.#.#..##..##....##.###...#.##..#..#....###....#####.##..##.....####.#
            #..####...####..##..#..#..#.#.....##.#..#####..#.#.###.#..###.....#.#.###.#
            .##..####.####...#.#....##..#####..#...#...#..###..##......#...#..#.##.###.
            #..##..###.###.#.#..##..#.#.###....##..##.###.#..#....#.#.#..#..##.#..####.
            #.####..##....#...#.#...###..#..##...##.#.#.#.#...#..#...##...#...#.#....#.
            #..###.###...###.#.###.#....#..##....#.#.#..#.##.###.#####..##.###...#.#...
            .#..###.#.#..#.#.#...####.#.#.##.###.#..##.##.......#.#.#.#.#...#..#.#...##
            ######.....###...#..#..###.#.#...#...##..##.##.#..#.#...###.##.##....#.#...
            #..##...#...##...###..#....#...###.#.###.###..#..##.##..####..#.###.##....#
            ..##...#..#.#.#######..#.###..#.#.#.#.##.#..#.....#.##.###.#####.#.######..
            #.#.#..##.#.###.#....#..#..######.#....##.#..###.####.#......#..##.##.##.##
            ######....##......#..#...####..###..######.#.#.##....#...##.#.#..#.#.#.##..
            ###.####.###.#####.....######..#...#.##.#....##.#.##..#.#.##.#.##..###..###
            ..######....####..###..##..##.##...#.###..#.##..#.#...#.##.##.....#..#..###
            .##..##..#.##....#...##.###.##..#...##..##.##.#.#..###...##....#.#.#..#...#
            ..#..##.########.##..#####.#....#..##.##.##.#...#.##..##.#..#..###...#.#.##
            #.##....#..#.#..##.#......#.#..##.#.............#.##.#...##.#..#...##.#.###
            #..#..###.#.##.#..#....##..#..#..##.##..#.##...###.......#..###..####...##.
            """;
}
