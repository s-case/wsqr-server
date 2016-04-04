package server;

import java.util.ArrayList;
import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import org.json.JSONException;
import org.json.JSONObject;
import ontology.OntologyManager;
import com.hp.hpl.jena.query.ResultSet;
import eu.scase.qosontology.OntologyQoSAPI;

/**
 * Provides the WSQR Restful set of methods for all the WSQR operations in the API.
 * 
 * @author Carola Bianchi and Davide Tosi.
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
		// connection to the ontology
		OntologyQoSAPI ontology = new OntologyQoSAPI();

		System.out.println("POST service_name: " + serviceName);

		// Get the name of the service
		String service_name;
		// Option 1: from the path parameters
		// service_name = serviceName;
		// Option 2: from the json parameters
		JSONObject jsonRequest = new JSONObject(request);
		if (!jsonRequest.has("service_name"))
			throw new WebApplicationException(Response.status(422).entity("Please include a \"phrase\" JSON key")
					.type("text/plain").build());
		service_name = jsonRequest.getString("service_name");
		
		// add web service
		ontology.addWebService(service_name);
		JSONObject json = new JSONObject();
		json.put("service_name", service_name);
		ontology.close();
		return Response.status(200).entity(json.toString()).build();
	}

	@Path("/{service_name}/delete")
	@DELETE
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public Response deleteWebService(@PathParam("service_name") String serviceName) {
		OntologyQoSAPI ontology = new OntologyQoSAPI();

		System.out.println("DELETE service_name:" + serviceName);

		ontology.deleteWebService(serviceName);
		ontology.close();

		return Response.status(200).build();
	}

	@Path("/{service_name}/measures")
	@GET
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public Response getAllMeasuresOfWebService(@PathParam("service_name") String serviceName) throws JSONException {
		OntologyQoSAPI ontology = new OntologyQoSAPI();

		System.out.println("GET measures: " + serviceName);
		// Service service = readService(serviceName);

		ArrayList<String> measures = ontology.getAllMeasuresOfWebService(serviceName);

		JSONObject json = new JSONObject();
		prepareJson(measures, json);
		// prepareInternalJson(service, json);
		// prepareExternalJson(service, json);

		ontology.close();

		return Response.status(200).entity(json.toString()).type("application/json").build();
	}

	@Path("/{service_name}/internal/{measure_name}/delete")
	@DELETE
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public Response deleteInternalMeasureOfWebService(@PathParam("service_name") String serviceName,
			@PathParam("measure_name") String measure, String valueKind) {
		OntologyQoSAPI ontology = new OntologyQoSAPI();
		System.out.println("DELETE measure from: " + serviceName);
		System.out.println("Measure: " + measure);
		ontology.deleteMeasureOfWebService(serviceName, measure, valueKind);
		ontology.close();
		// deleteInternal(serviceName, measure);
		return Response.status(200).build();
	}

	@Path("/{service_name}/external/{measure_name}/delete")
	@DELETE
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public Response deleteExternalMeasureOfWebService(@PathParam("service_name") String serviceName,
			@PathParam("measure_name") String measure, String valueKind) {
		OntologyQoSAPI ontology = new OntologyQoSAPI();
		System.out.println("DELETE measure from: " + serviceName);
		System.out.println("Measure: " + measure);
		ontology.deleteMeasureOfWebService(serviceName, measure, valueKind);
		ontology.close();
		// deleteExternal(serviceName, measure);
		return Response.status(200).build();
	}

	@Path("/{service_name}/internal/{measure_name}/update")
	@POST
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public Response updateInternalMeasureOfWebService(@PathParam("service_name") String serviceName,
			@PathParam("measure_name") String measure, String valueKind, float value) {
		OntologyQoSAPI ontology = new OntologyQoSAPI();
		System.out.println("POST measure from: " + serviceName);
		System.out.println("Measure:" + measure);
		ontology.updateMeasureOfWebService(serviceName, measure, valueKind, value);
		ontology.close();
		// updateInternal(serviceName, measure, measure);
		return Response.status(200).build();
	}

	@Path("/{service_name}/external/{measure_name}/update")
	@POST
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public Response updateExternalMeasureOfWebService(@PathParam("service_name") String serviceName,
			@PathParam("measure_name") String measure, String valueKind, float value) {
		OntologyQoSAPI ontology = new OntologyQoSAPI();
		System.out.println("POST measure from: " + serviceName);
		System.out.println("Measure: " + measure);
		ontology.updateMeasureOfWebService(serviceName, measure, valueKind, value);
		ontology.close();
		// updateExternal(serviceName, measure);
		return Response.status(200).build();
	}

	@Path("/{service_name}/internal/{measure_name}/add")
	@POST
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public Response addInternalMeasure(@PathParam("service_name") String serviceName,
			@PathParam("measure_name") String measure, String request) throws JSONException {
		OntologyQoSAPI ontology = new OntologyQoSAPI();

		System.out.println("POST internal:" + serviceName);
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

		// addInternal(serviceName, measure);
		return Response.status(200).entity(json.toString()).build();
	}

	@Path("/{service_name}/external/{measure_name}/add")
	@POST
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public Response addExternalMeasure(@PathParam("service_name") String serviceName, @PathParam("service_name") String measure,
			String measureKind, float measureValue) {
		OntologyQoSAPI ontology = new OntologyQoSAPI();

		System.out.println("POST external: " + serviceName);
		System.out.println("Measure: " + measure);

		ontology.addMeasureToWebService(serviceName, measure, measureKind, measureValue);
		ontology.close();
		// addExternal(serviceName, measure);
		return Response.status(200).build();
	}

	@Path("/{service_name}/internal/{measure_position}")
	@GET
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public Response getInternalMeasure(@PathParam("service_name") String serviceName,
			@PathParam("measure_position") int measurePos) throws JSONException {
		OntologyQoSAPI ontology = new OntologyQoSAPI();

		System.out.println("GET internal :" + serviceName);
		System.out.println("MeasurePosition: " + measurePos);

		String measure = ontology.getMeasureOfWebService(serviceName, measurePos);

		ontology.close();
		return Response.status(200).entity(new JSONObject().put("Measure", measure).toString()).type("application/json").build();
	}

	@Path("/{service_name}/internal/{measure_name}/{value_kind}")
	@GET
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public Response getInternalMeasure(@PathParam("service_name") String serviceName,
			@PathParam("measure_name") String measureName, @PathParam("value_kind") String valueKind)
			throws JSONException {
		OntologyQoSAPI ontology = new OntologyQoSAPI();

		System.out.println("GET internal with vk: " + serviceName);
		System.out.println("Measure: " + measureName);

		float measure = ontology.getMeasureValueForWebService(serviceName, measureName, valueKind);

		ontology.close();
		return Response.status(200).entity(new JSONObject().put("MeasureValue", measure).toString()).type("application/json")
				.build();
	}

	@Path("/{service_name}/external/{measure_position}")
	@GET
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public Response getExternalMeasure(@PathParam("service_name") String serviceName,
			@PathParam("measure_position") int measurePos) throws JSONException {
		OntologyQoSAPI ontology = new OntologyQoSAPI();

		System.out.println("GET external: " + serviceName);
		System.out.println("Measure: " + measurePos);

		String measure = ontology.getMeasureOfWebService(serviceName, measurePos);

		ontology.close();
		return Response.status(200).entity(new JSONObject().put("Measure", measure).toString()).type("application/json").build();
	}

	@Path("/{service_name}/external/{measure_name}/{value_kind}/get")
	@GET
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public Response getExternalMeasure(@PathParam("service_name") String serviceName,
			@PathParam("measure_name") String measureName, @PathParam("value_kind") String valueKind)
			throws JSONException {
		OntologyQoSAPI ontology = new OntologyQoSAPI();

		System.out.println("GET external with vk: " + serviceName);
		System.out.println("Measure: " + measureName);
		float measure = ontology.getMeasureValueForWebService(serviceName, measureName, valueKind);

		ontology.close();
		return Response.status(200).entity(new JSONObject().put("MeasureValue", measure).toString()).type("application/json")
				.build();
	}

	@Path("/ontology")
	@DELETE
	public Response deleteOntology() {
		System.out.println("DELETE ontology: ");
		ResultSet rs = OntologyManager.listServices();
		while (rs.hasNext()) {
			String next = rs.next().toString();
			System.out.println("next: " + next);
			String name = next.substring(next.indexOf("\"") + 1, next.lastIndexOf("\""));
			System.out.println("name:" + name);
			OntologyManager.deleteService(name);
		}
		return Response.status(200).build();
	}

	@Path("/{service_name}/ontology")
	@POST
	@Consumes(MediaType.APPLICATION_JSON)
	public Response createOntology(@PathParam("service_name") Service service) {
		System.out.println("POST ontology: " + service.getName());
		OntologyManager.createOntology(service);
		return Response.status(200).build();
	}

	@Path("/{service_name}/ontology")
	@GET
	@Consumes(MediaType.APPLICATION_JSON)
	public Response getOntology(@PathParam("service_name") String serviceName) {
		System.out.println("GET ontology: " + serviceName);
		String ontology = OntologyManager.getOntology(serviceName);
		return Response.status(200).entity(ontology.toString()).build();
	}

	@Path("/{service_name}/internal/{measure_name}/{validation_means}/add")
	@POST
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public Response addInternalValidationMeansToWebService(@PathParam("service_name") String serviceName,
			@PathParam("validation_means") String means, @PathParam("measure_name") String measure,
			String MeasureValueKind, String AccuracyIndicatorUsed, float AccuracyLevel, String StatisticalTestUsed,
			float PValue, float StatisticalSignificanceLevel) {
		OntologyQoSAPI ontology = new OntologyQoSAPI();

		System.out.println("POST internal: " + serviceName);
		System.out.println("Validation Means: " + means);

		ontology.addValidationMeansToMeasure(serviceName, measure, MeasureValueKind, means, AccuracyIndicatorUsed,
				AccuracyLevel, StatisticalTestUsed, PValue, StatisticalSignificanceLevel);
		ontology.close();
		// addInternalValidationMeans(serviceName, measure, means);
		return Response.status(200).build();
	}

	@Path("/{service_name}/external/{measure_name}/{validation_means}/add")
	@POST
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public Response addExternalValidationMeansToWebService(@PathParam("service_name") String serviceName,
			@PathParam("validation_means") String means, @PathParam("measure_name") String measure,
			String MeasureValueKind, String AccuracyIndicatorUsed, float AccuracyLevel, String StatisticalTestUsed,
			float PValue, float StatisticalSignificanceLevel) {
		OntologyQoSAPI ontology = new OntologyQoSAPI();

		System.out.println("POST external: " + serviceName);
		System.out.println("Validation Means: " + means);

		ontology.addValidationMeansToMeasure(serviceName, measure, MeasureValueKind, means, AccuracyIndicatorUsed,
				AccuracyLevel, StatisticalTestUsed, PValue, StatisticalSignificanceLevel);
		ontology.close();
		// addExternalValidationMeans(serviceName, measure, means);
		return Response.status(200).build();
	}

	@Path("/{service_name}/internal/{measure_name}/{validation_means}/update")
	@PUT
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public Response updateInternalValidationMeansToWebService(@PathParam("service_name") String serviceName,
			@PathParam("measure_name") String measure, @PathParam("validation_means") String means, String valueKind,
			String AccuracyIndicatorUsed, float AccuracyLevel, String StatisticalTestUsed, float PValue,
			float StatisticalSignificanceLevel) {
		OntologyQoSAPI ontology = new OntologyQoSAPI();

		System.out.println("POST internal: " + serviceName);
		System.out.println("Validation means: " + means);

		ontology.updateValidationMeansOfMeasure(serviceName, measure, valueKind, means, AccuracyIndicatorUsed,
				AccuracyLevel, StatisticalTestUsed, PValue, StatisticalSignificanceLevel);
		ontology.close();
		// addInternalValidationMeans(serviceName, measure, means);
		return Response.status(200).build();
	}

	@Path("/{service_name}/external/{measure_name}/{validation_means}/update")
	@PUT
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public Response updateExternalValidationMeansToWebService(@PathParam("service_name") String serviceName,
			@PathParam("measure_name") String measure, @PathParam("validation_means") String means, String valueKind,
			String AccuracyIndicatorUsed, float AccuracyLevel, String StatisticalTestUsed, float PValue,
			float StatisticalSignificanceLevel) {
		OntologyQoSAPI ontology = new OntologyQoSAPI();

		System.out.println("POST internal: " + serviceName);
		System.out.println("Validation means: " + means);

		ontology.updateValidationMeansOfMeasure(serviceName, measure, valueKind, means, AccuracyIndicatorUsed,
				AccuracyLevel, StatisticalTestUsed, PValue, StatisticalSignificanceLevel);
		ontology.close();
		// addExternalValidationMeans(serviceName, measure, means);
		return Response.status(200).build();
	}

	@Path("/{service_name}/internal/{measure_name}/{validation_means}/delete")
	@DELETE
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public Response deleteInternalValidationMeansToWebService(@PathParam("service_name") String serviceName,
			@PathParam("measure_name") String measure, @PathParam("validation_means") String means, String valueKind) {
		OntologyQoSAPI ontology = new OntologyQoSAPI();

		System.out.println("DELETE internal: " + serviceName);
		System.out.println("Validation means: " + means);

		ontology.deleteValidationMeansFromMeasure(serviceName, measure, valueKind);
		ontology.close();
		// deleteInternalValidationMeans(serviceName, measure);
		return Response.status(200).build();
	}

	@Path("/{service_name}/external/{measure_name}/{validation_means}/delete")
	@DELETE
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public Response deleteExternalValidationMeansToWebService(@PathParam("service_name") String serviceName,
			@PathParam("measure_name") String measure, @PathParam("validation_means") String means, String valueKind) {
		OntologyQoSAPI ontology = new OntologyQoSAPI();

		System.out.println("DELETE external: " + serviceName);
		System.out.println("Validation means: " + means);

		ontology.deleteValidationMeansFromMeasure(serviceName, measure, valueKind);
		ontology.close();
		// deleteExternalValidationMeans(serviceName, measure);
		return Response.status(200).build();
	}

	private void prepareJson(ArrayList<String> measures, JSONObject json) throws JSONException {
		json.put("Measures", measures);
	}
}