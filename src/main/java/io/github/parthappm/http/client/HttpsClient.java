package io.github.parthappm.http.client;

import javax.net.SocketFactory;
import javax.net.ssl.SSLSocket;
import javax.net.ssl.SSLSocketFactory;
import java.io.IOException;
import java.net.Socket;

public class HttpsClient extends Client
{
	public HttpsClient(String host)
	{
		this(host, 443);
	}

	public HttpsClient(String host, int port)
	{
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