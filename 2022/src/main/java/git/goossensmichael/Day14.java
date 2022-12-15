package git.goossensmichael;

import git.goossensmichael.utils.Pair;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

public class Day14 {

    private static final Logger LOGGER = Logger.getLogger(Day14.class.getName());

    private static long part1(final String[] rockStructures) {
        final Cave cave = parse(rockStructures);

        boolean loop = true;
        long unitsOfSand = 0;
        while (loop) {
            loop = dropSand(cave, 0, 500 - cave.xFrom);
            if (loop) {
                unitsOfSand++;
            }
        }

        return unitsOfSand;
    }

    // Returns false when sand is falling into the abyss.
    private static boolean dropSand(final Cave cave, final int row, final int column) {
        final int[] dropPoint = findDropPoint(cave, row, column);

        int dropX = row;
        int dropY = column;
        if (dropPoint != null) {
            dropX = dropPoint[0];
            dropY = dropPoint[1];
        }

        if (dropY > 0 && sandCanGo(cave, dropX, dropY - 1)) {
            return dropSand(cave, dropX + 1, dropY - 1);
        } else if (dropY + 1 < cave.map()[0].length && sandCanGo(cave, dropX, dropY + 1)) {
            return dropSand(cave, row + 1, column + 1);
        } else if (dropY > 0 && dropY + 1 < cave.map()[0].length && dropPoint != null) {
            cave.map()[dropPoint[0]][dropPoint[1]] = 'o';
            return true;
        } else {
            return false;
        }
    }

    private static boolean sandCanGo(final Cave cave, final int row, final int column) {
        return row >= 0 && row < cave.map().length && column >= 0 && column < cave.map()[0].length &&
                row + 1 < cave.map().length && cave.map()[row + 1][column] == '.';
    }

    private static int[] findDropPoint(final Cave cave, final int row, final int column) {
        final int rows = cave.map().length;

        boolean loop = true;
        int currentRow = row;
        while (loop) {
            if (currentRow + 1 < rows && cave.map()[currentRow + 1][column] == '.') {
                currentRow++;
            } else {
                loop = false;
            }
        }

        if (currentRow < rows && cave.map()[currentRow][column] == '.') {
            return new int[]{currentRow, column};
        } else {
            return null;
        }
    }

    private static long part2(final String[] rockStructures) {
        final Cave cave = parse(rockStructures, true);

        boolean loop = true;
        long unitsOfSand = 0;
        while (loop) {
            dropSand(cave, 0, 500 - cave.xFrom + cave.shift);
            if (loop) {
                unitsOfSand++;
            }
            loop = cave.map()[0][500 - cave.xFrom + cave.shift] != 'o';
        }

        return unitsOfSand;
    }

    public static void main(final String[] args) {

        // Parsing input
        final var testInput = TST_INPUT.split("\n");
        final var input = INPUT.split("\n");

        {
            final var expectedResult = 24;
            final var part1 = part1(testInput);
            LOGGER.log(Level.INFO, () -> String.format("Test part 1: %d", part1));

            if (expectedResult == part1) {
                final var result = part1(input);
                LOGGER.log(Level.INFO, () -> String.format("Part 1: %d", result));
            }
        }

        {
            final var expectedResult = 93;
            final var testResult = part2(testInput);
            LOGGER.log(Level.INFO, () -> String.format("Test part 2: %d", testResult));

            if (expectedResult == testResult) {
                final var result = part2(input);
                LOGGER.log(Level.INFO, () -> String.format("Part 2: %d", result));
            }
        }
    }

    private static Cave parse(final String[] rockStructures) {
        return parse(rockStructures, false);
    }

    private static Cave parse(final String[] rockStructures, final boolean infiniteFloor) {
        final Set<Pair<Integer, Integer>> rocks = new HashSet<>();
        int xMin = 500;
        int xMax = 500;
        int yMin = 0;
        int yMax = Integer.MIN_VALUE;

        for (final String rockStructure : rockStructures) {
            final String[] coordinates = rockStructure.split(" -> ");

            int[] previous = toCoordinates(coordinates[0]);
            xMin = Math.min(xMin, previous[0]);
            xMax = Math.max(xMax, previous[0]);
            yMin = Math.min(yMin, previous[1]);
            yMax = Math.max(yMax, previous[1]);
            for (int i = 1; i < coordinates.length; i++) {
                int[] current = toCoordinates(coordinates[i]);
                addRockFormation(rocks, previous, current);
                xMin = Math.min(xMin, current[0]);
                xMax = Math.max(xMax, current[0]);
                yMin = Math.min(yMin, current[1]);
                yMax = Math.max(yMax, current[1]);

                previous = current;
            }
        }

        final int yRange;
        final int xRange;
        final int shift;
        if (infiniteFloor) {
            yRange = yMax - yMin + 1 + 2;
            shift = yRange;
            xRange = xMax - xMin + 1 + (2 * shift);
        } else {
            yRange = yMax - yMin + 1;
            shift = 0;
            xRange = xMax - xMin + 1;
        }
        final char[][] map = new char[yRange][xRange];
        for (int i = 0; i < yRange; i++) {
            for (int j = 0; j < xRange; j++) {
                if (infiniteFloor && i == yRange - 1) {
                    map[i][j] = '#';
                } else {
                    map[i][j] = '.';
                }
            }
        }

        final int xNorm = xMin;
        final int yNorm = yMin;
        rocks.forEach(rock -> map[rock.left() - yNorm][shift + rock.right() - xNorm] = '#');

        return new Cave(map, xMin, xMax, yMin, yMax, shift);
    }

    // Also returns the min and max values for x and y.
    private static void addRockFormation(final Set<Pair<Integer, Integer>> rocks, final int[] previous, final int[] current) {
        final int xFrom = Math.min(previous[0], current[0]);
        final int xTo = Math.max(previous[0], current[0]);
        final int yFrom = Math.min(previous[1], current[1]);
        final int yTo = Math.max(previous[1], current[1]);

        for (int i = xFrom; i <= xTo; i++) {
            for (int j = yFrom; j <= yTo; j++) {
                rocks.add(new Pair<>(j, i));
            }
        }
    }

    private static int[] toCoordinates(final String coords) {
        return Arrays.stream(coords.split(","))
                .mapToInt(Integer::parseInt)
                .toArray();
    }

    private record Cave(char[][] map, int xFrom, int xTo, int yFrom, int yTo, int shift) {
        @Override
        public String toString() {
            final StringBuffer stringBuffer = new StringBuffer();
            for (int i = 0; i < map.length; i++) {
                for (int j = 0; j < map[0].length; j++) {
                    stringBuffer.append(map[i][j]);
                }
                stringBuffer.append("\n");
            }

            return stringBuffer.toString();
        }
    }

    private static final String TST_INPUT = """
            498,4 -> 498,6 -> 496,6
            503,4 -> 502,4 -> 502,9 -> 494,9
            """;

    private static final String INPUT = """
            541,130 -> 541,122 -> 541,130 -> 543,130 -> 543,123 -> 543,130 -> 545,130 -> 545,124 -> 545,130 -> 547,130 -> 547,122 -> 547,130 -> 549,130 -> 549,120 -> 549,130 -> 551,130 -> 551,121 -> 551,130 -> 553,130 -> 553,128 -> 553,130 -> 555,130 -> 555,128 -> 555,130 -> 557,130 -> 557,126 -> 557,130 -> 559,130 -> 559,125 -> 559,130
            528,67 -> 533,67
            541,130 -> 541,122 -> 541,130 -> 543,130 -> 543,123 -> 543,130 -> 545,130 -> 545,124 -> 545,130 -> 547,130 -> 547,122 -> 547,130 -> 549,130 -> 549,120 -> 549,130 -> 551,130 -> 551,121 -> 551,130 -> 553,130 -> 553,128 -> 553,130 -> 555,130 -> 555,128 -> 555,130 -> 557,130 -> 557,126 -> 557,130 -> 559,130 -> 559,125 -> 559,130
            496,23 -> 496,16 -> 496,23 -> 498,23 -> 498,16 -> 498,23 -> 500,23 -> 500,17 -> 500,23 -> 502,23 -> 502,16 -> 502,23
            541,130 -> 541,122 -> 541,130 -> 543,130 -> 543,123 -> 543,130 -> 545,130 -> 545,124 -> 545,130 -> 547,130 -> 547,122 -> 547,130 -> 549,130 -> 549,120 -> 549,130 -> 551,130 -> 551,121 -> 551,130 -> 553,130 -> 553,128 -> 553,130 -> 555,130 -> 555,128 -> 555,130 -> 557,130 -> 557,126 -> 557,130 -> 559,130 -> 559,125 -> 559,130
            514,62 -> 519,62
            509,56 -> 514,56
            497,36 -> 497,30 -> 497,36 -> 499,36 -> 499,27 -> 499,36 -> 501,36 -> 501,33 -> 501,36 -> 503,36 -> 503,27 -> 503,36 -> 505,36 -> 505,28 -> 505,36 -> 507,36 -> 507,35 -> 507,36
            541,130 -> 541,122 -> 541,130 -> 543,130 -> 543,123 -> 543,130 -> 545,130 -> 545,124 -> 545,130 -> 547,130 -> 547,122 -> 547,130 -> 549,130 -> 549,120 -> 549,130 -> 551,130 -> 551,121 -> 551,130 -> 553,130 -> 553,128 -> 553,130 -> 555,130 -> 555,128 -> 555,130 -> 557,130 -> 557,126 -> 557,130 -> 559,130 -> 559,125 -> 559,130
            541,130 -> 541,122 -> 541,130 -> 543,130 -> 543,123 -> 543,130 -> 545,130 -> 545,124 -> 545,130 -> 547,130 -> 547,122 -> 547,130 -> 549,130 -> 549,120 -> 549,130 -> 551,130 -> 551,121 -> 551,130 -> 553,130 -> 553,128 -> 553,130 -> 555,130 -> 555,128 -> 555,130 -> 557,130 -> 557,126 -> 557,130 -> 559,130 -> 559,125 -> 559,130
            574,133 -> 574,137 -> 567,137 -> 567,141 -> 584,141 -> 584,137 -> 576,137 -> 576,133
            524,60 -> 529,60
            574,133 -> 574,137 -> 567,137 -> 567,141 -> 584,141 -> 584,137 -> 576,137 -> 576,133
            541,130 -> 541,122 -> 541,130 -> 543,130 -> 543,123 -> 543,130 -> 545,130 -> 545,124 -> 545,130 -> 547,130 -> 547,122 -> 547,130 -> 549,130 -> 549,120 -> 549,130 -> 551,130 -> 551,121 -> 551,130 -> 553,130 -> 553,128 -> 553,130 -> 555,130 -> 555,128 -> 555,130 -> 557,130 -> 557,126 -> 557,130 -> 559,130 -> 559,125 -> 559,130
            541,130 -> 541,122 -> 541,130 -> 543,130 -> 543,123 -> 543,130 -> 545,130 -> 545,124 -> 545,130 -> 547,130 -> 547,122 -> 547,130 -> 549,130 -> 549,120 -> 549,130 -> 551,130 -> 551,121 -> 551,130 -> 553,130 -> 553,128 -> 553,130 -> 555,130 -> 555,128 -> 555,130 -> 557,130 -> 557,126 -> 557,130 -> 559,130 -> 559,125 -> 559,130
            506,39 -> 506,43 -> 499,43 -> 499,51 -> 514,51 -> 514,43 -> 510,43 -> 510,39
            541,130 -> 541,122 -> 541,130 -> 543,130 -> 543,123 -> 543,130 -> 545,130 -> 545,124 -> 545,130 -> 547,130 -> 547,122 -> 547,130 -> 549,130 -> 549,120 -> 549,130 -> 551,130 -> 551,121 -> 551,130 -> 553,130 -> 553,128 -> 553,130 -> 555,130 -> 555,128 -> 555,130 -> 557,130 -> 557,126 -> 557,130 -> 559,130 -> 559,125 -> 559,130
            541,130 -> 541,122 -> 541,130 -> 543,130 -> 543,123 -> 543,130 -> 545,130 -> 545,124 -> 545,130 -> 547,130 -> 547,122 -> 547,130 -> 549,130 -> 549,120 -> 549,130 -> 551,130 -> 551,121 -> 551,130 -> 553,130 -> 553,128 -> 553,130 -> 555,130 -> 555,128 -> 555,130 -> 557,130 -> 557,126 -> 557,130 -> 559,130 -> 559,125 -> 559,130
            503,60 -> 508,60
            567,115 -> 571,115
            556,151 -> 556,153 -> 548,153 -> 548,157 -> 561,157 -> 561,153 -> 560,153 -> 560,151
            497,36 -> 497,30 -> 497,36 -> 499,36 -> 499,27 -> 499,36 -> 501,36 -> 501,33 -> 501,36 -> 503,36 -> 503,27 -> 503,36 -> 505,36 -> 505,28 -> 505,36 -> 507,36 -> 507,35 -> 507,36
            525,69 -> 530,69
            557,83 -> 557,85 -> 550,85 -> 550,89 -> 569,89 -> 569,85 -> 561,85 -> 561,83
            574,133 -> 574,137 -> 567,137 -> 567,141 -> 584,141 -> 584,137 -> 576,137 -> 576,133
            541,130 -> 541,122 -> 541,130 -> 543,130 -> 543,123 -> 543,130 -> 545,130 -> 545,124 -> 545,130 -> 547,130 -> 547,122 -> 547,130 -> 549,130 -> 549,120 -> 549,130 -> 551,130 -> 551,121 -> 551,130 -> 553,130 -> 553,128 -> 553,130 -> 555,130 -> 555,128 -> 555,130 -> 557,130 -> 557,126 -> 557,130 -> 559,130 -> 559,125 -> 559,130
            496,23 -> 496,16 -> 496,23 -> 498,23 -> 498,16 -> 498,23 -> 500,23 -> 500,17 -> 500,23 -> 502,23 -> 502,16 -> 502,23
            564,108 -> 564,107 -> 564,108 -> 566,108 -> 566,101 -> 566,108 -> 568,108 -> 568,100 -> 568,108
            496,23 -> 496,16 -> 496,23 -> 498,23 -> 498,16 -> 498,23 -> 500,23 -> 500,17 -> 500,23 -> 502,23 -> 502,16 -> 502,23
            556,151 -> 556,153 -> 548,153 -> 548,157 -> 561,157 -> 561,153 -> 560,153 -> 560,151
            541,130 -> 541,122 -> 541,130 -> 543,130 -> 543,123 -> 543,130 -> 545,130 -> 545,124 -> 545,130 -> 547,130 -> 547,122 -> 547,130 -> 549,130 -> 549,120 -> 549,130 -> 551,130 -> 551,121 -> 551,130 -> 553,130 -> 553,128 -> 553,130 -> 555,130 -> 555,128 -> 555,130 -> 557,130 -> 557,126 -> 557,130 -> 559,130 -> 559,125 -> 559,130
            497,36 -> 497,30 -> 497,36 -> 499,36 -> 499,27 -> 499,36 -> 501,36 -> 501,33 -> 501,36 -> 503,36 -> 503,27 -> 503,36 -> 505,36 -> 505,28 -> 505,36 -> 507,36 -> 507,35 -> 507,36
            556,151 -> 556,153 -> 548,153 -> 548,157 -> 561,157 -> 561,153 -> 560,153 -> 560,151
            561,115 -> 565,115
            564,144 -> 568,144
            497,36 -> 497,30 -> 497,36 -> 499,36 -> 499,27 -> 499,36 -> 501,36 -> 501,33 -> 501,36 -> 503,36 -> 503,27 -> 503,36 -> 505,36 -> 505,28 -> 505,36 -> 507,36 -> 507,35 -> 507,36
            541,130 -> 541,122 -> 541,130 -> 543,130 -> 543,123 -> 543,130 -> 545,130 -> 545,124 -> 545,130 -> 547,130 -> 547,122 -> 547,130 -> 549,130 -> 549,120 -> 549,130 -> 551,130 -> 551,121 -> 551,130 -> 553,130 -> 553,128 -> 553,130 -> 555,130 -> 555,128 -> 555,130 -> 557,130 -> 557,126 -> 557,130 -> 559,130 -> 559,125 -> 559,130
            497,36 -> 497,30 -> 497,36 -> 499,36 -> 499,27 -> 499,36 -> 501,36 -> 501,33 -> 501,36 -> 503,36 -> 503,27 -> 503,36 -> 505,36 -> 505,28 -> 505,36 -> 507,36 -> 507,35 -> 507,36
            556,151 -> 556,153 -> 548,153 -> 548,157 -> 561,157 -> 561,153 -> 560,153 -> 560,151
            557,83 -> 557,85 -> 550,85 -> 550,89 -> 569,89 -> 569,85 -> 561,85 -> 561,83
            496,23 -> 496,16 -> 496,23 -> 498,23 -> 498,16 -> 498,23 -> 500,23 -> 500,17 -> 500,23 -> 502,23 -> 502,16 -> 502,23
            567,146 -> 571,146
            531,65 -> 536,65
            528,62 -> 533,62
            541,130 -> 541,122 -> 541,130 -> 543,130 -> 543,123 -> 543,130 -> 545,130 -> 545,124 -> 545,130 -> 547,130 -> 547,122 -> 547,130 -> 549,130 -> 549,120 -> 549,130 -> 551,130 -> 551,121 -> 551,130 -> 553,130 -> 553,128 -> 553,130 -> 555,130 -> 555,128 -> 555,130 -> 557,130 -> 557,126 -> 557,130 -> 559,130 -> 559,125 -> 559,130
            541,130 -> 541,122 -> 541,130 -> 543,130 -> 543,123 -> 543,130 -> 545,130 -> 545,124 -> 545,130 -> 547,130 -> 547,122 -> 547,130 -> 549,130 -> 549,120 -> 549,130 -> 551,130 -> 551,121 -> 551,130 -> 553,130 -> 553,128 -> 553,130 -> 555,130 -> 555,128 -> 555,130 -> 557,130 -> 557,126 -> 557,130 -> 559,130 -> 559,125 -> 559,130
            574,133 -> 574,137 -> 567,137 -> 567,141 -> 584,141 -> 584,137 -> 576,137 -> 576,133
            568,95 -> 571,95 -> 571,94
            497,36 -> 497,30 -> 497,36 -> 499,36 -> 499,27 -> 499,36 -> 501,36 -> 501,33 -> 501,36 -> 503,36 -> 503,27 -> 503,36 -> 505,36 -> 505,28 -> 505,36 -> 507,36 -> 507,35 -> 507,36
            497,36 -> 497,30 -> 497,36 -> 499,36 -> 499,27 -> 499,36 -> 501,36 -> 501,33 -> 501,36 -> 503,36 -> 503,27 -> 503,36 -> 505,36 -> 505,28 -> 505,36 -> 507,36 -> 507,35 -> 507,36
            497,36 -> 497,30 -> 497,36 -> 499,36 -> 499,27 -> 499,36 -> 501,36 -> 501,33 -> 501,36 -> 503,36 -> 503,27 -> 503,36 -> 505,36 -> 505,28 -> 505,36 -> 507,36 -> 507,35 -> 507,36
            541,130 -> 541,122 -> 541,130 -> 543,130 -> 543,123 -> 543,130 -> 545,130 -> 545,124 -> 545,130 -> 547,130 -> 547,122 -> 547,130 -> 549,130 -> 549,120 -> 549,130 -> 551,130 -> 551,121 -> 551,130 -> 553,130 -> 553,128 -> 553,130 -> 555,130 -> 555,128 -> 555,130 -> 557,130 -> 557,126 -> 557,130 -> 559,130 -> 559,125 -> 559,130
            557,83 -> 557,85 -> 550,85 -> 550,89 -> 569,89 -> 569,85 -> 561,85 -> 561,83
            543,72 -> 543,73 -> 554,73 -> 554,72
            506,39 -> 506,43 -> 499,43 -> 499,51 -> 514,51 -> 514,43 -> 510,43 -> 510,39
            555,115 -> 559,115
            496,23 -> 496,16 -> 496,23 -> 498,23 -> 498,16 -> 498,23 -> 500,23 -> 500,17 -> 500,23 -> 502,23 -> 502,16 -> 502,23
            506,39 -> 506,43 -> 499,43 -> 499,51 -> 514,51 -> 514,43 -> 510,43 -> 510,39
            536,75 -> 536,76 -> 547,76 -> 547,75
            521,62 -> 526,62
            564,108 -> 564,107 -> 564,108 -> 566,108 -> 566,101 -> 566,108 -> 568,108 -> 568,100 -> 568,108
            574,133 -> 574,137 -> 567,137 -> 567,141 -> 584,141 -> 584,137 -> 576,137 -> 576,133
            516,56 -> 521,56
            564,108 -> 564,107 -> 564,108 -> 566,108 -> 566,101 -> 566,108 -> 568,108 -> 568,100 -> 568,108
            543,72 -> 543,73 -> 554,73 -> 554,72
            574,133 -> 574,137 -> 567,137 -> 567,141 -> 584,141 -> 584,137 -> 576,137 -> 576,133
            564,113 -> 568,113
            506,39 -> 506,43 -> 499,43 -> 499,51 -> 514,51 -> 514,43 -> 510,43 -> 510,39
            541,130 -> 541,122 -> 541,130 -> 543,130 -> 543,123 -> 543,130 -> 545,130 -> 545,124 -> 545,130 -> 547,130 -> 547,122 -> 547,130 -> 549,130 -> 549,120 -> 549,130 -> 551,130 -> 551,121 -> 551,130 -> 553,130 -> 553,128 -> 553,130 -> 555,130 -> 555,128 -> 555,130 -> 557,130 -> 557,126 -> 557,130 -> 559,130 -> 559,125 -> 559,130
            496,23 -> 496,16 -> 496,23 -> 498,23 -> 498,16 -> 498,23 -> 500,23 -> 500,17 -> 500,23 -> 502,23 -> 502,16 -> 502,23
            557,83 -> 557,85 -> 550,85 -> 550,89 -> 569,89 -> 569,85 -> 561,85 -> 561,83
            558,113 -> 562,113
            497,36 -> 497,30 -> 497,36 -> 499,36 -> 499,27 -> 499,36 -> 501,36 -> 501,33 -> 501,36 -> 503,36 -> 503,27 -> 503,36 -> 505,36 -> 505,28 -> 505,36 -> 507,36 -> 507,35 -> 507,36
            506,39 -> 506,43 -> 499,43 -> 499,51 -> 514,51 -> 514,43 -> 510,43 -> 510,39
            497,36 -> 497,30 -> 497,36 -> 499,36 -> 499,27 -> 499,36 -> 501,36 -> 501,33 -> 501,36 -> 503,36 -> 503,27 -> 503,36 -> 505,36 -> 505,28 -> 505,36 -> 507,36 -> 507,35 -> 507,36
            568,95 -> 571,95 -> 571,94
            497,36 -> 497,30 -> 497,36 -> 499,36 -> 499,27 -> 499,36 -> 501,36 -> 501,33 -> 501,36 -> 503,36 -> 503,27 -> 503,36 -> 505,36 -> 505,28 -> 505,36 -> 507,36 -> 507,35 -> 507,36
            510,60 -> 515,60
            497,36 -> 497,30 -> 497,36 -> 499,36 -> 499,27 -> 499,36 -> 501,36 -> 501,33 -> 501,36 -> 503,36 -> 503,27 -> 503,36 -> 505,36 -> 505,28 -> 505,36 -> 507,36 -> 507,35 -> 507,36
            557,83 -> 557,85 -> 550,85 -> 550,89 -> 569,89 -> 569,85 -> 561,85 -> 561,83
            570,117 -> 574,117
            497,36 -> 497,30 -> 497,36 -> 499,36 -> 499,27 -> 499,36 -> 501,36 -> 501,33 -> 501,36 -> 503,36 -> 503,27 -> 503,36 -> 505,36 -> 505,28 -> 505,36 -> 507,36 -> 507,35 -> 507,36
            507,62 -> 512,62
            541,130 -> 541,122 -> 541,130 -> 543,130 -> 543,123 -> 543,130 -> 545,130 -> 545,124 -> 545,130 -> 547,130 -> 547,122 -> 547,130 -> 549,130 -> 549,120 -> 549,130 -> 551,130 -> 551,121 -> 551,130 -> 553,130 -> 553,128 -> 553,130 -> 555,130 -> 555,128 -> 555,130 -> 557,130 -> 557,126 -> 557,130 -> 559,130 -> 559,125 -> 559,130
            556,151 -> 556,153 -> 548,153 -> 548,157 -> 561,157 -> 561,153 -> 560,153 -> 560,151
            564,108 -> 564,107 -> 564,108 -> 566,108 -> 566,101 -> 566,108 -> 568,108 -> 568,100 -> 568,108
            541,130 -> 541,122 -> 541,130 -> 543,130 -> 543,123 -> 543,130 -> 545,130 -> 545,124 -> 545,130 -> 547,130 -> 547,122 -> 547,130 -> 549,130 -> 549,120 -> 549,130 -> 551,130 -> 551,121 -> 551,130 -> 553,130 -> 553,128 -> 553,130 -> 555,130 -> 555,128 -> 555,130 -> 557,130 -> 557,126 -> 557,130 -> 559,130 -> 559,125 -> 559,130
            506,39 -> 506,43 -> 499,43 -> 499,51 -> 514,51 -> 514,43 -> 510,43 -> 510,39
            564,108 -> 564,107 -> 564,108 -> 566,108 -> 566,101 -> 566,108 -> 568,108 -> 568,100 -> 568,108
            497,36 -> 497,30 -> 497,36 -> 499,36 -> 499,27 -> 499,36 -> 501,36 -> 501,33 -> 501,36 -> 503,36 -> 503,27 -> 503,36 -> 505,36 -> 505,28 -> 505,36 -> 507,36 -> 507,35 -> 507,36
            561,111 -> 565,111
            541,130 -> 541,122 -> 541,130 -> 543,130 -> 543,123 -> 543,130 -> 545,130 -> 545,124 -> 545,130 -> 547,130 -> 547,122 -> 547,130 -> 549,130 -> 549,120 -> 549,130 -> 551,130 -> 551,121 -> 551,130 -> 553,130 -> 553,128 -> 553,130 -> 555,130 -> 555,128 -> 555,130 -> 557,130 -> 557,126 -> 557,130 -> 559,130 -> 559,125 -> 559,130
            541,130 -> 541,122 -> 541,130 -> 543,130 -> 543,123 -> 543,130 -> 545,130 -> 545,124 -> 545,130 -> 547,130 -> 547,122 -> 547,130 -> 549,130 -> 549,120 -> 549,130 -> 551,130 -> 551,121 -> 551,130 -> 553,130 -> 553,128 -> 553,130 -> 555,130 -> 555,128 -> 555,130 -> 557,130 -> 557,126 -> 557,130 -> 559,130 -> 559,125 -> 559,130
            541,130 -> 541,122 -> 541,130 -> 543,130 -> 543,123 -> 543,130 -> 545,130 -> 545,124 -> 545,130 -> 547,130 -> 547,122 -> 547,130 -> 549,130 -> 549,120 -> 549,130 -> 551,130 -> 551,121 -> 551,130 -> 553,130 -> 553,128 -> 553,130 -> 555,130 -> 555,128 -> 555,130 -> 557,130 -> 557,126 -> 557,130 -> 559,130 -> 559,125 -> 559,130
            496,23 -> 496,16 -> 496,23 -> 498,23 -> 498,16 -> 498,23 -> 500,23 -> 500,17 -> 500,23 -> 502,23 -> 502,16 -> 502,23
            558,148 -> 562,148
            513,58 -> 518,58
            564,108 -> 564,107 -> 564,108 -> 566,108 -> 566,101 -> 566,108 -> 568,108 -> 568,100 -> 568,108
            558,117 -> 562,117
            557,83 -> 557,85 -> 550,85 -> 550,89 -> 569,89 -> 569,85 -> 561,85 -> 561,83
            546,79 -> 546,80 -> 558,80
            541,130 -> 541,122 -> 541,130 -> 543,130 -> 543,123 -> 543,130 -> 545,130 -> 545,124 -> 545,130 -> 547,130 -> 547,122 -> 547,130 -> 549,130 -> 549,120 -> 549,130 -> 551,130 -> 551,121 -> 551,130 -> 553,130 -> 553,128 -> 553,130 -> 555,130 -> 555,128 -> 555,130 -> 557,130 -> 557,126 -> 557,130 -> 559,130 -> 559,125 -> 559,130
            541,130 -> 541,122 -> 541,130 -> 543,130 -> 543,123 -> 543,130 -> 545,130 -> 545,124 -> 545,130 -> 547,130 -> 547,122 -> 547,130 -> 549,130 -> 549,120 -> 549,130 -> 551,130 -> 551,121 -> 551,130 -> 553,130 -> 553,128 -> 553,130 -> 555,130 -> 555,128 -> 555,130 -> 557,130 -> 557,126 -> 557,130 -> 559,130 -> 559,125 -> 559,130
            536,75 -> 536,76 -> 547,76 -> 547,75
            500,62 -> 505,62
            506,39 -> 506,43 -> 499,43 -> 499,51 -> 514,51 -> 514,43 -> 510,43 -> 510,39
            556,151 -> 556,153 -> 548,153 -> 548,157 -> 561,157 -> 561,153 -> 560,153 -> 560,151
            520,58 -> 525,58
            535,67 -> 540,67
            574,133 -> 574,137 -> 567,137 -> 567,141 -> 584,141 -> 584,137 -> 576,137 -> 576,133
            497,36 -> 497,30 -> 497,36 -> 499,36 -> 499,27 -> 499,36 -> 501,36 -> 501,33 -> 501,36 -> 503,36 -> 503,27 -> 503,36 -> 505,36 -> 505,28 -> 505,36 -> 507,36 -> 507,35 -> 507,36
            541,130 -> 541,122 -> 541,130 -> 543,130 -> 543,123 -> 543,130 -> 545,130 -> 545,124 -> 545,130 -> 547,130 -> 547,122 -> 547,130 -> 549,130 -> 549,120 -> 549,130 -> 551,130 -> 551,121 -> 551,130 -> 553,130 -> 553,128 -> 553,130 -> 555,130 -> 555,128 -> 555,130 -> 557,130 -> 557,126 -> 557,130 -> 559,130 -> 559,125 -> 559,130
            546,79 -> 546,80 -> 558,80
            564,108 -> 564,107 -> 564,108 -> 566,108 -> 566,101 -> 566,108 -> 568,108 -> 568,100 -> 568,108
            543,72 -> 543,73 -> 554,73 -> 554,72
            564,117 -> 568,117
            497,36 -> 497,30 -> 497,36 -> 499,36 -> 499,27 -> 499,36 -> 501,36 -> 501,33 -> 501,36 -> 503,36 -> 503,27 -> 503,36 -> 505,36 -> 505,28 -> 505,36 -> 507,36 -> 507,35 -> 507,36
            497,36 -> 497,30 -> 497,36 -> 499,36 -> 499,27 -> 499,36 -> 501,36 -> 501,33 -> 501,36 -> 503,36 -> 503,27 -> 503,36 -> 505,36 -> 505,28 -> 505,36 -> 507,36 -> 507,35 -> 507,36
            536,75 -> 536,76 -> 547,76 -> 547,75
            557,83 -> 557,85 -> 550,85 -> 550,89 -> 569,89 -> 569,85 -> 561,85 -> 561,83
            496,23 -> 496,16 -> 496,23 -> 498,23 -> 498,16 -> 498,23 -> 500,23 -> 500,17 -> 500,23 -> 502,23 -> 502,16 -> 502,23
            541,130 -> 541,122 -> 541,130 -> 543,130 -> 543,123 -> 543,130 -> 545,130 -> 545,124 -> 545,130 -> 547,130 -> 547,122 -> 547,130 -> 549,130 -> 549,120 -> 549,130 -> 551,130 -> 551,121 -> 551,130 -> 553,130 -> 553,128 -> 553,130 -> 555,130 -> 555,128 -> 555,130 -> 557,130 -> 557,126 -> 557,130 -> 559,130 -> 559,125 -> 559,130
            539,69 -> 544,69
            552,117 -> 556,117
            556,151 -> 556,153 -> 548,153 -> 548,157 -> 561,157 -> 561,153 -> 560,153 -> 560,151
            496,23 -> 496,16 -> 496,23 -> 498,23 -> 498,16 -> 498,23 -> 500,23 -> 500,17 -> 500,23 -> 502,23 -> 502,16 -> 502,23
            561,146 -> 565,146
            570,148 -> 574,148
            496,23 -> 496,16 -> 496,23 -> 498,23 -> 498,16 -> 498,23 -> 500,23 -> 500,17 -> 500,23 -> 502,23 -> 502,16 -> 502,23
            496,23 -> 496,16 -> 496,23 -> 498,23 -> 498,16 -> 498,23 -> 500,23 -> 500,17 -> 500,23 -> 502,23 -> 502,16 -> 502,23
            532,69 -> 537,69
            564,108 -> 564,107 -> 564,108 -> 566,108 -> 566,101 -> 566,108 -> 568,108 -> 568,100 -> 568,108
            512,54 -> 517,54
            541,130 -> 541,122 -> 541,130 -> 543,130 -> 543,123 -> 543,130 -> 545,130 -> 545,124 -> 545,130 -> 547,130 -> 547,122 -> 547,130 -> 549,130 -> 549,120 -> 549,130 -> 551,130 -> 551,121 -> 551,130 -> 553,130 -> 553,128 -> 553,130 -> 555,130 -> 555,128 -> 555,130 -> 557,130 -> 557,126 -> 557,130 -> 559,130 -> 559,125 -> 559,130
            541,130 -> 541,122 -> 541,130 -> 543,130 -> 543,123 -> 543,130 -> 545,130 -> 545,124 -> 545,130 -> 547,130 -> 547,122 -> 547,130 -> 549,130 -> 549,120 -> 549,130 -> 551,130 -> 551,121 -> 551,130 -> 553,130 -> 553,128 -> 553,130 -> 555,130 -> 555,128 -> 555,130 -> 557,130 -> 557,126 -> 557,130 -> 559,130 -> 559,125 -> 559,130
            517,60 -> 522,60
            564,148 -> 568,148
            506,58 -> 511,58
            541,130 -> 541,122 -> 541,130 -> 543,130 -> 543,123 -> 543,130 -> 545,130 -> 545,124 -> 545,130 -> 547,130 -> 547,122 -> 547,130 -> 549,130 -> 549,120 -> 549,130 -> 551,130 -> 551,121 -> 551,130 -> 553,130 -> 553,128 -> 553,130 -> 555,130 -> 555,128 -> 555,130 -> 557,130 -> 557,126 -> 557,130 -> 559,130 -> 559,125 -> 559,130
            541,130 -> 541,122 -> 541,130 -> 543,130 -> 543,123 -> 543,130 -> 545,130 -> 545,124 -> 545,130 -> 547,130 -> 547,122 -> 547,130 -> 549,130 -> 549,120 -> 549,130 -> 551,130 -> 551,121 -> 551,130 -> 553,130 -> 553,128 -> 553,130 -> 555,130 -> 555,128 -> 555,130 -> 557,130 -> 557,126 -> 557,130 -> 559,130 -> 559,125 -> 559,130
            """;
}
