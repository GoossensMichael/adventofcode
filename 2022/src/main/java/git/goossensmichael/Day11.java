package git.goossensmichael;

import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicLong;
import java.util.function.Function;
import java.util.stream.Collectors;

public class Day11 {

    public static void main(final String[] args) {
        // Parsing input
        final String[] input = INPUT.split("\n\n");

        {
            final long monkeyBusiness = calculateMonkeyBusiness(input, 3L, 20);
            System.out.println("Part 1:" + monkeyBusiness);
        }

        {
            final long monkeyBusiness = calculateMonkeyBusiness(input, 1L, 10_000);
            System.out.println("Part 2:" + monkeyBusiness);
        }
    }

    private static long calculateMonkeyBusiness(final String[] input, final long worryLevelDivisor, final int roundsToPlay) {
        final Map<Integer, Monkey> monkeys = Arrays.stream(input)
                .map(Day11::mapToMonkey)
                .collect(Collectors.toMap(Monkey::id, m -> m));

        final long superModulo = monkeys.values().stream().mapToLong(Monkey::divider).reduce(1, (a, c) -> a * c);

        for (int round = 0; round < roundsToPlay; round++) {
            for (final Monkey monkey : monkeys.values()) {
                for (Long item : monkey.items) {
                    final Long newValue;
                    if (worryLevelDivisor > 1) {
                        newValue = (long) Math.floor(monkey.worryFunction().apply(item) / worryLevelDivisor);
                    } else {
                        newValue = monkey.worryFunction().apply(item) % superModulo;
                    }
                    final int newMonkey = monkey.decider().apply(newValue);
                    monkeys.get(newMonkey).items().add(newValue);
                    monkey.inspections().incrementAndGet();
                }
                monkey.items().clear();
            }
        }

        return monkeys.values().stream()
                .map(m -> m.inspections().get())
                .sorted(Comparator.reverseOrder())
                .limit(2)
                .mapToLong(inspections -> inspections)
                .reduce(1L, (acc, cur) -> acc * cur);
    }

    private static Monkey mapToMonkey(final String s) {
        final String[] definition = s.split("\n");

        final int id = Integer.parseInt(definition[0].substring(7, 8));
        final List<Long> items = mapItems(definition[1]);
        final long divider = Long.parseLong(definition[3].substring(21));
        final Function<Long, Long> worryFunction = mapOperation(definition[2]);
        final Function<Long, Integer> decider = mapDecider(Arrays.copyOfRange(definition, 3, 6));

        return new Monkey(id, divider, new AtomicLong(0L), items, worryFunction, decider);
    }

    private static List<Long> mapItems(final String itemDefinition) {
        return Arrays.stream(itemDefinition.substring(18).split(", "))
                .map(Long::parseLong)
                .collect(Collectors.toList());
    }

    private static Function<Long, Integer> mapDecider(final String[] deciderDefinition) {
        final Long divisor = Long.parseLong(deciderDefinition[0].substring(21));

        return (worryLevel) -> (worryLevel % divisor == 0)
                ? Integer.parseInt(deciderDefinition[1].substring(29))
                : Integer.parseInt(deciderDefinition[2].substring(30));
    }

    private static Function<Long, Long> mapOperation(final String worryFunctionDefinition) {
        final String[] parts = worryFunctionDefinition.trim().split(" ");

        final char operation = parts[4].charAt(0);

        return switch (operation) {
            case '*' -> (old) -> ("old".equals(parts[5]) ? old * old : old * Long.parseLong(parts[5]));
            case '+' -> (old) -> ("old".equals(parts[5]) ? old + old : old + Long.parseLong(parts[5]));
            default -> throw new IllegalArgumentException("Did not expect operation " + operation);
        };
    }

    private record Monkey(int id, long divider, AtomicLong inspections, List<Long> items, Function<Long, Long> worryFunction, Function<Long, Integer> decider) {}

    private static final String TST_INPUT = """
            Monkey 0:
              Starting items: 79, 98
              Operation: new = old * 19
              Test: divisible by 23
                If true: throw to monkey 2
                If false: throw to monkey 3
                        
            Monkey 1:
              Starting items: 54, 65, 75, 74
              Operation: new = old + 6
              Test: divisible by 19
                If true: throw to monkey 2
                If false: throw to monkey 0
                        
            Monkey 2:
              Starting items: 79, 60, 97
              Operation: new = old * old
              Test: divisible by 13
                If true: throw to monkey 1
                If false: throw to monkey 3
                        
            Monkey 3:
              Starting items: 74
              Operation: new = old + 3
              Test: divisible by 17
                If true: throw to monkey 0
                If false: throw to monkey 1
            """;

    private static final String INPUT = """
            Monkey 0:
              Starting items: 66, 79
              Operation: new = old * 11
              Test: divisible by 7
                If true: throw to monkey 6
                If false: throw to monkey 7
                        
            Monkey 1:
              Starting items: 84, 94, 94, 81, 98, 75
              Operation: new = old * 17
              Test: divisible by 13
                If true: throw to monkey 5
                If false: throw to monkey 2
                        
            Monkey 2:
              Starting items: 85, 79, 59, 64, 79, 95, 67
              Operation: new = old + 8
              Test: divisible by 5
                If true: throw to monkey 4
                If false: throw to monkey 5
                        
            Monkey 3:
              Starting items: 70
              Operation: new = old + 3
              Test: divisible by 19
                If true: throw to monkey 6
                If false: throw to monkey 0
                        
            Monkey 4:
              Starting items: 57, 69, 78, 78
              Operation: new = old + 4
              Test: divisible by 2
                If true: throw to monkey 0
                If false: throw to monkey 3
                        
            Monkey 5:
              Starting items: 65, 92, 60, 74, 72
              Operation: new = old + 7
              Test: divisible by 11
                If true: throw to monkey 3
                If false: throw to monkey 4
                        
            Monkey 6:
              Starting items: 77, 91, 91
              Operation: new = old * old
              Test: divisible by 17
                If true: throw to monkey 1
                If false: throw to monkey 7
                        
            Monkey 7:
              Starting items: 76, 58, 57, 55, 67, 77, 54, 99
              Operation: new = old + 6
              Test: divisible by 3
                If true: throw to monkey 2
                If false: throw to monkey 1
            """;
}
