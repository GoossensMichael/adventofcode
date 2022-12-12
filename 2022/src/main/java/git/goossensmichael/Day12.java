package git.goossensmichael;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.OptionalInt;
import java.util.logging.Level;
import java.util.logging.Logger;

public class Day12 {

    private static final Logger LOGGER = Logger.getLogger(Day12.class.getName());

    private static List<Coord> part1(final HeightMap heightMap) {
        return part1(heightMap, heightMap.start());
    }

    private static List<Coord> part1(final HeightMap heightMap, final Coord startingPoint) {
        final List<Coord> partialPath = new ArrayList<>();
        partialPath.add(startingPoint);

        final Map<Coord, List<Coord>> pathsByCoord = new HashMap<>();
        pathsByCoord.put(startingPoint, partialPath);

        final Map<Integer, List<List<Coord>>> pathsByLength = new HashMap<>();
        final List<List<Coord>> paths = new ArrayList<>();
        paths.add(partialPath);
        pathsByLength.put(1, paths);

        return part1(heightMap, pathsByCoord, pathsByLength);
    }

    private static List<Coord> part1(final HeightMap heightMap, final Map<Coord, List<Coord>> pathsByCoord, final Map<Integer, List<List<Coord>>> pathsByLength) {
        while (!pathsByCoord.containsKey(heightMap.end())) {
            final OptionalInt min = pathsByLength.keySet().stream().mapToInt(Integer::intValue).min();
            if (min.isEmpty()) {
                return Collections.emptyList();
            }

            final int shortestPathLength = min.getAsInt();
            final List<List<Coord>> shortestPaths = pathsByLength.get(shortestPathLength);

            // Selecting a working path.
            final List<Coord> aShortPath = shortestPaths.remove(0);
            if (shortestPaths.isEmpty()) {
                pathsByLength.remove(shortestPathLength);
            }

            // Put all possible paths on the maps from this point.
            final List<Coord> possibleDirections = possibleDirectionsFrom(aShortPath.get(shortestPathLength - 1), heightMap);
            possibleDirections.stream()
                    // There is no shorter path to the possible next coordinate that is already shorter
                    .filter(coord -> !pathsByCoord.containsKey(coord) || (pathsByCoord.get(coord).size() > shortestPathLength + 1))
                    .forEach(coord -> {
                        final List<Coord> pathExtension = new ArrayList<>(aShortPath);
                        pathExtension.add(coord);

                        pathsByCoord.put(coord, pathExtension);
                        if (pathsByLength.containsKey(pathExtension.size())) {
                            pathsByLength.get(pathExtension.size()).add(pathExtension);
                        } else {
                            final List<List<Coord>> newPathsByLength = new ArrayList<>();
                            newPathsByLength.add(pathExtension);
                            pathsByLength.put(pathExtension.size(), newPathsByLength);
                        }
                    });
        }

        return pathsByCoord.get(heightMap.end());
    }

    private static List<Coord> possibleDirectionsFrom(final Coord current, final HeightMap heightMap) {
        final List<Coord> coords = new ArrayList<>(4);

        addIfNotTooSteep(coords, heightMap, current, 1, 0);
        addIfNotTooSteep(coords, heightMap, current, 0, 1);
        addIfNotTooSteep(coords, heightMap, current, -1, 0);
        addIfNotTooSteep(coords, heightMap, current, 0, -1);

        return coords;
    }

    private static void addIfNotTooSteep(final List<Coord> coords, final HeightMap heightMap, final Coord current, final int x, final int y) {
        final int newX = current.x() + x;
        final int newY = current.y() + y;

        if (newX >= 0 && newX < heightMap.map().length &&
            newY >= 0 && newY < heightMap.map()[0].length &&
                (heightMap.map()[current.x()][current.y()] + 1 >= heightMap.map()[newX][newY])) {
            coords.add(new Coord(newX, newY, heightMap.map()[newX][newY]));
        }
    }

    private static int part2(final HeightMap heightMap) {
        int shortestPathLength = Integer.MAX_VALUE;
        int amount = 0;
        for (int i = 0; i < heightMap.map().length; i++) {
            for (int j = 0; j < heightMap.map().length; j++) {
                if (heightMap.map()[i][j] == 'a') {
                    final List<Coord> shortestPath = part1(heightMap, new Coord(i, j, 'a'));
                    if (shortestPath.size() > 0 && shortestPath.size() - 1 < shortestPathLength) {
                        shortestPathLength = shortestPath.size() - 1;
                    }
                }
            }
        }

        return shortestPathLength;
    }


    public static void main(final String[] args) {

        // Parsing input
        final var testInput = parseInput(TST_INPUT.split("\n"));
        final var input = parseInput(INPUT.split("\n"));

        {
            final var expectedResult = 31;
            final var part1 = part1(testInput);
            final var testResult = part1.size() - 1;
            LOGGER.log(Level.INFO, () -> String.format("Test part 1: %d", testResult));

            if (expectedResult == testResult) {
                final var result = part1(input).size() - 1;
                LOGGER.log(Level.INFO, () -> String.format("Part 1: %d", result));
            }
        }

        {
            final var expectedResult = 29;
            final var testResult = part2(testInput);
            LOGGER.log(Level.INFO, () -> String.format("Test part 2: %d", testResult));

            if (expectedResult == testResult) {
                final var result = part2(input);
                LOGGER.log(Level.INFO, () -> String.format("Part 2: %d", result));
            }
        }
    }

    private static HeightMap parseInput(final String[] split) {
        final int xDim = split.length;
        final int yDim = split[0].length();

        Coord start = null;
        Coord end = null;
        final char[][] map = new char[xDim][yDim];
        for (int i = 0; i < xDim; i++) {
            for (int j = 0; j < yDim; j++) {
                final char slope = split[i].charAt(j);
                if (slope == 'S') {
                    start = new Coord(i, j, 'a');
                    map[i][j] = 'a';
                } else if (slope == 'E') {
                    end = new Coord(i, j, 'z');
                    map[i][j] = 'z';
                } else {
                    map[i][j] = slope;
                }
            }
        }

        return new HeightMap(start, end, map);
    }

    private record Coord(int x, int y, char c) {}
    private record HeightMap(Coord start, Coord end, char[][] map) {}

    private static final String TST_INPUT = """
            Sabqponm
            abcryxxl
            accszExk
            acctuvwj
            abdefghi
            """;

    private static final String INPUT = """
            abccccccccccccccccccccaaaaaaaacccccccccccccaacaaaaacccccccccccccccccccccaaaaaacccccaaaaaccccccccccccccccccccaaaccccccccccccccccccccccccccccccccccccccccccccccccccccccccccaaaa
            abcccccccccccccccccccaaaaaaaaacccccccccccccaaaaaaaaccccccccccccccaacccccaaaaaaaaaaaaaaaaccccccccccccccccccccaaaccccccccccccccccccccccccaccaccccccccccccccccccccccccccccaaaaaa
            abccccccccccccccccccaaaaaaaaaacccccaacccccccaaaaaccccccccccccccaaaaaacccaaaaaaaaaaaaaaaaccccccccccccccccccaacaaaaacccccccccccccccccccccaaaaccccccccccccccccccccccccccccaaaaaa
            abccccccccccaaacaaacaaacaaacccccacaaaccccccccaaaaacccccccccccccaaaaaacaaaaaaaaaaaaaaaaaaccccccccccccccccccaaaaaaaaccccccccccccccccccccaaaaaccccccccaaaccccaaaccccccccccaaacaa
            abccccccccaaaaaccaaaaaccaaaccccaaaaaaaacccccaaacaaccccccccccccccaaaaccaaaaaaaaccccaaaaaacccccccccccccccccccaaaaaccccccccccccccccccccccaaaaaacccccccaaaacccaaaccccccccccccccaa
            abccccccccaaaaaaccaaaaaaaaaaaccaaaaaaaaccccccaacccccccccccccccccaaaacaaaacaaaacccccaaacccccccaacaaccccccccccaaaaacccaaccccccccccccccccaaaaaaccccccccaaaaaaaacccccccccccccccaa
            abccccccccaaaaaaaaaaaaaaaaaaacccaaaaaaccccccccccccccccccccccccccaccaccccccaaaaaccccccccccccccaaaaacccccccccaaacaacaaaaaaccccccccccccccccaacccccccckkkkkkaaaaccccccccccccccccc
            abccccccccaaaaacaaaaaccaaaaaaaacaaaaaccccccccccccccccccccccccccccccccccccccaaaccccccccccccccccaaaaacccccccccaaccccaaaaaaccccccccccccccccccccccccckkkkkkklaaccccccccaacccccccc
            abccccccccaaaaacaacaaacaaaaaaaacaaaaaacccccccccccccccccccccaacccccccccccccccaaaccccccccccccccaaaaaacccccccccccccccaaaaaacccccccccccccccccccccccckkkkkkkklllccccccccccaaaacccc
            abcaaccccccccccccccaaaccaaaaaaaccccaaccccccccccccccccccaaccaacccccccccccccccccccaacccccccccccaaaacccccccccccccccccaaaaacccccccaaaacccccccccccccckkkoppppllllllccccccccaaccccc
            abcaacccccccccccccccccccaaaaaccccccccccccccccccccccccccaaaaaacccccccccccccccccaaaaaacccccccccccaaccccccccccccccccccaaaacccccccaaaaacccccccccccckkkooppppplllllllllccccdaccccc
            abaaaccccccaaacccccccccaaaaaaccccccccaaaccccccccccccccccaaaaaaacccccccccccccccaaaaaacccccccccccccccccccccccccccccccccccccccccaaaaaaccccccccccccjkoooopuppplllllllmmmddddacccc
            abaaaaaccccaaaaacccccccccccaaaaacccccaaaaccccccccccccccccaaaaaaccccccccccccccccaaaacccccccccccccccccaaaccccccccccccccccccccccaaaaaacccccccccccjjjooouuuuppppppqqmmmmmdddacccc
            abaaaaacccaaaaaacccaacaaacccaaaaaacccaaaacccccccccccccccaaaaaccccccccccaaccccccaaaaccccccaacccccacccaaccccccccccaacaaccccccccaaaaaacccaaaccccjjjjoouuuuuuppppqqqqqmmmdddacccc
            abaaccacccaaaaaacccaaaaaacccaaaaaacccaaacccccccccccccccaaaaaaccccccccaaaaaaccccaccacccccaaaacccaaaaaaaccccccccccaaaaaccccccccccaacacccaaccccjjjjooouuuxuuupppqqqqqmmmdddccccc
            abaaaccccccaaaaacccaaaaaacccaaaaaccccccccccccccccccccccccccaaccccccccaaaaaacccccccccccccaaaaccccaaaaaaaaccccccccaaaaaaccccccccccccaaaaaaacjjjjjoooouuxxxuuvvvvvvqqqmmdddccccc
            abaaaccccccaacaacccaaaaaaacccaaaaacccccccccccccccaaacccccccccccccccccaaaaaccccaaacccccccaaaaccccaaaaaaaaacccccccaaaaaaccccccccccccaaaaaacjjjjjoooouuuxxxuuvvvvvvqqqmmdddccccc
            abccccccccccccccccaaaaaaaacccaaaaacccccccccccccccaaaccccccccccccccccccaaaaacccaaacacccccccccccccaaaaaaaaacccccccaaaaaccccccaacccccaaaaaaajjjnoooottuuxxxxvyyyvvvqqmmmdddccccc
            abccccccccccccccccaaaaaaaacccccccccccccccccaaaaaaaaaccaaccccccccccccccaaaaacaacaaaaaccccccccccccaaaaaaaaccccccccccaaacccccaaaaccccaaaaaajjjnnnntttttxxxxyyyyyvvvqqmmmdddccccc
            abccccccaaaccccccccccaaacccaaccccccccccccccaaaaaaaaaaaaaaaccccccccaaacccccccaaaaaaaacccccccccccaaaaaaaaccccccccccccaacccccaaaaacacaaaaaaiiinnntttxxxxxxxyyyyyvvqqqmmdddcccccc
            SbccccccaaaaaccccccccaaccccaaaccccccccccccccaaaaaaaaaaaaaacccccccaaaaaaccccccaaaaaccccccccacccaaaccaaacccccccccaaacaaccaaaaaaaaaaaaaaaaaiiinnntttxxxEzzzzyyyvvqqqmmmeeecccccc
            abcccccaaaaaacccccccccccaaaaaaaaccccccccccccaaaaaaaaaaaaaccccccccaaaaaacccccccaaaaacccccccaacaaaccccaaacccccccccaaaaaccaaaaaaaaaacaccaaaiiinnntttxxxxxyyyyyvvvqqqnnneeecccccc
            abaacccaaaaaacccccccccccaaaaaaaaccccaaaccccaaaaaaaaaaaaaaacccccccaaaaaccccccccaacaaaaaccccaaaaacccccccccccccccccaaaaaaaccaaaaaacccccccaaiiinnnttttxxxxyyyyyyvvvrrnnneeecccccc
            abaaaaaaaaaaaccccccccccccaaaaaaccccaaaacccaaaaaaaaaaaaacaaccccccccaaaaacccccccaaccccaaaacccaaaaaacaacaaccccccccaaaaaaaaccaaaaaaccccccccciiiinnnttttxxwyywyyyyvvrrrnneeecccccc
            abaaaaacaacaaccccccccccccaaaaaaccccaaaacccaaacaaaacaaaccccccccccccaacaaccccaacccccaaaaaacaaaaaaaacaaaaacccccaaaaaaaaaaaccaaaaaacccccccccciiiinnnttttwwyywwywwwvrrrnneeecccccc
            abaaaacccccccccccccccccccaaaaaacccccaaacccccccaaacccacaaccccccccccccccccccaaccccccaaaaaccaaaaaaaaaaaaaccccccaaaaaaaaacccaaaaaaaacccccccccciiinnnnntswwywwwwwwwwrrrnnneecccccc
            abaaaaaccccccccccccccccccaacaaacccccccccccccccaacaaacaaacccccccccccccccaaaaacaaccccaaaaaccccaacccaaaaaacccccaaacaaaaaccccacccccccaaacccccciiiiinnmsswwwwwswwwwrrrrnnneecccccc
            abaaaaaaccaaaccccccccccccccccccccccccccccccccccccaaaaaaaccccccccaaaccccaaaaaaaaccccaaccaccccaacccccaaaaccaaaaaaaaaaccccccccccccccaaaaacccccciihmmmsswwwwssrrrrrrrrnnneecccccc
            abaaccaacaaaaccccccccccccccccccccccccccccccccccccaaaaaacccccccccaaaaaccccaaaaacccccccccccccccccccccacccccaaaaaaaaacccccccaaccaacaaaaacccccccchhhmmssswwsssrrrrrrrnnneeecccccc
            abaacccccaaaacccccccccccccccccccccccccccccccccccccaaaaaaaacccccaaaaaacccaaaaacccccccccccccccccccccccccccccaaaaaaaccccccccaaaaaacaaaaacccccccchhhmmssssssslllllllnnnnfeecccccc
            abccccccccaaacccccccccccccccaaccccccccccccccaaaccaaaaaaaaacccccaaaaaacccaacaaaccccccccccccccccccccccccaacccaaaaaaccccccccaaaaacccaaaaaccccccchhhmmmssssslllllllllnnfffeaacccc
            abcccccccccccccccccccccccacaaaccccccccccccccaaaaaaaaaaaaaaccaaccaaaaaccccccaaccaacccccccccccccccccccccaaaccaaaaaaacccccccaaaaaaccaaccccccccccchhhmmmmsmllllllllllfffffaaacccc
            abccccccccccccccccccccccaaaaaaaaccccccccccccaaaaaaacaaacaaaaaaccaaaaccccccccccaaaacccccccccccccccccaaaaaaaaaaacaaaccccccaaaaaaaacccccccccccccchhhmmmmmmlllggfffffffffaacccccc
            abccccccccccccccccccccccaaaaaaaaccccccccccccaaacccccaaacaaaaacccccccccccaaacccaaaacccccccccccccccccaaaaaaaaaacccccccccccaaaaaaaaccccccccccccccchhhmmmmmlggggffffffffaaacccccc
            abccccccccccccccccaaaacccaaaaaacccccccccccccccccccccaaacaaaaaaccccccccccaaacccaaaaccccccacccccccccccaaaaaacccccccccccccccccaacccaaccccccccccccchhhhgmgggggggffaccccccaacccccc
            abccccccccccccccccaaaacccaaaaacccccccccccccccccccccccccaaaaaaaacccccccccaaaaccaaacccccccaaacaaacccccaaaaaacccccccccccccccccaaccaaccccccccccccccchhhgggggggaaaaacccccccccccccc
            abccccccccccccccccaaaacccaaaaaaccccccccccccccccccccccccaaaaaaaaccccccccccaaaaaaaaaccccccaaaaaaacccccaaaaaaccccccccccccccccccaaaaacaaccccccccccccchggggggaacccccccccccccccccca
            abcccccccccccccccccaacccccccaaccccccccccccccccccccccccccccaacaccaaacccaacaaaaaaaacccccccaaaaaaccccccaaccaacaaaccacccccaaccccaaaaaaaaccccccccccccccccccaaaccccccccccccccccccca
            abcccccaaccccccccccccccccaaccccccccaacccccccccaaacccccccccaacaaaaaacccaaacaaaaaacccccccaaaaaaacccccccccccccaaaaaacccaaaaccccccaaaaaccccaaacccccccccccccaaccccccccccccccaaaaaa
            abccccaaaacccccccccccccccaaaacccaaaaccccccccccaaaacccccccccccaaaaaaccaaaaaaaaaacccccccaaaaaaaaaaccccccccccccaaaaacccaaaaaacccaaaaacccccaaaacccccccccccaaccccccccccccccccaaaaa
            abccccaaaacccccccccccccaaaaaacccaaaaaaccccccccaaaaccccccccccaaaaaaaacaaaaaaaaaaaaaccccaaaaaaaaaaccccccccccaaaaaaaacccaaaaccccaacaaaccccaaaacccccccccccccccccccccccccccccaaaaa
            """;
}
