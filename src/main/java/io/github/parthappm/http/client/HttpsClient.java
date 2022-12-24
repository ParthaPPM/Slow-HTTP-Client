package io.github.parthappm.http.client;

import javax.net.SocketFactory;
import javax.net.ssl.SSLSocket;
import javax.net.ssl.SSLSocketFactory;
import java.io.IOException;
import java.net.Socket;
import java.util.Map;

class HttpsClient implements Client
{
	private RequestHandler requestHandler;

	HttpsClient(String host)
	{
		this(host, 443);
	}

	HttpsClient(String host, int port)
	{
		this(host, port, true);
	}

	HttpsClient(String host, int port, boolean keepConnectionOpen)
	{
		try
		{
			SocketFactory socketFactory = SSLSocketFactory.getDefault();
			Socket socket = socketFactory.createSocket(host, port);
			((SSLSocket)socket).startHandshake();
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