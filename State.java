import java.util.List;

public class State {
    private String name;
    private List<City> cities;

    State(String name, List<City> cities) {
        this.name = name;
        this.cities = cities;
    }

    State(String name) {
        this.name = name;
    }

    String getName() {
        return this.name;
    }

    List<City> getCities() {
        return this.cities;
    }

    void setCities(List<City> cities) {
        this.cities = cities;
    }
}
