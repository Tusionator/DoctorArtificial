import javafx.event.EventHandler;
import javafx.scene.input.MouseEvent;


public class ShowParameterChartButtonClickHandler implements EventHandler<MouseEvent> {

    private String variableName;
    private FuzzyDoctor fuzzyDoctor;

    public ShowParameterChartButtonClickHandler(String variableName, FuzzyDoctor fuzzyDoctor) {
        this.variableName = variableName;
        this.fuzzyDoctor = fuzzyDoctor;
    }

    @Override
    public void handle(MouseEvent event) {
        fuzzyDoctor.showParameterChart(variableName);
    }
}
