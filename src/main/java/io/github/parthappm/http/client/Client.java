package io.github.parthappm.http.client;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.net.Socket;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.*;

public class Client
{
	protected Socket socket;
	private boolean keepConnectionOpen;
	private String method;
	private String path;
	private final Map<String, String> parameters;
	private final Map<String, String> headers;
	private byte[] body;

	Client()
	{
		this.method = "GET";
		this.path = "/";
		this.parameters = new HashMap<>();
		this.headers = new HashMap<>();
		this.body = new byte[0];
	}

	protected void setSocket(Socket socket, boolean keepConnectionOpen)
	{
		this.socket = socket;
		this.keepConnectionOpen = keepConnectionOpen;
	}

	public void setMethod(String method)
	{
		this.method = method;
	}

	public void setPath(String path)
	{
		this.path = path;
	}

	public void setParameters(Map<String, String> parameters)
	{
		if (parameters != null)
		{
			this.parameters.putAll(parameters);
		}
	}

	public void setHeader(Map<String, String> headers)
	{
		if (headers != null)
		{
			this.headers.putAll(headers);
		}
	}

	public void setBody(byte[] body)
	{
		if (body != null)
		{
			this.body = body;
		}
	}

	public Response request()
	{
		// building the headers
		headers.put("Connection", keepConnectionOpen ? "keep-alive" : "close");
		headers.put("User-Agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/92.0.4515.131 Safari/537.36 Edg/92.0.902.67");

		// sending and receiving the data
		try
		{
			OutputStream os = socket.getOutputStream();
			InputStream is = socket.getInputStream();

			String lineSeparator = "\r\n";
			String requestLine = getRequestLine();

			// sending the request line
			os.write((requestLine + lineSeparator).getBytes(StandardCharsets.UTF_8));

			//sending the headers
			for (String key : headers.keySet())
			{
				String line = key + ": " + headers.get(key) + lineSeparator;
				os.write(line.getBytes(StandardCharsets.UTF_8));
			}
			os.write(lineSeparator.getBytes(StandardCharsets.UTF_8));

			// sending the body
			os.write(body);

			StringBuilder line = new StringBuilder();
			boolean isStatusLine = true;
			String version = null;
			int responseCode = 0;
			String responseCodeText = null;
			Map<String, String> responseHeadersMap = new HashMap<>();
			int bytesToRead = 0;
			byte[] responseBody;
			//reading the response header byte by byte
			while (true)
			{
				int b = is.read();
				if(b != '\r')
				{
					line.append((char) b);
				}
				else
				{
					is.read();
					String l = line.toString();
					if(l.equals(""))
					{
						break;
					}
					if(isStatusLine)
					{
						isStatusLine = false;
						Scanner sc = new Scanner(l);
						version = sc.next();
						responseCode = sc.nextInt();
						responseCodeText = sc.nextLine().trim();
					}
					else
					{
						int colonIndexPos = l.indexOf(':');
						String key = l.substring(0, colonIndexPos).trim();
						String value = l.substring(colonIndexPos + 1).trim();
						responseHeadersMap.put(key, value);
						if(key.equals("Content-Length"))
						{
							bytesToRead = Integer.parseInt(value);
						}
						if(key.equals("Connection"))
						{
							if(value.equalsIgnoreCase("Closed"))
							{
								keepConnectionOpen = false;
							}
						}
					}
					line = new StringBuilder();
				}
			}

			//reading the body
			if(bytesToRead > 0)
			{
				responseBody = new byte[bytesToRead];
				is.read(responseBody, 0, bytesToRead);
			}
			else
			{
				responseBody = null;
			}

			if(!keepConnectionOpen)
			{
				close();
			}

			return new Response(version, responseCode, responseCodeText, responseHeadersMap, responseBody);
		}
		catch (IOException e)
		{
			e.printStackTrace();
			close();
			return new Response();
		}
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

	private String getRequestLine() throws UnsupportedEncodingException
	{
		String parametersAsString = getParametersAsString();
		StringJoiner requestLine = new StringJoiner(" ");
		requestLine.add(this.method);
		requestLine.add(this.path + ((parametersAsString.length() == 0) ? "" : "?" + parametersAsString));
		requestLine.add("HTTP/1.1");
		return requestLine.toString();
	}

	private String getParametersAsString()
	{
		StringJoiner parameterString = new StringJoiner("&");
		for (String key : this.parameters.keySet())
		{
			String k = URLEncoder.encode(key, StandardCharsets.UTF_8);
			String v = URLEncoder.encode(this.parameters.get(key), StandardCharsets.UTF_8);
			parameterString.add(k + "=" + v);
		}
		return parameterString.toString();
	}
}
