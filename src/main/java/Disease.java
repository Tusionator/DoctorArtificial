import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Disease {
    public static final List<String> DISEASES_NAMES = new ArrayList<>(
            Arrays.asList(
                    "nadcisnienie_krwi"));

    private String name;
    private double value;

    public Disease(String name, double value) {
        this.name = name;
        this.value = value;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public double getValue() {
        return value;
    }

    public void setValue(double value) {
        this.value = value;
    }
}
