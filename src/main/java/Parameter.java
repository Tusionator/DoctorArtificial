public class Parameter {
    private String name;
    private String label;
    private String unit;
    private double value;

    public Parameter(String name, String label, String unit, double value) {
        this.name = name;
        this.label = label;
        this.unit = unit;
        this.value = value;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getLabel() {
        return label;
    }

    public void setLabel(String label) {
        this.label = label;
    }

    public String getUnit() {
        return unit;
    }

    public void setUnit(String unit) {
        this.unit = unit;
    }

    public double getValue() {
        return value;
    }

    public void setValue(double value) {
        this.value = value;
    }
}
