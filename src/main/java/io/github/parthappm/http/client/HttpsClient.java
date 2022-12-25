package io.github.parthappm.http.client;

import javax.net.SocketFactory;
import javax.net.ssl.SSLSocket;
import javax.net.ssl.SSLSocketFactory;
import java.io.IOException;
import java.net.Socket;

class HttpsClient extends Client
{
	HttpsClient(String host)
	{
		this(host, 443);
	}

	HttpsClient(String host, int port)
	{
		this(host, port, false);
	}

	HttpsClient(String host, int port, boolean keepConnectionOpen)
	{
		try
		{
			SocketFactory socketFactory = SSLSocketFactory.getDefault();
			Socket socket = socketFactory.createSocket(host, port);
			((SSLSocket) socket).startHandshake();
			setSocket(socket, keepConnectionOpen);
		} catch (IOException e)
		{
			e.printStackTrace();
		}
	}
}