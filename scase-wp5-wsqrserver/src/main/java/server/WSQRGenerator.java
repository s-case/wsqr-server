package server;

import java.util.ArrayList;
import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import org.json.JSONException;
import org.json.JSONObject;
import eu.scase.qosontology.OntologyQoSAPI;

/**
 * Provides the WSQR Restful set of methods for all the WSQR operations in the API.
 * 
 * @author Carola Bianchi.
 */

/*
 * XML example
 * <?xml version="1.0" encoding="UTF-8"?>
 * <WSQRMeasure>
 * 
 * <InternalMeasure>
 * <InternalMeasureValidationMeans InternalValidationMeansName="MeasurementTheory">
 * <InternalValidationMeansAttributes AttributeName="AttributeName" AttributeValue="AttributeValue" />
 * </InternalMeasureValidationMeans>
 * <InternalMeasureKind>DynamicMeasure</InternalMeasureKind>
 * </InternalMeasure>
 * 
 * <MeasureName>MeasureName</MeasureName>
 * <MeasureValue>MeasureValue</MeasureValue>
 * <MeasureValueKind>Average</MeasureValueKind>
 * <MeasureDefinition Description="Description" Formula="Formula" />
 * 
 * <ExternalMeasure>
 * <ExternalMeasureValidationMeans ExternalValidationMeansName="MeasurementTheory">
 * <ExternalValidationMeansAttribute>
 * <StatisticalSignificanceLevel Value="SSL" />
 * <PValue Value="PV" />
 * <StatisticalTestUsed Name="STU" />
 * <AccuracyLevel Value="ALV" />
 * <AccuracyIndicatorUsed Name="AIUN" />
 * </ExternalValidationMeansAttribute>
 * </ExternalMeasureValidationMeans>
 * </ExternalMeasure>
 * 
 * <MeasureName>MeasureName</MeasureName>
 * <MeasureValue>MeasureValue</MeasureValue>
 * <MeasureValueKind>Average</MeasureValueKind>
 * <MeasureDefinition Description="Description" Formula="Formula" />
 * 
 * </WSQRMeasure>
 */

@Path("/services")
public class WSQRGenerator {

	// private final String servicesDirectory="/Applications/eclipse-Luna/workspace/WSQRRESTfulService/services/";

	/*
	 * public String getName(String serviceName) {
	 * return serviceName;
	 * }
	 */

	@GET
	public Response helloWorld() {
		System.out.println("Hello, World!! ");
		return Response.status(200).entity("Hello, World!!").build();
	}	
	
	@Path("/{service_name}/add")
	@POST
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public Response addWebService(@PathParam("service_name") String serviceName, String request) throws JSONException {
		OntologyQoSAPI ontology = new OntologyQoSAPI();

		System.out.println("POST service_name: " + serviceName);

		String service_name;
		JSONObject jsonRequest = new JSONObject(request);
		if (!jsonRequest.has("service_name"))
			throw new WebApplicationException(Response.status(422).entity("Please include a \"phrase\" JSON key")
					.type("text/plain").build());
		service_name = jsonRequest.getString("service_name");
	
		ontology.addWebService(service_name);
		JSONObject json = new JSONObject();
		json.put("service_name", service_name);
		return Response.status(200).entity(json.toString()).build();
	}
	
	@Path("/{service_name}/internal/{measure_name}/{validation_means}/add")
	@POST
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public Response addInternalValidationMeansToWebService(@PathParam("service_name") String serviceName,
			@PathParam("validation_means") String means,
			@PathParam("measure_name") String measure, String request) throws JSONException {
		OntologyQoSAPI ontology = new OntologyQoSAPI();

		System.out.println("POST internal: " + serviceName);
		System.out.println("Validation Means: " + means);
		
		JSONObject jsonRequest = new JSONObject(request);
		if (!jsonRequest.has("service_name"))
			throw new WebApplicationException(Response.status(422).entity("Please include a \"phrase\" JSON key")
					.type("text/plain").build());
		String measure_kind = jsonRequest.getString("measure_kind");
		String attribute_name = jsonRequest.getString("attribute_name");
		String attribute_value = jsonRequest.getString("attribute_value");
		String internal_measure_kind = jsonRequest.getString("internal_measure_kind");
		
		ontology.addInternalValidationMeansToMeasure(serviceName, measure, measure_kind, means, attribute_name, attribute_value, internal_measure_kind);
		ontology.close();
		System.out.println("INTERNAL VALIDATION AGGIUNTA!");
		JSONObject json = new JSONObject();
		json.put("service_name", serviceName);
		json.put("measure_name", measure);
		//json.put("measure_kind", measure_kind);
		json.put("validation_means", means);
		//json.put("accuracy_indicator_used", accuracy_indicator_used);
		//json.put("accuracy_level", accuracy_level);
		//json.put("statistical_test_used", statistical_test_used);
		//json.put("p_value", p_value);
		//json.put("statistical_significance_level", statistical_significance_level);
		
		return Response.status(200).entity(json.toString()).build();
	}
	
	@Path("/{service_name}/external/{measure_name}/{validation_means}/add")
	@POST
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public Response addExternalValidationMeansToWebService(@PathParam("service_name") String serviceName,
			@PathParam("measure_name") String measure, @PathParam("validation_means") String means,
			String request) throws JSONException {
		OntologyQoSAPI ontology = new OntologyQoSAPI();

		System.out.println("POST external: " + measure);
		System.out.println("Validation Means: " + means);
	
		JSONObject jsonRequest = new JSONObject(request);
		if (!jsonRequest.has("service_name"))
			throw new WebApplicationException(Response.status(422).entity("Please include a \"phrase\" JSON key")
					.type("text/plain").build());
		String measure_kind = jsonRequest.getString("measure_value_kind");
		String accuracy_indicator_used = jsonRequest.getString("accuracy_indicator_used");
		double accuracy_level = jsonRequest.getDouble("accuracy_level");
		String statistical_test_used = jsonRequest.getString("statistical_test_used");
		double p_value = jsonRequest.getDouble("p_value");
		double statistical_significance_level = jsonRequest.getDouble("statistical_significance_level");
		System.out.println("Before invoking the method on the ontology");
		
		ontology.addValidationMeansToMeasure(serviceName, measure, measure_kind, means, accuracy_indicator_used,
				(float) accuracy_level, statistical_test_used, (float) p_value, (float) statistical_significance_level);
		ontology.close();
		JSONObject json = new JSONObject();
		json.put("service_name", serviceName);
		json.put("measure_name", measure);
		//json.put("measure_kind", measure_kind);
		json.put("validation_means", means);
		//json.put("accuracy_indicator_used", accuracy_indicator_used);
		//json.put("accuracy_level", accuracy_level);
		//json.put("statistical_test_used", statistical_test_used);
		//json.put("p_value", p_value);
		//json.put("statistical_significance_level", statistical_significance_level);
		return Response.status(200).entity(json.toString()).build();
	}
	
	@Path("/{service_name}/internal/{measure_name}/add")
	@POST
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public Response addInternalMeasure(@PathParam("service_name") String serviceName,
			@PathParam("measure_name") String measure, String request) throws JSONException {
		OntologyQoSAPI ontology = new OntologyQoSAPI();

		System.out.println("POST internal: " + serviceName);
		System.out.println("Measure: " + measure);
		
		JSONObject jsonRequest = new JSONObject(request);
		if (!jsonRequest.has("service_name"))
			throw new WebApplicationException(Response.status(422).entity("Please include a \"phrase\" JSON key")
					.type("text/plain").build());
		String measureKind = jsonRequest.getString("measure_kind");
		double measureValue = jsonRequest.getDouble("measure_value");
		
		ontology.addMeasureToWebService(serviceName, measure, measureKind, (float) measureValue);
		ontology.close();
		
		JSONObject json = new JSONObject();
		json.put("service_name", serviceName);
		json.put("measure_name", measure);
		return Response.status(200).entity(json.toString()).build();
	}
	
	@Path("/{service_name}/external/{measure_name}/add")
	@POST
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public Response addExternalMeasure(@PathParam("service_name") String serviceName,
			@PathParam("measure_name") String measure, String request) throws JSONException {
		OntologyQoSAPI ontology = new OntologyQoSAPI();

		System.out.println("POST external: " + serviceName);
		System.out.println("Measure: " + measure);

		JSONObject jsonRequest = new JSONObject(request);
		if (!jsonRequest.has("service_name"))
			throw new WebApplicationException(Response.status(422).entity("Please include a \"phrase\" JSON key")
				.type("text/plain").build());
		String measureKind = jsonRequest.getString("measure_kind");
		double measureValue = jsonRequest.getDouble("measure_value");
		
		ontology.addMeasureToWebService(serviceName, measure, measureKind, (float) measureValue);
		ontology.close();
		
		JSONObject json = new JSONObject();
		json.put("service_name", serviceName);
		json.put("measure_name", measure);
		return Response.status(200).entity(json.toString()).build();
	}
	
	@Path("/{service_name}/delete")
	@DELETE
	@Produces(MediaType.APPLICATION_JSON)
	public Response deleteWebService(@PathParam("service_name") String serviceName) throws JSONException {
		System.out.println("mi sto connettendo all'ontologia");
		OntologyQoSAPI ontology = new OntologyQoSAPI();

		System.out.println("DELETE service_name:" + serviceName);
		
		ontology.deleteWebService(serviceName);
		ontology.close();
		JSONObject json = new JSONObject();
		json.put("service_name", serviceName);
		return Response.status(200).entity(json.toString()).build();
	}
	
	@Path("/{service_name}/measures")
	@GET
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public Response getAllMeasuresOfWebService(@PathParam("service_name") String serviceName, String request) throws JSONException {
		OntologyQoSAPI ontology = new OntologyQoSAPI();

		System.out.println("GET measures: " + serviceName);
		JSONObject jsonRequest = new JSONObject(request);
		if (!jsonRequest.has("service_name"))
			throw new WebApplicationException(Response.status(422).entity("Please include a \"phrase\" JSON key")
					.type("text/plain").build());
		
		ArrayList<String> measures = ontology.getAllMeasuresOfWebService(serviceName);
		ontology.close();
		JSONObject json = new JSONObject();
		prepareJson(measures, json);
		return Response.status(200).entity(json.toString()).build();
	}
	
	@Path("/{service_name}/internal/{measure_position}/get")
	@GET
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public Response getInternalMeasure(@PathParam("service_name") String serviceName,
			@PathParam("measure_position") int measurePos, String request) throws JSONException {
		OntologyQoSAPI ontology = new OntologyQoSAPI();
		System.out.println("GET internal :" + serviceName);
		System.out.println("MeasurePosition: " + measurePos);

		JSONObject jsonRequest = new JSONObject(request);
		if (!jsonRequest.has("service_name"))
			throw new WebApplicationException(Response.status(422).entity("Please include a \"phrase\" JSON key")
					.type("text/plain").build());

		String measure = ontology.getMeasureOfWebService(serviceName, measurePos);
		ontology.close();
		JSONObject json = new JSONObject();
		json.put("measure", measure);
		return Response.status(200).entity(json.toString()).build();
	}
	
	@Path("/{service_name}/internal/{measure_name}/{value_kind}")
	@GET
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public Response getInternalMeasure(@PathParam("service_name") String serviceName,
			@PathParam("measure_name") String measureName, @PathParam("value_kind") String valueKind, String request) throws JSONException {
		OntologyQoSAPI ontology = new OntologyQoSAPI();

		System.out.println("GET internal with vk: " + serviceName);
		System.out.println("Measure: " + measureName);
	
		JSONObject jsonRequest = new JSONObject(request);
		if (!jsonRequest.has("service_name"))
			throw new WebApplicationException(Response.status(422).entity("Please include a \"phrase\" JSON key")
					.type("text/plain").build());

		float measure = ontology.getMeasureValueForWebService(serviceName, measureName, valueKind);
		ontology.close();
		JSONObject json = new JSONObject();
		json.put("measure_value", measure);
		return Response.status(200).entity(json.toString()).build();
	}
	
	@Path("/{service_name}/external/{measure_position}")
	@GET
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public Response getExternalMeasure(@PathParam("service_name") String serviceName,
			@PathParam("measure_position") int measurePos, String request) throws JSONException {
		OntologyQoSAPI ontology = new OntologyQoSAPI();

		System.out.println("GET external: " + serviceName);
		System.out.println("Measure: " + measurePos);
		
		JSONObject jsonRequest = new JSONObject(request);
		if (!jsonRequest.has("service_name"))
			throw new WebApplicationException(Response.status(422).entity("Please include a \"phrase\" JSON key")
					.type("text/plain").build());

		String measure = ontology.getMeasureOfWebService(serviceName, measurePos);
		ontology.close();
		JSONObject json = new JSONObject();
		json.put("measure", measure);
		return Response.status(200).entity(json.toString()).build();
	}
	
	@Path("/{service_name}/external/{measure_name}/{value_kind}/get")
	@GET
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public Response getExternalMeasure(@PathParam("service_name") String serviceName,
			@PathParam("measure_name") String measureName, @PathParam("value_kind") String valueKind, String request) throws JSONException {
		OntologyQoSAPI ontology = new OntologyQoSAPI();

		System.out.println("GET external with vk: " + serviceName);
		System.out.println("Measure: " + measureName);
		
		JSONObject jsonRequest = new JSONObject(request);
		if (!jsonRequest.has("service_name"))
			throw new WebApplicationException(Response.status(422).entity("Please include a \"phrase\" JSON key")
					.type("text/plain").build());
				
		float measure = ontology.getMeasureValueForWebService(serviceName, measureName, valueKind);
		ontology.close();
		JSONObject json = new JSONObject();
		json.put("measure_value", measure);
		
		return Response.status(200).entity(json.toString())/*.type("application/json")*/.build();
	}
	
	@Path("/{service_name}/internal/{measure_name}/update")
	@POST
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public Response updateInternalMeasureOfWebService(@PathParam("service_name") String serviceName,
			@PathParam("measure_name") String measure, String request) throws JSONException {
		OntologyQoSAPI ontology = new OntologyQoSAPI();
			
		System.out.println("POST measure from: " + serviceName);
		System.out.println("Measure:" + measure);
		
		JSONObject jsonRequest = new JSONObject(request);
		if (!jsonRequest.has("service_name") && !jsonRequest.has("measure_name"))
			throw new WebApplicationException(Response.status(422).entity("Please include a \"phrase\" JSON key")
					.type("text/plain").build());
		String measureKind = jsonRequest.getString("measure_kind");
		double measureValue = jsonRequest.getDouble("measure_value");
					
		ontology.updateMeasureOfWebService(serviceName, measure, measureKind, (float) measureValue);
		ontology.close();
		JSONObject json = new JSONObject();
		json.put("service_name", serviceName);
		json.put("measure_name", measure);
		json.put("value_kind", measureKind);
		json.put("value", measureValue);
		return Response.status(200).entity(json.toString()).build();
	}
	
	@Path("/{service_name}/external/{measure_name}/update")
	@POST
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public Response updateExternalMeasureOfWebService(@PathParam("service_name") String serviceName,
			@PathParam("measure_name") String measure, String request) throws JSONException {
		OntologyQoSAPI ontology = new OntologyQoSAPI();
		
		System.out.println("POST measure from: " + serviceName);
		System.out.println("Measure: " + measure);
			
		JSONObject jsonRequest = new JSONObject(request);
		if (!jsonRequest.has("service_name") && !jsonRequest.has("measure_name"))
			throw new WebApplicationException(Response.status(422).entity("Please include a \"phrase\" JSON key")
					.type("text/plain").build());
		String measureKind = jsonRequest.getString("measure_kind");
		double measureValue = jsonRequest.getDouble("measure_value");
					
		ontology.updateMeasureOfWebService(serviceName, measure, measureKind, (float) measureValue);
		ontology.close();

		JSONObject json = new JSONObject();
		json.put("service_name", serviceName);
		json.put("measure_name", measure);
		//json.put("value_kind", measureKind);
		//json.put("value", measureValue);
		return Response.status(200).entity(json.toString()).build();
	}
	
	@Path("/{service_name}/internal/{measure_name}/{validation_means}/update")
	@POST
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public Response updateInternalValidationMeansToWebService(@PathParam("service_name") String serviceName,
			@PathParam("measure_name") String measure, @PathParam("validation_means") String means, String request) throws JSONException {
		OntologyQoSAPI ontology = new OntologyQoSAPI();

		System.out.println("POST internal: " + serviceName);
		System.out.println("Validation means: " + means);
		
		JSONObject jsonRequest = new JSONObject(request);
		if (!jsonRequest.has("service_name"))
			throw new WebApplicationException(Response.status(422).entity("Please include a \"phrase\" JSON key")
				.type("text/plain").build());
		String measure_kind = jsonRequest.getString("measure_kind");
		String attribute_name = jsonRequest.getString("attribute_name");
		String attribute_value = jsonRequest.getString("attribute_value");
		String internal_measure_kind = jsonRequest.getString("internal_measure_kind");
		ontology.updateInternalValidationMeansOfMeasure(serviceName, measure, measure_kind,
				means, attribute_name, attribute_value, internal_measure_kind);
		ontology.close();
		JSONObject json = new JSONObject();
		json.put("service_name", serviceName);
		json.put("measure_name", measure);
		json.put("validation_means", means);
		return Response.status(200).entity(json.toString()).build();
	}
	
	@Path("/{service_name}/external/{measure_name}/{validation_means}/update")
	@POST
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public Response updateExternalValidationMeansToWebService(@PathParam("service_name") String serviceName,
			@PathParam("measure_name") String measure, @PathParam("validation_means") String means,
			String request) throws JSONException {
		OntologyQoSAPI ontology = new OntologyQoSAPI();

		System.out.println("POST internal: " + serviceName);
		System.out.println("Validation means: " + means);

		JSONObject jsonRequest = new JSONObject(request);
		if (!jsonRequest.has("service_name") && !jsonRequest.has("measure_name") && !jsonRequest.has("validation_means"))
			throw new WebApplicationException(Response.status(422).entity("Please include a \"phrase\" JSON key")
				.type("text/plain").build());
		String measure_kind = jsonRequest.getString("measure_value_kind");
		String accuracy_indicator_used = jsonRequest.getString("accuracy_indicator_used");
		double accuracy_level = jsonRequest.getDouble("accuracy_level");
		String statistical_test_used = jsonRequest.getString("statistical_test_used");
		double p_value = jsonRequest.getDouble("p_value");
		double statistical_significance_level = jsonRequest.getDouble("statistical_significance_level");
		
		ontology.updateValidationMeansOfMeasure(serviceName, measure, measure_kind, means, accuracy_indicator_used,
				(float) accuracy_level, statistical_test_used, (float) p_value, (float) statistical_significance_level);
		ontology.close();
		
		JSONObject json = new JSONObject();
		json.put("service_name", serviceName);
		json.put("measure_name", measure);
		json.put("validation_means", means);

		return Response.status(200).entity(json.toString()).build();
	}
	
	@Path("/{service_name}/internal/{measure_name}/delete")
	@DELETE
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public Response deleteInternalMeasureOfWebService(@PathParam("service_name") String serviceName,
			@PathParam("measure_name") String measure, String request) throws JSONException {
		OntologyQoSAPI ontology = new OntologyQoSAPI();
		
		System.out.println("DELETE measure from: " + serviceName);
		System.out.println("Measure: " + measure);
		
		JSONObject jsonRequest = new JSONObject(request);
		if (!jsonRequest.has("service_name"))
			throw new WebApplicationException(Response.status(422).entity("Please include a \"phrase\" JSON key")
					.type("text/plain").build());
		String measureKind = jsonRequest.getString("measure_kind");
		
		ontology.deleteMeasureOfWebService(serviceName, measure, measureKind);
		ontology.close();
		JSONObject json = new JSONObject();
		json.put("service_name", serviceName);
		json.put("measure_name", measure);
		//json.put("value_kind", measureKind);
		return Response.status(200).entity(json.toString()).build();
	}
	
	@Path("/{service_name}/external/{measure_name}/delete")
	@DELETE
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public Response deleteExternalMeasureOfWebService(@PathParam("service_name") String serviceName,
			@PathParam("measure_name") String measure, String request) throws JSONException {
		OntologyQoSAPI ontology = new OntologyQoSAPI();
		
		System.out.println("DELETE measure from: " + serviceName);
		System.out.println("Measure: " + measure);
		
		JSONObject jsonRequest = new JSONObject(request);
		if (!jsonRequest.has("service_name"))
			throw new WebApplicationException(Response.status(422).entity("Please include a \"phrase\" JSON key")
					.type("text/plain").build());
		String measureKind = jsonRequest.getString("measure_kind");
		
		ontology.deleteMeasureOfWebService(serviceName, measure, measureKind);
		ontology.close();
		JSONObject json = new JSONObject();
		json.put("service_name", serviceName);
		json.put("measure_name", measure);
		//json.put("value_kind", measureKind);
		return Response.status(200).entity(json.toString()).build();
	}
	
	@Path("/{service_name}/internal/{measure_name}/{validation_means}/delete")
	@DELETE
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public Response deleteInternalValidationMeansToWebService(@PathParam("service_name") String serviceName,
			@PathParam("measure_name") String measure, @PathParam("validation_means") String means, String request) throws JSONException {
		OntologyQoSAPI ontology = new OntologyQoSAPI();

		System.out.println("DELETE internal: " + serviceName);
		System.out.println("Validation means: " + means);

		JSONObject jsonRequest = new JSONObject(request);
		if (!jsonRequest.has("service_name"))
			throw new WebApplicationException(Response.status(422).entity("Please include a \"phrase\" JSON key")
					.type("text/plain").build());
		String valueKind = jsonRequest.getString("value_kind");
		
		ontology.deleteValidationMeansFromMeasure(serviceName, measure, valueKind);
		ontology.close();
		JSONObject json = new JSONObject();
		json.put("service_name", serviceName);
		json.put("measure_name", measure);
		return Response.status(200).entity(json.toString()).build();
	}

	@Path("/{service_name}/external/{measure_name}/{validation_means}/delete")
	@DELETE
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public Response deleteExternalValidationMeansToWebService(@PathParam("service_name") String serviceName,
			@PathParam("measure_name") String measure, @PathParam("validation_means") String means, String request) throws JSONException {
		OntologyQoSAPI ontology = new OntologyQoSAPI();

		System.out.println("DELETE external: " + serviceName);
		System.out.println("Validation means: " + means);

		JSONObject jsonRequest = new JSONObject(request);
		if (!jsonRequest.has("service_name"))
			throw new WebApplicationException(Response.status(422).entity("Please include a \"phrase\" JSON key")
					.type("text/plain").build());
		String valueKind = jsonRequest.getString("value_kind");
				
		ontology.deleteValidationMeansFromMeasure(serviceName, measure, valueKind);
		ontology.close();
		JSONObject json = new JSONObject();
		json.put("service_name", serviceName);
		json.put("measure_name", measure);
		return Response.status(200).entity(json.toString()).build();
	}
	
	private void prepareJson(ArrayList<String> measures, JSONObject json) throws JSONException {
		json.put("Measures", measures);
	}
}
