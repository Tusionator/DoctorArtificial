import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.util.ArrayList;
import java.util.List;

public class Main extends Application {

    @Override
    public void start(Stage primaryStage) throws Exception {
        Parent root = FXMLLoader.load(getClass().getClassLoader().getResource("view.fxml"));
        primaryStage.setTitle("Hello World");
        primaryStage.setScene(new Scene(root, 800, 600));
        primaryStage.show();

        FuzzyDoctor fuzzyDoctor = new FuzzyDoctor();

        List<Parameter> parameters = new ArrayList<>();
        parameters.add(new Parameter("cisnienie_krwi", 8));
        List<Disease> results = fuzzyDoctor.makeAnalysis(parameters);


        results.forEach(result -> System.out.println(
                result.getName() + "  " +  result.getValue()));
    }


    public static void main(String[] args) {
        launch(args);
    }
}
