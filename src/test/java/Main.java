import io.github.parthappm.http.client.Response;
import io.github.parthappm.http.client.SimpleClient;

public class Main
{
	public static void main(String[] args)
	{
		Response response = SimpleClient.get("https://www.google.com");
		System.out.println(response.getText());
	}
}
