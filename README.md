# Slow-Client
This is a http client program which can be used with another program that requires a (simple) client implementation to call an API.

**Feel free to give any feedback.**

## How to use (Option 1):-
1. Create an instance of `HttpClient` or `HttpsClient` class and assign it to `Client` class.
2. The setter methods can be chained to the Client object.
3. Call the **request()** method of the client object to make the request and get the response.

**Note:-**
1. If the HTTP method is not set, then **GET** method will be used by default.
2. Auto redirect is still not implemented.

### Example:-
(You may refer the Main class in test folder)
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

## How to use (Option 2):-
**1. GET request:**
```java
public class Main
{
	public static void main(String[] args)
	{
		Response response = SimpleClient.get("url").request();
	}
}
```
**2. GET request with some headers:**
```java
public class Main
{
	public static void main(String[] args)
	{
		Response response = SimpleClient.get("url")
				.setHeader(headersMap) // to all the headers at once
				.addHeader(key, value) // headers can be added one by one
				.request();
	}
}
```
**3. POST request:**
```java
public class Main
{
	public static void main(String[] args)
	{
		Response response = SimpleClient.post("url", body)
				.request();
	}
}
```
**4. Any other methods:**
```java
public class Main
{
	public static void main(String[] args)
	{
		Response response = SimpleClient.request(method, url)
				.keepConnectionOpen(false)
				.followRedirect(false)
				.setMethod("POST")
				.setPath("/")
				.setBody(new byte[0])
				.suppressException(false)
				.request();
	}
}
```