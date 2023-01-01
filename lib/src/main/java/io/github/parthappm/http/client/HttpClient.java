package io.github.parthappm.http.client;

import java.io.IOException;
import java.net.Socket;

/**
 * The HTTP Client implementation for non-secured HTTP connection.
 */
public class HttpClient extends Client
{
	/**
	 * Creates a socket connection to the specified host at port 80 and constructs a new HttpClient object.
	 * @param host The host name or ip address of the server
	 */
	public HttpClient(String host)
	{
		this(host, 80);
	}

	/**
	 * Creates a socket connection to the specified host at specified port and constructs a new HttpClient object.
	 * @param host The host name or ip address of the server
	 * @param port The port number at which the server is running
	 */
	public HttpClient(String host, int port)
	{
		super(host);
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