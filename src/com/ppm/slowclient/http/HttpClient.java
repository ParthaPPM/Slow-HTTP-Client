package com.ppm.slowclient.http;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;
import java.util.StringJoiner;

public class HttpClient
{
	private String host;
	private int HTTP_PORT;
	private Socket socket;
	private Request request;
	private Response response;
	private boolean keepConnectionOpen;
	private final boolean followRedirects;

	public HttpClient(String host)
	{
		this(host, 80);
	}

	public HttpClient(String host, boolean followRedirects)
	{
		this(host, false, followRedirects);
	}

	public HttpClient(String host, int port)
	{
		this(host, port, false, false);
	}

	public HttpClient(String host, int port, boolean followRedirects)
	{
		this(host, port, false, followRedirects);
	}

	public HttpClient(String host, boolean keepConnectionOpen, boolean followRedirects)
	{
		this(host, 80, keepConnectionOpen, followRedirects);
	}

	public HttpClient(String host, int port, boolean keepConnectionOpen, boolean followRedirects)
	{
		this.host = host;
		this.HTTP_PORT = port;
		this.socket = null;
		this.request = null;
		this.response = null;
		this.keepConnectionOpen = keepConnectionOpen;
		this.followRedirects = followRedirects;
	}

	public void createRequest()
	{
		request = new Request("GET");
	}

	public void createRequest(String method)
	{
		request = new Request(method);
	}

	public void createRequest(String method, String location)
	{
		request = new Request(method, location);
	}

	public void createRequest(Request request)
	{
		this.request = request;
	}

	public void makeRequest()
	{
		if(request == null)
		{
			return;
		}

		try
		{
			// adding the request headers
			request.addHeader("User-Agent", "Nebula");
			request.addHeader("Host", host);
			if (keepConnectionOpen)
			{
				request.addHeader("Connection", "keep-alive");
			}
			else
			{
				request.addHeader("Connection", "close");
			}

			// creating the connection
			if(socket == null)
			{
				socket = new Socket(host, HTTP_PORT);
			}
			OutputStream os = socket.getOutputStream();
			InputStream is = socket.getInputStream();

			//sending the request
			String lineSeparator = "\r\n";
			Map<String, String> parametersMap = request.getParameters();
			Map<String, String> headersMap = request.getHeaders();
			byte[] body = request.getBody();
			StringJoiner psj = new StringJoiner("&");
			for(String key : parametersMap.keySet())
			{
				String k = URLEncoder.encode(key, "UTF-8");
				String v = URLEncoder.encode(parametersMap.get(key), "UTF-8");
				psj.add(k+"="+v);
			}
			String encodedParameters = psj.toString();
			String requestLine;
			if(encodedParameters.length() == 0)
			{
				requestLine = request.getMethod() + " " + request.getLocation() + " " + request.getVersion() + lineSeparator;
			}
			else
			{
				requestLine = request.getMethod() + " " + request.getLocation() + "?" + encodedParameters + " " + request.getVersion() + lineSeparator;
			}
			os.write(requestLine.getBytes(StandardCharsets.UTF_8));
			for(String key : headersMap.keySet())
			{
				String line = key + ": " + headersMap.get(key) + lineSeparator;
				os.write(line.getBytes(StandardCharsets.UTF_8));
			}
			os.write(lineSeparator.getBytes(StandardCharsets.UTF_8));
			if(body!=null)
			{
				os.write(body);
			}

			StringBuilder line = new StringBuilder();
			boolean isStatusLine = true;
			String version = null;
			int responseCode = 0;
			String responseCodeText = null;
			Map<String, String> responseHeadersMap = new HashMap<>();
			int bytesToRead = 0;
			byte[] responseBody = null;
			//reading the response
			while(true)
			{
				int b = is.read();
				if(b!=-1)
				{
					if(b != '\r')
					{
						line.append((char)b);
					}
					else
					{
						is.read();
						if(line.toString().equals(""))
						{
							break;
						}
						if(isStatusLine)
						{
							isStatusLine = false;
							Scanner sc = new Scanner(line.toString());
							version = sc.next();
							responseCode = sc.nextInt();
							responseCodeText = sc.nextLine().trim();
						}
						else
						{
							String l = line.toString();
							int colonIndexPos = l.indexOf(':');
							String key = l.substring(0, colonIndexPos).trim();
							String value = l.substring(colonIndexPos+1).trim();
							responseHeadersMap.put(key, value);
							if(key.equals("Content-Length"))
							{
								bytesToRead = Integer.parseInt(value);
							}
							if(key.equals("Connection"))
							{
								if(value.equalsIgnoreCase("Closed"))
								{
									this.keepConnectionOpen = false;
								}
							}
						}
						line = new StringBuilder();
					}
				}
			}

			//reading the body
			if(bytesToRead>0)
			{
				responseBody = new byte[bytesToRead];
				is.read(responseBody, 0, bytesToRead);
			}

			this.response = new Response(version, responseCode, responseCodeText, responseHeadersMap, responseBody);

			if(!keepConnectionOpen)
			{
				close();
			}
		}
		catch (IOException e)
		{
			e.printStackTrace();
			close();
		}

		// following the redirect
		if(followRedirects && response!=null)
		{
			String newUrl = response.getHeaders().get("Location");
			if(newUrl!=null)
			{
				int protocolSeparatorIndex = newUrl.indexOf("://");
				int portSeparatorIndex = newUrl.indexOf(':', protocolSeparatorIndex+5);
				int hostEndIndex = newUrl.indexOf('/', protocolSeparatorIndex+3);
				String protocol = newUrl.substring(0, protocolSeparatorIndex);
				if(portSeparatorIndex==-1)
				{
					this.host = newUrl.substring(protocolSeparatorIndex+3, hostEndIndex);
				}
				else
				{
					this.host = newUrl.substring(protocolSeparatorIndex+3, portSeparatorIndex);
					HTTP_PORT = Integer.parseInt(newUrl.substring(portSeparatorIndex+1, hostEndIndex));
				}
				String location = newUrl.substring(hostEndIndex);
				if(protocol.equals("http"))
				{
					Request r = new Request("GET", location);
					createRequest(r);
					makeRequest();
				}
			}
		}
	}

	public Response getResponse()
	{
		return response;
	}

	public void close()
	{
		try
		{
			if(socket!=null)
			{
				socket.close();
			}
		}
		catch (IOException e)
		{
			e.printStackTrace();
		}
		socket = null;
	}
}