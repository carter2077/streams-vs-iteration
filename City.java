public class City {
    private String name;
    private Integer population;

    City(String name, Integer population) {
        this.name = name;
        this.population = population;
    }

    String getName() {
        return this.name;
    }

    Integer getPopulation() {
        return this.population;
    }
}

