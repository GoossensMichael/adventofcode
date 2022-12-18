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
import java.util.function.Function;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;

public class Day16 {

    private static final Logger LOGGER = Logger.getLogger(Day16.class.getName());
    public static final String START_VALVE = "AA";

    private static long part1Old(final Map<String, Valve> valvesByName, int minutes) {
        final Valve start = valvesByName.get("AA");
        final int usableValves = valvesByName.values().stream().filter(v -> v.flowRate() > 0).toList().size();


        final List<CaveSTate> previousStates = new ArrayList<>();
        previousStates.add(new CaveSTate(start, new HashSet<>(), 0, 0, minutes));

        boolean minutesLeft = true;
        while (minutesLeft) {
            minutesLeft = false;

            final Set<CaveSTate> newStates = new HashSet<>();

            for (int i = 0; i < previousStates.size(); i++) {
                final CaveSTate currentState = previousStates.get(i);

                if (currentState.remainingMinutes() == 0) {
                    newStates.add(currentState);
                } else {
                    minutesLeft = true;

                    final Valve currentLocation = currentState.location();
                    if (currentState.openValves().size() == usableValves) {
                        // All valves are open
                        newStates.add(new CaveSTate(currentLocation, currentState.openValves(), currentState.flowRate(), currentState.pressureReleased() + currentState.flowRate(), currentState.remainingMinutes() - 1));
                    } else {
                        // Always open a valve when possible.
                        if (!currentState.isLocationValveOpen() && currentLocation.flowRate() > 0) {
                            // Open current valve
                            final Set<Valve> openValves = new HashSet<>(currentState.openValves());
                            openValves.add(currentLocation);
                            newStates.add(new CaveSTate(currentLocation, openValves, currentState.flowRate() + currentLocation.flowRate(), currentState.pressureReleased() + currentState.flowRate(), currentState.remainingMinutes() - 1));
                        }

                        // Change location to all possible positions.
                        for (final Valve newLocation : currentLocation.tunnels()) {
                            newStates.add(new CaveSTate(newLocation, new HashSet<>(currentState.openValves()), currentState.flowRate(), currentState.pressureReleased() + currentState.flowRate(), currentState.remainingMinutes() - 1));
                        }
                    }

                }
            }

            previousStates.clear();
            final List<CaveSTate> cutoffStates = newStates.stream().sorted(Comparator.comparing(CaveSTate::pressureReleased).reversed()).toList();
            previousStates.addAll(cutoffStates.subList(0, Math.min(cutoffStates.size(), 100_000)));
        }

        return previousStates.stream().mapToInt(CaveSTate::pressureReleased).max().orElseThrow();
    }

    private static long part1(final Map<String, Valve> valvesByName) {
        return solve(new HashMap<>(), valvesByName.get(START_VALVE), 30, new ArrayList<>(), valvesByName, 1);
    }

    private static long part2(final Map<String, Valve> valvesByName) {
        return solve(new HashMap<>(), valvesByName.get(START_VALVE), 26, new ArrayList<>(), valvesByName, 2);
    }

    private static long solve(final Map<State, Long> memo, final Valve start, final int minute, final List<Valve> openValves, final Map<String, Valve> valves, final int runners) {
        if (minute == 0) {
            return runners > 1 ? solve(memo, valves.get(START_VALVE), 26, openValves, valves, runners - 1) : 0;
        }

        final State state = new State(start, minute, openValves, runners);
        if (memo.containsKey(state)) {
            return memo.get(state);
        }

        long maxReleasePressure = 0L;
        if (start.flowRate() > 0 && !openValves.contains(start)) {
            openValves.add(start);
            Collections.sort(openValves);
            maxReleasePressure = ((long) (minute - 1) * start.flowRate()) +
                    solve(memo, start, minute - 1, openValves, valves, runners);
            openValves.remove(start);
        }

        for (final Valve valve : start.tunnels()) {
            maxReleasePressure = Math.max(maxReleasePressure, solve(memo, valve, minute - 1, openValves, valves, runners));
        }
        memo.put(state, maxReleasePressure);

        return maxReleasePressure;
    }

    public static void main(final String[] args) {

        // Parsing input
        final var testInput = parse(TST_INPUT.split("\n"));
        final var input = parse(INPUT.split("\n"));

        {
            final var expectedResult = 1651;
            final var part1 = part1(testInput);
            LOGGER.log(Level.INFO, () -> String.format("Test part 1: %d", part1));

            if (expectedResult == part1) {
                final var result = part1(input);
                LOGGER.log(Level.INFO, () -> String.format("Part 1: %d", result));
            }
        }

        {
            final var expectedResult = 1707;
            final var testResult = part2(testInput);
            LOGGER.log(Level.INFO, () -> String.format("Test part 2: %d", testResult));

            if (expectedResult == testResult) {
                final var result = part2(input);
                LOGGER.log(Level.INFO, () -> String.format("Part 2: %d", result));
            }
        }
    }

    private record State(Valve location, int minute, List<Valve> openValves, int runners) {}

    private record CaveSTate(Valve location, Set<Valve> openValves, int flowRate, int pressureReleased, int remainingMinutes) {

        public boolean isLocationValveOpen() {
            return openValves.contains(location);
        }

        @Override
        public String toString() {
            return String.format("%02d - %s (flowRate: %d, pressureReleased: %d)", remainingMinutes, location.name(), flowRate, pressureReleased);
        }
    }

    private record Valve(String name, int flowRate, List<Valve> tunnels) implements Comparable<Valve> {

        @Override
        public String toString() {
            return String.format("Valve %s has flow rate=%d; tunnels lead to valves %s", name, flowRate,
                    tunnels.stream().map(Valve::name).collect(Collectors.joining(", ")));
        }

        @Override
        public int hashCode() {
            return name.hashCode();
        }

        @Override
        public boolean equals(final Object o) {
            return name.equals(((Valve) o).name);
        }

        @Override
        public int compareTo(final Valve o) {
            return name.compareTo(o.name);
        }
    }

    private static Map<String, Valve> parse(final String[] input) {
        final Map<String, Valve> valves = Arrays.stream(input)
                .map(Day16::toValve)
                .collect(Collectors.toMap(Valve::name, Function.identity()));

        linkValves(input, valves);

        return valves;
    }

    private static void linkValves(final String[] input, final Map<String, Valve> valves) {
        Arrays.stream(input)
                .forEach(i -> {
                    final String name = resolveValveName(i);
                    final Valve valve = valves.get(name);

                    final int indexOfFirstComma = (i.indexOf(',') != -1 ? i.indexOf(',') : i.length());
                    valve.tunnels().addAll(Arrays.stream(i.substring(indexOfFirstComma - 2).split(", "))
                            .map(valves::get)
                            .toList());

                });
    }

    private static Valve toValve(final String input) {
        final String name = resolveValveName(input);
        final int flowRate = Integer.parseInt(input.substring(input.indexOf("=") + 1, input.indexOf(';')));

        return new Valve(name, flowRate, new ArrayList<>());
    }

    private static String resolveValveName(final String input) {
        return input.substring(6, 8);
    }

    private static final String TST_INPUT = """
            Valve AA has flow rate=0; tunnels lead to valves DD, II, BB
            Valve BB has flow rate=13; tunnels lead to valves CC, AA
            Valve CC has flow rate=2; tunnels lead to valves DD, BB
            Valve DD has flow rate=20; tunnels lead to valves CC, AA, EE
            Valve EE has flow rate=3; tunnels lead to valves FF, DD
            Valve FF has flow rate=0; tunnels lead to valves EE, GG
            Valve GG has flow rate=0; tunnels lead to valves FF, HH
            Valve HH has flow rate=22; tunnel leads to valve GG
            Valve II has flow rate=0; tunnels lead to valves AA, JJ
            Valve JJ has flow rate=21; tunnel leads to valve II
            """;

    public static final String INPUT = """
            Valve QJ has flow rate=11; tunnels lead to valves HB, GL
            Valve VZ has flow rate=10; tunnel leads to valve NE
            Valve TX has flow rate=19; tunnels lead to valves MG, OQ, HM
            Valve ZI has flow rate=5; tunnels lead to valves BY, ON, RU, LF, JR
            Valve IH has flow rate=0; tunnels lead to valves YB, QS
            Valve QS has flow rate=22; tunnel leads to valve IH
            Valve QB has flow rate=0; tunnels lead to valves QX, ES
            Valve NX has flow rate=0; tunnels lead to valves UH, OP
            Valve PJ has flow rate=0; tunnels lead to valves OC, UH
            Valve OR has flow rate=6; tunnels lead to valves QH, BH, HB, JD
            Valve OC has flow rate=7; tunnels lead to valves IZ, JR, TA, ZH, PJ
            Valve UC has flow rate=0; tunnels lead to valves AA, BY
            Valve QX has flow rate=0; tunnels lead to valves AA, QB
            Valve IZ has flow rate=0; tunnels lead to valves OC, SX
            Valve AG has flow rate=13; tunnels lead to valves NW, GL, SM
            Valve ON has flow rate=0; tunnels lead to valves MO, ZI
            Valve XT has flow rate=18; tunnels lead to valves QZ, PG
            Valve AX has flow rate=0; tunnels lead to valves UH, MO
            Valve JD has flow rate=0; tunnels lead to valves OR, SM
            Valve HM has flow rate=0; tunnels lead to valves TX, QH
            Valve LF has flow rate=0; tunnels lead to valves ZI, UH
            Valve QH has flow rate=0; tunnels lead to valves OR, HM
            Valve RT has flow rate=21; tunnel leads to valve PG
            Valve NE has flow rate=0; tunnels lead to valves VZ, TA
            Valve OQ has flow rate=0; tunnels lead to valves TX, GE
            Valve AA has flow rate=0; tunnels lead to valves QZ, UC, OP, QX, EH
            Valve UH has flow rate=17; tunnels lead to valves PJ, NX, AX, LF
            Valve GE has flow rate=0; tunnels lead to valves YB, OQ
            Valve EH has flow rate=0; tunnels lead to valves AA, MO
            Valve MG has flow rate=0; tunnels lead to valves TX, NW
            Valve YB has flow rate=20; tunnels lead to valves IH, GE, XG
            Valve MO has flow rate=15; tunnels lead to valves EH, ON, AX, ZH, CB
            Valve JR has flow rate=0; tunnels lead to valves ZI, OC
            Valve GL has flow rate=0; tunnels lead to valves AG, QJ
            Valve SM has flow rate=0; tunnels lead to valves JD, AG
            Valve HB has flow rate=0; tunnels lead to valves OR, QJ
            Valve TA has flow rate=0; tunnels lead to valves OC, NE
            Valve PG has flow rate=0; tunnels lead to valves RT, XT
            Valve XG has flow rate=0; tunnels lead to valves CB, YB
            Valve ES has flow rate=9; tunnels lead to valves QB, FL
            Valve BH has flow rate=0; tunnels lead to valves RU, OR
            Valve FL has flow rate=0; tunnels lead to valves SX, ES
            Valve CB has flow rate=0; tunnels lead to valves MO, XG
            Valve QZ has flow rate=0; tunnels lead to valves AA, XT
            Valve BY has flow rate=0; tunnels lead to valves UC, ZI
            Valve ZH has flow rate=0; tunnels lead to valves MO, OC
            Valve OP has flow rate=0; tunnels lead to valves NX, AA
            Valve NW has flow rate=0; tunnels lead to valves MG, AG
            Valve RU has flow rate=0; tunnels lead to valves ZI, BH
            Valve SX has flow rate=16; tunnels lead to valves IZ, FL
            """;
}
