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
import java.util.Optional;
import java.util.stream.Collectors;

import java.io.File;
import java.io.IOException;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

public class ViewController {
    @FXML
    private GridPane parametersGrid;
    @FXML
    private GridPane doctorAnswer;
    private NumberFormat formatter = new DecimalFormat("#0.0");
    private FuzzyDoctor fuzzyDoctor = new FuzzyDoctor();
    //private List<String> parametersNames = new ArrayList<>();
    private List<Parameter> paramList = new ArrayList<Parameter>();
    private List<Disease> diseaseList = new ArrayList<Disease>();

    public ViewController() {

    }

    @FXML
    private void initialize() {
        //parametersNames.clear();
        parametersGrid.setVgap(10.0);
        loadXML();
        //fuzzyDoctor.getInputVariables().stream().sorted().collect(Collectors.toList()).forEach(this::addParameter);
        
        // szukaj etykiety dla każdej ze znalezionych automatycznie zmiennych wejściowych
        int np = 0;
        for(String s : fuzzyDoctor.getInputVariables().stream().sorted().collect(Collectors.toList())) {
        	Optional<Parameter> optional = paramList.stream()
                    .filter(x -> s.equals(x.getName()))
                    .findFirst();
			if(optional.isPresent()) {
				Parameter p = optional.get();
				addParameter(p,np++);
			} else {
				// nie ma etykiety, dodaj z nazwą zmiennej jako etykietą
				addParameter(new Parameter(s,s,"?",1),np++);
			}
        }
    }
    
    // odczytywanie etykiet parametrów wejściowych z pliku XML
    private void loadXML() {
  	  String filePath = "./src/main/resources/parameters.xml";
          File xmlFile = new File(filePath);
          DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
          DocumentBuilder dBuilder;
          
          try {
        	  dBuilder = dbFactory.newDocumentBuilder();
              Document doc = dBuilder.parse(xmlFile);
              doc.getDocumentElement().normalize();
              
              NodeList paramNodeList = doc.getElementsByTagName("parameter");
              for (int i = 0; i < paramNodeList.getLength(); i++)
                 paramList.add(xmlParam(paramNodeList.item(i)));
              
              // choroby
              NodeList diseaseNodeList = doc.getElementsByTagName("disease");
              for (int i = 0; i < diseaseNodeList.getLength(); i++)
                diseaseList.add(xmlDisease(diseaseNodeList.item(i)));
              
          } catch (SAXException | ParserConfigurationException | IOException e1) {
             e1.printStackTrace();
          }
    }
    
    // utworzenie obiektu Parameter na podstawie właściwości zapisanych w węzle XML-a
    private Parameter xmlParam(Node node) {
        Parameter item = new Parameter("test", "test", "???", 0.0);
        if (node.getNodeType() == Node.ELEMENT_NODE) {
            Element element = (Element) node;
            item.setName(element.getAttribute("id"));
            item.setLabel(getTagValue("label", element));
            item.setUnit(getTagValue("unit", element));
        }

        return item;
    }
    
    // utworzenie obiektu Disease na podstawie właściwości zapisanych w węzle XML-a
    private Disease xmlDisease(Node node) {
        Disease item = new Disease("test-n", "test-l", "url", -1.0);
        if (node.getNodeType() == Node.ELEMENT_NODE) {
            Element element = (Element) node;
            item.setName(element.getAttribute("id"));
            item.setLabel(getTagValue("label", element));
            item.setUrl(getTagValue("url", element));
        }
        return item;
    }
    
    private static String getTagValue(String tag, Element element) {
        NodeList nodeList = element.getElementsByTagName(tag).item(0).getChildNodes();
        if(nodeList.getLength() > 0) {
	        Node node = (Node) nodeList.item(0);
	        return node.getNodeValue();
        } else return "";
    }

    private void addParameter(Parameter param, int row) {
        Label newLabel = new Label(param.getLabel()+" ["+param.getUnit()+"]");
        newLabel.setWrapText(true);
        TextField newTextField = new TextField();
        newTextField.setId(param.getName());
        Button showChartButton = new Button();

        Image image = new Image(getClass().getResourceAsStream("chart.png"));
        showChartButton.setGraphic(new ImageView(image));

        showChartButton.setOnMouseClicked(new ShowParameterChartButtonClickHandler(param.getName(), fuzzyDoctor));

        parametersGrid.getRowConstraints().size();
        parametersGrid.addRow(row, showChartButton, newLabel, newTextField);
        //parametersNames.add(param.getName());
    }

    @FXML
    public void getDoctorAnalysis() {
        List<Parameter> parameters = getParametersFromFields();
        List<Disease> diseases = fuzzyDoctor.makeAnalysis(parameters);
        // dodanie etykiet
        for(int i = 0; i<diseases.size();++i) {
           diseases.set(i, diseaseProperties(diseases.get(i)));
        }
//        fuzzyDoctor.showFuzzyficationCharts();
//        fuzzyDoctor.showResultsCharts();
        printAnswer(diseases);
    }
    private Disease diseaseProperties(Disease d) {
    	Optional <Disease> dd = diseaseList.stream().filter(x->x.getName().equals(d.getName())).findFirst();
        if(dd.isPresent()) {
        	Disease disease = dd.get();
        	disease.setValue(d.getValue());
        	return disease;
        } else {
        	d.setLabel(d.getName());
        	return d;
        }
    }

    private List<Parameter> getParametersFromFields() {
        List<Parameter> parameters = new ArrayList<>();
        parametersGrid.getChildren().stream()
                .filter(child -> paramList.stream()
                        .filter(x -> x.getName().equals(child.getId()))
                        .findFirst().isPresent()
                        && ((TextField) child).getText() != null
                        && !((TextField) child).getText().equals(""))
                .forEach(child -> parameters.add(getParameterFromField((TextField) child)));
        
        return parameters;
    }

    private Parameter getParameterFromField(TextField parameterField) {
        Optional<Parameter> optional = paramList.stream()
                .filter(x -> parameterField.getId().equals(x.getName()))
                .findFirst();
        if(optional.isPresent()) {
        	Parameter p = optional.get();
        	p.setValue(Double.parseDouble(parameterField.getText()));
        	return p;
        } else {
			return (new Parameter("error","error","?",1));
		}
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
    	String labelText = disease.getLabel();
        if (disease.getValue() < 0) {
            Label emptyLabel = new Label();
            Label diseaseNameLabel = new Label(labelText);
            diseaseNameLabel.setWrapText(true);
            Label infoLabel = new Label("podano za mało danych, aby określenie było możliwe.");
            infoLabel.setWrapText(true);
            doctorAnswer.addRow(index, emptyLabel, diseaseNameLabel, infoLabel);
        } else {
            Label diseaseNameLabel = new Label(labelText);
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
