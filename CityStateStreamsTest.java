import java.util.*;
import java.util.stream.*;
import java.math.BigDecimal;
import java.math.RoundingMode;

public class CityStateStreamsTest {
    // arg0: number of states
    // arg1: number of cities per states
    // arg2: number of testing rounds
    public static void main(String... args) {
        // Setup
        Integer numOfStates = Integer.valueOf(args[0]); // 10000
        Integer numOfCitiesPerState = Integer.valueOf(args[1]); // 100
        Integer rounds = Integer.valueOf(args[2]); // 10
        List<State> states = setup(numOfStates, numOfCitiesPerState);
        
        // Run tests
        List<BigDecimal> results = new ArrayList<BigDecimal>();
        for(Integer i = 1; i <= rounds; i++) {
            println("--- Begin round " + i + "\n");
            BigDecimal result = doTest(states);
            println("*** *** ***");
            println("End round " + i + ". Stream was " + result.doubleValue() + "% faster");
            println("*** *** ***\n");
            results.add(result);
        }

        // Results
        Stream<BigDecimal> bds = results.stream();
        DoubleStream ds = bds.mapToDouble(bd -> bd.doubleValue());
        OptionalDouble avg = ds.average();
        println("Streams averaged " + 
                new BigDecimal(avg.getAsDouble()).setScale(2, RoundingMode.UP) + "% faster");
    }

    private static List<State> setup(Integer numOfStates, Integer numOfCitiesPerState) {
        Random rnd = new Random();
        List<State> states = new ArrayList<State>();

        List<City> newCities;
        State newState;
        println("\nPopulating states...\n");
        for(Integer i = 0; i < numOfStates; i++) {
            newState = new State(UUID.randomUUID().toString());
            newCities = new ArrayList<City>();
            for(Integer j = 0; j < numOfCitiesPerState; j++) {
                newCities.add(
                        new City(
                            UUID.randomUUID().toString(), 
                            Integer.valueOf(rnd.nextInt(100) + 1)
                            )
                        );
            }
            newState.setCities(newCities);
            states.add(newState);
        }
        return states;
    }


    private static BigDecimal doTest(List<State> states) {
        // Iterative
        println("Start iterative section");
        Long startTime = System.currentTimeMillis();

        Integer iterTotal = 0;
        for(State state: states) {
            for(City city: state.getCities()) {
                iterTotal += city.getPopulation();
            }
        }

        Long endTime = System.currentTimeMillis();
        Long iterTime = endTime - startTime;
        println("Iteration counted " + iterTotal + " cities.");
        println("End iterative section; took " + iterTime + " ms\n");

        // Stream based
        println("Start stream section");
        startTime = System.currentTimeMillis();

        int streamTotal = 
            states.stream()
                .flatMap(state -> state.getCities().stream())
                .mapToInt(City::getPopulation)
                .sum();

        println("Stream API counted " + streamTotal + " cities.");
        endTime = System.currentTimeMillis();
        Long streamTime = endTime - startTime;
        println("End stream section; took " + streamTime + " ms\n");

        // Comparison
        Long diff = iterTime - streamTime;
        if(iterTime != 0) {
            Double pctInc = Double.valueOf(diff) / Double.valueOf(iterTime);
            BigDecimal res = new BigDecimal(pctInc);
            res = res.multiply(BigDecimal.valueOf(100));
            return res.setScale(2, RoundingMode.UP);
        }
        return new BigDecimal(0);
    }

    private static void println(String s) {
        System.out.println(s);
    }
}
