package io.github.parthappm.http.client;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

public class SimpleClient
{
	public static Client get(String url) throws MalformedURLException
	{
		return request("GET", url);
	}

	public static Client post(String url, byte[] body) throws MalformedURLException
	{
		return request("POST", url).setBody(body);
	}

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
		Map<String, String> headers = new HashMap<>();
		headers.put("Host", host);
		return client.setMethod(method).setPath(path).setHeader(headers);
	}
}