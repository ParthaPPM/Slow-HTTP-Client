import io.github.parthappm.http.client.HttpClient;
import io.github.parthappm.http.client.HttpsClient;
import io.github.parthappm.http.client.Response;
import io.github.parthappm.http.client.SimpleClient;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;

public class Main
{
	public static void main(String[] args) throws IOException
	{
		System.out.println("API 1: GET using SimpleClient");
		Response response1 = SimpleClient.get("").request();
		System.out.println(response1.getStatusCode() + ": " + response1.getStatusText());
		System.out.println(response1.getText());

		System.out.println("API 2: POST using SimpleClient");
		Response response2 = SimpleClient.post("http://calapi.inadiutorium.cz/api/v0/en/calendars", "This is http body".getBytes(StandardCharsets.UTF_8)).request();
		System.out.println(response2.getStatusCode() + ": " + response2.getStatusText());
		System.out.println(response2.getText());

		System.out.println("API 3: POST using SimpleClient without body");
		Response response3 = SimpleClient.post("", null).request();
		System.out.println(response3.getStatusCode() + ": " + response3.getStatusText());
		System.out.println(response3.getText());

		System.out.println("API 4: GET using HttpClient with one header");
		Response response4 = new HttpClient("ip address / host name")
				.addHeader("Host", "host name") // adding host to the header is mandatory as per the http specification
				.request();
		System.out.println(response4.getStatusCode() + ": " + response4.getStatusText());
		System.out.println(new String(response4.getBody()));

		System.out.println("API 5: POST using HttpsClient with all parameters");
		Response response5 = new HttpsClient("ip address / host", 443)
				.keepConnectionOpen(false)
				.followRedirect(false)
				.setMethod("POST")
				.setPath("/")
				.setParameters(new HashMap<>()) // to add all the parameters at once
				.addParameter("key", "value") // parameters can be added one by one
				.setHeader(new HashMap<>()) // to all the headers at once
				.addHeader("key", "value") // headers can be added one by one
				.setBody(new byte[0])
				.suppressException(false)
				.request();
		System.out.println(response5.getVersion() + " " + response5.getStatusCode() + ": " + response5.getStatusText());
		response5.getHeaders().forEach((String key, String value) -> System.out.println(key + ": " + value));
		System.out.println(response5.getText());
	}
}
