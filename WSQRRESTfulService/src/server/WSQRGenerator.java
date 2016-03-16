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
import javax.ws.rs.core.Response;
import org.jdom2.Attribute;
import org.jdom2.Document;
import org.jdom2.Element;
import org.jdom2.output.Format;
import org.jdom2.output.XMLOutputter;
import org.json.JSONObject;
import ontology.OntologyManager;
import com.hp.hpl.jena.query.ResultSet;

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

	private final String servicesDirectory="/Applications/eclipse-Luna/workspace/WSQRRESTfulService/services/";
	
	public String getName(String serviceName) {
		return serviceName;
	}
	
	@GET
	public Response helloWorld(){
		System.out.println("Hello, World!! ");
		return Response.status(200).entity("Hello, World!!").build();
	}
	
	@Path("/{service_name}")
	@GET
	@Produces("application/xml")
	public Response getService(@PathParam("service_name") String serviceName){
		System.out.println("GET service_name:"+serviceName);
		//read service file
		Service service = readService(serviceName);
		// return service file
		return Response.status(200).entity(service.toString()).build();
	}
	
	@Path("/{service_name}")
	@POST
	@Consumes("application/xml")
	public Response addWebService(@PathParam("service_name") String serviceName, String service){
		System.out.println("POST service_name: " + serviceName);
		System.out.println("FILE: " + service);
		String servName = createServiceOntology(service);
		if(servName.equals(serviceName)){
			JSONObject json = new JSONObject();
			json.put("message", "200 OK"
					+ "{"
					+ "		serviceName:	" + serviceName + ";"
					+ "}");
			Response.ok(json);
			return Response.status(200).build();
		}
		else {
			return Response.status(500).build();
		}
	}
	
	@Path("/{service_name}")
	@DELETE
	public Response deleteWebService(@PathParam("service_name") String serviceName){
		System.out.println("DELETE service_name:" + serviceName);
		//OntologyManager.deleteService(serviceName);
		File f = new File(servicesDirectory + serviceName);
		f.delete();
		return Response.status(200).build();	
	}

	@Path("/{service_name}/measures")
	@GET
	public Response getAllMeasuresOfWebService(@PathParam("service_name") String serviceName){
		System.out.println("GET measures: " + serviceName);
		ArrayList<Measure> measures = new ArrayList<Measure>();
		Service service = readService(serviceName);
		
		measures.addAll(service.getInternal());
		measures.addAll(service.getExternal());
		return Response.status(200).entity(measures).build();
	}
	
	@Path("/{service_name}/internal/{measure_name}/{value}")
	@GET
	@Consumes("application/xml")
	public Response getInternalMeasureValueForWebService(@PathParam("service_name") String serviceName,
			@PathParam("measure_name") String measure, @PathParam("value") String valueKind){
		System.out.println("GET measure value from: " + serviceName);
		String value = getInternal(serviceName, measure, valueKind);
		return Response.status(200).entity(value).build();
	}
	
	@Path("/{service_name}/external/{measure_name}/{value_kind}")
	@GET
	@Consumes("application/xml")
	public Response getExternalMeasureValueForWebService(@PathParam("service_name") String serviceName,
			@PathParam("measure_name") String measure, @PathParam("value") String valueKind){
		System.out.println("GET measure value from: " + serviceName);
		String value = getExternal(serviceName, measure, valueKind);
		return Response.status(200).entity(value).build();
	}

	@Path("/{service_name}/internal/{measure_name}")
	@DELETE
	@Consumes("application/xml")
	public Response deleteInternalMeasureOfWebService(@PathParam("service_name") String serviceName,
			@PathParam("measure_name") String measure) {
		System.out.println("DELETE measure from: " + serviceName);
		System.out.println("Measure: " + measure);
		deleteInternal(serviceName, measure);
		return Response.status(200).build();
	}

	@Path("/{service_name}/external/{measure_name}")
	@DELETE
	@Consumes("application/xml")
	public Response deleteExternalMeasureOfWebService(@PathParam("service_name") String serviceName,
			@PathParam("measure_name") String measure) {
		System.out.println("DELETE measure from: " + serviceName);
		System.out.println("Measure: " + measure);
		deleteExternal(serviceName, measure);
		return Response.status(200).build();
	}

	@Path("/{service_name}/internal/{measure_name}")
	@POST
	@Consumes("application/xml")
	public Response updateInternalMeasureOfWebService(@PathParam("service_name") String serviceName,
			@PathParam("measure_name") String oldMeasure, String newMeasure) {
		System.out.println("POST measure from: " + serviceName);
		System.out.println("Measure:" + oldMeasure);
		updateInternal(serviceName, oldMeasure, newMeasure);
		return Response.status(200).build();
	}

	@Path("/{service_name}/external/{measure_name}")
	@POST
	@Consumes("application/xml")
	public Response updateExternalMeasureOfWebService(@PathParam("service_name") String serviceName,
			@PathParam("measure_name") String oldMeasure, String newMeasure) {
		System.out.println("POST measure from: " + serviceName);
		System.out.println("Measure: " + oldMeasure);
		updateExternal(serviceName, oldMeasure, newMeasure);
		return Response.status(200).build();
	}

	@Path("/{service_name}/internal")
	@POST
	@Consumes("application/xml")
	public Response addInternalMeasure(@PathParam("service_name") String serviceName, String measure) {
		System.out.println("POST internal:" + serviceName);
		System.out.println("Measure: " + measure);
		addInternal(serviceName, measure);
		return Response.status(200).build();
	}

	@Path("/{service_name}/internal/{measure_name}")
	@GET
	@Consumes("application/xml")
	public Response getInternalMeasure(@PathParam("service_name") String serviceName,
			@PathParam("measure_name") String measureName) {
		System.out.println("GET internal :" + serviceName);
		System.out.println("Measure: " + measureName);
		String measure = getInternal(serviceName, measureName);
		return Response.status(200).entity(measure).build();
	}

	// TODO: Fix this, or find a way to get a measure value given also its kind
	/*@Path("/{service_name}/internal/{measure_name}/{value_kind}")
	@GET
	@Consumes("application/xml")
	public Response getInternalMeasure(@PathParam("service_name") String serviceName,
			@PathParam("measure_name") String measureName,
			@PathParam("value_kind") String valueKind) {
		System.out.println("GET internal with vk: " + serviceName);
		System.out.println("Measure: " + measureName);
		String measure = getInternal(serviceName, measureName, valueKind);
		return Response.status(200).entity(measure).build();
	}*/

	@Path("/{service_name}/external")
	@POST
	@Consumes("application/xml")
	public Response addExternalMeasure(@PathParam("service_name") String serviceName, String measure) {
		System.out.println("POST external: "+serviceName);
		addExternal(serviceName, measure );
		return Response.status(200).build();
	}

	@Path("/{service_name}/external/{measure_name}")
	@GET
	@Consumes("application/xml")
	public Response getExternalMeasure(@PathParam("service_name") String serviceName,
			@PathParam("measure_name") String measureName) {
		System.out.println("GET external: " + serviceName);
		System.out.println("Measure: " + measureName);
		String measure = getExternal(serviceName, measureName);
		return Response.status(200).entity(measure).build();
	}

	// TODO: Fix this, or find a way to get a measure value given also its kind
	/*@Path("/{service_name}/external/{measure_name}/{value_kind}")
	@GET
	@Consumes("application/xml")
	public Response getExternalMeasure(@PathParam("service_name") String serviceName,
			@PathParam("measure_name") String measureName,
			@PathParam("value_kind") String valueKind) {
		
		System.out.println("GET external with vk: " + serviceName);
		System.out.println("Measure: " + measureName);
		String measure = getExternal(serviceName, measureName, valueKind);
		return Response.status(200).entity(measure).build();
	}*/

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
	@Consumes("application/xml")
	public Response createOntology(@PathParam("service_name") String serviceName) {
		System.out.println("POST ontology: "+serviceName);
		String ontology = createServiceOntology(serviceName);		
		return Response.status(200).entity(ontology).build();
	}

	@Path("/{service_name}/ontology")
	@GET
	@Consumes("application/xml")
	public Response getOntology(@PathParam("service_name") String serviceName) {
		System.out.println("GET ontology: "+serviceName);
		String ontology = OntologyManager.getOntology(serviceName);
		return Response.status(200).entity(ontology).build();
	}

	@Path("/{service_name}/internal/{measure_name}/{validation_means}")
	@POST
	@Consumes("application/xml")
	public Response addInternalValidationMeansToWebService(@PathParam("service_name") String serviceName,
			@PathParam("validation_means") String means, @PathParam("measure_name") String measure) {
		addInternalValidationMeans(serviceName, measure, means);
		return Response.status(200).build();
	}

	@Path("/{service_name}/external/{measure_name}/{validation_means}")
	@POST
	@Consumes("application/xml")
	public Response addExternalValidationMeansToWebService(@PathParam("service_name") String serviceName,
			@PathParam("validation_means") String means, @PathParam("measure_name") String measure) {
		addExternalValidationMeans(serviceName, measure, means);
		return Response.status(200).build();
	}

	@Path("/{service_name}/internal/{measure_name}/{validation_means}")
	@PUT
	@Consumes("application/xml")
	public Response updateInternalValidationMeansToWebService(@PathParam("service_name") String serviceName,
			@PathParam("measure_name") String measure, @PathParam("validation_means") String means) {
		//è corretto che, sebbene sia un update, il metodo richiamato è anche qui addValidationMeans??
		addInternalValidationMeans(serviceName, measure, means);
		return Response.status(200).build();
	}

	@Path("/{service_name}/external/{measure_name}/{validation_means}")
	@PUT
	@Consumes("application/xml")
	public Response updateExternalValidationMeansToWebService(@PathParam("service_name") String serviceName,
			@PathParam("measure_name") String measure, @PathParam("validation_means") String means) {
		addExternalValidationMeans(serviceName, measure, means);
		return Response.status(200).build();
	}
	
	//SISTEMATO (è corretto che passiamo solo due parametri al metodo?)
	@Path("/{service_name}/internal/{measure_name}/{validation_means}")
	@DELETE
	@Consumes("application/xml")
	public Response deleteInternalValidationMeansToWebService(@PathParam("service_name") String serviceName,
			@PathParam("measure_name") String measure) {
		deleteInternalValidationMeans(serviceName, measure);
		return Response.status(200).build();
	}
	
	//SISTEMATO (è corretto che passiamo solo due parametri al metodo?)
	@Path("/{service_name}/external/{measure_name}/{validation_means}")
	@DELETE
	@Consumes("application/xml")
	public Response deleteExternalValidationMeansToWebService(@PathParam("service_name") String serviceName,
			@PathParam("measure_name") String measure) {
		deleteExternalValidationMeans(serviceName, measure);
		return Response.status(200).build();
	}
	
	@Path("/{service_name}/xml")
	@GET
	@Consumes("application/xml")
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
	}

	private String createServiceOntology(String serviceName) {
		Service service= readService(serviceName);
		OntologyManager.createOntology(service);
		return OntologyManager.getOntology(serviceName);
	}
	
	private void deleteInternalValidationMeans(String serviceName, String measure) {
		Service service = readService(serviceName);
		for(int i=0; i<service.getInternal().size(); i++) {
			if(service.getInternal().get(i).equals(measure))
				service.getInternal().get(i).setValidationMeans("");
		}
	}
	
	private void deleteExternalValidationMeans(String serviceName, String measure) {
		Service service = readService(serviceName);
		for(int i=0; i<service.getExternal().size(); i++) {
			if(service.getExternal().get(i).equals(measure))
				service.getExternal().get(i).setValidationMeans("");
		}
	}
	
	private void addInternalValidationMeans(String serviceName, String measure, String means) {
		Service service = readService(serviceName);
		for(int i=0; i<service.getInternal().size(); i++) {
			if(service.getInternal().get(i).equals(measure)) {
				service.getInternal().get(i).setValidationMeans(means);
			}
		}
	}
	
	private void addExternalValidationMeans(String serviceName, String measure, String means) {
		Service service = readService(serviceName);
		for(int i=0; i<service.getExternal().size(); i++) {
			if(service.getExternal().get(i).equals(measure)) {
				service.getExternal().get(i).setValidationMeans(means);
			}
		}
	}
	
	private void addInternal(String serviceName, String measure) {
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
	}
	
	private void addExternal(String serviceName, String measure) {
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
	}
	
	private String getExternal(String serviceName, String measureName) {
		Service service = readService(serviceName);
		for(int i=0;i<service.getExternal().size();i++) {
			if(service.getExternal().get(i).getName().equals(measureName)) {
				return service.getExternal().get(i).getContent();
			}
		}
		return null;
	}
	
	private String getInternal(String serviceName, String measureName) {
		Service service = readService(serviceName);
		for(int i=0;i<service.getInternal().size();i++) {
			if(service.getInternal().get(i).getName().equals(measureName)) {
				return service.getInternal().get(i).getContent();
			}
		}
		return null;
	}
	
	private void deleteInternal(String serviceName, String measureName) {
		Service service = readService(serviceName);
		for(int i=0;i<service.getInternal().size();i++) {
			if(service.getInternal().get(i).getName().equals(measureName)) {
				service.getInternal().get(i).setContent("");
			}
		}
	}
	
	private void deleteExternal(String serviceName, String measureName) {
		Service service = readService(serviceName);
		for(int i=0;i<service.getExternal().size();i++) {
			if(service.getExternal().get(i).getName().equals(measureName)) {
				service.getExternal().get(i).setContent("");
			}
		}
	}
	
	private void updateInternal(String serviceName, String oldName, String newName) {
		Service service = readService(serviceName);
		for(int i=0;i<service.getInternal().size();i++) {
			if(service.getInternal().get(i).getName().equals(oldName)) {
				service.getInternal().get(i).setContent(newName);
			}
		}
	}
	
	private void updateExternal(String serviceName, String oldName, String newName) {
		Service service = readService(serviceName);
		for(int i=0;i<service.getExternal().size();i++) {
			if(service.getExternal().get(i).getName().equals(oldName)) {
				service.getExternal().get(i).setContent(newName);
			}
		}
	}
	
	private String getExternal(String serviceName, String measureName,String valueKind) {
		Service service = readService(serviceName);
		for(int i=0;i<service.getExternal().size();i++){
			if(service.getExternal().get(i).getName().equals(measureName)
					&& service.getExternal().get(i).getValueKind().equals(valueKind)) {
				return service.getExternal().get(i).getContent();
			}
		}
		return null;
	}
	
	private String getInternal(String serviceName, String measureName, String valueKind) {
		Service service = readService(serviceName);
		for(int i=0;i<service.getInternal().size();i++) {
			if(service.getInternal().get(i).getName().equals(measureName)
					&& service.getInternal().get(i).getValueKind().equals(valueKind)) {
				return service.getInternal().get(i).getContent();
			}
		}
		return null;
	}
	
	private Service readService(String serviceName) {
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
	}
	
	private void writeService(String serviceName, Service service) {
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
	}
}