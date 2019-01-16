import net.sourceforge.jFuzzyLogic.FIS;
import net.sourceforge.jFuzzyLogic.FunctionBlock;
import net.sourceforge.jFuzzyLogic.plot.JFuzzyChart;
import net.sourceforge.jFuzzyLogic.rule.Variable;

import java.util.List;
import java.util.stream.Collectors;

public class FuzzyDoctor {
    private static final String FUNCTION_BLOCK_NAME = "fuzzyLogicDoctor";
    private FIS fis;
    private FunctionBlock functionBlock;


    public FuzzyDoctor() {
        initDoctor();
    }

    public List<Disease> makeAnalysis(List<Parameter> parameters) {
        setParameters(parameters);
        evaluate();
        return getResults();
    }

    private void setParameters(List<Parameter> parameters) {
        parameters.forEach(parameter -> fis.setVariable(parameter.getName(), parameter.getValue()));
    }

    private void evaluate() {
        fis.evaluate();
    }

    private List<Disease> getResults() {
        return getOutputVariables().stream()
                .map(diseaseName -> new Disease(diseaseName, "", "", fis.getVariable(diseaseName).getValue()))
                .collect(Collectors.toList());
    }

    private void initDoctor() {
        // Load from 'FCL' file
        String fileName = "./src/main/resources/diseases.fcl";
        fis = FIS.load(fileName, true);

        // Error while loading?
        if (fis == null) {
            System.err.println("Can't load file: '" + fileName + "'");
        }

        functionBlock = fis.getFunctionBlock(FUNCTION_BLOCK_NAME);
    }

    public List<String> getInputVariables() {
        return functionBlock.getVariables().keySet().stream()
                .filter(s -> s.matches("in_\\S+")).collect(Collectors.toList());
    }

    private List<String> getOutputVariables() {
        return functionBlock.getVariables().keySet().stream()
                .filter(s -> s.matches("out_\\S+")).collect(Collectors.toList());
    }

    public void showDiseasesChart(String diseaseName) {
        Variable disease = functionBlock.getVariable(diseaseName);
        JFuzzyChart.get().chart(disease, disease.getDefuzzifier(), true);
    }

    public void showParameterChart(String diseaseName) {
        Variable disease = functionBlock.getVariable(diseaseName);
        JFuzzyChart.get().chart(disease, true);
    }
}
