# Slow-Client
This is a http client program which can be used with another program that requires a (simple) client implementation to call an API.

**How to use (Option 1):-**
1. Create an instance of `HttpClient` or `HttpsClient` class and assign it to `Client` class.
2. The setter methods can be chained to the Client object.
3. Call the **request()** method of the client object to make the request and get the response.

**How to use (Option 2):-**
Coming soon

**Note:-**
1. If the HTTP method is not set, then **GET** method will be used by default.
2. Auto redirect is still not implemented.

### Example:-
```java
public class Main
{
	public static void main(String[] args)
	{
		Client client = new HttpClient("www.google.com")
				.setMethod("GET");
		Response response = client.request();
		System.out.println(response.getText());
	}
}
```