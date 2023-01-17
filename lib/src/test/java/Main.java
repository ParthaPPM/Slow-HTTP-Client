import io.github.parthappm.http.client.Response;
import io.github.parthappm.http.client.SimpleClient;

import java.io.IOException;

public class Main
{
	public static void main(String[] args) throws IOException
	{
		Response response = SimpleClient.get("http://www.google.com").request();
		System.out.println(response.getStatusCode() + ": " + response.getStatusText());
		response.getHeaders().forEach((key, value) -> System.out.println(key + ": " + value));
		System.out.println(response.getText());
		System.out.println(response.getBody().length);
	}
}
