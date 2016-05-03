package eu.scase.qosontology;

import java.util.ArrayList;
import com.hp.hpl.jena.query.ResultSet;

/**
 * Provides an API for the Web Services Quality ontology. Allows adding/deleting and updating web services and their
 * measures.
 * 
 * @author Themistoklis Diamantopoulos, Davide Tosi and Carola Bianchi
 */
public class OntologyQoSAPI {

	/** The ontology API that is called for every I/O operation to/from the ontology. */
	OntologyJenaAPI ontology;

	/**
	 * Initializes this instance and connects to the ontology. <u><b>NOTE</b></u> that you have to call {@link #close()}
	 * in order to save your changes to disk. If, however, you only use the connection for queries, it is better that
	 * you do <u><b>NOT</b></u> call {@link #close()}.
	 */
	public OntologyQoSAPI() {
		String filename = "/Users/davidetosi/Documents/workspace_Mars/wsqr-server-master/scase-wp5-wsqrserver/ontology/WSQuality.owl";
		//String filename = "/home/ubuntu/WSQuality.owl";
		String SOURCE = "http://www.owl-ontologies.com/Ontology1406103978.owl";
		ontology = new OntologyJenaAPI(filename, SOURCE);
	}
	
	/**
	 * Adds a new web service to the ontology. If the web service already exists, it is not added.
	 * 
	 * @param WebServiceName the name of the new web service.
	 */
	public void addWebService(String WebServiceName) {
		ontology.addIndividual("WebService", WebServiceName);
		ontology.addPropertyToIndividual(WebServiceName, "WebServiceName", WebServiceName);
	}

	/**
	 * Deletes a web service from the ontology. Note that this function also deletes any measures of the web services
	 * including all their properties. If the web service does not exist, then nothing happens.
	 * 
	 * @param WebServiceName the name of the web service to be deleted.
	 */
	public void deleteWebService(String WebServiceName) {
		ontology.removeIndividualsGivenIndividualAndProperty(WebServiceName, "has_measure");
		ontology.removeIndividual(WebServiceName);
	}
	
	/**
	 * Finds and returns a specific measure name for a web service.
	 * 
	 * @param WebServiceName the name of the web service of which the measure name is found.
	 * @return a value containing the measure name of the web service.
	 */
	public String getMeasureOfWebService(String WebServiceName, int x) {
		return ontology.getIndividualNamesGivenIndividualAndProperty(WebServiceName, "has_measure").get(x);
	}
	
	/**
	 * Finds and returns all the measure names for a web service.
	 * 
	 * @param WebServiceName the name of the web service of which the measure names are found.
	 * @return an {@link ArrayList} containing the measure names of the web service.
	 */
	public ArrayList<String> getAllMeasuresOfWebService(String WebServiceName) {
		return ontology.getIndividualNamesGivenIndividualAndProperty(WebServiceName, "has_measure");
	}

	/**
	 * Adds a new measure to a web service. The values of the properties of the new measure are given as parameters.
	 * This function also connects the newly added measure to the web service.
	 * 
	 * @param WebServiceName the name of the web service to connect the measure to.
	 * @param MeasureName the name of the newly added measure.
	 * @param MeasureValueKind the kind of the measure value, either {@code "Raw"} or {@code "Average"} or
	 *            {@code Median}.
	 * @param MeasureValue the value of the newly added measure.
	 */
	public void addMeasureToWebService(String WebServiceName, String MeasureName, String MeasureValueKind,
			float MeasureValue) {
		String individualName = WebServiceName + "__" + MeasureName + "__" + MeasureValueKind;
		ontology.addIndividual(MeasureName, individualName);
		ontology.addPropertyToIndividual(individualName, "MeasureValueKind", MeasureValueKind);
		ontology.addPropertyToIndividual(individualName, "MeasureValue", MeasureValue);
		ontology.addPropertyBetweenIndividuals(WebServiceName, "has_measure", individualName);
		ontology.addPropertyBetweenIndividuals(individualName, "is_measure_of", WebServiceName);
	}

	/**
	 * Returns the value of a measure for a specific web service.
	 * 
	 * @param WebServiceName the name of the web service.
	 * @param MeasureName the name of the measure of which the value is returned.
	 * @param MeasureValueKind the kind of the measure value, either {@code "Raw"} or {@code "Average"} or
	 *            {@code Median}.
	 * @return the numeric value of the web service measure
	 */
	public float getMeasureValueForWebService(String WebServiceName, String MeasureName, String MeasureValueKind) {
		String individualName = WebServiceName + "__" + MeasureName + "__" + MeasureValueKind;
		return ontology.getIndividualPropertyValue(individualName, "MeasureValue").getFloat();
	}

	/**
	 * Deletes a measure of a specific web service. If the measure does not exist, nothing happens.
	 * 
	 * @param WebServiceName the name of the web service that has the measure to be deleted.
	 * @param MeasureName the name of the measure to be deleted.
	 * @param MeasureValueKind the kind of the measure value, either {@code "Raw"} or {@code "Average"} or
	 *            {@code Median}.
	 */
	public void deleteMeasureOfWebService(String WebServiceName, String MeasureName, String MeasureValueKind) {
		String individualName = WebServiceName + "__" + MeasureName + "__" + MeasureValueKind;
		ontology.removeIndividual(individualName);
	}

	/**
	 * Updates the values of a measure of a web service. If the measure does not exist, this function adds it.
	 * 
	 * @param WebServiceName the name of the web service to update the measure.
	 * @param MeasureName the name of the measure to be updated.
	 * @param MeasureValueKind the kind of the measure value, either {@code "Raw"} or {@code "Average"} or
	 *            {@code Median}.
	 * @param MeasureValue the new value of the measure.
	 */
	public void updateMeasureOfWebService(String WebServiceName, String MeasureName, String MeasureValueKind,
			float MeasureValue) {
		deleteMeasureOfWebService(WebServiceName, MeasureName, MeasureValueKind);
		addMeasureToWebService(WebServiceName, MeasureName, MeasureValueKind, MeasureValue);
	}
	
	/**
	 * Adds validation means to a measure.
	 * 
	 * @param WebServiceName the name of the web service.
	 * @param MeasureName the name of the measure to add the validation means to.
	 * @param MeasureValueKind the kind of the measure value, either {@code "Raw"} or {@code "Average"} or
	 *            {@code Median}.
	 * @param MeasureValidationMeans the name of the measure validation means.
	 * @param AccuracyIndicatorUsed the accuracy indicator used for validating.
	 * @param AccuracyLevel the accuracy level of validation.
	 * @param StatisticalTestUsed the statistical test used for validating.
	 * @param PValue the p value of the validation.
	 * @param StatisticalSignificanceLevel the statistical significance level of the validation.
	 */
	public void addValidationMeansToMeasure(String WebServiceName, String MeasureName, String MeasureValueKind, String MeasureValidationMeans,
			String AccuracyIndicatorUsed, float AccuracyLevel, String StatisticalTestUsed, float PValue,
			float StatisticalSignificanceLevel) {
		String individualName = WebServiceName + "__" + MeasureName + "__" + MeasureValueKind;
		ontology.addPropertyToIndividual(individualName, "MeasureValidationMeans", MeasureValidationMeans);
		ontology.addPropertyToIndividual(individualName, "AccuracyIndicatorUsed", AccuracyIndicatorUsed);
		ontology.addPropertyToIndividual(individualName, "AccuracyLevel", AccuracyLevel);
		ontology.addPropertyToIndividual(individualName, "StatisticalTestUsed", StatisticalTestUsed);
		ontology.addPropertyToIndividual(individualName, "PValue", PValue);
		ontology.addPropertyToIndividual(individualName, "StatisticalSignificanceLevel", StatisticalSignificanceLevel);
	}
	
	public void addInternalValidationMeansToMeasure(String WebServiceName, String MeasureName, String MeasureValueKind,
			String MeasureValidationMeans, String AttributeName, String AttributeValue, String InternalMeasureKind) {
		String individualName = WebServiceName + "__" + MeasureName + "__" + MeasureValueKind;
		System.out.println("NELL'ONTOLOGIA FACCIO ORA ADD PER: "+individualName);
		ontology.addPropertyToIndividual(individualName, "MeasureValidationMeans", MeasureValidationMeans);
		ontology.addPropertyToIndividual(individualName, "AttributeName", AttributeName);
		ontology.addPropertyToIndividual(individualName, "AttributeValue", AttributeValue);
		ontology.addPropertyToIndividual(individualName, "InternalMeasureKind", InternalMeasureKind);
	}

	/**
	 * Deletes the validation means of a measure. If the measure does not have validation means, nothing happens.
	 * 
	 * @param WebServiceName the name of the web service.
	 * @param MeasureName the name of the measure of which the validation means is deleted.
	 * @param MeasureValueKind the kind of the measure value, either {@code "Raw"} or {@code "Average"} or
	 *            {@code Median}.
	 */
	public void deleteValidationMeansFromMeasure(String WebServiceName, String MeasureName, String MeasureValueKind) {
		String individualName = WebServiceName + "__" + MeasureName + "__" + MeasureValueKind;
		ontology.removePropertyFromIndividual(individualName, "MeasureValidationMeans");
		ontology.removePropertyFromIndividual(individualName, "AccuracyIndicatorUsed");
		ontology.removePropertyFromIndividual(individualName, "AccuracyLevel");
		ontology.removePropertyFromIndividual(individualName, "StatisticalTestUsed");
		ontology.removePropertyFromIndividual(individualName, "PValue");
		ontology.removePropertyFromIndividual(individualName, "StatisticalSignificanceLevel");
	}
	
	public void deleteInternalValidationMeansFromMeasure(String WebServiceName, String MeasureName, String MeasureValueKind) {
		String individualName = WebServiceName + "__" + MeasureName + "__" + MeasureValueKind;
		System.out.println("NELL'ONTOLOGIA FACCIO DELETE PER: "+individualName);
		ontology.removePropertyFromIndividual(individualName, "MeasureValidationMeans");
		ontology.removePropertyFromIndividual(individualName, "AttributeName");
		ontology.removePropertyFromIndividual(individualName, "AttributeValue");
	}
	

	/**
	 * Updates the validation means of a measure. If the measure does not exist, nothing happens. If the measure does
	 * not have validation means, then this function adds it.
	 * 
	 * @param WebServiceName the name of the web service.
	 * @param MeasureName the name of the measure to update its validation means.
	 * @param MeasureValueKind the kind of the measure value, either {@code "Raw"} or {@code "Average"} or
	 *            {@code Median}.
	 * @param MeasureValidationMeans the name of the new measure validation means.
	 * @param AccuracyIndicatorUsed the new accuracy indicator used for validating.
	 * @param AccuracyLevel the new accuracy level of validation.
	 * @param StatisticalTestUsed the new statistical test used for validating.
	 * @param PValue the new p value of the validation.
	 * @param StatisticalSignificanceLevel the new statistical significance level of the validation.
	 */
	public void updateValidationMeansOfMeasure(String WebServiceName, String MeasureName, String MeasureValueKind,
			String MeasureValidationMeans, String AccuracyIndicatorUsed, float AccuracyLevel,
			String StatisticalTestUsed, float PValue, float StatisticalSignificanceLevel) {
		deleteValidationMeansFromMeasure(WebServiceName, MeasureName, MeasureValueKind);
		addValidationMeansToMeasure(WebServiceName, MeasureName, MeasureValueKind, MeasureValidationMeans, AccuracyIndicatorUsed,
				AccuracyLevel, StatisticalTestUsed, PValue, StatisticalSignificanceLevel);
	}
	
	public void updateInternalValidationMeansOfMeasure(String WebServiceName, String MeasureName, String MeasureValueKind,
			String MeasureValidationMeans, String AttributeName, String AttributeValue, String InternalMeasureKind) {
		System.out.println("ONTOLOGIA PARAMETRI: " +WebServiceName +" , "+MeasureName +" , "+MeasureValueKind+" , "+MeasureValidationMeans+" , "+AttributeName+" , "+AttributeValue+" , "+InternalMeasureKind);
		deleteInternalValidationMeansFromMeasure(WebServiceName, MeasureName, MeasureValueKind);
		System.out.println("ONTOLOGIA DI RITORNO DALLA DELETE");		
		addInternalValidationMeansToMeasure(WebServiceName, MeasureName, MeasureValueKind, MeasureValidationMeans,
				AttributeName, AttributeValue, InternalMeasureKind);
	}
	
		
	/**
	 * This methods retrieves all the measurements in an XML document, which is traceable with the path properties.
	 * 
	 * @param document is the document created in which user wrote
	 * @param WebServiceName the name of the web service, which is used to generate an XML document
	 */
	/*public void generateXML(String WebServiceName, Document document) {
		try {
			//Create the XMLOutputter Object
			XMLOutputter outputter = new XMLOutputter(); 
			//Set outputter format as "prettyFormat" 
			outputter.setFormat(Format.getPrettyFormat()); 

			//Create the Output file xml.foo 
			outputter.output(document, new FileOutputStream(WebServiceName +".xml")); 
			//If you need to print the XML output on the standard output 
			//outputter.output(document, System.out);
		}  
		catch (IOException e) { 
			System.err.println("Errore durante il parsing del documento");
			e.printStackTrace(); 
		}
		//String for the PATH
		String pathJar = this.getClass().getProtectionDomain().getCodeSource().getLocation().toString();
		int s = pathJar.lastIndexOf("/");
		String end = pathJar.substring(0, s);
		JOptionPane.showMessageDialog(null, "Your WSQR_"+ WebServiceName +".xml"+" file has been saved into folder: "+ end, "File Saved", JOptionPane.INFORMATION_MESSAGE);
	}*/
	
	/**
	 * Performs a query on the ontology. It receives a query string in SPARQL format and adds to it the most common
	 * prefixes before calling the ontology.
	 * 
	 * @param queryString the query string in SPARQL format.
	 * @return an object of type {@link com.hp.hpl.jena.query.ResultSet ResultSet} that contains the results.
	 */
	public ResultSet performQuery(String queryString) {
		queryString = "PREFIX rdfs: <http://www.w3.org/2000/01/rdf-schema#>"
				+ "PREFIX rdf:<http://www.w3.org/1999/02/22-rdf-syntax-ns#> "
				+ "PREFIX wn:<http://www.owl-ontologies.com/Ontology1406103978.owl#>" + queryString;
		return ontology.performQuery(queryString);
	}

	/**
	 * Performs a query on the ontology. It receives an object of type {@link OntologyQoSQuery} and calls the overloaded
	 * function that receives as input the string version of the query.
	 * 
	 * @param query the query of type {@link OntologyQoSQuery}.
	 * @return an object of type {@link com.hp.hpl.jena.query.ResultSet ResultSet} that contains the results.
	 */
	public ResultSet performQuery(OntologyQoSQuery query) {
		return performQuery(query.toString());
	}

	/**
	 * Closes the connection of the ontology and saves it to disk. <u><b>NOTE</b></u> that if this function is not
	 * called, then the ontology is not saved. If no changes are made to the ontology (i.e. the connection handled
	 * only queries) then this function should <u><b>NOT</b></u> be called.
	 */
	public void close() {
		ontology.close();
	}
}