package git.goossensmichael;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;

public class Day24 {

    private static final Logger LOGGER = Logger.getLogger(Day24.class.getName());

    private static final char BLIZZARD_LEFT = '<';
    private static final char BLIZZARD_RIGHT = '>';
    private static final char BLIZZARD_UP = '^';
    private static final char BLIZZARD_DOWN = 'v';
    private static final char EMPTY = '.';

    private static long part1(final String[] input) {
        final String[][] map = parseMap(input);

        final Coord start = new Coord(0, 1);
        final Coord end = new Coord(map.length - 2, map[0].length - 2);
        final Solution toEnd = solve(map, start, end);

        return toEnd.time;
    }

    private record Solution(String[][] map, int time) {}

    private static Solution solve(final String[][] map, final Coord start, final Coord end) {
        // At this point the cycle of blizzards will repeat itself. No need to calculate more maps.
        final int mapCycle = (map.length - 2) * (map[0].length - 2);

        int time = 0;

        final Map<Integer, String[][]> mapsInTime = new HashMap<>();
        mapsInTime.put(time, map);

        final Map<Integer, Set<Coord>> coordsInTime = new HashMap<>();
        coordsInTime.put(time, Set.of(start));

        boolean searching = true;
        while (searching) {
            time++;

            final String[][] nextMap;
            if (time / mapCycle == 0) {
                nextMap = nextMap(mapsInTime.get(time - 1));
            } else {
                nextMap = mapsInTime.get(time % mapCycle);
            }
            mapsInTime.put(time, nextMap);

            final Set<Coord> newCoords = findPossibilities(coordsInTime.get(time - 1), nextMap);
            if (newCoords.contains(end)) {
                searching = false;
            } else {
                coordsInTime.put(time, newCoords);
            }
        }

        return new Solution(nextMap(mapsInTime.get(time)), time + 1);
    }

    private static void printMap(final String[][] map) {
        for (int i = 0; i < map.length; i++) {
            for (int j = 0; j < map[i].length; j++) {
                if (map[i][j] == null) {
                    System.out.print('.');
                } else if (map[i][j].length() == 1) {
                    System.out.print(map[i][j]);
                } else {
                    System.out.print(map[i][j].length());
                }
            }
            System.out.println();
        }
        System.out.println();
    }

    private static Set<Coord> findPossibilities(final Set<Coord> coords, final String[][] nextMap) {
        return coords.stream()
                .flatMap(coord -> {
                    final Set<Coord> possibilities = new HashSet<>();
                    // Stay put at the current location. It might be more convenient to wait for the next opportunity.
                    if (nextMap[coord.x][coord.y] == null) {
                        possibilities.add(coord);
                    }

                    // Stay put at the current location. It might be more convenient to wait for the next opportunity.
                    // But only if possible as your current spot might become a blizzard.
                    addWhenPossible(possibilities, new Coord(coord.x, coord.y), nextMap);
                    // All possible directions.
                    addWhenPossible(possibilities, new Coord(coord.x - 1, coord.y), nextMap);
                    addWhenPossible(possibilities, new Coord(coord.x, coord.y + 1), nextMap);
                    addWhenPossible(possibilities, new Coord(coord.x + 1, coord.y), nextMap);
                    addWhenPossible(possibilities, new Coord(coord.x, coord.y - 1), nextMap);

                    return possibilities.stream();
                })
                .collect(Collectors.toSet());
    }

    private static void addWhenPossible(final Set<Coord> possibilities, final Coord coord, final String[][] nextMap) {
        // Must not be out of bounds or in the wall.
        if (coord.x > 0 && coord.x < nextMap.length - 1 && coord.y > 0 && coord.y < nextMap[0].length) {
            // The spot is free.
            if (nextMap[coord.x][coord.y] == null) {
                possibilities.add(coord);
            }
        } else if ((coord.x == 0 && coord.y == 1) || (coord.x == nextMap.length - 1 && coord.y == nextMap[0].length - 2)){
            // Always add the possibility to stay on the initial position.
            possibilities.add(coord);
        }

    }


    private static String[][] nextMap(final String[][] map) {
        final String[][] nextMap = new String[map.length][map[0].length];
        for (int i = 0; i < map.length; i++) {
            for (int j = 0; j < map[0].length; j++) {
                if (map[i][j] != null) {
                    for (char c : map[i][j].toCharArray()) {
                        switch (c) {
                            case '#' -> nextMap[i][j] = "" + c;
                            case BLIZZARD_UP -> {
                                if (i == 1) {
                                    addToMap(nextMap, map.length - 2, j, c);
                                } else {
                                    addToMap(nextMap, i - 1, j, c);
                                }
                            }
                            case BLIZZARD_RIGHT -> {
                                if (j == map[i].length - 2) {
                                    addToMap(nextMap, i, 1, c);
                                } else {
                                    addToMap(nextMap, i, j + 1, c);
                                }
                            }
                            case BLIZZARD_DOWN -> {
                                if (i == map.length - 2) {
                                    addToMap(nextMap, 1, j, c);
                                } else {
                                    addToMap(nextMap, i + 1, j, c);
                                }
                            }
                            case BLIZZARD_LEFT -> {
                                if (j == 1) {
                                    addToMap(nextMap, i, map[i].length - 2, c);
                                } else {
                                    addToMap(nextMap, i, j - 1, c);
                                }
                            }
                            default -> throw new IllegalArgumentException("Did not expect character " + c + " on the map.");
                        }
                    }
                }
            }
        }

        return nextMap;
    }

    private static void addToMap(final String[][] nextMap, final int i, final int j, final char c) {
        if (nextMap[i][j] == null) {
            nextMap[i][j] = "" + c;
        } else {
            nextMap[i][j] += c;
        }
    }

    private static String[][] parseMap(final String[] input) {
        final String[][] map = new String[input.length][input[0].length()];
        for (int i = 0; i < input.length; i++) {
            for (int j = 0; j < input[i].length(); j++) {
                if (input[i].charAt(j) != EMPTY) {
                    map[i][j] = "" + input[i].charAt(j);
                }
            }
        }

        return map;
    }

    private static long part2(final String[] input) {
        final String[][] map = parseMap(input);

        final Coord start = new Coord(0, 1);
        final Coord end = new Coord(map.length - 2, map[0].length - 2);

        final Coord secondStart = new Coord(map.length - 1, map[0].length - 2);
        final Coord secondEnd = new Coord(1, 1);

        final Solution toEnd = solve(map, start, end);
        final Solution backToStart = solve(toEnd.map, secondStart, secondEnd);
        final Solution backToEnd = solve(backToStart.map, start, end);

        return toEnd.time + backToStart.time + backToEnd.time;
    }

    private record Coord(int x, int y) {}

    public static void main(final String[] args) {

        // Parsing input
        final var testInput = TST_INPUT.split("\n");
        final var input = INPUT.split("\n");

        {
            final var expectedResult = 18;
            final var part1 = part1(testInput);
            LOGGER.log(Level.INFO, () -> String.format("Test part 1: %d", part1));

            if (expectedResult == part1) {
                final var result = part1(input);
                LOGGER.log(Level.INFO, () -> String.format("Part 1: %d", result));
            }
        }

        {
            final var expectedResult = 54;
            final var testResult = part2(testInput);
            LOGGER.log(Level.INFO, () -> String.format("Test part 2: %d", testResult));

            if (expectedResult == testResult) {
                final var result = part2(input);
                LOGGER.log(Level.INFO, () -> String.format("Part 2: %d", result));
            }
        }
    }
    private static final String TST_INPUT = """
            #.######
            #>>.<^<#
            #.<..<<#
            #>v.><>#
            #<^v^^>#
            ######.#
            """;

    private static final String INPUT = """
            #.####################################################################################################
            #<v>v>v><>^<>^.^vv<>><>>v.>v<.v>>v<<v<.^<>>>v<v^>.^<^>v>^<^v.^>v^>>><v<>v^<v<<^^.vv>.^>v>v<.^v<><v<>>#
            #>>v<^<>vvv.>^.v^^>^<<vv>>v.<^<>v^<v^>v^<>>.v.^<<v^.^v^v><<.^vv<^<^v>.vvv<v>.<>>^>.<<<<<<.<^^<v.<>.v<#
            #<<<>^^>>>>.v^>v<>^>^^v>v^^v..v^.<<^>^>.>v^^vv.<>>^><<>v.<v<^^<<v^v<.>^>v<>^^<<>v<<^^><vv>vv<<^^...^>#
            #>>v><><<<^<.>v^<<v^>v^^<>>v<<>>.<>v><^<^<.^<><^^^vv>><v<v>vv>^>v^>>vv>><<v^<><>^v.vv>^<^<>v<vvvv^v><#
            #<^<^>v^^<>>^<.^^>^vv><v<^^^.v^>><v^>v^.>>v^<..>>.v>v<v^^<.v<<^v<<>v.^>><>v<<>.^<<<<<>>>^<><v>><^>>v>#
            #>v>^^^v<v^.>^>^>v>>>v>v<v^v^<v>>><<>v..>>vvv<^><<>>^^.>v^<^>>.^v><v<^<.^>>vv<v^<^vv^<<<^><v.>v<^<.v>#
            #>v^>>v^<^<<^<v>..<.v^<.<><>v>v^<.><<<<^><<.<><><><.<.^.>^v^><<<vv.<>vvv<v^v.<^<>>>^>v<^v^<><><v>.^><#
            #<><>>vv..<v^vv><^v^.^^vv.^>^>>v<>vv>^><<<><^vvv>>><>><>>^^<v<><^^v^^>.^>^<.<<^..v<v>v^>^v^v<<.v^<v<>#
            #>><^<>.v.><^.^^..^<v.v><v^<<v>^<><^>^..<vv>><^^>v^>.^><v>v^v>>>>vv<<>><v^<<^^v^vv<.>vv^><v^.^v>...v>#
            #>vv<v<><v>v>v>><^>>.<^<<^<^>^.^v>>vv^vv<^v><>>v^<^v.v^<v<vv^v><.^^>><.v^^><<^<><^>>vv^.><vv^<>>v.><<#
            #<^<^<>v^v^^^<<v<>vv<<<.v<v^v^^><.>^.v.>vvv>^<<<v>^^>.^<<.v<v>^v..v<<<<<^^v^v^<>^>>^.>^<^v<.v..^v^>^>#
            #<<^^<>^^><v>v.<<><<<v<^<^>^<<^v<<>^^<.^^^^<>^>><^>.v^v>v.<.^<<vv^^>>>^v<v<<vv^><<.<>>><.v<^^>>^v>^^<#
            #>v><>..v^>v>^<<^>vv>.^v^v>>v^^^>><><<>v^.vv^.<><v>^^v^>.<^><<^^>>v^.^^^>v.<>^^<^>>^<^<<<^>^<>>^<<v<>#
            #>vv<.vv><>^>>>>.<>.>v^>>v><<<v<.v^v>v^<v^<>>vv><.<><v>^>^<^^v^>v^<><>^^^>^<<^^.>..>.>^>^>>^>v>v<v<><#
            #<>>v^>^<.v^^.^<vv^<>^>^^<^.^.>^<<^>><^vv><.v><^vv^^^^^v><><vv.v.>.^^v^v.>^v.<><v>>v>vv^.vv^^<>^>.><<#
            #>>>>>>>.v<>^<^.>><v^^<v.<^<^>vv.^v>^>.>^>>^>>v^>v>.v.><<^^<^<vv.vv<vvvvv^^v><^^^>>><^>>v<v>>>v<^^.^>#
            #>^^^^<<v^^^...^<v>>^^v^^><<.<><>^v^v^>>>^^>.v<v.v<v^^v<vv>v>vv>^v^<>^vv^vv.^vv>v^v>v>>.<>>><^^<^^>^<#
            #<^^^<v>^.<>^>^<^<v>vv<vv<^vv^^<><v^>v>^.><^^v<>><>vvvv^>^^<<^v><<v^v<><^<<>^.v<><vv<><>>^.>v>vv>><^<#
            #>v^^^^><^<vv<^<v>v.<^.><.^>>^<>v.<<^<>.>vv<<.vv.v>v^.<^<^^>>^.><>v<v<v>v<v>>vv>^<><^<v>>><v>><<vvv^.#
            #>><>><^^<^^>v<^.^>><<v<^v<>v>v^<<<<^^.v>v^.>v^<^><^v<v.>><v<^>^^>^^^^.<v>^>.v^<^^<<><>.<v^v>^^>v<^.<#
            #><v>v^^<>.vvv>>.<..^^.<<^>>v^<>.<v<<vv><v<^v.^>>>v>.<^>.vvv<v<^<^^>^>v>v<>v<^v<vv^>v<<<<<>.^v^^.>><.#
            #<>>.<>^^>>><>>^<><<^^v^><v><<<<v>>^<^vv<<.v^<<<^>v>v^<^.<<<<^<^v^>vv.<.^><v<.^^<^>^.>^v^v>^>^<^.v><.#
            #<^<.<^^<>^v<^<^>^>^.>.<<^.v<>v^^>>..v^v<.>^>^.^^v>^v^>vv>>^<^>>>^>><<v><><<v^<>>v.vv><>.><^.v<^<v<<>#
            #<v^.v<>^^>v>.vv><>^v<^<^.<^>>v^^.><^<v<>v<<vv<>.<>v<v<>.>^^.v.^v><^^.^v<vv<<^<>>v.>^^.>^^><>>>>.^^.<#
            #<>^<>^><.^^^v<^v^>v.>^<>^^v<vv^><v^^>^.><>^v<v>v<>.^><^^>^v<<.>>^^<.v<><vv>vv>>>v^v><v^<^<>v<^>>^<.<#
            #>v^<v^>^>>^<^<^>v<^vvvv<<<vv<.><^<.^^>.>^.v<v<><<.<v>v^vv<>^<v<^<>>.^<>^v<vv<<><<.<^><v<<^><^<><<>^>#
            #<<><..v<^v<>^.v>v^.<v.><>>v>>vv>v^^^><vvv<.>^^<v<vv<<v<.^^^..^<<<^^v^>><>>^v>v><<>^^v..>^<^vv^<v><^<#
            #><>vv<<<.vv>^^<v>^<^<vvv^v<<<<<^v<.>>^><^v>vv><v>>>^^^<v.><>><<v^^.vv.^v><<<<<<^>^v>>.>>v.>v^<<>^v^.#
            #.^>>>vv>vv>^^v>>^.<v>^v.>^>..<v.><^<^>.^<v^<>^v^v^>vv<>v^^><<.^>>^..<^^>><.vv>>v^<v><<^v>>>>v><>.^v>#
            #>^^^>v<><><v>>>^^vv^^^v.v><v<>>^v^v^^>^v.>vv..^>>^v^<.^<<v<>^v<^<..^v>^<^^^v.v^>^^<>v><^<.^<v^.v.v>>#
            #>v>v>>>vvvv<>>v^><>v<v^.^>>v>.v^vv.^^v.^>>>^^^>v.v<^>>v<v<>^^v^^>^<v..v^<^>^v^vv<<v><vv.v^v.vv>v<^.>#
            #><^<>v^>>^><>vv>v^><<v<.>^.v^><<<^v>^>.v<<>^vvvv>.vv^v.><<^<v^.<.vv.vv^^>v^>v>>v^v><<v<^<v<><^v<>^^<#
            #<vv^^>v>v<^>.<>..>>vv^^v^<vv>^.>^<^><v><^>>>><.^>v^<v^>v^<>.<.>.<<^<v>><v>>vvv<>.v^<>vvvv>v<.>^vv^v>#
            #>.<^<<^<>^v.^v^>v^<<>^^>^<.vvvv<^<.v^<>v>><v^^.v^v^<>><.^<v<<>.>v^>^><.>^>>.v^.^..^<v^^v^vv<^v.><<>>#
            #<>>>v<vv.^v>v^.v<<v<><v..v>vv^^v^<vv>><<v<v^>v<.>^.^v^<.>><><>^<>>vv>^>.>v><><^vv>>v>^^<<..><.>.>^.>#
            ####################################################################################################.#
            """;
}
