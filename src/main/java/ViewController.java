import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.GridPane;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.List;

public class ViewController {
    @FXML
    private GridPane parametersGrid;
    @FXML
    private GridPane doctorAnswer;
    private NumberFormat formatter = new DecimalFormat("#0.0");
    private FuzzyDoctor fuzzyDoctor = new FuzzyDoctor();
    private List<String> parametersNames = new ArrayList<>();


    public ViewController() {

    }

    @FXML
    private void initialize() {
        parametersNames.clear();
        parametersGrid.setVgap(10.0);
        fuzzyDoctor.getInputVariables().forEach(this::addParameter);
    }

    private void addParameter(String parameterName) {
        Label newLabel = new Label(parameterName.replace("in_", "").replace("_", " "));
        newLabel.setWrapText(true);
        TextField newTextField = new TextField();
        newTextField.setId(parameterName);
        Button showChartButton = new Button();

        Image image = new Image(getClass().getResourceAsStream("chart.png"));
        showChartButton.setGraphic(new ImageView(image));

        showChartButton.setOnMouseClicked(new ShowParameterChartButtonClickHandler(parameterName, fuzzyDoctor));

        parametersGrid.getRowConstraints().size();
        parametersGrid.addRow(parametersNames.size(), showChartButton, newLabel, newTextField);
        parametersNames.add(parameterName);
    }

    @FXML
    public void getDoctorAnalysis() {
        List<Parameter> parameters = getParametersFromFields();
        List<Disease> diseases = fuzzyDoctor.makeAnalysis(parameters);
//        fuzzyDoctor.showFuzzyficationCharts();
//        fuzzyDoctor.showResultsCharts();
        printAnswer(diseases);
    }

    private List<Parameter> getParametersFromFields() {
        List<Parameter> parameters = new ArrayList<>();
        parametersGrid.getChildren().stream()
                .filter(child -> parametersNames.contains(child.getId())
                        && ((TextField) child).getText() != null
                        && !((TextField) child).getText().equals(""))
                .forEach(child -> parameters.add(getParameterFromField((TextField) child)));
        return parameters;
    }

    private Parameter getParameterFromField(TextField parameterField) {
        return new Parameter(parameterField.getId(), Double.parseDouble(parameterField.getText()));
    }

    private void printAnswer(List<Disease> diseases) {
        doctorAnswer.getChildren().clear();
        doctorAnswer.setVgap(20.0);
        doctorAnswer.setHgap(5.0);
        for (int i = 0; i < diseases.size(); i++) {
            addDiseaseAnswerRow(diseases.get(i), i + 1);
        }
    }

    private void addDiseaseAnswerRow(Disease disease, int index) {
        if (disease.getValue() < 0) {
            Label emptyLabel = new Label();
            Label diseaseNameLabel = new Label(disease.getName().replace("out_", "")
                    .replace("_", " ") + " : ");
            diseaseNameLabel.setWrapText(true);
            Label infoLabel = new Label("podano za mało danych, aby określenie było możliwe.");
            infoLabel.setWrapText(true);
            doctorAnswer.addRow(index, emptyLabel, diseaseNameLabel, infoLabel);
        } else {
            Label diseaseNameLabel = new Label(disease.getName().replace("out_", "")
                    .replace("_", " ") + " : ");
            diseaseNameLabel.setWrapText(true);
            Label answer = new Label(getDiseaseDescription(disease));
            answer.setWrapText(true);
            Button showChartButton = new Button();
            Image image = new Image(getClass().getResourceAsStream("chart.png"));
            showChartButton.setGraphic(new ImageView(image));
            showChartButton.setOnMouseClicked(
                    new ShowDiseaseAnswerChartButtonClickHandler(disease.getName(), fuzzyDoctor));

            doctorAnswer.getRowConstraints().size();
            doctorAnswer.addRow(index, showChartButton, diseaseNameLabel, answer);
        }
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
