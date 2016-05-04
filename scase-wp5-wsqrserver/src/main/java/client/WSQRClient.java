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
	 * Performs a DELETE request on the server.
	 * 
	 * @param address the address of the request.
	 * @param credentials the username and the password to be added to the request header.
	 * @return a {@link org.codehaus.jettison.json.JSONObject JSONObject} containing the response body, if the request
	 *         is correct, else an exception is thrown.
	 * @throws JSONException when the request or the credentials are wrong.
	 */
	public static JSONObject performJsonDeleteRequest(String address, String... credentials) throws JSONException {
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
				.delete(ClientResponse.class);

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
		//String initAddress = "http://109.231.121.200:8022/";
		String initAddress = "http://localhost:8022/";
		System.out.println("GET " + initAddress);
		JSONObject initOutput = performJsonGetRequest(initAddress);
		System.out.println(initOutput.toString(3).replaceAll("\\\\/", "/"));

		// Get all measures of web service
		//String address = "http://109.231.121.200:8022/services/ArtistRegistryWS/measures";
		String address = "http://localhost:8022/services/DavideTosiWS/measures";
		System.out.println("\nGET " + address);
		JSONObject input = performJsonGetRequest(address);
		System.out.println(input.toString(3).replaceAll("\\\\/", "/"));
		
		// Add a new web service
		/*//String webServiceAddress = "http://109.231.121.200:8022/services/DavideTosiWS/add";
		String webServiceAddress = "http://localhost:8022/services/DavideTosiWS/add";
		JSONObject webServiceInput = new JSONObject();
		webServiceInput.put("service_name", "DavideTosiWS");
		System.out.println("\nPOST " + webServiceAddress);
		JSONObject phraseOutput = performJsonPostRequest(webServiceAddress, webServiceInput);
		System.out.println(phraseOutput.toString(3).replaceAll("\\\\/", "/"));
		*/
		
		// Add internal measure to web service
		/*//String intaddress = "http://109.231.121.200:8022/services/DavideTosiWS/internal/McCabe_Cyclomatic_Complexity_CC/add";
		String intaddress = "http://localhost:8022/services/DavideTosiWS/internal/McCabe_Cyclomatic_Complexity_CC/add";
		JSONObject internal = new JSONObject();
		internal.put("service_name", "DavideTosiWS");
		internal.put("measure_name", "McCabe_Cyclomatic_Complexity_CC");
		internal.put("measure_kind", "Average");
		internal.put("measure_value", "2.26");
		System.out.println("\nPOST " + intaddress);
		JSONObject intoutput = performJsonPostRequest(intaddress, internal);
		System.out.println(intoutput.toString(3).replaceAll("\\\\/", "/"));
		*/
		
		// Add internal validation means to web service
		/*//String intvaladdress = "http://109.231.121.200:8022/services/DavideTosiWS/internal/McCabe_Cyclomatic_Complexity_CC/AxiomaticApproach/add";
		String intvaladdress = "http://localhost:8022/services/DavideTosiWS/internal/McCabe_Cyclomatic_Complexity_CC/AxiomaticApproach/add";
		JSONObject intvalinput = new JSONObject();
		intvalinput.put("service_name", "DavideTosiWS");
		intvalinput.put("measure_name", "McCabe_Cyclomatic_Complexity_CC");
		intvalinput.put("validation_means", "AxiomaticApproach");
		intvalinput.put("measure_kind", "Average");
		intvalinput.put("attribute_name", "Axiom1");
		intvalinput.put("attribute_value", "Axiom1 definition");
		intvalinput.put("internal_measure_kind", "static");
		System.out.println("\nPOST " + intvaladdress);
		JSONObject intvaloutput = performJsonPostRequest(intvaladdress, intvalinput);
		System.out.println(intvaloutput.toString(3).replaceAll("\\\\/", "/"));
		*/
		
		
		// Add external measure to web service
		/*//String extaddress = "http://109.231.121.200:8022/services/DavideTosiWS/external/Successability_SU/add";
		String extaddress = "http://localhost:8022/services/DavideTosiWS/external/Successability_SU/add";
		JSONObject external = new JSONObject();
		external.put("service_name", "DavideTosiWS");
		external.put("measure_name", "Successability_SU");
		external.put("measure_kind", "Raw");
		external.put("measure_value", "0.998");
		System.out.println("\nPOST " + extaddress);
		JSONObject extoutput = performJsonPostRequest(extaddress, external);
		System.out.println(extoutput.toString(3).replaceAll("\\\\/", "/"));
		*/
		
		
		// Add external validation means to web service
		//String extvaladdress = "http://109.231.121.200:8022/services/DavideTosiWS/external/Successability_SU/EmpiricalValidation/add";
		String extvaladdress = "http://localhost:8022/services/DavideTosiWS/external/Successability_SU/EmpiricalValidation/add";
		JSONObject extvalinput = new JSONObject();
		extvalinput.put("service_name", "DavideTosiWS");
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
		/*//String intupaddress = "http://109.231.121.200:8022/services/DavideTosiWS/internal/McCabe_Cyclomatic_Complexity_CC/update";
		String intupaddress = "http://localhost:8022/services/DavideTosiWS/internal/McCabe_Cyclomatic_Complexity_CC/update";
		JSONObject intupinput = new JSONObject();
		intupinput.put("service_name", "DavideTosiWS");
		intupinput.put("measure_name", "McCabe_Cyclomatic_Complexity_CC");
		intupinput.put("measure_kind", "Average");
		intupinput.put("measure_value", "3.2");
		System.out.println("\nPOST " + intupaddress);
		JSONObject intupoutput = performJsonPostRequest(intupaddress, intupinput);
		System.out.println(intupoutput.toString(3).replaceAll("\\\\/", "/"));
		*/
		
		// Update external measure to web service
		/*//String extupaddress = "http://109.231.121.200:8022/services/DavideTosiWS/external/Successability_SU/update";
		String extupaddress = "http://localhost:8022/services/DavideTosiWS/external/Successability_SU/update";
		JSONObject exupinput = new JSONObject();
		exupinput.put("service_name", "DavideTosiWS");
		exupinput.put("measure_name", "Successability_SU");
		exupinput.put("measure_kind", "Raw");
		exupinput.put("measure_value", "1.0");
		System.out.println("\nPOST " + extupaddress);
		JSONObject exupoutput = performJsonPostRequest(extupaddress, exupinput);
		System.out.println(exupoutput.toString(3).replaceAll("\\\\/", "/"));		
		*/
		
		// Update internal validation means to web service
		/*//String intvalupaddress = "http://109.231.121.200:8022/services/DavideTosiWS/internal/McCabe_Cyclomatic_Complexity_CC/AxiomaticApproach/update";
		String intvalupaddress = "http://localhost:8022/services/DavideTosiWS/internal/McCabe_Cyclomatic_Complexity_CC/AxiomaticApproach/update";
		JSONObject intvalupinput = new JSONObject();
		intvalupinput.put("service_name", "DavideTosiWS");
		intvalupinput.put("measure_name", "McCabe_Cyclomatic_Complexity_CC");
		intvalupinput.put("validation_means", "AxiomaticApproach");
		intvalupinput.put("measure_kind", "Average");
		intvalupinput.put("attribute_name", "Axiom2");
		intvalupinput.put("attribute_value", "Axiom2 definition");
		intvalupinput.put("internal_measure_kind", "static");
		System.out.println("\nPOST " + intvalupaddress);
		JSONObject intvalupoutput = performJsonPostRequest(intvalupaddress, intvalupinput);
		System.out.println(intvalupoutput.toString(3).replaceAll("\\\\/", "/"));
		*/
							
		// Update external validation means to web service
		//String extvalupaddress = "http://109.231.121.200:8022/services/DavideTosiWS/external/Successability_SU/EmpiricalValidation/update";
		String extvalupaddress = "http://localhost:8022/services/DavideTosiWS/external/Successability_SU/EmpiricalValidation/update";
		JSONObject extvalupinput = new JSONObject();
		extvalupinput.put("service_name", "DavideTosiWS");
		extvalupinput.put("measure_name", "Successability_SU");
		extvalupinput.put("measure_value_kind", "Raw");
		extvalupinput.put("validation_means", "EmpiricalValidation");
		extvalupinput.put("accuracy_indicator_used", "R^2");
		extvalupinput.put("accuracy_level", (float) 0.923);
		extvalupinput.put("statistical_test_used", "T-test");
		extvalupinput.put("p_value", (float) 0.005);
		extvalupinput.put("statistical_significance_level", (float) 0.9);
		System.out.println("\nPOST " + extvalupaddress);
		JSONObject extvalupoutput = performJsonPostRequest(extvalupaddress, extvalupinput);
		System.out.println(extvalupoutput.toString(3).replaceAll("\\\\/", "/"));
		

		
		// Get internal measure of web service by position  --IT RETURNS THE MEASURE SAVED EVEN THOUGH IT'S AN EXTERNAL ONE.
		/*//String intgetaddress = "http://109.231.121.200:8022/services/DavideTosiWS/internal/0/get";
		String intgetaddress = "http://localhost:8022/services/DavideTosiWS/internal/0/get";
		System.out.println("\nGET " + intgetaddress);
		JSONObject intgetoutput = performJsonGetRequest(intgetaddress);
		System.out.println(intgetoutput.toString(3).replaceAll("\\\\/", "/"));
		*/
		
		// Get external measure of web service by position
		/*//String extgetaddress = "http://109.231.121.200:8022/services/DavideTosiWS/external/0/get";
		String extgetaddress = "http://localhost:8022/services/DavideTosiWS/external/0/get";
		System.out.println("\nGET " + extgetaddress);
		JSONObject extgetoutput = performJsonGetRequest(extgetaddress);
		System.out.println(extgetoutput.toString(3).replaceAll("\\\\/", "/"));
		*/
				
		// Get internal measure by value
		/*//String intgetadd = "http://109.231.121.200:8022/services/DavideTosiWS/internal/McCabe_Cyclomatic_Complexity_CC/Average/get";
		String intgetadd = "http://localhost:8022/services/DavideTosiWS/internal/McCabe_Cyclomatic_Complexity_CC/Average/get";
		System.out.println("\nGET " + intgetadd);
		JSONObject intgetout = performJsonGetRequest(intgetadd);
		System.out.println(intgetout.toString(3).replaceAll("\\\\/", "/"));
		*/
		
		// Get external measure by value
		/*//String extgetadd = "http://109.231.121.200:8022/services/DavideTosiWS/external/Successability_SU/Raw/get";
		String extgetadd = "http://localhost:8022/services/DavideTosiWS/external/Successability_SU/Raw/get";
		System.out.println("\nGET " + extgetadd);
		JSONObject extgetout = performJsonGetRequest(extgetadd);
		System.out.println(extgetout.toString(3).replaceAll("\\\\/", "/"));
		*/
				

		
		
		// Delete a web service
		/*//String add = "http://109.231.121.200:8022/services/DavideTosiWS/delete";
		String add = "http://localhost:8022/services/DavideTosiWS/delete";
		JSONObject in = new JSONObject();
		in.put("service_name", "DavideTosiWS");
		System.out.println("\nDELETE " + add);
		JSONObject output = performJsonDeleteRequest(add);
		System.out.println(output.toString(3).replaceAll("\\\\/", "/"));
		*/
		
		// Delete an internal measure 500
		/*//String delintadd = "http://109.231.121.200:8022/services/DavideTosiWS/internal/McCabe_Cyclomatic_Complexity_CC/Average/delete";
		String delintadd = "http://localhost:8022/services/DavideTosiWS/internal/McCabe_Cyclomatic_Complexity_CC/Average/delete";
		JSONObject delintinput = new JSONObject();
		delintinput.put("service_name","DavideTosiWS");
		delintinput.put("measure_name", "McCabe_Cyclomatic_Complexity_CC");
		delintinput.put("measure_kind", "Average");
		System.out.println("\nDELETE " + delintadd);
		JSONObject delintoutput = performJsonDeleteRequest(delintadd);
		System.out.println(delintoutput.toString(3).replaceAll("\\\\/", "/"));
		*/
		
		// Delete an external measure
		/*//String delextadd = "http://109.231.121.200:8022/services/DavideTosiWS/external/Successability_SU/Raw/delete";
		String delextadd = "http://localhost:8022/services/DavideTosiWS/external/Successability_SU/Raw/delete";
		JSONObject delextinput = new JSONObject();
		delextinput.put("service_name","DavideTosiWS");
		delextinput.put("measure_name", "Successability_SU");
		delextinput.put("measure_kind", "Raw");
		System.out.println("\nDELETE " + delextadd);
		JSONObject delextoutput = performJsonDeleteRequest(delextadd);
		System.out.println(delextoutput.toString(3).replaceAll("\\\\/", "/"));
		*/
		
		// Delete an internal validation means 500
		/*//String delintvali = "http://109.231.121.200:8022/services/DavideTosiWS/internal/McCabe_Cyclomatic_Complexity_CC/EmpiricalValidation/delete";
		String delintvali = "http://localhost:8022/services/DavideTosiWS/internal/McCabe_Cyclomatic_Complexity_CC/EmpiricalValidation/delete";
		JSONObject delintvalm = new JSONObject();
		delintvalm.put("service_name", "DavideTosiWS");
		delintvalm.put("measure_name", "McCabe_Cyclomatic_Complexity_CC");
		delintvalm.put("validation_means", "EmpiricalValidation");
		System.out.println("\nDELETE " + delintvali);
		JSONObject delintvalo = performJsonDeleteRequest(delintvali);
		System.out.println(delintvalo.toString(3).replaceAll("\\\\/", "/"));
		*/
		
		// Delete an external validation means 500
		/*//String delextvali = "http://109.231.121.200:8022/services/DavideTosiWS/external/Successability_SU/EmpiricalValidation/delete";
		String delextvali = "http://localhost:8022/services/DavideTosiWS/external/Successability_SU/EmpiricalValidation/delete";
		JSONObject delextvalm = new JSONObject();
		delextvalm.put("service_name", "DavideTosiWS");
		delextvalm.put("measure_name", "EmpiricalValidation");
		delextvalm.put("validation_means", "AxiomaticApproach");
		System.out.println("\nDELETE " + delextvali);
		JSONObject delextvalo = performJsonDeleteRequest(delextvali);
		System.out.println(delextvalo.toString(3).replaceAll("\\\\/", "/"));
		*/
	}
}