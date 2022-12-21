package git.goossensmichael;

import git.goossensmichael.utils.Pair;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

public class Day19 {

    private static final Logger LOGGER = Logger.getLogger(Day19.class.getName());

    private static long part1(final String[] blueprints) {
        final List<Pair<Integer, Map<RobotType, RobotBlueprint>>> robotBlueprints = parse(blueprints);

        return robotBlueprints.stream()
                .mapToLong(robotBlueprint -> robotBlueprint.left() * solve(robotBlueprint.right(), 24))
                .sum();
    }

    private static long solve(final Map<RobotType, RobotBlueprint> factoryBlueprint, final int minutes) {
        final long begin = System.currentTimeMillis();

        final Set<Integer> solution = new HashSet<>();

        final Map<RobotType, Integer> maxAmountOfRobotsNeeded = getMaxAmountOfRobotsNeeded(factoryBlueprint);
        final Set<FactoryState> memo = new HashSet<>();
        final Set<FactoryState> states = new HashSet<>();
        // Initial state
        states.add(new FactoryState(1, 0, 0, 0 , 0, 0, 0, 0));
        int i = 0;
        while (i <= minutes) {
            final List<FactoryState> newStates = new ArrayList<>();
            for (final FactoryState state : states) {
                // Only treat states that have not reached the 24th minute yet.
                if (i + 1 <= minutes || state.canSkip(factoryBlueprint, maxAmountOfRobotsNeeded)) {
                    // Always add a state where no robots are made. It will never be part of memo yet.
                    final FactoryState noConstructionState = new FactoryState(state.oreRobots(), state.clayRobots(),
                            state.obsidianRobots(), state.geodeRobots(),
                            state.ore() + state.oreRobots(), state.clay() + state.clayRobots(),
                            state.obsidian() + state.obsidianRobots(), state.geodes() + state.geodeRobots());
                    newStates.add(noConstructionState);
                    memo.add(noConstructionState);

                    possibleStates(state, factoryBlueprint, maxAmountOfRobotsNeeded).stream()
                            .map(s -> addResources(s, state))
                            .filter(s -> !memo.contains(s))
                            .forEach(s -> {
                                memo.add(s);
                                newStates.add(s);
                            });

                } else {
                    solution.add(state.geodes() + (minutes - i));
                }

            }

            // Prepare states for next round.
            states.clear();
            final int maxGeodes = newStates.stream().mapToInt(FactoryState::geodes).max().orElse(0);
            final int maxGeodesToFarm = minutes - i;
            states.addAll(newStates.stream()
                    .filter(newState -> isStillInTheRunning(maxGeodes, maxGeodesToFarm, newState))
                    // This is of course faster. But I don't know why the value - 2 is needed. Probably depends on the input.
                    //.filter(newState -> newState.geodes() >= maxGeodes - 2)
                    .toList());
            i++;
        }

        return solution.stream().mapToInt(n -> n).max().orElseThrow();
    }

    private static boolean isStillInTheRunning(final int maxGeodes, final int maxGeodesToFarm, final FactoryState newState) {
        return newState.geodes() >= maxGeodes - maxGeodesToFarm;
    }

    private static Map<RobotType, Integer> getMaxAmountOfRobotsNeeded(final Map<RobotType, RobotBlueprint> factoryBlueprint) {
        return Map.of(
                RobotType.ORE_COLLECTING, factoryBlueprint.values().stream().mapToInt(RobotBlueprint::ore).max().orElse(0),
                RobotType.CLAY_COLLECTING, factoryBlueprint.values().stream().mapToInt(RobotBlueprint::clay).max().orElse(0),
                RobotType.OBSIDIAN_COLLECTING, factoryBlueprint.values().stream().mapToInt(RobotBlueprint::obsidian).max().orElse(0),
                RobotType.GEODE_CRACKING, Integer.MAX_VALUE);
    }

    private static List<FactoryState> possibleStates(final FactoryState state,
                                                     final Map<RobotType, RobotBlueprint> factoryBlueprint,
                                                     final Map<RobotType, Integer> maxAmountOfRobotsNeeded) {
        if (factoryBlueprint.get(RobotType.GEODE_CRACKING).canBeMade(state)) {
            return List.of(make(state, factoryBlueprint.get(RobotType.GEODE_CRACKING), 1));
        }

        return factoryBlueprint.values().stream()
                .filter(robotBlueprint -> robotBlueprint.canBeMade(state) && robotBlueprint.shouldBeMade(state, maxAmountOfRobotsNeeded))
                .map(robotBluePrint -> make(state, robotBluePrint, 1))
                .toList();
    }

    private static FactoryState addResources(final FactoryState state, final FactoryState previousState) {
        return new FactoryState(state.oreRobots(), state.clayRobots(), state.obsidianRobots(), state.geodeRobots(),
                state.ore + previousState.oreRobots(), state.clay + previousState.clayRobots(),
                state.obsidian() + previousState.obsidianRobots(), state.geodes() + previousState.geodeRobots());
    }

    // Makes the given type of robot
    private static FactoryState make(final FactoryState state, final RobotBlueprint robotBlueprint, final int amount) {
        final int ore = state.ore() - (amount * robotBlueprint.ore());
        final int clay = state.clay() - (amount * robotBlueprint.clay());
        final int obsidian = state.obsidian() - (amount * robotBlueprint.obsidian());
        return switch (robotBlueprint.type()) {
            case ORE_COLLECTING -> new FactoryState(state.oreRobots() + amount, state.clayRobots(),
                    state.obsidianRobots(), state.geodeRobots(), ore, clay, obsidian, state.geodes);
            case CLAY_COLLECTING -> new FactoryState(state.oreRobots(), state.clayRobots() + amount,
                    state.obsidianRobots(), state.geodeRobots(), ore, clay, obsidian, state.geodes);
            case OBSIDIAN_COLLECTING -> new FactoryState(state.oreRobots(), state.clayRobots(),
                    state.obsidianRobots() + amount, state.geodeRobots(), ore, clay, obsidian, state.geodes);
            case GEODE_CRACKING -> new FactoryState(state.oreRobots(), state.clayRobots(), state.obsidianRobots(),
                    state.geodeRobots() + amount, ore, clay, obsidian, state.geodes);
        };
    }

    private static List<Pair<Integer, Map<RobotType, RobotBlueprint>>> parse(final String[] blueprints) {
        return Arrays.stream(blueprints).map(Day19::toRobotBlueprint).toList();
    }

    private static Pair<Integer, Map<RobotType, RobotBlueprint>> toRobotBlueprint(final String robotBlueprint) {
        final Map<RobotType, RobotBlueprint> robotBlueprints = new HashMap<>(4);

        {
            final int ore = extractResource(robotBlueprint, robotBlueprint.indexOf("ore robot") + 16,
                                    robotBlueprint.indexOf("clay robot") - 11);
            robotBlueprints.put(RobotType.ORE_COLLECTING, new RobotBlueprint(RobotType.ORE_COLLECTING, ore, 0, 0));
        }

        {
            final int ore = extractResource(robotBlueprint, robotBlueprint.indexOf("clay robot") + 17,
                    robotBlueprint.indexOf("obsidian robot") - 11);
            robotBlueprints.put(RobotType.CLAY_COLLECTING, new RobotBlueprint(RobotType.CLAY_COLLECTING, ore, 0, 0));
        }

        {
            final int ore = extractResource(robotBlueprint, robotBlueprint.indexOf("obsidian robot") + 21,
                    robotBlueprint.indexOf("ore and") - 1);
            final int clay = extractResource(robotBlueprint, robotBlueprint.indexOf("ore and") + 8,
                    robotBlueprint.indexOf("geode robot") - 12);
            robotBlueprints.put(RobotType.OBSIDIAN_COLLECTING, new RobotBlueprint(RobotType.OBSIDIAN_COLLECTING, ore, clay, 0));
        }

        {
            final int ore = extractResource(robotBlueprint, robotBlueprint.indexOf("geode robot") + 18,
                    robotBlueprint.lastIndexOf("ore and") - 1);
            final int obsidian = extractResource(robotBlueprint, robotBlueprint.lastIndexOf("ore and") + 8,
                    robotBlueprint.lastIndexOf("obsidian") - 1);
            robotBlueprints.put(RobotType.GEODE_CRACKING, new RobotBlueprint(RobotType.GEODE_CRACKING, ore, 0, obsidian));
        }

        final int blueprintId = extractResource(robotBlueprint, 10, robotBlueprint.indexOf(':'));

        return new Pair<>(blueprintId, robotBlueprints);
    }

    private static int extractResource(final String robotBlueprint, final int from, final int to) {
        return Integer.parseInt(robotBlueprint.substring(from, to));
    }

    private static long part2(final String[] blueprints) {
        final List<Pair<Integer, Map<RobotType, RobotBlueprint>>> robotBlueprints = parse(blueprints).subList(0, Math.min(blueprints.length, 3));

        return robotBlueprints.stream()
                .mapToLong(robotBlueprint -> solve(robotBlueprint.right(), 32))
                .reduce(1, (acc, p) -> acc * p);
    }

    private enum RobotType {
        GEODE_CRACKING,
        OBSIDIAN_COLLECTING,
        CLAY_COLLECTING,
        ORE_COLLECTING
    }

    private record FactoryState(int oreRobots, int clayRobots, int obsidianRobots, int geodeRobots, int ore, int clay, int obsidian, int geodes) {
        @Override
        public boolean equals(final Object other) {
            final boolean equals;
            if (other instanceof FactoryState state) {
                equals = oreRobots == state.oreRobots && clayRobots == state.clayRobots &&
                        obsidianRobots == state.obsidianRobots && geodeRobots == state.geodeRobots &&
                        ore == state.ore && clay == state.clay && obsidian == state.obsidian;
            } else {
                equals = false;
            }

            return equals;
        }

        public boolean canSkip(final Map<RobotType, RobotBlueprint> factoryBlueprint, Map<RobotType, Integer> maxRobotsNeeded) {
            return !factoryBlueprint.get(RobotType.ORE_COLLECTING).shouldBeMade(this, maxRobotsNeeded) &&
                    !factoryBlueprint.get(RobotType.CLAY_COLLECTING).shouldBeMade(this, maxRobotsNeeded) &&
                    !factoryBlueprint.get(RobotType.OBSIDIAN_COLLECTING).shouldBeMade(this, maxRobotsNeeded);
        }
    }

    private record RobotBlueprint(RobotType type, int ore, int clay, int obsidian) {
        private boolean canBeMade(final FactoryState state) {
            return state.ore() >= ore && state.clay() >= clay && state.obsidian() >= obsidian;
        }

        public boolean shouldBeMade(final FactoryState state, final Map<RobotType, Integer> maxRobotsNeeded) {
            final boolean result;
            if (type == RobotType.ORE_COLLECTING) {
                result = state.oreRobots() < maxRobotsNeeded.get(RobotType.ORE_COLLECTING);
            } else if (type == RobotType.CLAY_COLLECTING) {
                result = state.clayRobots() < maxRobotsNeeded.get(RobotType.CLAY_COLLECTING);
            } else if (type == RobotType.OBSIDIAN_COLLECTING) {
                result = state.obsidianRobots() < maxRobotsNeeded.get(RobotType.OBSIDIAN_COLLECTING);
            } else if (type == RobotType.GEODE_CRACKING) {
                result = true;
            } else {
                result = false;
            }

            return result;
        }
    }

    public static void main(final String[] args) {

        // Parsing input
        final var testInput = TST_INPUT.split("\n");
        final var input = INPUT.split("\n");

        {
            final var expectedResult = 33;
            final var part1 = part1(testInput);
            LOGGER.log(Level.INFO, () -> String.format("Test part 1: %d", part1));

            if (expectedResult == part1) {
                final var result = part1(input);
                LOGGER.log(Level.INFO, () -> String.format("Part 1: %d", result));
            }
        }

        {
            final var expectedResult = 3472;
            final var testResult = part2(testInput);
            LOGGER.log(Level.INFO, () -> String.format("Test part 2: %d", testResult));

            if (expectedResult == testResult) {
                final var result = part2(input);
                LOGGER.log(Level.INFO, () -> String.format("Part 2: %d", result));
            }
        }
    }
    private static final String TST_INPUT = """
            Blueprint 1: Each ore robot costs 4 ore. Each clay robot costs 2 ore. Each obsidian robot costs 3 ore and 14 clay. Each geode robot costs 2 ore and 7 obsidian.
            Blueprint 2: Each ore robot costs 2 ore. Each clay robot costs 3 ore. Each obsidian robot costs 3 ore and 8 clay. Each geode robot costs 3 ore and 12 obsidian.
            """;

    private static final String INPUT = """
            Blueprint 1: Each ore robot costs 3 ore. Each clay robot costs 3 ore. Each obsidian robot costs 3 ore and 16 clay. Each geode robot costs 3 ore and 20 obsidian.
            Blueprint 2: Each ore robot costs 4 ore. Each clay robot costs 3 ore. Each obsidian robot costs 4 ore and 20 clay. Each geode robot costs 2 ore and 15 obsidian.
            Blueprint 3: Each ore robot costs 4 ore. Each clay robot costs 4 ore. Each obsidian robot costs 4 ore and 15 clay. Each geode robot costs 3 ore and 8 obsidian.
            Blueprint 4: Each ore robot costs 3 ore. Each clay robot costs 4 ore. Each obsidian robot costs 2 ore and 15 clay. Each geode robot costs 2 ore and 13 obsidian.
            Blueprint 5: Each ore robot costs 4 ore. Each clay robot costs 4 ore. Each obsidian robot costs 2 ore and 12 clay. Each geode robot costs 3 ore and 15 obsidian.
            Blueprint 6: Each ore robot costs 2 ore. Each clay robot costs 3 ore. Each obsidian robot costs 3 ore and 11 clay. Each geode robot costs 2 ore and 16 obsidian.
            Blueprint 7: Each ore robot costs 4 ore. Each clay robot costs 4 ore. Each obsidian robot costs 4 ore and 8 clay. Each geode robot costs 2 ore and 15 obsidian.
            Blueprint 8: Each ore robot costs 3 ore. Each clay robot costs 4 ore. Each obsidian robot costs 2 ore and 11 clay. Each geode robot costs 2 ore and 10 obsidian.
            Blueprint 9: Each ore robot costs 3 ore. Each clay robot costs 3 ore. Each obsidian robot costs 3 ore and 16 clay. Each geode robot costs 3 ore and 9 obsidian.
            Blueprint 10: Each ore robot costs 4 ore. Each clay robot costs 2 ore. Each obsidian robot costs 2 ore and 16 clay. Each geode robot costs 2 ore and 8 obsidian.
            Blueprint 11: Each ore robot costs 3 ore. Each clay robot costs 4 ore. Each obsidian robot costs 4 ore and 5 clay. Each geode robot costs 3 ore and 12 obsidian.
            Blueprint 12: Each ore robot costs 4 ore. Each clay robot costs 4 ore. Each obsidian robot costs 3 ore and 7 clay. Each geode robot costs 4 ore and 20 obsidian.
            Blueprint 13: Each ore robot costs 3 ore. Each clay robot costs 4 ore. Each obsidian robot costs 4 ore and 18 clay. Each geode robot costs 2 ore and 11 obsidian.
            Blueprint 14: Each ore robot costs 4 ore. Each clay robot costs 4 ore. Each obsidian robot costs 4 ore and 20 clay. Each geode robot costs 2 ore and 8 obsidian.
            Blueprint 15: Each ore robot costs 4 ore. Each clay robot costs 4 ore. Each obsidian robot costs 4 ore and 10 clay. Each geode robot costs 2 ore and 7 obsidian.
            Blueprint 16: Each ore robot costs 4 ore. Each clay robot costs 4 ore. Each obsidian robot costs 4 ore and 9 clay. Each geode robot costs 2 ore and 20 obsidian.
            Blueprint 17: Each ore robot costs 4 ore. Each clay robot costs 4 ore. Each obsidian robot costs 4 ore and 17 clay. Each geode robot costs 2 ore and 13 obsidian.
            Blueprint 18: Each ore robot costs 4 ore. Each clay robot costs 4 ore. Each obsidian robot costs 2 ore and 16 clay. Each geode robot costs 4 ore and 16 obsidian.
            Blueprint 19: Each ore robot costs 4 ore. Each clay robot costs 4 ore. Each obsidian robot costs 2 ore and 7 clay. Each geode robot costs 4 ore and 13 obsidian.
            Blueprint 20: Each ore robot costs 3 ore. Each clay robot costs 3 ore. Each obsidian robot costs 2 ore and 14 clay. Each geode robot costs 3 ore and 17 obsidian.
            Blueprint 21: Each ore robot costs 3 ore. Each clay robot costs 3 ore. Each obsidian robot costs 3 ore and 19 clay. Each geode robot costs 3 ore and 19 obsidian.
            Blueprint 22: Each ore robot costs 4 ore. Each clay robot costs 4 ore. Each obsidian robot costs 4 ore and 7 clay. Each geode robot costs 2 ore and 16 obsidian.
            Blueprint 23: Each ore robot costs 3 ore. Each clay robot costs 3 ore. Each obsidian robot costs 3 ore and 19 clay. Each geode robot costs 3 ore and 17 obsidian.
            Blueprint 24: Each ore robot costs 3 ore. Each clay robot costs 3 ore. Each obsidian robot costs 2 ore and 20 clay. Each geode robot costs 2 ore and 20 obsidian.
            Blueprint 25: Each ore robot costs 4 ore. Each clay robot costs 4 ore. Each obsidian robot costs 4 ore and 14 clay. Each geode robot costs 3 ore and 16 obsidian.
            Blueprint 26: Each ore robot costs 4 ore. Each clay robot costs 4 ore. Each obsidian robot costs 3 ore and 5 clay. Each geode robot costs 3 ore and 18 obsidian.
            Blueprint 27: Each ore robot costs 3 ore. Each clay robot costs 4 ore. Each obsidian robot costs 2 ore and 19 clay. Each geode robot costs 2 ore and 12 obsidian.
            Blueprint 28: Each ore robot costs 2 ore. Each clay robot costs 4 ore. Each obsidian robot costs 4 ore and 15 clay. Each geode robot costs 2 ore and 20 obsidian.
            Blueprint 29: Each ore robot costs 3 ore. Each clay robot costs 4 ore. Each obsidian robot costs 3 ore and 6 clay. Each geode robot costs 2 ore and 10 obsidian.
            Blueprint 30: Each ore robot costs 4 ore. Each clay robot costs 3 ore. Each obsidian robot costs 3 ore and 7 clay. Each geode robot costs 3 ore and 9 obsidian.
            """;
}
