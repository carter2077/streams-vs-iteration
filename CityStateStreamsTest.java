import java.util.*;
import java.util.stream.*;
import java.math.BigDecimal;
import java.math.RoundingMode;

public class CityStateStreamsTest {
    public static void main(String... args) {
        // Setup
        Integer numOfStates = 100000;
        Integer numOfCitiesPerState = 100;
        int rounds = 20;
        Random rnd = new Random();
        List<State> states = new ArrayList<State>();

        List<City> newCities;
        State newState;
        for(int i = 0; i < numOfStates; i++) {
            newState = new State(UUID.randomUUID().toString());
            newCities = new ArrayList<City>();
            for(int j = 0; j < numOfCitiesPerState; j++) {
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
        
        List<BigDecimal> results = new ArrayList<BigDecimal>();
        for(int i = 1; i <= rounds; i++) {
            BigDecimal result = doTest(states);
            System.out.println("Round " + i + ": Stream was " + result.doubleValue() + "% faster");
            results.add(result);
        }
        // Maybe try to stick with big decimal instead of doing this
        Stream<BigDecimal> bds = results.stream();
        DoubleStream ds = bds.mapToDouble(bd -> bd.doubleValue());
        OptionalDouble avg = ds.average();

        System.out.println("Streams averaged " + avg.getAsDouble() + "% faster");
    }

    private static BigDecimal doTest(List<State> states) {
        // Iterative
        System.out.println("Start iterative");
        long startTime = System.currentTimeMillis();

        Integer iterTotal = 0;
        for(State state: states) {
            for(City city: state.getCities()) {
                iterTotal += city.getPopulation();
            }
        }

        long endTime = System.currentTimeMillis();
        long iterTime = endTime - startTime;
        System.out.println("Iter total: " + iterTotal);
        System.out.println("End iterative: " + iterTime);

        // Stream based
        System.out.println("Start stream based");
        startTime = System.currentTimeMillis();

        int streamTotal = 
            states.stream()
                .flatMap(state -> state.getCities().stream())
                .mapToInt(City::getPopulation)
                .sum();

        System.out.println("Stream total: " + streamTotal);
        endTime = System.currentTimeMillis();
        long streamTime = endTime - startTime;
        System.out.println("End stream based: " + streamTime);
        long diff = iterTime - streamTime;
        double pctInc = ((double) diff) / ((double) iterTime);
        BigDecimal res = new BigDecimal(pctInc);
        res = res.multiply(BigDecimal.valueOf(100));
        return res.setScale(2, RoundingMode.UP);
    }
}
