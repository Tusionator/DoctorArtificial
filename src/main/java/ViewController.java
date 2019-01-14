import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.List;

public class ViewController {
    @FXML
    private GridPane parametersGrid;
    @FXML
    private TextArea doctorAnswer;
    private NumberFormat formatter = new DecimalFormat("#0.0");
    private FuzzyDoctor fuzzyDoctor = new FuzzyDoctor();
    private List<String> parametesNames = new ArrayList<>();


    public ViewController() {

    }

    @FXML
    private void initialize() {
        parametesNames.clear();
        fuzzyDoctor.getInputVariables().forEach(this::addParameter);
    }

    private void addParameter(String parameterName) {
        Label newLabel = new Label(parameterName.replace("in_", " "));
        TextField newTextField = new TextField();
        newTextField.setId(parameterName);

        parametersGrid.getRowConstraints().size();
        parametersGrid.addRow(parametesNames.size(), newLabel, newTextField);
        parametesNames.add(parameterName);
    }

    @FXML
    public void getDoctorAnalysis() {
        List<Parameter> parameters = getParametersFromFields();
        FuzzyDoctor fuzzyDoctor = new FuzzyDoctor();
        List<Disease> diseases = fuzzyDoctor.makeAnalysis(parameters);
        fuzzyDoctor.showFuzzyficationCharts();
        fuzzyDoctor.showResultsCharts();
        printAnswer(diseases);
    }

    private List<Parameter> getParametersFromFields() {
        List<Parameter> parameters = new ArrayList<>();
        parametersGrid.getChildren().stream()
                .filter(child -> parametesNames.contains(child.getId())
                        && ((TextField) child).getText() != null
                        && !((TextField) child).getText().equals(""))
                .forEach(child -> parameters.add(getParameterFromField((TextField) child)));
        return parameters;
    }

    private Parameter getParameterFromField(TextField parameterField) {
        return new Parameter(parameterField.getId(), Double.parseDouble(parameterField.getText()));
    }

    private void printAnswer(List<Disease> diseases) {
        doctorAnswer.clear();
        diseases.stream()
                .filter(disease -> disease.getValue() > -1)
                .forEach(disease -> doctorAnswer.appendText(getDiseaseDescription(disease)));
    }

    private String getDiseaseDescription(Disease disease) {

        return disease.getName().replace("_", " ") + " : "
                + "prawdopodobienstwo wystapienia " + formatter.format(disease.getValue()) + "% ("
                + getVerboseProbability(disease.getValue()) + ")\n";
    }

    private String getVerboseProbability(double probability) {
        if (probability < 30) {
            return "male prawdopodobienstwo";
        } else if (probability < 70) {
            return "srednie prawdopobodienstwo, zalecane wiecej badan";
        } else {
            return "bardzo wysokie prawdopodobienstwo, konieczna wizyta u prawdziwego lekarza";
        }
    }

}
