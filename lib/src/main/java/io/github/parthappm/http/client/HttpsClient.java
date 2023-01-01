package io.github.parthappm.http.client;

import javax.net.SocketFactory;
import javax.net.ssl.SSLSocket;
import javax.net.ssl.SSLSocketFactory;
import java.io.IOException;
import java.net.Socket;

/**
 * The HTTP Client implementation for secured HTTP connection over Secured Sockets Layer (SSL)
 */
public class HttpsClient extends Client
{
	/**
	 * Creates a socket connection to the specified host at port 443 and constructs a new HttpsClient object.
	 * @param host The host name or ip address of the server
	 */
	public HttpsClient(String host)
	{
		this(host, 443);
	}

	/**
	 * Creates a socket connection to the specified host at specified port and constructs a new HttpsClient object.
	 * @param host The host name or ip address of the server
	 * @param port The port number at which the server is running
	 */
	public HttpsClient(String host, int port)
	{
		super(host);
		try
		{
			SocketFactory socketFactory = SSLSocketFactory.getDefault();
			Socket socket = socketFactory.createSocket(host, port);
			((SSLSocket) socket).startHandshake();
			setSocket(socket);
		}
		catch (IOException e)
		{
			e.printStackTrace();
		}
	}
}