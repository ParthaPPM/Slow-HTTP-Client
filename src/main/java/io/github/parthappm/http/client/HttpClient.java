package io.github.parthappm.http.client;

import java.io.IOException;
import java.net.Socket;
import java.util.Map;

class HttpClient implements Client
{
	private RequestHandler requestHandler;

	HttpClient(String host)
	{
		this(host, 433);
	}

	HttpClient(String host, int port)
	{
		this(host, port, true);
	}

	HttpClient(String host, int port, boolean keepConnectionOpen)
	{
		try
		{
			Socket socket = new Socket(host, port);
			requestHandler = new RequestHandler(host, socket, keepConnectionOpen);
		}
		catch (IOException e)
		{
			e.printStackTrace();
		}
	}

	public void createRequest(String method, String location, Map<String, String> parameters, Map<String, String> extraHeaders, byte[] body)
	{
		requestHandler.createRequest(method, location, parameters, extraHeaders, body);
	}

	public Response makeRequest()
	{
		return requestHandler.makeRequest();
	}

	public void close()
	{
		requestHandler.close();
	}
}