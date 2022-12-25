package io.github.parthappm.http.client;

import java.io.IOException;
import java.net.Socket;

public class HttpClient extends Client
{
	public HttpClient(String host)
	{
		this(host, 80);
	}

	public HttpClient(String host, int port)
	{
		try
		{
			setSocket(new Socket(host, port));
		}
		catch (IOException e)
		{
			e.printStackTrace();
		}
	}
}