# Slow-Client

This is a http client program which can be used with another program that requires a (simple) client implementation to call an API.

**Feel free to give any feedback.**

[![Project License](https://img.shields.io/github/license/ParthaPPM/http-client)](https://github.com/ParthaPPM/http-client/blob/master/LICENSE)
[![javadoc](https://javadoc.io/badge2/io.github.parthappm/http-client/javadoc.svg)](https://javadoc.io/doc/io.github.parthappm/http-client)
[![POM](https://img.shields.io/maven-central/v/io.github.parthappm/http-client)](https://central.sonatype.dev/artifact/io.github.parthappm/http-client/1.0.0)
[![Artifact JAR](https://javadoc.io/badge2/io.github.parthappm/http-client/JAR.svg)](https://repo1.maven.org/maven2/io/github/parthappm/http-client/1.0.0/http-client-1.0.0.jar)

## How to use (Option 1):-
1. Create an instance of `HttpClient` or `HttpsClient` class and assign it to `Client` class.
2. The setter methods can be chained to the Client object.
3. Call the **request()** method of the client object to make the request and get the response.

```ignorelang
Client client = new HttpClient("www.google.com").setMethod("GET");
Response response = client.request();
System.out.println(response.getText());
```

**Note:-**
1. If the HTTP method is not set, then **GET** method will be used by default.
2. Auto redirect is still not implemented.

## How to use (Option 2):-
**1. GET request:**
```ignorelang
Response response = SimpleClient.get("url").request();
```
**2. GET request with some headers:**
```ignorelang
Response response = SimpleClient.get("url")
		.setHeader(headersMap) // to all the headers at once
		.addHeader(key, value) // headers can be added one by one
		.request();
```
**3. POST request:**
```ignorelang
Response response = SimpleClient.post("url", body).request();
```
**4. Any other methods:**
```ignorelang
Response response = SimpleClient.request(method, url)
		.keepConnectionOpen(false)
		.followRedirect(false)
		.setMethod("POST")
		.setPath("/")
		.setBody(new byte[0])
		.connectionTimeout(Duration.ofSeconds(10))
		.request();
```

## Examples:-
```java
public class Main
{
	public static void main(String[] args) throws IOException
	{
		System.out.println("API 1: GET using SimpleClient");
		Response response1 = SimpleClient.get("https://www.google.com").request();
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
				.followRedirects(false)
				.setMethod("POST")
				.setPath("/")
				.setParameters(new HashMap<>()) // to add all the parameters at once
				.addParameter("key", "value") // parameters can be added one by one
				.setHeader(new HashMap<>()) // to all the headers at once
				.addHeader("key", "value") // headers can be added one by one
				.setBody(new byte[0])
				.connectionTimeout(Duration.ofSeconds(20))
				.request();
		System.out.println(response5.getVersion() + " " + response5.getStatusCode() + ": " + response5.getStatusText());
		response5.getHeaders().forEach((String key, String value) -> System.out.println(key + ": " + value));
		System.out.println(response5.getText());
	}
}
```