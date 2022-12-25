package io.github.parthappm.http.client;

import java.io.IOException;
import java.net.Socket;

class HttpClient extends Client
{
	HttpClient(String host)
	{
		this(host, 80);
	}

	HttpClient(String host, int port)
	{
		this(host, port, false);
	}

	HttpClient(String host, int port, boolean keepConnectionOpen)
	{
		try
		{
			setSocket(new Socket(host, port), keepConnectionOpen);
		}
		catch (IOException e)
		{
			e.printStackTrace();
		}
	}
}