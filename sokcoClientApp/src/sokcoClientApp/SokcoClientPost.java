package sokcoClientApp;

import com.sun.jersey.api.client.Client;
import com.sun.jersey.api.client.ClientResponse;
import com.sun.jersey.api.client.WebResource;

public class SokcoClientPost {

	public static void main(String[] args) {

		try {

			Client client = Client.create();

			String funcionality = "";
			
			//funcionality = "http://localhost:8081/sokco/app/listFileIncompleteness";
			funcionality = "http://localhost:8081/sokco/app/completePropertyIncompleteness";
			//funcionality = "http://localhost:8081/sokco/app/completePropertyIncompletenessSet";
			
			WebResource webResource = client
					.resource(funcionality);

			String input = "{\"strength\":\"FULL\",\"pathOwlFileString\":\"C://Users//fabio_000//Desktop//OntologiasOWL//assassinato.owl\",\"setInstances\":[\"http://www.semanticweb.org/ontologies/2013/8/ontology.owl#fabio\"],\"reasonerOption\":\"PELLET\"}";

			ClientResponse response = webResource.type("application/json")
					.post(ClientResponse.class, input);

			if (response.getStatus() != 201) {
				throw new RuntimeException("Failed : HTTP error code : "
						+ response.getStatus());
			}

			System.out.println("Output from Server .... \n");
			String output = response.getEntity(String.class);
			System.out.println(output);

		} catch (Exception e) {

			e.printStackTrace();

		}
	}

}
