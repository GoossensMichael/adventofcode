package git.goossensmichael;

import git.goossensmichael.utils.MathUtils;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Stack;
import java.util.logging.Level;
import java.util.logging.Logger;

public class Day22 {

    private static final Logger LOGGER = Logger.getLogger(Day22.class.getName());
    public static final int[] X_Y_NORMAL = {0, 0, 1, 1};

    private static int part1(final String[] input) {
        input[1] = input[1].replace("\n", "");
        final char[][] map = parseMap(input[0].split("\n"));

        Position current = findFirstPosition(map);
        Direction direction = new Direction(0, 1);
        int i = 0;
        while (i < input[1].length()) {
            // Perform the moves first
            final int endOfNextDigit = endOfNextDigit(i, input[1]);
            int moves = Integer.parseInt(input[1].substring(i, endOfNextDigit));
            boolean move = true;
            for (int m = moves; m > 0 && move; m--) {
                final Position possibleNextPosition = current.move(direction, map);
                if (map[possibleNextPosition.row()][possibleNextPosition.col()] == '#') {
                    move = false;
                } else {
                    current = possibleNextPosition;
                }
            }
            i = endOfNextDigit;

            // Now the rotation
            if (i < input[1].length()) {
                direction = direction.rotate(input[1].charAt(i++));
            }
        }

        return 1000 * (current.row() + 1) + 4 * (current.col() + 1) + direction.value();
    }

    private static int endOfNextDigit(final int i, final String instructions) {
        int end = i + 1;
        while (end < instructions.length() && Character.isDigit(instructions.charAt(end))) {
            end++;
        }

        return end;
    }

    private static Position findFirstPosition(final char[][] map) {
        for (int row = 0; row < map.length; row++) {
            for (int col = 0; col < map[0].length; col++) {
                if (map[row][col] == '.') {
                    return new Position(row, col);
                }
            }
        }

        throw new IllegalStateException();
    }

    private static char[][] parseMap(final String[] split) {
        final int rows = split.length;
        final int cols = split[0].length();

        final char[][] map = new char[rows][cols];

        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < cols; j++) {
                if (j < split[i].length()) {
                    map[i][j] = split[i].charAt(j);
                } else {
                    map[i][j] = ' ';
                }
            }
        }

        return map;
    }

    private record Position(int row, int col) {

        public Position move(final Direction direction, final char[][] map) {
            boolean move = true;
            Position p = new Position(row + direction.x(), col + direction.y());
            while (move) {
                if (p.row() < 0) {
                    p = new Position(map.length - 1, p.col);
                } else if (p.row() >= map.length) {
                    p = new Position(0, p.col);
                } else if (p.col() < 0) {
                    p = new Position(p.row, map[0].length - 1);
                } else if (p.col() >= map[0].length) {
                    p = new Position(p.row, 0);
                } else if (map[p.row()][p.col()] == ' ') {
                    p = new Position(p.row + direction.x(), p.col + direction.y());
                } else {
                    move = false;
                }
            }

            return p;
        }

    }

    private record Direction(int x, int y) {

        public Direction {
            if (Math.abs(x) > 1 || Math.abs(y) > 1 || (x != 0 && y != 0)) {
                throw new IllegalArgumentException();
            }

        }

        public Direction rotate(final char rotation) {
            final Direction changedDirection;
            if (rotation == 'L') {
                changedDirection = new Direction(-y, x);
            } else if (rotation == 'R') {
                changedDirection = new Direction(y, -x);
            } else {
                throw new IllegalArgumentException(String.format("Illegal rotation: '%s'", rotation));
            }

            return changedDirection;
        }

        public int value() {
            final int value;
            if (y == 1) {
                value = 0;
            } else if (x == 1) {
                value = 1;
            } else if (y == -1) {
                value = 2;
            } else if (x == -1){
                value = 3;
            } else {
                throw new IllegalStateException();
            }

            return value;
        }

    }

    private static long part2(final String[] input) {
        final String[] map = input[0].split("\n");

        final int xDim = map.length;
        final int yDim = Arrays.stream(map).mapToInt(String::length).max().orElseThrow();

        final int gcd = MathUtils.gcd(xDim, yDim);
        final int divider = (xDim / gcd) * (yDim / gcd);
        final int s = (int) Math.sqrt(xDim * yDim / divider);

        final Cubelet[][][] cube = new Cubelet[s][s][s];
        fillCube(cube, s, map);

        return solve(cube, input[1].replace("\n", ""));
    }

    private static int solve(final Cubelet[][][] cube, final String instructions) {
        Step step = new Step(findFirstPosition(cube), new int[] { 0, 1, 0, 1 }, new int[] { 0, 0, 1 });
        Step previousStep = null;

        boolean solve = true;
        int i = 0;
        while (solve) {
            int startSteps = i;
            while (i < instructions.length() && Character.isDigit(instructions.charAt(i))) {
                i++;
            }
            int steps = Integer.parseInt(instructions.substring(startSteps, i));

            boolean moving = true;
            while (moving) {
                final Step newStep = nextStep(step, cube);
                if (newStep == null) {
                    moving = false;
                } else {
                    previousStep = step;
                    step = newStep;
                    steps--;
                }

                if (steps <= 0) {
                    moving = false;
                }
            }

            if (i < instructions.length()) {
                final char r = instructions.charAt(i++);
                step = new Step(step.position, rotate(r, step.direction, step.normal), step.normal);
            }

            if (i >= instructions.length()) {
                solve = false;
            }
        }

        final Cubelet cubelet = cube[step.position[0]][step.position[1]][step.position[2]];
        final Cubelet.CubeFace cubeFace = cubelet.getFaces().get(new Face(step.normal[0], step.normal[1], step.normal[2]));
        final int faceValue = determineFaceValue(step, previousStep, cube);

        return (1000 * (cubeFace.coord.x + 1)) + (4 * (cubeFace.coord.y + 1)) + faceValue;
    }

    private static int[] rotate(final char r, final int[] direction, final int[] normal) {
        final int[] cross = MathUtils.cross(direction, normal);

        final int[] newDirection;
        if (r == 'R') {
            newDirection = cross;
        } else if (r == 'L') {
            newDirection = new int[] { -cross[0], -cross[1], -cross[2] };
        } else {
            throw new IllegalArgumentException("Did not expect rotation: '" + r + "'.");
        }

        return newDirection;
    }

    // right = 0, down = 1, left = 2 and up = 3
    private static int determineFaceValue(final Step step, final Step previousStep, final Cubelet[][][] cube) {
        final int faceValue;
        // Same cubelet between two steps. Only the facing changed.
        if (step.position[0] == previousStep.position[0] && step.position[1] == previousStep.position[1] && step.position[2] == previousStep.position[2]) {
            // Just assume you can move to in the same direction one more time. This is always possible if the dimension of the cube is bigger than 1.
            // The direction will remain the same.
            final int[] newPosition = MathUtils.add(step.position, step.direction);
            faceValue = determineFaceValue(new Step(newPosition, step.direction, step.normal), step, cube);
        } else {
            final Cubelet.CubeFace current = cube[step.position[0]][step.position[1]][step.position[2]].faces.get(new Face(step.normal[0], step.normal[1], step.normal[2]));
            final Cubelet.CubeFace previous = cube[previousStep.position[0]][previousStep.position[1]][previousStep.position[2]].faces.get(new Face(previousStep.normal[0], previousStep.normal[1], previousStep.normal[2]));

            final Coord2D currentCoord = current.coord();
            final Coord2D previousCoord = previous.coord();

            if (currentCoord.x == previousCoord.x) {
                if (currentCoord.y < previousCoord.y) {
                    faceValue = 2;
                } else {
                    faceValue = 0;
                }
            } else {
                if (currentCoord.x < previousCoord.x) {
                    faceValue = 3;
                } else {
                    faceValue = 1;
                }
            }
        }

        return faceValue;
    }

    private record Step(int[] position, int[] direction, int[] normal) {}

    private static Step nextStep(final Step step, final Cubelet[][][] cube) {
        final int[] position = MathUtils.add(step.position, step.direction);

        final Step nextStep;
        // When the new position is within range of the cube then that is the next step.
        if (position[0] < cube.length && position[1] < cube.length && position[2] < cube.length &&
            position[0] >= 0 && position[1] >= 0 && position[2] >= 0) {
            nextStep = new Step(position, step.direction, step.normal);
        } else {
            // Next position needs to change side of the cube.
            final int[] newDirection = new int[] { -step.normal[0], -step.normal[1], -step.normal[2] };
            final int[] newNormal = new int[] { step.direction[0], step.direction[1], step.direction[2] };
            nextStep = new Step(step.position, newDirection, newNormal);
        }

        // Verify that next step is a tile and thus not a wall.
        if (cube[nextStep.position[0]][nextStep.position[1]][nextStep.position[2]].faces.get(Face.valueOf(nextStep.normal)).mapItem == MapItem.TILE) {
            return nextStep;
        } else {
            return null;
        }
    }

    private static int[] findFirstPosition(final Cubelet[][][] cube) {
        final int z = cube.length - 1;

        for (int i = 0; i < cube.length; i++) {
            for (int j = 0; j < cube.length; j++) {
                if (cube[i][j][z].faces.get(new Face(0, 0, 1)).mapItem == MapItem.TILE) {
                    return new int[] { i, j, z };
                }
            }
        }

        throw new IllegalStateException("No starting position could be found.");
    }

    private static void fillCube(final Cubelet[][][] cube, final int s, final String[] input) {
        final Stack<int[][]> transformations = new Stack<>();
        transformations.push(MathUtils.IDENTITY);
        final Stack<int[][]> faceTransformations = new Stack<>();
        faceTransformations.push(MathUtils.IDENTITY);
        // Default translation = Do nothing.
        fillForInputRow(findFirstTile(input, 0), s, input, cube, transformations, faceTransformations);

    }

    private static void fillForInputRow(final Coord2D startCoord, final int s, final String[] input,
                                        final Cubelet[][][] cube, final Stack<int[][]> transformations,
                                        final Stack<int[][]> faceTransformations) {
        boolean rowInProgress = true;
        Coord2D workingCoord = startCoord;
        Face workingFace = Face.valueOf(MathUtils.transform(faceTransformations.peek(), X_Y_NORMAL));

        while (rowInProgress) {
            int[][] workingTransformation = transformations.peek();

            for (int i = 0; i < s; i++) {
                for (int j = 0; j < s; j++) {
                    // Translate the point 0 0 1 1
                    final int[] target = {i, j, s - 1, 1};
                    int[] t = MathUtils.transform(workingTransformation, target);
                    // Check if the cubelet already exists on the cube and otherwise add one
                    if (cube[t[0]][t[1]][t[2]] == null) {
                        cube[t[0]][t[1]][t[2]] = new Cubelet();
                    }
                    // Add the new mapping on the given face of the cubelet.
                    cube[t[0]][t[1]][t[2]].getFaces()
                            .put(workingFace, new Cubelet.CubeFace(new Coord2D(workingCoord.x + i, workingCoord.y + j),
                                    MapItem.valueOf(input[workingCoord.x + i].charAt(workingCoord.y + j))));
                }
            }

            final Coord2D rightSide = workingCoord.add(new Coord2D(0, s));
            if (rightSide.x >= input.length || rightSide.y >= input[rightSide.x].length() || input[rightSide.x()].charAt(rightSide.y) == ' ') {
                rowInProgress = false;
            } else {
                workingCoord = rightSide;
                transformations.push(
                        MathUtils.transform(
                            workingTransformation,
                            MathUtils.rotationWithRepositioning(MathUtils.Rotation.X_CLOCKWISE, s - 1)));
                // Change face with same rotations but without the translation
                faceTransformations.push(MathUtils.transform(faceTransformations.peek(), MathUtils.ROTATE_X_INVERSE));
                workingFace = Face.valueOf(MathUtils.transform(faceTransformations.peek(), X_Y_NORMAL));

            }
        }

        if (workingCoord.x() + s < input.length) {
            // Find column to go down
            final Coord2D goDownCoord = findDownTile(input, workingCoord, s);
            // Remove 'go right' transformations up until the down transformation
            final int back = ((workingCoord.y - goDownCoord.y) / s);
            for (int i = 0; i < back; i++) {
                transformations.pop();
                faceTransformations.pop();
            }
            // * Apply the down transformation
            faceTransformations.push(MathUtils.transform(faceTransformations.peek(), MathUtils.ROTATE_Y));
            transformations.push(MathUtils.transform(
                    transformations.peek(),
                    MathUtils.rotationWithRepositioning(MathUtils.Rotation.Y_COUNTERCLOCKWISE, s - 1)));
            // Find the next start coordinate
            final Coord2D nextStartCoord = findFirstTile(input, workingCoord.x() + s);
            // Apply the 'go left' transformation up until the next start coord.
            final int left = ((goDownCoord.y - nextStartCoord.y) / s);
            if (left > 0) {
                int[][] transformation = transformations.peek();
                for (int i = 0; i < left; i++) {
                    faceTransformations.push(MathUtils.transform(faceTransformations.peek(), MathUtils.ROTATE_X));
                    transformation = MathUtils.transform(
                            transformation,
                            MathUtils.rotationWithRepositioning(MathUtils.Rotation.X_COUNTERCLOCKWISE, s - 1));
                }
                transformations.push(transformation);
            }
            // Start next iteration for row.
            fillForInputRow(nextStartCoord, s, input, cube, transformations, faceTransformations);
        }
    }

    private static Coord2D findDownTile(final String[] input, final Coord2D coord, final int s) {
        boolean searching = true;

        int column = coord.y;
        final int row = coord.x + s;
        final String downRow = input[row];
        while (searching) {
            if (column >= 0 && column < downRow.length() && downRow.charAt(column) != ' ') {
                searching = false;
            } else if (column < 0) {
                searching = false;
            } else {
                column = column - s;
            }
        }

        return new Coord2D(row, column);
    }

    private static Coord2D findFirstTile(final String[] input, final int row) {
        int i = 0;
        while (i < input[row].length() && input[row].charAt(i) == ' ') {
            i++;
        }

        return new Coord2D(row, i);
    }

    private record Coord2D(int x, int y) {
        public Coord2D add(final Coord2D v) {
            return new Coord2D(x + v.x, y + v.y);
        }
    }

    private enum MapItem {
        TILE, WALL
        ;

        public static MapItem valueOf(final char c) {
            return switch (c) {
                case '.' -> MapItem.TILE;
                case '#' -> MapItem.WALL;
                default -> throw new IllegalArgumentException("Unexpected type of map item: <" + c + "?.");
            };
        }
    }

    private static final class Cubelet {

        private Map<Face, CubeFace> faces;

        public Cubelet() {
            this.faces = new HashMap<>(3);
        }

        public Map<Face, CubeFace> getFaces() {
            if (faces == null) {
                faces = new HashMap(3);
            }

            return faces;
        }

        private record CubeFace(Coord2D coord, MapItem mapItem) { }
    }

    private record Face(int x, int y, int z) {
        public static Face valueOf(int[] face) {
            return new Face(face[0], face[1], face[2]);
        }
    }

    public static void main(final String[] args) {

        // Parsing input
        final var tinyInput = TINY_INPUT.split("\n\n");
        final var tinyInput2 = TINY_INPUT2.split("\n\n");
        final var testInput = TST_INPUT.split("\n\n");
        final var input = INPUT.split("\n\n");

        final var tinyPart = part2(tinyInput);
        final var tinyPart2 = part2(tinyInput2);

        {
            final var expectedResult = 6032L;
            final var part1 = part1(testInput);
            LOGGER.log(Level.INFO, () -> String.format("Test part 1: %d", part1));

            if (expectedResult == part1) {
                final var result = part1(input);
                LOGGER.log(Level.INFO, () -> String.format("Part 1: %d", result));
            }
        }

        {
            final var expectedResult = 5031;
            final var testResult = part2(testInput);
            LOGGER.log(Level.INFO, () -> String.format("Test part 2: %d", testResult));

            if (expectedResult == testResult) {
                final var result = part2(input);
                LOGGER.log(Level.INFO, () -> String.format("Part 2: %d", result));
            }
        }
    }

    private static final String TINY_INPUT = """
              ...#
              .#..
              .#
              ..
            ...#
            ....
            #.
            ..
            
            10R5L5R10L4R5L5
            """;

    private static final String TINY_INPUT2 = """
            .....#
            ...#..
              .#
              ..
              .#
              ..
              #.
              ..
            
            10R5L5R10L4R5L5
            """;

    private static final String TST_INPUT = """
                    ...#
                    .#..
                    #...
                    ....
            ...#.......#
            ........#...
            ..#....#....
            ..........#.
                    ...#....
                    .....#..
                    .#......
                    ......#.
                        
            10R5L5R10L4R5L5
            """;

    private static final String INPUT = """
                                                              .#..#.....#.....................................................................#...........#.......
                                                              ................#.....#......#...#................#..........#.#...........#........................
                                                              #............#..#.......................##...................................#.........#.........#..
                                                              #......#.......##..#...#....##..............#.....#....#...........#.#......#.......................
                                                              #.....#.#...#......#..........#.........................#..#........#................#.........#....
                                                              .....#....#..............##.......#...........#.#............#..........#............#...#..........
                                                              ....#......#.......#..........#.................#.....#.......#.....#.........#...#.#...............
                                                              .........#............#..................##........#......#.........................................
                                                              .............#......#....#.#..................#.....................................#.....#...#....#
                                                              .....#..........##...........#.#..............................##..........#.....................#...
                                                              #............................#..#.......#.....#.......................#.............................
                                                              ..##...........#...#............#.....#.#...#.....#...................#...#.........................
                                                              ..#.....#..............................................................##.......#...................
                                                              ..##......#.....#......#..#..................#..........................#.......##.....#....#.......
                                                              ..........#.....#.....#.........#.#....#....#..#.............#.........#.......#...#......#.....#...
                                                              #...#........#.....#....................##...........#.#.......#.#..............................##..
                                                              .....#.......#.......#..........#...#.........#..#............#..#......#............#.........#....
                                                              .......#...#............#...................................................................#.......
                                                              ...........................#.............#..........#.........#..#......................#..###...#..
                                                              ................#..#..............#....#....#.....###.#........#.#...#...............#.....#........
                                                              ....#........................................#....#................#...##.#...............##........
                                                              #..................#.............#................#............#......#..#......#.......#..........#
                                                              .#.............#.#...............#...##.#.#...............#...#....#..#.............................
                                                              ........#........................#..#.#.............#.............##...........#..#.....#...........
                                                              ..............#..........#.....#................##.............#.......#.......#.#..................
                                                              ..##.#.#..#...#.........................#....#.........................#.............##............#
                                                              ...#..............................#........................................#.....................#..
                                                              #.........##...#....................................................................#...............
                                                              ..##........#...#..................#.............................#..........................#.......
                                                              ....#...#.........................................##...#.......................#...#.#........#.....
                                                              ##......#.##........#....#.....#..............#....#........................#..#...#................
                                                              ........#..............#.#......#...#........#................................#.......#....##.......
                                                              ..#..#.........................#.......#.......#................#.........#.............#.....#.....
                                                              ......#......#..............#....#......................#................#....#.........#...........
                                                              ..#..............................#......##...#.................#..........##...#.#..........#.......
                                                              ........#.............#.........#......................#..##......................#.................
                                                              #..#.....#...............#...............#..................................#.....#..#..............
                                                              .#..............#..#...#..#...................#..........................##....#...#..#.##..........
                                                              ...#.......#.#..........................#.....#.#...............#....#.................#..##........
                                                              ....#..........#......#.....##.#.........#..###..................#.....................#............
                                                              ..##.#...........#................#..#.......#.............#............#...#...#.....##......#.....
                                                              #.#...............................#........................#................#.......#.#.............
                                                              ................................#...#..#.....#............#..............#............#....#........
                                                              .........#......#..................#......#..............#....#..........................#.....#..#.
                                                              ......#....##........#...........##.................................................................
                                                              .....#......................................#......#......#......#...#.....#.................#......
                                                              ........#..#.......................##......##.......#..............#.................#..........#.#.
                                                              ........................#........................#..................................................
                                                              ...#.#....#...........#......................#............#..#..#............................#......
                                                              ...............................#.........#...................#.......................#...........#..
                                                              ......#......#.......................#.........#..
                                                              ..........#..........#........#...................
                                                              ...#....#............................#...#........
                                                              ..........#.............#.....................#...
                                                              ....#......#.......#........#.##................##
                                                              ........#...#....#...#......#..#.....##.#.#.......
                                                              ...........#.#.........#..............#...........
                                                              ........................#...........#..#..........
                                                              .......###..............................#.......#.
                                                              #...................#....#.#..#..........#........
                                                              ...................#..#...........#.......#.......
                                                              #...#........##............#......#.....#.#.#.#...
                                                              ..#.........#........#............................
                                                              ...................#...............#..#...........
                                                              .........................#..#..............#......
                                                              ...........#....#.....#.#........#....#..#..#.....
                                                              .....#......#.........#......#........##.......#..
                                                              ..#......#.....#..........#...#....#.#......#.....
                                                              .....#...#.#................................#.....
                                                              ..................................................
                                                              ..........................#.......................
                                                              .#.#....#........#.#.......................#......
                                                              .#............#.....#.....#........#..............
                                                              #................#....................#.........#.
                                                              ..................................#....#..........
                                                              .#.#.....#..................#...#.................
                                                              .##.#.......#...#......#..........................
                                                              ...............#.......#..........................
                                                              ...#...#...#.#........#.........#.#.......#.##...#
                                                              .#..........#...#..............#.#................
                                                              .....##......##.......#.#.......................#.
                                                              ..................................................
                                                              #.........................#...##............#.....
                                                              .#.#................##.................#.........#
                                                              ...#...#.#....#........#....#.#.....#...##.....#..
                                                              ......####.........................#..............
                                                              .......#.......#.......#........#.......#..#......
                                                              ............#........#.#..#...........##.....#....
                                                              .....#.......#..#.........#...#.........#.........
                                                              ...#.##..#....................#............##.....
                                                              ..........#.............#..#......#....#...#.....#
                                                              ..#............#........#.....................#...
                                                              .............................#....................
                                                              #...#.......#.#............#.....#................
                                                              ...................................#.......#......
                                                              #.........................#........#.......#...#..
                                                              ......................#..#........................
                                                              ...............#....#....................#......#.
                                                              .......#................#....#.#............##....
                                                              ..........#.....#....................##.#..#....#.
            .#.................#......##...#..#...#.#...#................#..................#...........#.......
            #..........#..........#..........#.#..#................#.......#.....................#.............#
            .............#..#..#......#.#...#..#................#...#.........#......#.....#......#.............
            #...#...#................##...#.##...#................#....................#.#...##.................
            .......................#.....................#............#.#...#.#.........................#.......
            ...............##...#.#...........................#................#................................
            ........#.........#.##..........................#..........#..........#.........#...#....##.........
            ............................#.....##...............#....#.....#..#..##.............#......#.........
            ........#.#..#..........................................##...#.#.....#.....................#....#...
            ........................#.#..#............................#...#...................#.................
            ........#.........................................#.........#...#..#.#..........#.............#...##
            ..................#..#..............###...#.....#.........................................###.#.....
            .........#..#...#...#......##.....#..........#..............#....#............###.....#..#.......#..
            .#....................................#.............#..........#..................#..#..........#...
            ...........#..#...............#...##......#.............#..............#........#............#......
            ....#...........#....##.#....................#........#..............#..........#..#.#........#.....
            ..#.....#...#......#.......#..#.....#.......#.............................#..#.....#................
            .....#..#..#........##...........#...#.................#......#....#.....#.........#....##....#.....
            ...#.........................................##............##......#......................#.........
            ....#..........##..#.................#..............#.............###.........................#.....
            ...............#..#....#........##..#.....##...#...#.....#......#....##...##......#.#..........#....
            ....#...........#..#............#.............#....................#..#...........#.................
            .....##...#........................#....#...............#..#...........#.........#.......#.#........
            ....##..#.......#................#........#.........#............#..................#.....#...##..#.
            ...........#.#..........#......#........#...#.#.....#.#...#.....#....#.........#....................
            ........#......#.#.#......#............#..........#.....#..#....#........#.#.....#.#..........#.....
            .##.......#...#..............................#.......#.......#...#..#...............................
            .#...#..##......#.#..#....##...............#...............#.......#....#...............#.......#...
            ..............#.............#.....#..........#.................#......#.......#....................#
            #....#..#.....#..#.........#......#.##..............#.........#.....................................
            #.#.......#......#..#......#....#...............................................#..................#
            .............................................#...............#.#...#.......#.#.............#........
            ..........#...........#..#................#...........#....#........................................
            .....#..#...............#.......##...............##....#.......#...............#..............#.#...
            ...#.........#...........................#..........#.#..........#.............#..#.....#.....#.#...
            ...........................#..#...........#..#....##....#...........##.................#...........#
            ............#........................#...#.#......#......#..........................................
            ..................#..............................................#......#................#...#..#...
            ##...#.#...#.##.#.....#.................#...##....##..........#.........................#...........
            ......#..#..#.....#....#.#....#.......................#......................##....................#
            .....#.............#..................#..............#........#.....#.....#.#..........##...........
            .........#......#......................#...........#.......#.##..#............#...........#.........
            ...#............#...##........#.......#......#...#..#..............#.#...........#......#......#....
            ........#........................##..#..........................#.....#.......#...............#.....
            ..........#.......#....#...#....#.........#..#.#.#.........#.........#......#............#........#.
            ..#............#......................#............#.........#..........#.....#............#........
            .....#........#.........#.#..#.............#..#...#....#...#..........#...#.#.......##....#.....#...
            ....................#...##....................#....#..........#.#...#.......#...........#.#.........
            ...#......................#.#........#.......#....................#.......#.....##....#...#.........
            ...............#...............................#...#.............#......#..................#.......#
            .#.......#..#....#.............###....#..#........
            #...#.......................................#.....
            ..............#........#.........#............#.#.
            ##..................#....#........................
            ...............#....................#.....#..#.##.
            ..#........#............#............#......#.....
            .......#.....#...#.....#.......#.........#........
            .............................................#....
            #...........#.....#...#............#......##....#.
            ....#....#......#..#.....#...#..#......#........#.
            ..........#...#...#............................#..
            ....##...............#..............##......#...#.
            ...##................#............#...#....#...#..
            .....##................#.....#..........#.........
            ...#...#...........#..#.#...#......#....#.#.......
            .....#.........#..................#...............
            ...#.........#.#..#.........#............#..#.....
            ...#...#.#.#......................................
            #.......................##........#...............
            .........#...####........#.....#...#..............
            .............................................#....
            .................#...............................#
            ........#..#...#.............................#....
            .........#.#......................................
            ..............#.....##....#........#......#..#....
            ....#....##....#..............#.......#...........
            ..............................#.........#.#..#....
            ......#..#......#........#.#......#......#....#...
            ....#.................#.......#.....#.......#.....
            .............#.........##.#.......#..#..#.....#...
            .....#..#.......#......................#.....#....
            ..........#........#...#.............#........##..
            .............................#..................#.
            ...#...............#....#......................#..
            ....................#........................#....
            ..#..............#..#.............................
            ....##...............#....#.......................
            ....#.......#.......#...........#.....#...........
            ........#..#........#.........###....#...#..##....
            .#....#.......#........#..........#........#..##..
            ................#.........#........#.#......#.....
            ........#.#....#..............#...................
            ......#...........#..#............#.....#..#......
            .#......#.#........#....#.....#..........#..#.....
            ........#...#...........#.......#.................
            ...........#...#..##..#........................#..
            ...#.....#.#.#..........................#...#.....
            ....#......#.................#.....#.............#
            ............#.#......#.......................#....
            .....#..............#......#..............#..#....
                        
            7R13R36L22L38L10R29R47R1R32R49R12R49L45R32R41R50R47L13R35R31L35R11R47R30R36R17R38L46L20R47R17L28L6L1R1L15R23R9R4R24R14R40L21R7R39R37L12R7L17L16R46L50L40R12L11R11R30R27R39L6L36L46L30L7L37R7L14R48L30R10L47L4R46R50R48R19R31R41R4L48L8R15L39R28R35R37R22L17L31R46R34L49R28R46L41L46R16L11L30L30L30L43R42L49L32L3L40R33R3R18R12R41L35R47L3L14L3L40R18L11R4R25R6R46R31L9L42L25L46L36R24L25R8R27R1R20R9L35L21L26L12R23L42L8L29R38L41R37R6L40L11L30R21R6L47R49L41R3L46R31L29R35L40L16L26L35L41R43R3R29L8L31L20L22R35R15R4R10R33L13R26R32L8L49L32L13L34R21L13L41R17R9R37L10R20R19R13R8R20R39R4R17R42L26R48R31R47L24L35R15L29R11L17R46R49R14R38R13R10L30L13R17L3L17R42R47L42R1L27R26L21R4R2L40R33L34R39R19L43R8L42L24L38L31L22R33R2L35R41L28L37R5L39R24R35R49R15L13R23R18R26R48R11L11L33R24L13R36L28R15R4L26L45L27R37R45L48L7R23R40L40R24R9L10L49L16L49L16R28L16L33L10R1L3L3R23L41L19R1R45R33L46L45L18R27L5L45R5L8L45L29R44L5R46L19L21R9L46L50L48R30L26R22R16L26R10R39L16L2R47L40L49L4R15L49L3R8L1R3L3R23L9L31L29L2R35R3L24R44R10R8R45L24R10L8L42R13R43R26L14R8L18R34R9R36R10R11R46L38R17L21L5R40L23R35R40L36L36L35R19R10R1R6L4R17R29R34R48R35R25L44L42L16L49R2R22R40L15L41R33L43R13L37L40R6R18L48R33L5L38L39R45L21L38L1R1R23R46R25L46R9L21L8R35R8R8R3R48L33R37R37R14R44L25R37R5R8R33L2R3L18R11L11R4R49R41L2R36R36R2R21R9R48L48R1L14L15R10L9R46R40R46L18R4L28R41L36R31R32R34L39R5L25R18L9R14R22L29L8L41L11L44L15L47R10L4L11R21L50L31L49R28L49R25L11L49R41L44L29R13L19L47L9R6L3R30L1L10L48R27L6L6L9L28L4L37L31R2L38R19L11R32L17R22L45L36R17R49R38L15L44L49L3R44L27R32R21L7L23R16L44L41R36L5R5R24L4L25L47R14L7R4R36L48L39L44L39L46L23R5R44L45L16L47R41L2R47L42R45L47R8R28L29L33R18L1R9L43L26L33R3L37R49L31R31R37L49L42R21L15L37R15L34R19L20L40L24L20L50R6L27L36L17R6R31R23R33L45L46R13R19L37R2R16L41L46R1L45L36L42R23R47L32L42R21L2R14R13L37R46R35L49R40L19L43R34R4R44L34L5R3R37R16L45L32L28L7L40R3L26L49R13R43R9R50L43L6L43R50R49R42R23L32R6R37R33L18L37L10L13L48R48R35L42L7R50L47R31L41L9R3L33L11R45R32L1R28L2R40R43L45L28L11R41L28R38R15L49L40R13L14L12L11R32L36L9L6R37L16L26L28R28L49R20L2L33L41L50L19L43R5L32R13L48R35L4R44L19R47R28L9L32L35R36R50R18L21L1L48L10L31R31R40R38R9R50L4R31R39L9L48R34R50R9L9L49R39R14L39R19L21R43R24L37R19R1R33R43R22L14R38R22L29R13L37L44L18R22R49L17L33L22L9L23L40R44L41R11R4R6R17R21L44L6L45R18R19R12R37L48R45R37R17L26R24R43L34L32L38R11L44L13L6R2L40R23L25L46L39L50L15L11L49L28L14R13R19L41R37R47R19R31R37L19L38R34L6R49L30R34R31L12R46L28L19L17R2L14L11R42R38L41L7L15R8R2L43R3R13R46R31R3L5R50L23R10R3R41R50L49R47R25L46L9R9R8R18R43R25R13R2R14L23R46L35R47L18L1R3R23L16L50L32R3R42L45L49L2R20R8R35R27L38L13R16R48L34L48R15R30L12L46R49R5L41L50L50L12R13R41R28R12R49R21L28R16R10L26R39R41R16R11L31R40R48R9R18L22R41R19L21L26R13L40L18R33R15R23L40R5R7L7R33L44L37L5L4R41L44L15R17L1L10L3R43R23L44L44L49L24L47L8L48L17R49L25R10R22R34L29R31R23R31R36R40L34L49R27R49L4R30R4R35R15R7L32L22R16R9R13R8R8L9R12L17L30L16R50R3L40L46R12R3R44L33L15L24L6L46R40R26R9L46R29R23L23L49L31R37L48R43R16L44L15R31R12L40L45R32L10R6R50L15L31R20R22R31R28L16L27R39L4R1R18R33R3R44L32R30L14R5L37R28L28L22L10R27L39L48R22L13R33L16L34R44L17R18R42L41L30L17L48R17R9R37R26L14L11R14L38R18L35R43R50L9R18L39R32R6L18L49R41L3L39R1R12R6L41L6R35R10R27R30L12L32R26L50L10R38R20L36R10R4R11R22R26L35R37R15L6R12L16L35L3R15L47R34L36L43L40R38R7L13L15R2R21R46L11L27L37R24R47R42L35R45L11R43R13R9L17L38R21R31R9L9L45L42R7L47L45L21R10R32R35R13R40R28R19L46L25L9L32L30L49R3R33R33R46L19L18R9R35L25R12R5R2L21L16R24L41R43R31L1L9L17L20R33L15L18R37R15L18L30L14L25R12R50L21R20L38R19L14R13R33R48L18L15L23R17R14L30R41R23L12R25R26L34L1L43L48R24R44L18R20L27L38L25R13R1R7R12L36R33R16L43L11R14R34L26L21L6R43L4R38L25R28L9L5R48L41R4L40R34L24R6R2R8R7L1L42L14R26L13R4L36R18R15L19R18R48R37R30L25R20L16R27L25L42L4L42R32L14L30R33R29R48R19L42R29R39R33L36R36L9R47L20L30R39L8L23L4L17L50R44L38L8L39R6R50L6R13R35L1R12L8L35R44R25R16R10R45R49L42R20L30R50R31R33L34L35R42R32L32L27L3L6L26R2L26L35L5R40R26L4R19L12L37L50R1L6L12R10L6L48R22R23R3R40R17R38L48R23L16L26R21R49R9R9R3R30L3L39R11L19L17L15L12L4R21R14R50L2L21L40R33L50L48R43R4R18R41L10R19L18L17R15L43L15L15R10R43L20L31R49R8R32R3L27R48R38R32R9L25L15L25R10L49R39L32R35L29R24L39L2R31L27R32R45R33R10R15L4R12R23L19R30R17L44R10L40R24L22L30R28L7R33L39R22L15L6L2R4R43R30R26L7R47L16R28L22R14L16R33R15L21R4R11R41R16L24L11L18R22L46R49L1L8L38L7R19L44R43L40R16R9R13L31L28R22L29R44R4L32R28L18L41R40R46L25L48L29L36L18L13R23R6L44L47L9R21R44L38R16L48R30L17R38R13L4L14L19R27L40R43L7R28R42L26R15L8R15R50R29L45R46L9R34L50L1L20L15L14R50R33L8R1L23L19R44R36L26L9L50R33L21R3L36R24R25R30R16R9L38R18L50R29R27R24L31R31L9L36R26L36R33R5L41R42L13R44R12R25L39R10R16R46L7R30L23L49L11L17R28R9R32R3L13L7L47R29L6R25R45R45L10L50L45L45R31L18L24L33L33R50L36L42L20L14R30L43R14L22L11R9L15R20R18R44L16L34L41R24L40L10R22R31R50R26R34L45R10L42R8R20L49L50L9L43R37L19R37L28L41R25L10L48R48L11L48L49L33R50L2R31R26L10R47L14L43R30R1R17L8R11L12L10R10L45R18L9R50R26R1L4L37L42L34R21R40R40R32L36L32R47L50R50R8L46L45L16L25R12L26L19R46L42R3L32L43L7R15L6L44R45L8L46L5L4R26R48L44R43R15L31L46L48R17R20L38R9L43R48L41R15L7R10R38R13R2L41L35L43R35L46L15R1L39R21R8R6R47L36L18L32R20R37R14L44R43L5L40R33L19R47L44R43R3L47L16L27L38R45R28L46L49R14L44R26L30R38R15L33L23L38R47R15L11R12R34L5R30L30R35L42R9L34L45R42R50L11L5L24L12L33R29R50R24L24L30R50L37L10R9R42L16R33R34L15L5L2R25R37R20L35L34R4R30R32L32L44L32R38L20R24L18L25R26R4R7R27L34L19R11R27R14L17L7R10R46R17R9R4L2L15R21R46L41R48L18L46R1R26R22L9L20L12L35R33L20R6R6R28R43L14R44L45R31L29R33L23R25L13L10L21R17R23R9L49L15L42L19R6L41L24R24R15L28R5R24L23R27L2L1L31L36L39R18R32L32R13R44R1L20R20L36R45R8R6L31L37L4R36L8L24L48R17R41L19R11R15R44R9L25L42R1R28R23R18L26R34R23R3L25L27L14L7R22L46R17R37L29R6R29L19R50R9R33L20L43L44L23R37R28L43L34R2R27L14L38L8L45R46R18L36L20L23L34R31R42R22
            """;
}
