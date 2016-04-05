package client;

import org.codehaus.jettison.json.JSONException;
import org.codehaus.jettison.json.JSONObject;
import com.sun.jersey.api.client.Client;
import com.sun.jersey.api.client.ClientResponse;
import com.sun.jersey.api.client.WebResource;
import com.sun.jersey.api.client.config.ClientConfig;
import com.sun.jersey.api.client.config.DefaultClientConfig;
import com.sun.jersey.api.client.filter.HTTPBasicAuthFilter;
import com.sun.jersey.api.json.JSONConfiguration;

/**
 * A WSQR web client. This class serves as an example of using the WSQR Server.
 * 
 * @author Themistoklis Diamantopoulos
 */
public class WSQRClient {

	/**
	 * Performs a POST request on the server.
	 * 
	 * @param address the address of the request.
	 * @param input the body of the request in JSON format
	 * @param credentials the username and the password to be added to the request header.
	 * @return a {@link org.codehaus.jettison.json.JSONObject JSONObject} containing the response body, if the request
	 *         is correct, else an exception is thrown.
	 * @throws JSONException when the request or the credentials are wrong.
	 */
	public static JSONObject performJsonPostRequest(String address, JSONObject input, String... credentials)
			throws JSONException {
		ClientConfig clientConfig = new DefaultClientConfig();
		clientConfig.getFeatures().put(JSONConfiguration.FEATURE_POJO_MAPPING, Boolean.TRUE);
		Client client = Client.create(clientConfig);
		if (credentials.length == 2) {
			String username = credentials[0];
			String password = credentials[1];
			client.addFilter(new HTTPBasicAuthFilter(username, password));
		}

		WebResource webResource = client.resource(address);
		ClientResponse response = webResource.accept("application/json").type("application/json")
				.post(ClientResponse.class, input);

		if (response.getStatus() != 200) {
			throw new RuntimeException(response.getStatus() + " Error: " + response.getEntity(String.class));
		}

		String stringoutput = response.getEntity(String.class);
		return new JSONObject(stringoutput);
	}

	/**
	 * Performs a GET request on the server.
	 * 
	 * @param address the address of the request.
	 * @param credentials the username and the password to be added to the request header.
	 * @return a {@link org.codehaus.jettison.json.JSONObject JSONObject} containing the response body, if the request
	 *         is correct, else an exception is thrown.
	 * @throws JSONException when the request or the credentials are wrong.
	 */
	public static JSONObject performJsonGetRequest(String address, String... credentials) throws JSONException {
		ClientConfig clientConfig = new DefaultClientConfig();
		clientConfig.getFeatures().put(JSONConfiguration.FEATURE_POJO_MAPPING, Boolean.TRUE);
		Client client = Client.create(clientConfig);

		WebResource webResource = client.resource(address);
		ClientResponse response = webResource.accept("application/json").type("application/json")
				.get(ClientResponse.class);

		if (response.getStatus() != 200) {
			throw new RuntimeException(response.getStatus() + " Error: " + response.getEntity(String.class));
		}

		String stringoutput = response.getEntity(String.class);
		return new JSONObject(stringoutput);
	}

	/**
	 * Performs request on the server.
	 * 
	 * @param args unused parameter.
	 * @throws JSONException when a JSON object is not defined correctly.
	 */
	public static void main(String[] args) throws JSONException {
		// Get info about the web service
		String initAddress = "http://localhost:8022/";
		System.out.println("GET " + initAddress);
		JSONObject initOutput = performJsonGetRequest(initAddress);
		System.out.println(initOutput.toString(3).replaceAll("\\\\/", "/"));

		// Add a new web service
		String webServiceAddress = "http://localhost:8022/services/ArtistRegistryWS/add";
		JSONObject webServiceInput = new JSONObject();
		webServiceInput.put("service_name", "ArtistRegistryWS");
		System.out.println("\nPOST " + webServiceAddress);
		JSONObject phraseOutput = performJsonPostRequest(webServiceAddress, webServiceInput);
		System.out.println(phraseOutput.toString(3).replaceAll("\\\\/", "/"));
		
		// Add internal measure to web service
		String intaddress = "http://localhost:8022/services/ArtistRegistryWS/internal/McCabe_Cyclomatic_Complexity_CC/add";
		JSONObject internal = new JSONObject();
		internal.put("service_name", "ArtistRegistryWS");
		internal.put("measure_name", "McCabe_Cyclomatic_Complexity_CC");
		internal.put("measure_kind", "Average");
		internal.put("measure_value", "2.26");
		System.out.println("\nPOST " + intaddress);
		JSONObject intoutput = performJsonPostRequest(intaddress, internal);
		System.out.println(intoutput.toString(3).replaceAll("\\\\/", "/"));
		
		// Add external measure to web service
		String extaddress = "http://localhost:8022/services/ArtistRegistryWS/external/Successability_SU/add";
		JSONObject external = new JSONObject();
		external.put("service_name", "ArtistRegistryWS");
		external.put("measure_name", "Successability_SU");
		external.put("measure_kind", "Raw");
		external.put("measure_value", "0.998");
		System.out.println("\nPOST " + extaddress);
		JSONObject extoutput = performJsonPostRequest(extaddress, external);
		System.out.println(extoutput.toString(3).replaceAll("\\\\/", "/"));
		
		// Add internal validation means to web service
		/*String intvaladdress = "http://localhost:8022/services/ArtistRegistryWS/internal/Dynamic_Coupling_Between_Objects_DCBO/AxiomaticApproach/add";
		JSONObject intvalinput = new JSONObject();
		intvalinput.put("service_name", "ArtistRegistryWS");
		intvalinput.put("measure_name", "Dynamic_Coupling_Between_Objects_DCBO");
		intvalinput.put("validation_means", "AxiomaticApproach");
		intvalinput.put("measure_kind", "Raw");
		intvalinput.put("attribute_name", "DynamicCoupling Axiom1: Monoticity");
		intvalinput.put("attribute_value", "OuterR(m1) c OuterR(m2) AND R1 c R2 tr1 c tr2 -> DynCoupling(m1,tr1) =< DynCoupling(m2,tr2)");
		intvalinput.put("internal_measure_kind", "DynamicMeasure");
		System.out.println("\nPOST " + intvaladdress);
		JSONObject intvaloutput = performJsonPostRequest(intvaladdress, intvalinput);
		System.out.println(intvaloutput.toString(3).replaceAll("\\\\/", "/"));*/
		
		// Add external validation means to web service
		String extvaladdress = "http://localhost:8022/services/ArtistRegistryWS/external/Successability_SU/EmpiricalValidation/add";
		JSONObject extvalinput = new JSONObject();
		extvalinput.put("service_name", "ArtistRegistryWS");
		extvalinput.put("measure_name", "Successability_SU");
		extvalinput.put("validation_means", "EmpiricalValidation");
		extvalinput.put("measure_value_kind", "Raw");
		extvalinput.put("accuracy_indicator_used", "R^2");
		extvalinput.put("accuracy_level", (float) 0.923);
		extvalinput.put("statistical_test_used", "T-test");
		extvalinput.put("p_value", (float) 0.005);
		extvalinput.put("statistical_significance_level", (float) 0.8);
		System.out.println("\nPOST " + extvaladdress);
		JSONObject extvaloutput = performJsonPostRequest(extvaladdress, extvalinput);
		System.out.println(extvaloutput.toString(3).replaceAll("\\\\/", "/"));
		
		// Update internal measure to web service
		String intupaddress = "http://localhost:8022/services/ArtistRegistryWS/internal/McCabe_Cyclomatic_Complexity_CC/update";
		JSONObject intupinput = new JSONObject();
		intupinput.put("service_name", "ArtistRegistryWS");
		intupinput.put("measure_name", "McCabe_Cyclomatic_Complexity_CC");
		intupinput.put("measure_kind", "Average");
		intupinput.put("measure_value", "3.0");
		System.out.println("\nPOST " + intupaddress);
		JSONObject intupoutput = performJsonPostRequest(intupaddress, intupinput);
		System.out.println(intupoutput.toString(3).replaceAll("\\\\/", "/"));
		
		// Update external measure to web service
		String extupaddress = "http://localhost:8022/services/ArtistRegistryWS/external/Successability_SU/update";
		JSONObject exupinput = new JSONObject();
		exupinput.put("service_name", "ArtistRegistryWS");
		exupinput.put("measure_name", "Successability_SU");
		exupinput.put("measure_kind", "Raw");
		exupinput.put("measure_value", "1.0");
		System.out.println("\nPOST " + extupaddress);
		JSONObject exupoutput = performJsonPostRequest(extupaddress, exupinput);
		System.out.println(exupoutput.toString(3).replaceAll("\\\\/", "/"));
		
		// Delete internal validation means
		/*String delintvaladdress = "http://localhost:8022/services/ArtistRegistryWS/internal/Dynamic_Coupling_Between_Objects_DCBO/AxiomaticApproach/delete";
		JSONObject delintvalinput = new JSONObject();
		delintvalinput.put("service_name", "ArtistRegistryWS");
		delintvalinput.put("measure_name", "Dynamic_Coupling_Between_Objects_DCBO");
		delintvalinput.put("validation_means", "AxiomaticApproach");
		System.out.println("\nPOST " + delintvaladdress);
		JSONObject delintvaloutput = performJsonPostRequest(delintvaladdress, delintvalinput);
		System.out.println(delintvaloutput.toString(3).replaceAll("\\\\/", "/"));*/
		
		// Delete external validation means
		/*String delextvaladdress = "http://localhost:8022/services/ArtistRegistryWS/external/Successability_SU/EmpiricalValidation/delete";
		JSONObject delextvalinput = new JSONObject();
		delextvalinput.put("service_name", "ArtistRegistryWS");
		delextvalinput.put("measure_name", "Successability_SU");
		delextvalinput.put("validation_means", "EmpiricalValidation");
		JSONObject delextvaloutput = performJsonPostRequest(delextvaladdress, delextvalinput);
		System.out.println(delextvaloutput.toString(3).replaceAll("\\\\/", "/"));*/
		
		// Delete an internal measure
		/*String intdeladdress = "http://localhost:8022/services/ArtistRegistryWS/internal/McCabe_Cyclomatic_Complexity_CC/delete";
		JSONObject intdel = new JSONObject();
		intdel.put("service_name", "ArtistRegistryWS");
		intdel.put("measure_name", "McCabe_Cyclomatic_Complexity_CC");
		System.out.println("\nPOST " + intdeladdress);
		JSONObject intdeloutput = performJsonPostRequest(intdeladdress, intdel);
		System.out.println(intdeloutput.toString(3).replaceAll("\\\\/", "/"));*/
		
		// Delete an external measure
		/*String extdeladdress = "http://localhost:8022/services/ArtistRegistryWS/external/Successability_SU/delete";
		JSONObject extdelinput = new JSONObject();
		extdelinput.put("service_name", "ArtistRegistryWS");
		extdelinput.put("measure_name", "Successability_SU");
		extdelinput.put("measure_kind", "Raw");
		extdelinput.put("measure_value", "0.998");
		System.out.println("\nPOST " + extdeladdress);
		JSONObject extdeloutput = performJsonPostRequest(extdeladdress, extdelinput);
		System.out.println(extdeloutput.toString(3).replaceAll("\\\\/", "/"));*/
		
		// Get internal measure of web service
		/*String intgetaddress = "http://localhost:8022/services/ArtistRegistryWS/internal/1/get";
		System.out.println("\nGET " + intgetaddress);
		JSONObject intgetoutput = performJsonGetRequest(intgetaddress);
		System.out.println(intgetoutput.toString(3).replaceAll("\\\\/", "/"));*/
		
		//	Get internal measure by value
		/*String intgetaddress = "http://localhost:8022/services/ArtistRegistryWS/internal/McCabe_Cyclomatic_Complexity_CC/Average";
		System.out.println("\nGET " + intgetaddress);
		JSONObject intgetoutput = performJsonGetRequest(intgetaddress);
		System.out.println(intgetoutput.toString(3).replaceAll("\\\\/", "/"));*/
		
		// Add internal validation means to web service
		/*String intvalupaddress = "http://localhost:8022/services/ArtistRegistryWS/internal/Dynamic_Coupling_Between_Objects_DCBO/EmpiricalValidation/update";
		JSONObject intvalupinput = new JSONObject();
		intvalupinput.put("service_name", "ArtistRegistryWS");
		intvalupinput.put("measure_name", "Dynamic_Coupling_Between_Objects_DCBO");
		intvalupinput.put("validation_means", "EmpiricalValidation");
		intvalupinput.put("measure_kind", "Raw");
		intvalupinput.put("attribute_name", "DynamicCoupling Axiom1: Monoticity");
		intvalupinput.put("attribute_value", "OuterR(m1) c OuterR(m2) AND R1 c R2 tr1 c tr2 -> DynCoupling(m1,tr1) =< DynCoupling(m2,tr2)");
		intvalupinput.put("internal_measure_kind", "DynamicMeasure");
		System.out.println("\nPOST " + intvalupaddress);
		JSONObject intvalupoutput = performJsonPostRequest(intvalupaddress, intvalupinput);
		System.out.println(intvalupoutput.toString(3).replaceAll("\\\\/", "/"));*/
		
		// Update external validation means to web service
		/*String extvalupaddress = "http://localhost:8022/services/ArtistRegistryWS/external/Successability_SU/EmpiricalValidation/update";
		JSONObject extvalupinput = new JSONObject();
		extvalupinput.put("service_name", "ArtistRegistryWS");
		extvalupinput.put("measure_name", "Successability_SU");
		extvalupinput.put("validation_means", "EmpiricalValidation");
		extvalupinput.put("measure_value_kind", "Raw");
		extvalupinput.put("accuracy_indicator_used", "R^2");
		extvalupinput.put("accuracy_level", (float) 0.923);
		extvalupinput.put("statistical_test_used", "T-test");
		extvalupinput.put("p_value", (float) 0.005);
		extvalupinput.put("statistical_significance_level", (float) 0.8);
		System.out.println("\nPOST " + extvalupaddress);
		JSONObject extvalupoutput = performJsonPostRequest(extvalupaddress, extvalupinput);
		System.out.println(extvalupoutput.toString(3).replaceAll("\\\\/", "/"));*/
		
		// Delete a web service
		/*String address = "http://localhost:8022/services/ArtistRegistryWS/delete";
		JSONObject input = new JSONObject();
		input.put("service_name", "ArtistRegistryWS");
		System.out.println("\nPOST " + address);
		JSONObject output = performJsonPostRequest(address, input);
		System.out.println(output.toString(3).replaceAll("\\\\/", "/"));*/
		
		// Get all measures of web service
		/*String address = "http://localhost:8022/services/ArtistRegistryWS/measures";
		System.out.println("\nGET " + address);
		JSONObject input = performJsonGetRequest(address);
		System.out.println(input.toString(3).replaceAll("\\\\/", "/"));*/
		
	}
}