package git.goossensmichael;

import git.goossensmichael.utils.Pair;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.LongStream;

public class Day15 {

    private static final Logger LOGGER = Logger.getLogger(Day15.class.getName());

    private static long part1(final List<Pair<Sensor, Beacon>> pairs, final long y) {
        List<Range> impossibleBeaconRanges = determineImpossibleBeaconRanges(pairs, y);

        return impossibleBeaconRanges.stream()
                .mapToLong(range -> Math.abs(range.end() - range.begin()) + 1)
                .sum()
                -
                pairs.stream()
                        .map(Pair::right)
                        .distinct()
                        .filter(beacon -> beacon.y() == y && impossibleBeaconRanges.stream().anyMatch(r -> r.overlapsOrExtends(new Range(beacon.x(), beacon.x()))))
                        .count();
    }

    private static List<Range> determineImpossibleBeaconRanges(final List<Pair<Sensor, Beacon>> pairs, final long y) {
        final List<Range> impossibleBeaconRangesAtY = pairs.stream()
                .map(pair -> beaconLessPositions(pair, y))
                .filter(Optional::isPresent)
                .map(Optional::get)
                .toList();

        return mergeRanges(impossibleBeaconRangesAtY);
    }

    private static List<Range> mergeRanges(final List<Range> impossibleBeaconRangesAtY) {
        List<Range> impossibleBeaconRanges = new ArrayList<>();
        for (final Range impossibleBeaconRange : impossibleBeaconRangesAtY) {
            int overlapBegin = -1;
            int insertionPosition = 0;

            boolean loop = true;
            int i = 0;
            while (i < impossibleBeaconRanges.size() && loop) {
                if (overlapBegin == -1 && impossibleBeaconRanges.get(i).overlapsOrExtends(impossibleBeaconRange)) {
                    overlapBegin = i;
                }

                if (overlapBegin != -1 && !impossibleBeaconRanges.get(i).overlapsOrExtends(impossibleBeaconRange)) {
                    i--;
                    loop = false;
                }

                if (impossibleBeaconRange.begin() > impossibleBeaconRanges.get(i).begin()) {
                    insertionPosition++;
                }
                i++;
            }

            if (overlapBegin != -1) {
                final List<Range> mergeRange = new ArrayList<>(impossibleBeaconRanges.subList(overlapBegin, i));
                mergeRange.add(impossibleBeaconRange);
                final long lowerBound = mergeRange.stream().mapToLong(Range::begin).min().orElseThrow();
                final long upperBound = mergeRange.stream().mapToLong(Range::end).max().orElseThrow();

                impossibleBeaconRanges.removeAll(mergeRange);
                impossibleBeaconRanges.add(overlapBegin, new Range(lowerBound, upperBound));
            } else {
                impossibleBeaconRanges.add(insertionPosition, impossibleBeaconRange);
            }

        }
        return impossibleBeaconRanges;
    }

    private static Optional<Range> beaconLessPositions(final Pair<Sensor, Beacon> pair, final long y) {
        final long manhattanDistance = getManhattanDistance(pair);

        // Distance from the sensor position to the y line.
        final long yDistance = Math.abs(pair.left().y() - y);

        // Range at the y-line level of impossible positions
        final long xRange = manhattanDistance - yDistance;

        final Optional<Range> impossibleBeaconRange;
        if (xRange >= 0) {
            impossibleBeaconRange = Optional.of(new Range(pair.left().x() - xRange, pair.left().x() + xRange));
        } else {
            impossibleBeaconRange = Optional.empty();
        }

        return impossibleBeaconRange;
    }

    private static long getManhattanDistance(final Pair<Sensor, Beacon> pair) {
        return Math.abs(pair.left().x() - pair.right().x()) + Math.abs(pair.left().y() - pair.right().y());
    }

    private static long part2(final List<Pair<Sensor, Beacon>> pairs, final long dimension) {
        final Map<Long, List<Range>> rangesByLine = new HashMap<>();

        return LongStream.range(0L, dimension + 1L)
                .mapToObj(y -> {
                    final List<Range> impossibleBeaconRanges = determineImpossibleBeaconRanges(pairs, y);
                    rangesByLine.put(y, impossibleBeaconRanges);
                    return new Pair<>(y, impossibleBeaconRanges);
                })
                .filter(pair -> pair.right().size() > 1)
                .findFirst()
                .map(identifyingPair -> (identifyingPair.right().get(0).end() + 1L) * 4_000_000L + identifyingPair.left())
                .orElseThrow();
    }

    public static void main(final String[] args) {

        // Parsing input
        final var testInput = parse(TST_INPUT.split("\n"));
        final var input = parse(INPUT.split("\n"));

        {
            final var expectedResult = 26;
            final var part1 = part1(testInput, 10);
            LOGGER.log(Level.INFO, () -> String.format("Test part 1: %d", part1));

            if (expectedResult == part1) {
                final var result = part1(input, 2000000);
                LOGGER.log(Level.INFO, () -> String.format("Part 1: %d", result));
            }
        }

        {
            final var expectedResult = 56000011;
            final var testResult = part2(testInput, 30);
            LOGGER.log(Level.INFO, () -> String.format("Test part 2: %d", testResult));

            if (expectedResult == testResult) {
                final var result = part2(input, 4000000);
                LOGGER.log(Level.INFO, () -> String.format("Part 2: %d", result));
            }
        }
    }

    private static List<Pair<Sensor, Beacon>> parse(final String[] input) {
        return Arrays.stream(input)
                .map(line -> new Pair<>(
                        new Sensor(
                                Long.parseLong(line.substring(line.indexOf("x=") + 2, line.indexOf("y=") - 2)),
                                Long.parseLong(line.substring(line.indexOf("y=") + 2, line.indexOf(':')))),
                        new Beacon(
                                Long.parseLong(line.substring(line.lastIndexOf("x=") + 2, line.lastIndexOf("y=") - 2)),
                                Long.parseLong(line.substring(line.lastIndexOf("y=") + 2)))))
                .toList();
    }

    private record Sensor(long x, long y) {}

    private record Beacon(long x, long y) {}

    private record Range(long begin, long end) {

        public boolean overlapsOrExtends(final Range other) {
            return overlaps(other) || extendsRange(other);
        }

        private boolean extendsRange(final Range other) {
            return end + 1 == other.begin() || other.end() + 1 == begin;
        }

        private boolean overlaps(final Range other) {
            return begin <= other.end() && end >= other.begin();
        }
    }

    private static final String TST_INPUT = """
            Sensor at x=2, y=18: closest beacon is at x=-2, y=15
            Sensor at x=9, y=16: closest beacon is at x=10, y=16
            Sensor at x=13, y=2: closest beacon is at x=15, y=3
            Sensor at x=12, y=14: closest beacon is at x=10, y=16
            Sensor at x=10, y=20: closest beacon is at x=10, y=16
            Sensor at x=14, y=17: closest beacon is at x=10, y=16
            Sensor at x=8, y=7: closest beacon is at x=2, y=10
            Sensor at x=2, y=0: closest beacon is at x=2, y=10
            Sensor at x=0, y=11: closest beacon is at x=2, y=10
            Sensor at x=20, y=14: closest beacon is at x=25, y=17
            Sensor at x=17, y=20: closest beacon is at x=21, y=22
            Sensor at x=16, y=7: closest beacon is at x=15, y=3
            Sensor at x=14, y=3: closest beacon is at x=15, y=3
            Sensor at x=20, y=1: closest beacon is at x=15, y=3
            """;

    private static final String INPUT = """
            Sensor at x=2885528, y=2847539: closest beacon is at x=2966570, y=2470834
            Sensor at x=2224704, y=1992385: closest beacon is at x=2018927, y=2000000
            Sensor at x=3829144, y=1633329: closest beacon is at x=2966570, y=2470834
            Sensor at x=43913, y=426799: closest beacon is at x=152363, y=369618
            Sensor at x=2257417, y=2118161: closest beacon is at x=2386559, y=2090397
            Sensor at x=8318, y=3994839: closest beacon is at x=-266803, y=2440278
            Sensor at x=69961, y=586273: closest beacon is at x=152363, y=369618
            Sensor at x=3931562, y=3361721: closest beacon is at x=3580400, y=3200980
            Sensor at x=476279, y=3079924: closest beacon is at x=-266803, y=2440278
            Sensor at x=2719185, y=2361091: closest beacon is at x=2966570, y=2470834
            Sensor at x=2533382, y=3320911: closest beacon is at x=2260632, y=3415930
            Sensor at x=3112735, y=3334946: closest beacon is at x=3580400, y=3200980
            Sensor at x=1842258, y=3998928: closest beacon is at x=2260632, y=3415930
            Sensor at x=3712771, y=3760832: closest beacon is at x=3580400, y=3200980
            Sensor at x=1500246, y=2684955: closest beacon is at x=2018927, y=2000000
            Sensor at x=3589321, y=142859: closest beacon is at x=4547643, y=-589891
            Sensor at x=1754684, y=2330721: closest beacon is at x=2018927, y=2000000
            Sensor at x=2476631, y=3679883: closest beacon is at x=2260632, y=3415930
            Sensor at x=27333, y=274008: closest beacon is at x=152363, y=369618
            Sensor at x=158732, y=2405833: closest beacon is at x=-266803, y=2440278
            Sensor at x=2955669, y=3976939: closest beacon is at x=3035522, y=4959118
            Sensor at x=1744196, y=13645: closest beacon is at x=152363, y=369618
            Sensor at x=981165, y=1363480: closest beacon is at x=2018927, y=2000000
            Sensor at x=2612279, y=2151377: closest beacon is at x=2386559, y=2090397
            Sensor at x=3897, y=2076376: closest beacon is at x=-266803, y=2440278
            Sensor at x=2108479, y=1928318: closest beacon is at x=2018927, y=2000000
            Sensor at x=1913043, y=3017841: closest beacon is at x=2260632, y=3415930
            Sensor at x=2446778, y=785075: closest beacon is at x=2386559, y=2090397
            Sensor at x=2385258, y=2774943: closest beacon is at x=2386559, y=2090397
            Sensor at x=3337656, y=2916144: closest beacon is at x=3580400, y=3200980
            Sensor at x=380595, y=66906: closest beacon is at x=152363, y=369618
            Sensor at x=1593628, y=3408455: closest beacon is at x=2260632, y=3415930
            """;
}
