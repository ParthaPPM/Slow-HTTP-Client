package io.github.parthappm.http.client;

import java.net.MalformedURLException;
import java.net.URL;

/**
 * A class that contains all static methods to create an object of Client class by parsing the provided URL. The methods in this class does not make the request, to make the request, request() method of the returned object needs to be called.
 */
public class SimpleClient
{
	private SimpleClient()
	{}

	/**
	 * Builds a get request from the provided URL, but does not make the request.
	 * @param url The URL to which the request is made
	 * @return An object of Client class
	 * @throws MalformedURLException if the URL passed is not a valid URL
	 */
	public static Client get(String url) throws MalformedURLException
	{
		return request("GET", url);
	}

	/**
	 * Builds a post request from the provided URL and request body, but does not make the request.
	 * @param url The URL to which the request is made
	 * @param body The data to be sent as request body
	 * @return An object of Client class
	 * @throws MalformedURLException if the URL passed is not a valid URL
	 */
	public static Client post(String url, byte[] body) throws MalformedURLException
	{
		return request("POST", url).setBody(body);
	}

	/**
	 * Builds an HTTP request for the mentioned HTTP method from the provided URL, but does not make the request.
	 * @param method The HTTP method to be used or the type of request i.e. GET, POST, PUT, DELETE, etc.
	 * @param url The URL to which the request is made
	 * @return An object of Client class
	 * @throws MalformedURLException if the URL passed is not a valid URL
	 */
	public static Client request(String method, String url) throws MalformedURLException
	{
		URL parsedUrl = new URL(url);
		String protocol = parsedUrl.getProtocol();
		String host = parsedUrl.getHost();
		int port = parsedUrl.getPort();
		String fileName = parsedUrl.getPath().equals("") ? ("/" + parsedUrl.getFile()) : parsedUrl.getFile();
		String reference = parsedUrl.getRef();
		String path = (reference == null) ? fileName : fileName + "#" + reference;

		Client client;
		if (protocol.equals("http"))
		{
			client = port == -1 ? new HttpClient(host) : new HttpClient(host, port);
		}
		else if (protocol.equals("https"))
		{
			client = port == -1 ? new HttpsClient(host) : new HttpsClient(host, port);
		}
		else
		{
			throw new MalformedURLException("Protocol not supported");
		}
		return client.setMethod(method).setPath(path).addHeader("Host", host);
	}
}