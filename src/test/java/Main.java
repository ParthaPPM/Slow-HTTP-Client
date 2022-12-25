import io.github.parthappm.http.client.Client;
import io.github.parthappm.http.client.HttpClient;
import io.github.parthappm.http.client.Response;
import io.github.parthappm.http.client.SimpleClient;

import java.io.IOException;

public class Main
{
	public static void main(String[] args) throws IOException
	{
		Client client = new HttpClient("www.google.com")
				.setPath("/")
				.setMethod("GET")
				.setParameters(null)
				.setHeader(null)
				.setBody(null).keepConnectionOpen(false).followRedirect(false);
		Response one = client.request();
		Response two = SimpleClient.get("http://www.google.com");
	}
}
