package ontology;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map.Entry;
import server.Measure;
import server.Service;
import com.hp.hpl.jena.query.ResultSet;
import eu.scase.qosontology.OntologyQoSAPI;

public class OntologyManager {

	public static void createOntology(Service service){
		OntologyQoSAPI ontology = new OntologyQoSAPI();
		
		String serviceName=service.getName();
		ontology.addWebService(serviceName);
		System.out.println("Service: "+serviceName);
		
		for(Measure m : service.getInternal()){
			String measureName=m.getName();
			String valueKind=m.getValueKind();
			float value=m.getValue();
			/*String validationMeans= m.getValidationMeans();
			String accuracyIndicator = m.getAccuracyIndicator();
			float accuracyLevel = m.getAccuracyLevel();
			String statisticalTest = m.getStatisticalTest();
			float pValue= m.getpValue();
			float statisticalSignificance = m.getStatisticalSignificance();*/
			//print(m);
			System.out.println("SN: "+serviceName+" MN: "+measureName+" VK: "+valueKind+" V: "+value);
			ontology.addMeasureToWebService(serviceName, measureName, valueKind, value);
			//ontology.addValidationMeansToMeasure(serviceName, measureName, valueKind, validationMeans, accuracyIndicator, accuracyLevel, statisticalTest, pValue, statisticalSignificance);
		}
		for(Measure m : service.getExternal()){
			String measureName=m.getName();
			String valueKind=m.getValueKind();
			float value=m.getValue();
			String validationMeans= m.getValidationMeans();
			String accuracyIndicator = m.getAccuracyIndicator();
			float accuracyLevel = m.getAccuracyLevel();
			String statisticalTest = m.getStatisticalTest();
			float pValue= m.getpValue();
			float statisticalSignificance = m.getStatisticalSignificance();
			//print(m);
			ontology.addMeasureToWebService(serviceName, measureName, valueKind, value);
			ontology.addValidationMeansToMeasure(serviceName, measureName, valueKind, validationMeans, accuracyIndicator, accuracyLevel, statisticalTest, pValue, statisticalSignificance);
		}
		ontology.close();
	}
	private static void print(Measure m){
		System.out.println("Measure:");
		System.out.println("name: "+m.getName());
		System.out.println("value: "+m.getValue());
		System.out.println("valuekind: "+m.getValueKind());
		System.out.println("validationmeans: "+m.getValidationMeans());
		System.out.println("accuracyindicator: "+m.getAccuracyIndicator());
		System.out.println("accuracyLevel: "+m.getAccuracyLevel());
		System.out.println("statisticalsigni: "+m.getStatisticalSignificance());
		System.out.println("statisticaltest: "+m.getStatisticalTest());
		System.out.println("pvalue: "+m.getpValue());
		System.out.println("CONTENT____________________");
		System.out.println(m.getContent());
		System.out.println("CONTENT____________________END");
	}
	
	public static void createOntology(HashMap<String, HashMap<String, Float>> wsMeasures) {

		// Connect to the ontology.
		OntologyQoSAPI ontology = new OntologyQoSAPI();
		for (Entry<String, HashMap<String, Float>> wsEntry : wsMeasures.entrySet()) {
			String webService = wsEntry.getKey();
			HashMap<String, Float> measureNamesAndValues = wsEntry.getValue();

			// Add web service.
			ontology.addWebService(webService);
			System.out.println("webService: " + webService);

			// Iterate over all measures and add them to the ontology.
			for (Entry<String, Float> measureEntry : measureNamesAndValues.entrySet()) {
				String measureName = measureEntry.getKey();
				Float measureValue = measureEntry.getValue();

				// Get the value type of the measure
				String measureValueKind = "Raw";
				String[] splittedMeasureName = measureName.split("\\(");
				if (splittedMeasureName.length > 1) {
					measureName = splittedMeasureName[0];
					measureValueKind = splittedMeasureName[1].split("\\)")[0];
				}

				System.out.println("   " + measureName + ": " + measureValue);
				if (measureValue != null) {
					ontology.addMeasureToWebService(webService, measureName, measureValueKind, measureValue);
				}
			}
		}
		// Close the connection.
		ontology.close();
	}
	
	public  static void deleteService(String name){
		OntologyQoSAPI ontology = new OntologyQoSAPI();
		ontology.deleteWebService(name);
		ontology.close();
	}
	
	public static ResultSet listServices(){
		System.out.println("List Services");
		OntologyQoSAPI ontology = new OntologyQoSAPI();
		String query ="SELECT ?WebServiceName "
				+ "WHERE { ?webservice wn:WebServiceName ?WebServiceName; }";
		System.out.println("Query: "+query.toString());
		return ontology.performQuery(query);
	}

	public static String getOntology(String serviceName) {
		OntologyQoSAPI ontology = new OntologyQoSAPI();
		ArrayList<String> measures = ontology.getAllMeasuresOfWebService(serviceName);
		String result="";
		for(String m : measures ){
			System.out.println("Measure: "+m);
			result+=m+"\n";
		}
		return result;
	}
}
