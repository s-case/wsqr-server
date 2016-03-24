package server;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;

import javax.swing.JOptionPane;
import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.jdom2.Attribute;
import org.jdom2.Element;
import org.jdom2.output.Format;
import org.jdom2.output.XMLOutputter;
import org.json.JSONObject;
import ontology.OntologyManager;
import com.hp.hpl.jena.query.ResultSet;
import eu.scase.qosontology.OntologyQoSAPI;
import eu.scase.qosontology.OntologyQoSQuery;

/**
 * Provides the WSQR Restful set of methods for all the WSQR operations in the API.
 * 
 * @author Davide Tosi, Carola Bianchi, Marco Compagnoni and Matteo Tegnenti
 */

/*
 *	XML example
 *	<?xml version="1.0" encoding="UTF-8"?>
	<WSQRMeasure>

 	<InternalMeasure>
    	<InternalMeasureValidationMeans InternalValidationMeansName="MeasurementTheory">
      		<InternalValidationMeansAttributes AttributeName="AttributeName" AttributeValue="AttributeValue" />
    	</InternalMeasureValidationMeans>
    	<InternalMeasureKind>DynamicMeasure</InternalMeasureKind>
  	</InternalMeasure>
  
  	<MeasureName>MeasureName</MeasureName>
  	<MeasureValue>MeasureValue</MeasureValue>
  	<MeasureValueKind>Average</MeasureValueKind>
  	<MeasureDefinition Description="Description" Formula="Formula" />
  
  	<ExternalMeasure>
    	<ExternalMeasureValidationMeans ExternalValidationMeansName="MeasurementTheory">
      		<ExternalValidationMeansAttribute>
        		<StatisticalSignificanceLevel Value="SSL" />
        		<PValue Value="PV" />
        		<StatisticalTestUsed Name="STU" />
        		<AccuracyLevel Value="ALV" />
        		<AccuracyIndicatorUsed Name="AIUN" />
      		</ExternalValidationMeansAttribute>
    	</ExternalMeasureValidationMeans>
  	</ExternalMeasure>
  
  	<MeasureName>MeasureName</MeasureName>
  	<MeasureValue>MeasureValue</MeasureValue>
  	<MeasureValueKind>Average</MeasureValueKind>
  	<MeasureDefinition Description="Description" Formula="Formula" />
  
	</WSQRMeasure>
 * 
 */

@Path("/services")
public class WSQRGenerator {

	//private final String servicesDirectory="/Applications/eclipse-Luna/workspace/WSQRRESTfulService/services/";
	
	public String getName(String serviceName) {
		return serviceName;
	}
	
	@GET
	public Response helloWorld(){
		System.out.println("Hello, World!! ");
		return Response.status(200).entity("Hello, World!!").build();
	}
	
	
	//DA CONTROLLARE
	@Path("/{service_name}")
	@GET
	@Produces(MediaType.APPLICATION_JSON)
	public Response getWebService(@PathParam("service_name") String serviceName, String measureNames){
		OntologyQoSAPI ontology = new OntologyQoSAPI();
		
		System.out.println("GET service_name: " + serviceName);
		
		OntologyQoSQuery query = new OntologyQoSQuery()
						.selectWebServices(serviceName)
						.selectMeasures(measureNames);
		
		ontology.performQuery(query);
		ontology.getWebService(serviceName);
		//ontology.get
		//Service service = readService(serviceName);
		ontology.close();
		return Response.status(200).entity(new JSONObject().put("service", ontology /*controllare*/)).type("application/json").build();
	}
	
	
	
	@Path("/{service_name}")
	@POST
	@Consumes(MediaType.APPLICATION_JSON)
	public Response addWebService(@PathParam("service_name") String serviceName, String service){
		//connection to the ontology
		OntologyQoSAPI ontology = new OntologyQoSAPI();
		
		System.out.println("POST service_name: " + serviceName);
		//System.out.println("FILE: " + service);
		//String servName = createServiceOntology(service);
		//if(servName.equals(serviceName)){
			//add web service
			ontology.addWebService(serviceName);
			JSONObject json = new JSONObject();
			json.put("message", "200 OK"
					+ "{"
					+ "		serviceName:	" + serviceName + ";"
					+ "}");
			Response.ok(json);
			ontology.close();
			return Response.status(200).entity(json).build();
		//}
		//else {
			//ontology.close();
			//return Response.status(500).build();
		//}
	}
	
	
	@Path("/{service_name}")
	@DELETE
	public Response deleteWebService(@PathParam("service_name") String serviceName){
		OntologyQoSAPI ontology = new OntologyQoSAPI();
		
		System.out.println("DELETE service_name:" + serviceName);
		
		//OntologyQoSQuery query = new OntologyQoSQuery()
						//.selectWebServices(serviceName);
		
		//ResultSet results = ontology.performQuery(query);
		ontology.deleteWebService(serviceName);
		ontology.close();
		//OntologyManager.deleteService(serviceName);
		//File f = new File(servicesDirectory + serviceName);
		//f.delete();
		return Response.status(200).build();	
	}
	
	
	@Path("/{service_name}/measures")
	@GET
	@Produces(MediaType.APPLICATION_JSON)
	public Response getAllMeasuresOfWebService(@PathParam("service_name") String serviceName){
		OntologyQoSAPI ontology = new OntologyQoSAPI();

		System.out.println("GET measures: " + serviceName);
		//Service service = readService(serviceName);
		
		ArrayList<String> measures = ontology.getAllMeasuresOfWebService(serviceName);
		
		JSONObject json = new JSONObject();
		prepareJson(measures, json);
		//prepareInternalJson(service, json);
		//prepareExternalJson(service, json);
		
		ontology.close();
		
		return Response.status(200).entity(json).type("application/json").build();
	}
	
	
	@Path("/{service_name}/internal/{measure_name}")
	@DELETE
	@Consumes(MediaType.APPLICATION_JSON)
	public Response deleteInternalMeasureOfWebService(@PathParam("service_name") String serviceName,
			@PathParam("measure_name") String measure, String valueKind) {
		OntologyQoSAPI ontology = new OntologyQoSAPI();
		System.out.println("DELETE measure from: " + serviceName);
		System.out.println("Measure: " + measure);
		ontology.deleteMeasureOfWebService(serviceName, measure, valueKind);
		ontology.close();
		//deleteInternal(serviceName, measure);
		return Response.status(200).build();
	}
	
	
	@Path("/{service_name}/external/{measure_name}")
	@DELETE
	@Consumes(MediaType.APPLICATION_JSON)
	public Response deleteExternalMeasureOfWebService(@PathParam("service_name") String serviceName,
			@PathParam("measure_name") String measure, String valueKind) {
		OntologyQoSAPI ontology = new OntologyQoSAPI();
		System.out.println("DELETE measure from: " + serviceName);
		System.out.println("Measure: " + measure);
		ontology.deleteMeasureOfWebService(serviceName, measure, valueKind);
		ontology.close();
		//deleteExternal(serviceName, measure);
		return Response.status(200).build();
	}
	
	
	@Path("/{service_name}/internal/{measure_name}")
	@POST
	@Consumes(MediaType.APPLICATION_JSON)
	public Response updateInternalMeasureOfWebService(@PathParam("service_name") String serviceName,
			@PathParam("measure_name") String measure, String valueKind, float value) {
		OntologyQoSAPI ontology = new OntologyQoSAPI();
		System.out.println("POST measure from: " + serviceName);
		System.out.println("Measure:" + measure);
		ontology.updateMeasureOfWebService(serviceName, measure, valueKind, value);
		ontology.close();
		//updateInternal(serviceName, measure, measure);
		return Response.status(200).build();
	}
	
	
	@Path("/{service_name}/external/{measure_name}")
	@POST
	@Consumes(MediaType.APPLICATION_JSON)
	public Response updateExternalMeasureOfWebService(@PathParam("service_name") String serviceName,
			@PathParam("measure_name") String measure, String valueKind, float value) {
		OntologyQoSAPI ontology = new OntologyQoSAPI();
		System.out.println("POST measure from: " + serviceName);
		System.out.println("Measure: " + measure);
		ontology.updateMeasureOfWebService(serviceName, measure, valueKind, value);
		ontology.close();
		//updateExternal(serviceName, measure);
		return Response.status(200).build();
	}
	
	
	@Path("/{service_name}/internal")
	@POST
	@Consumes(MediaType.APPLICATION_JSON)
	public Response addInternalMeasure(@PathParam("service_name") String serviceName, String measure, String measureKind, float measureValue) {
		OntologyQoSAPI ontology = new OntologyQoSAPI();
		
		System.out.println("POST internal:" + serviceName);
		System.out.println("Measure: " + measure);
		
		ontology.addMeasureToWebService(serviceName, measure, measureKind, measureValue);
		ontology.close();
		//addInternal(serviceName, measure);
		return Response.status(200).build();
	}

	
	@Path("/{service_name}/external")
	@POST
	@Consumes(MediaType.APPLICATION_JSON)
	public Response addExternalMeasure(@PathParam("service_name") String serviceName, String measure, String measureKind, float measureValue) {
		OntologyQoSAPI ontology = new OntologyQoSAPI();

		System.out.println("POST external: "+serviceName);
		System.out.println("Measure: " + measure);
		
		ontology.addMeasureToWebService(serviceName, measure, measureKind, measureValue);
		ontology.close();
		//addExternal(serviceName, measure);
		return Response.status(200).build();
	}
	
	@Path("/{service_name}/internal/{measure_position}")
	@GET
	@Consumes(MediaType.APPLICATION_JSON)
	public Response getInternalMeasure(@PathParam("service_name") String serviceName,
			@PathParam("measure_position") int measurePos) {
		OntologyQoSAPI ontology = new OntologyQoSAPI();
		
		System.out.println("GET internal :" + serviceName);
		System.out.println("MeasurePosition: " + measurePos);
		
		String measure = ontology.getMeasureOfWebService(serviceName, measurePos);
		//Measure measure = null;
		
		//Service service = readService(serviceName);
		/*for(int i=0; i<service.getInternal().size(); i++){
			if(service.getInternal().get(i).getName().equals(measureName))
				measure = service.getInternal().get(i);
		}
		if(measure==null) {
			return Response.status(404).build();
		}
		JSONObject json = new JSONObject();
		json.put("MeasureName", measure.getName());
		json.put("MeasureDefinition", new JSONObject()
			.put("Description", measure.getDescription())
			.put("Formula", measure.getFormula()));
		json.put("MeasureValue", measure.getValue());
		json.put("MeasureValueKind", measure.getValueKind());
		json.put("InternalValidationMeansName", measure.getValidationMeans());
		json.put("InternalValidationMeansAttributes", new JSONObject()
			.put("AttributeName", measure.getAttributeName())
			.put("AttributeValue", measure.getAttributeValue()));
		json.put("InternalMeasureKind", measure.getMeasureKind());	*/
		ontology.close();
		return Response.status(200).entity(new JSONObject().put("Measure", measure)).type("application/json").build();
	}

	@Path("/{service_name}/internal/{measure_name}/{value_kind}")
	@GET
	@Consumes(MediaType.APPLICATION_JSON)
	public Response getInternalMeasure(@PathParam("service_name") String serviceName,
			@PathParam("measure_name") String measureName,
			@PathParam("value_kind") String valueKind) {
		OntologyQoSAPI ontology = new OntologyQoSAPI();
		
		System.out.println("GET internal with vk: " + serviceName);
		System.out.println("Measure: " + measureName);
		
		float measure = ontology.getMeasureValueForWebService(serviceName, measureName, valueKind);
		//ArrayList<Measure> measure = getInternalByKind(serviceName, valueKind);
		/*JSONObject json = new JSONObject();
		for(int i=0; i<measure.size(); i++) {
			json.put("MeasureName", measure.get(i).getName());
			json.put("MeasureDefinition", new JSONObject()
				.put("Description", measure.get(i).getDescription())
				.put("Formula", measure.get(i).getFormula()));
			json.put("MeasureValue", measure.get(i).getValue());
			json.put("MeasureValueKind", measure.get(i).getValueKind());
			json.put("InternalValidationMeansName", measure.get(i).getValidationMeans());
			json.put("InternalValidationMeansAttributes", new JSONObject()
				.put("AttributeName", measure.get(i).getAttributeName())
				.put("AttributeValue", measure.get(i).getAttributeValue()));
			json.put("InternalMeasureKind", measure.get(i).getMeasureKind());	
		}*/
		ontology.close();
		return Response.status(200).entity(new JSONObject().put("MeasureValue", measure)).type("application/json").build();
	}

	@Path("/{service_name}/external/{measure_position}")
	@GET
	@Consumes(MediaType.APPLICATION_JSON)
	public Response getExternalMeasure(@PathParam("service_name") String serviceName,
			@PathParam("measure_position") int measurePos) {
		OntologyQoSAPI ontology = new OntologyQoSAPI();
		
		System.out.println("GET external: " + serviceName);
		System.out.println("Measure: " + measurePos);
		
		String measure = ontology.getMeasureOfWebService(serviceName, measurePos);
		//Measure measure = null;
		/*Service service = readService(serviceName);
		for(int i=0; i<service.getExternal().size(); i++){
			if(service.getExternal().get(i).getName().equals(measureName))
				measure = service.getExternal().get(i);
		}
		if(measure==null) {
			return Response.status(404).build();
		}
		JSONObject json = new JSONObject();
		json.put("MeasureName", measure.getName());
		json.put("MeasureDefinition", new JSONObject()
			.put("Description", measure.getDescription())
			.put("Formula", measure.getFormula()));
		json.put("MeasureValue", measure.getValue());
		json.put("MeasureValueKind", measure.getValueKind());
		json.put("ExternalValidationMeansName", measure.getValidationMeans());
		json.put("ExternalValidationMeansAttributes", new JSONObject()
			.put("StatisticalSignificanceLevel", measure.getStatisticalSignificance())
			.put("PValue", measure.getpValue())
			.put("StatisticalTestUsed", measure.getStatisticalTest())
			.put("AccuracyLevel-Value", measure.getAccuracyLevel())
			.put("AccuracyIndicatorUser-Name", measure.getAccuracyIndicator()));
		json.put("InternalMeasureKind", measure.getMeasureKind());*/
		ontology.close();
		return Response.status(200).entity(new JSONObject().put("Measure", measure)).type("application/json").build();
	}

	@Path("/{service_name}/external/{measure_name}/{value_kind}")
	@GET
	@Consumes(MediaType.APPLICATION_JSON)
	public Response getExternalMeasure(@PathParam("service_name") String serviceName,
			@PathParam("measure_name") String measureName,
			@PathParam("value_kind") String valueKind) {
		OntologyQoSAPI ontology = new OntologyQoSAPI();
		
		System.out.println("GET external with vk: " + serviceName);
		System.out.println("Measure: " + measureName);
		float measure = ontology.getMeasureValueForWebService(serviceName, measureName, valueKind);
		/*JSONObject json = new JSONObject();
		for(int i=0; i<measure.size(); i++) {
			json.append("External", new JSONObject()
			.put("MeasureName", measure.get(i).getName())
			.put("MeasureDefinition", new JSONObject()
				.put("Description", measure.get(i).getDescription())
				.put("Formula", measure.get(i).getFormula()))
			.put("MeasureValue", measure.get(i).getValue())
			.put("MeasureValueKind", measure.get(i).getValueKind())
			.put("ExternalValidationMeansName", measure.get(i).getValidationMeans())
			.put("ExternalValidationMeansAttributes", new JSONObject()
				.put("StatisticalSignificanceLevel", measure.get(i).getStatisticalSignificance())
				.put("PValue", measure.get(i).getpValue())
				.put("StatisticalTestUsed", measure.get(i).getStatisticalTest())
				.put("AccuracyLevel-Value", measure.get(i).getAccuracyLevel())
				.put("AccuracyIndicatorUser-Name", measure.get(i).getAccuracyIndicator()))
			.put("InternalMeasureKind", measure.get(i).getMeasureKind())
			);	
		}*/
		ontology.close();
		return Response.status(200).entity(new JSONObject().put("MeasureValue", measure)).type("application/json").build();
	}

	
	@Path("/ontology")
	@DELETE
	public Response deleteOntology() {
		System.out.println("DELETE ontology: ");
		ResultSet rs=OntologyManager.listServices();
		while(rs.hasNext()){
			String next=rs.next().toString();
			System.out.println("next: "+next);
			String name = next.substring(next.indexOf("\"")+1, next.lastIndexOf("\""));
			System.out.println("name:" + name);
			OntologyManager.deleteService(name);
		}		
		return Response.status(200).build();
	}
	
	
	@Path("/{service_name}/ontology")
	@POST
	@Consumes(MediaType.APPLICATION_JSON)
	public Response createOntology(@PathParam("service_name") Service service) {
		System.out.println("POST ontology: "+service.getName());
		OntologyManager.createOntology(service);	
		return Response.status(200).build();
	}
	
	
	@Path("/{service_name}/ontology")
	@GET
	@Consumes(MediaType.APPLICATION_JSON)
	public Response getOntology(@PathParam("service_name") String serviceName) {
		System.out.println("GET ontology: "+serviceName);
		String ontology = OntologyManager.getOntology(serviceName);
		return Response.status(200).entity(ontology).build();
	}
	
	
	@Path("/{service_name}/internal/{measure_name}/{validation_means}")
	@POST
	@Consumes(MediaType.APPLICATION_JSON)
	public Response addInternalValidationMeansToWebService(@PathParam("service_name") String serviceName,
			@PathParam("validation_means") String means, @PathParam("measure_name") String measure,
			String MeasureValueKind, String AccuracyIndicatorUsed, float AccuracyLevel,
			String StatisticalTestUsed, float PValue, float StatisticalSignificanceLevel) {
		OntologyQoSAPI ontology = new OntologyQoSAPI();
		
		System.out.println("POST internal: " + serviceName);
		System.out.println("Validation Means: " + means);
		
		ontology.addValidationMeansToMeasure(serviceName, measure, MeasureValueKind, means, AccuracyIndicatorUsed,
				AccuracyLevel, StatisticalTestUsed, PValue, StatisticalSignificanceLevel);
		ontology.close();
		//addInternalValidationMeans(serviceName, measure, means);
		return Response.status(200).build();
	}
	
	
	@Path("/{service_name}/external/{measure_name}/{validation_means}")
	@POST
	@Consumes(MediaType.APPLICATION_JSON)
	public Response addExternalValidationMeansToWebService(@PathParam("service_name") String serviceName,
			@PathParam("validation_means") String means, @PathParam("measure_name") String measure,
			String MeasureValueKind, String AccuracyIndicatorUsed, float AccuracyLevel,
			String StatisticalTestUsed, float PValue, float StatisticalSignificanceLevel) {
		OntologyQoSAPI ontology = new OntologyQoSAPI();
		
		System.out.println("POST external: " + serviceName);
		System.out.println("Validation Means: " + means);
		
		ontology.addValidationMeansToMeasure(serviceName, measure, MeasureValueKind, means, AccuracyIndicatorUsed,
				AccuracyLevel, StatisticalTestUsed, PValue, StatisticalSignificanceLevel);
		ontology.close();
		//addExternalValidationMeans(serviceName, measure, means);
		return Response.status(200).build();
	}

	@Path("/{service_name}/internal/{measure_name}/{validation_means}")
	@PUT
	@Consumes(MediaType.APPLICATION_JSON)
	public Response updateInternalValidationMeansToWebService(@PathParam("service_name") String serviceName,
			@PathParam("measure_name") String measure, @PathParam("validation_means") String means,
			String valueKind, String AccuracyIndicatorUsed,	float AccuracyLevel,
			String StatisticalTestUsed, float PValue, float StatisticalSignificanceLevel) {
		OntologyQoSAPI ontology = new OntologyQoSAPI();
		
		System.out.println("POST internal: " + serviceName);
		System.out.println("Validation means: " + means);
		
		ontology.updateValidationMeansOfMeasure(serviceName, measure, valueKind, means, AccuracyIndicatorUsed,
				AccuracyLevel, StatisticalTestUsed, PValue, StatisticalSignificanceLevel);
		ontology.close();
		//addInternalValidationMeans(serviceName, measure, means);
		return Response.status(200).build();
	}
	
	
	@Path("/{service_name}/external/{measure_name}/{validation_means}")
	@PUT
	@Consumes(MediaType.APPLICATION_JSON)
	public Response updateExternalValidationMeansToWebService(@PathParam("service_name") String serviceName,
			@PathParam("measure_name") String measure, @PathParam("validation_means") String means,
			String valueKind, String AccuracyIndicatorUsed, float AccuracyLevel,
			String StatisticalTestUsed, float PValue, float StatisticalSignificanceLevel) {
		OntologyQoSAPI ontology = new OntologyQoSAPI();
		
		System.out.println("POST internal: " + serviceName);
		System.out.println("Validation means: " + means);
		
		ontology.updateValidationMeansOfMeasure(serviceName, measure, valueKind, means, AccuracyIndicatorUsed,
				AccuracyLevel, StatisticalTestUsed, PValue, StatisticalSignificanceLevel);
		ontology.close();
		//addExternalValidationMeans(serviceName, measure, means);
		return Response.status(200).build();
	}
	
	@Path("/{service_name}/internal/{measure_name}/{validation_means}")
	@DELETE
	@Consumes(MediaType.APPLICATION_JSON)
	public Response deleteInternalValidationMeansToWebService(@PathParam("service_name") String serviceName,
			@PathParam("measure_name") String measure, @PathParam("validation_means") String means, String valueKind) {
		OntologyQoSAPI ontology = new OntologyQoSAPI();
		
		System.out.println("DELETE internal: " + serviceName);
		System.out.println("Validation means: " + means);
		
		ontology.deleteValidationMeansFromMeasure(serviceName, measure, valueKind);
		ontology.close();
		//deleteInternalValidationMeans(serviceName, measure);
		return Response.status(200).build();
	}
	
	@Path("/{service_name}/external/{measure_name}/{validation_means}")
	@DELETE
	@Consumes(MediaType.APPLICATION_JSON)
	public Response deleteExternalValidationMeansToWebService(@PathParam("service_name") String serviceName,
			@PathParam("measure_name") String measure, @PathParam("validation_means") String means, String valueKind) {
		OntologyQoSAPI ontology = new OntologyQoSAPI();
		
		System.out.println("DELETE external: " + serviceName);
		System.out.println("Validation means: " + means);
		
		ontology.deleteValidationMeansFromMeasure(serviceName, measure, valueKind);
		ontology.close();
		//deleteExternalValidationMeans(serviceName, measure);
		return Response.status(200).build();
	}
	
	/*@Path("/{service_name}/xml")
	@GET
	@Consumes(MediaType.APPLICATION_XML)
	public Response generateXML(@PathParam("service_name") String serviceName) {
		System.out.println("GET XML document: " + serviceName);
		Service service = readService(serviceName);
		
		try {
			XMLOutputter outputter = new XMLOutputter();  
			outputter.setFormat(Format.getPrettyFormat()); 

			FileOutputStream writer = new FileOutputStream(serviceName+".xml");
			
			for(int x=0; x<service.getInternal().size(); x++){
				Element wsqr = new Element("WSQRMeasure");

				Element internalmeasure = new Element("InternalMeasure");
				Element internalmeasurekind = new Element("InternalMeasureKind");
				internalmeasurekind.setText(service.getInternal().get(x).getMeasureKind());
				Element internalvalidationmeans = new Element("InternalValidationMeans");
				internalvalidationmeans.setAttribute(new Attribute("InternalValidationMeansName", service.getInternal().get(x).getValidationMeans()));
				Element internalvalidationattributes = new Element("InternalValidationAttributes");
				
				internalvalidationattributes.setAttribute(new Attribute("AttributeName", service.getInternal().get(x).getAttributeName()));
				internalvalidationattributes.setAttribute(new Attribute("AttributeValue", service.getInternal().get(x).getAttributeValue()));
				internalmeasure.addContent(internalvalidationmeans);
				internalmeasure.addContent(internalmeasurekind);
				internalvalidationmeans.addContent(internalvalidationattributes);
				
				wsqr.addContent(internalmeasure);
				
				Element inmeasurename = new Element("MeasureName");
				inmeasurename.setText(service.getInternal().get(x).getName());
				Element inmeasurevalue = new Element("MeasureValue");
				inmeasurevalue.setText(service.getInternal().get(x).getValue()+"");
				Element inmeasurevaluekind = new Element("MeasureValueKind");
				inmeasurevaluekind.setText(service.getInternal().get(x).getValueKind());
				Element inmeasuredefinition = new Element("MeasureDefinition");
				
				inmeasuredefinition.setAttribute(new Attribute("Description", service.getInternal().get(x).getDescription()));
				inmeasuredefinition.setAttribute(new Attribute("Formula", service.getInternal().get(x).getFormula()));
				
				wsqr.addContent(inmeasurename);
				wsqr.addContent(inmeasurevalue);
				wsqr.addContent(inmeasurevaluekind);
				wsqr.addContent(inmeasuredefinition);
				
				outputter.output(wsqr, writer);
			}

			for(int x=0; x<service.getExternal().size(); x++){
				Element wsqr = new Element("WSQRMeasure");

				Element externalmeasure = new Element("ExternalMeasure");
				Element externalmeasurevalidationmeans = new Element("ExternalMeasureValidationMeans");
				externalmeasurevalidationmeans.setAttribute(new Attribute("ExternalValidationMeansName", service.getExternal().get(x).getValidationMeans()));
				Element externalvalidationmeansattribute = new Element("ExternalValidationMeansAttribute");
				Element statisticalsignificancelevel = new Element("StatisticalSignificanceLevel");
				statisticalsignificancelevel.setAttribute(new Attribute("Value", service.getExternal().get(x).getStatisticalSignificance()+""));
				Element pvalue = new Element("PValue");
				pvalue.setAttribute(new Attribute("Value", service.getExternal().get(x).getpValue()+"")); 
				Element statisticaltestused = new Element("StatisticalTestUsed");
				statisticaltestused.setAttribute(new Attribute("Name", service.getExternal().get(x).getStatisticalTest()));
				Element accuracylevel = new Element("AccuracyLevel");
				accuracylevel.setAttribute(new Attribute("Value", service.getExternal().get(x).getAccuracyLevel()+""));
				Element accuracyindicatorused = new Element("AccuracyIndicatorUsed");
				accuracyindicatorused.setAttribute(new Attribute("Name", service.getExternal().get(x).getAccuracyIndicator()));
				
				externalmeasure.addContent(externalmeasurevalidationmeans);
				externalmeasurevalidationmeans.addContent(externalvalidationmeansattribute);
				externalvalidationmeansattribute.addContent(statisticalsignificancelevel);
				externalvalidationmeansattribute.addContent(pvalue);
				externalvalidationmeansattribute.addContent(statisticaltestused);
				externalvalidationmeansattribute.addContent(accuracylevel);
				externalvalidationmeansattribute.addContent(accuracyindicatorused);
				
				wsqr.addContent(externalmeasure);

				Element exmeasurename = new Element("MeasureName");
				exmeasurename.setText(service.getExternal().get(x).getName());
				Element exmeasurevalue = new Element("MeasureValue");
				exmeasurevalue.setText(service.getExternal().get(x).getValue()+"");
				Element exmeasurevaluekind = new Element("MeasureValueKind");
				exmeasurevaluekind.setText(service.getExternal().get(x).getValueKind());
				Element exmeasuredefinition = new Element("MeasureDefinition");
				exmeasuredefinition.setAttribute(new Attribute("Description", service.getExternal().get(x).getDescription()));
				exmeasuredefinition.setAttribute(new Attribute("Formula", service.getExternal().get(x).getFormula()));
				
				wsqr.addContent(exmeasurename);
				wsqr.addContent(exmeasurevalue);
				wsqr.addContent(exmeasurevaluekind);
				wsqr.addContent(exmeasuredefinition);
				
				outputter.output(wsqr, writer);
			}
		}  
		catch (IOException e) { 
			System.err.println("Error while parsing the document");
			e.printStackTrace(); 
		}

		String pathJar = this.getClass().getProtectionDomain().getCodeSource().getLocation().toString();
		int s = pathJar.lastIndexOf("/");
		String end = pathJar.substring(0, s);
		JOptionPane.showMessageDialog(null, "Your WSQR_"+serviceName+".xml"+" file has been saved into folder: "+ end, "File Saved", JOptionPane.INFORMATION_MESSAGE);
 
		return Response.status(200).build();
	}*/

	
	
	// ----------------------- METODI UTILIZZATI ---------------------------
	/*private String createServiceOntology(String serviceName) {
		Service service= readService(serviceName);
		OntologyManager.createOntology(service);
		return OntologyManager.getOntology(serviceName);
	}*/
	
	/*private void prepareInternalJson(Service service, JSONObject json) {
		json.put("Internal", new ArrayList());
		for(int i=0; i<service.getInternal().size(); i++) {
			json.append("Internal", new JSONObject()
			.put("MeasureName", service.getInternal().get(i).getName())
			.put("MeasureDefinition", new JSONObject()
				.put("Description", service.getInternal().get(i).getDescription())
				.put("Formula", service.getInternal().get(i).getFormula()))
			.put("MeasureValue", service.getInternal().get(i).getValue())
			.put("MeasureValueKind", service.getInternal().get(i).getValueKind())
			.put("InternalValidationMeansName", service.getInternal().get(i).getValidationMeans())
			.put("InternalValidationMeansAttributes", new JSONObject()
				.put("AttributeName", service.getInternal().get(i).getAttributeName())
				.put("AttributeValue", service.getInternal().get(i).getAttributeValue()))
			.put("InternalMeasureKind", service.getInternal().get(i).getMeasureKind())
			);	
		}
	}*/
	
	/*private void prepareExternalJson(Service service, JSONObject json) {
		json.put("External", new ArrayList());
		for(int i=0; i<service.getExternal().size(); i++) {
			json.append("External", new JSONObject()
			.put("MeasureName", service.getExternal().get(i).getName())
			.put("MeasureDefinition", new JSONObject()
				.put("Description", service.getExternal().get(i).getDescription())
				.put("Formula", service.getExternal().get(i).getFormula()))
			.put("MeasureValue", service.getExternal().get(i).getValue())
			.put("MeasureValueKind", service.getExternal().get(i).getValueKind())
			.put("ExternalValidationMeansName", service.getExternal().get(i).getValidationMeans())
			.put("ExternalValidationMeansAttributes", new JSONObject()
				.put("StatisticalSignificanceLevel", service.getExternal().get(i).getStatisticalSignificance())
				.put("PValue", service.getExternal().get(i).getpValue())
				.put("StatisticalTestUsed", service.getExternal().get(i).getStatisticalTest())
				.put("AccuracyLevel-Value", service.getExternal().get(i).getAccuracyLevel())
				.put("AccuracyIndicatorUser-Name", service.getExternal().get(i).getAccuracyIndicator()))
			.put("InternalMeasureKind", service.getExternal().get(i).getMeasureKind())
			);	
		}
	}*/
	
	private void prepareJson(ArrayList<String> measures, JSONObject json){
		json.put("Measures", measures);
	}
	
	/*private void deleteInternalValidationMeans(String serviceName, String measure) {
		Service service = readService(serviceName);
		for(int i=0; i<service.getInternal().size(); i++) {
			if(service.getInternal().get(i).equals(measure))
				service.getInternal().get(i).setValidationMeans("");
		}
	}*/
	
	/*private void deleteExternalValidationMeans(String serviceName, String measure) {
		Service service = readService(serviceName);
		for(int i=0; i<service.getExternal().size(); i++) {
			if(service.getExternal().get(i).equals(measure))
				service.getExternal().get(i).setValidationMeans("");
		}
	}*/
	
	/*private void addInternalValidationMeans(String serviceName, String measure, String means) {
		Service service = readService(serviceName);
		for(int i=0; i<service.getInternal().size(); i++) {
			if(service.getInternal().get(i).equals(measure)) {
				service.getInternal().get(i).setValidationMeans(means);
			}
		}
	}*/
	
	/*private void addExternalValidationMeans(String serviceName, String measure, String means) {
		Service service = readService(serviceName);
		for(int i=0; i<service.getExternal().size(); i++) {
			if(service.getExternal().get(i).equals(measure)) {
				service.getExternal().get(i).setValidationMeans(means);
			}
		}
	}*/
	
	/*private void addInternal(String serviceName, String measure) {
		File f=new File(servicesDirectory+serviceName);
		Service service=null;
		if(!f.exists()){
			try {
				f.createNewFile();
				service = new Service(serviceName);
			} catch (IOException e) {
				e.printStackTrace();
			}
		} else{
			 service = readService(serviceName);
		}
		service.addInternalMeasure(measure);
		writeService(serviceName, service);
	}*/
	
	/*private void addExternal(String serviceName, String measure) {
		File f=new File(servicesDirectory+serviceName);
		Service service=null;
		if(!f.exists()) {
			try {
				f.createNewFile();
				service = new Service(serviceName);
			} catch (IOException e) {
				e.printStackTrace();
			}
		} else{
			 service = readService(serviceName);
		}
		service.addExternalMeasure(measure);
		writeService(serviceName, service);
	}*/
	
	/*private void deleteInternal(String serviceName, String measureName) {
		Service service = readService(serviceName);
		for(int i=0;i<service.getInternal().size();i++) {
			if(service.getInternal().get(i).getName().equals(measureName)) {
				service.getInternal().get(i).setContent("");
			}
		}
	}*/
	
	/*private void deleteExternal(String serviceName, String measureName) {
		Service service = readService(serviceName);
		for(int i=0;i<service.getExternal().size();i++) {
			if(service.getExternal().get(i).getName().equals(measureName)) {
				service.getExternal().get(i).setContent("");
			}
		}
	}*/
	
	/*private void updateInternal(String serviceName, String oldName, String newName) {
		Service service = readService(serviceName);
		for(int i=0;i<service.getInternal().size();i++) {
			if(service.getInternal().get(i).getName().equals(oldName)) {
				service.getInternal().get(i).setContent(newName);
			}
		}
	}*/
	
	/*private void updateExternal(String serviceName, String oldName) {
		Service service = readService(serviceName);
		for(int i=0;i<service.getExternal().size();i++) {
			if(service.getExternal().get(i).getName().equals(oldName)) {
				service.getExternal().get(i).setContent(oldName);
			}
		}
	}*/

	/*private ArrayList<Measure> getExternalByKind(String serviceName, String valueKind) {
		Service service = readService(serviceName);
		ArrayList<Measure> measures = new ArrayList<Measure>();
		for(int i=0; i<service.getExternal().size(); i++) {
			if(service.getExternal().get(i).getValueKind().equals(valueKind)) 
				measures.add(service.getExternal().get(i));
		} 
			return measures;
	}*/

	/*private ArrayList<Measure> getInternalByKind(String serviceName, String valueKind) {
		Service service = readService(serviceName);
		ArrayList<Measure> measures = new ArrayList<Measure>();
		for(int i=0; i<service.getInternal().size(); i++) {
			if(service.getInternal().get(i).getValueKind().equals(valueKind))
				measures.add(service.getInternal().get(i));
		}
			return measures;
	}*/
	
	/*private Service readService(String serviceName) {
		File f=new File(servicesDirectory + serviceName);
		System.out.println("readService:" + f.getAbsolutePath());
		ObjectInputStream ois;
		Service service=null;
		try {
			ois = new ObjectInputStream(new FileInputStream(f));
			service =(Service)ois.readObject();
			ois.close();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		}
		return service;	
	}*/
	
	/*private void writeService(String serviceName, Service service) {
		File f = new File(servicesDirectory+serviceName);
		System.out.println("WriteFile: "+f.getAbsolutePath());
		if(!f.exists()){
			try {
				f.createNewFile();
				
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		ObjectOutputStream oos=null;
		try {
			oos = new ObjectOutputStream(new FileOutputStream(f));
			oos.writeObject(service);
			oos.flush();
			oos.close();
		} catch (IOException e) {
			e.printStackTrace();
		}	
	}*/
}