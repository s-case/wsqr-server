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
	 * Performs a Get request on the server.
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
		String webServiceAddress = "http://localhost:8022/services/ArtistRegistryWS";
		JSONObject webServiceInput = new JSONObject();
		webServiceInput.put("service_name", "ArtistRegistryWS");
		System.out.println("\nPOST " + webServiceAddress);
		JSONObject phraseOutput = performJsonPostRequest(webServiceAddress, webServiceInput);
		System.out.println(phraseOutput.toString(3).replaceAll("\\\\/", "/"));
	}

}
