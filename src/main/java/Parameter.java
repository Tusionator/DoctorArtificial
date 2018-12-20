import com.sun.org.apache.xerces.internal.xs.StringList;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class Parameter {
    public static final List<String> PARAMETERS_NAMES = new ArrayList<>(
            Arrays.asList(
                    "cisnienie_krwi"));


    private String name;
    private double value;

    public Parameter(String name, double value) {
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
