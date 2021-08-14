package com.ppm.http.client;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.net.Socket;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.*;

class HttpClient
{
	private final String HOST;
	private final int PORT;
	private Socket socket;
	private Request request;
	private boolean keepConnectionOpen;

	public HttpClient(String host, int port)
	{
		this.HOST = host;
		this.PORT = port;
		this.socket = null;
		this.request = null;
		this.keepConnectionOpen = true;
	}

	public void createRequest(String method, String location, Map<String, String> parameters, Map<String, String> extraHeaders, byte[] body)
	{
		Map<String, String> headers = new HashMap<>();
		if(extraHeaders != null)
		{
			headers.putAll(extraHeaders);
		}
		headers.put("Host", this.HOST);
		if(this.keepConnectionOpen)
		{
			headers.put("Connection", "keep-alive");
		}
		else
		{
			headers.put("Connection", "close");
		}
		headers.put("User-Agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/92.0.4515.131 Safari/537.36 Edg/92.0.902.67");
		if(!headers.containsKey("Accept"))
		{
			headers.put("Accept", "*/*");
		}
		this.request = new Request(method, location, parameters, headers, body);

	}

	public Response makeRequest()
	{
		Response response = new Response();
		if(request != null)
		{
			try
			{
				// creating the connection
				if(socket == null)
				{
					socket = new Socket(HOST, PORT);
				}
				OutputStream os = socket.getOutputStream();
				InputStream is = socket.getInputStream();

				String lineSeparator = "\r\n";
				String requestLine = getRequestLine(request.getMethod(), request.getLocation(), request.getParameters(), request.getVersion());
				Map<String, String> headersMap = request.getHeaders();
				byte[] body = request.getBody();

				// sending the request
				os.write((requestLine + lineSeparator).getBytes(StandardCharsets.UTF_8));
				for (String key : headersMap.keySet())
				{
					String line = key + ": " + headersMap.get(key) + lineSeparator;
					os.write(line.getBytes(StandardCharsets.UTF_8));
				}
				os.write(lineSeparator.getBytes(StandardCharsets.UTF_8));
				if(body != null)
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
									this.keepConnectionOpen = false;
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

				response = new Response(version, responseCode, responseCodeText, responseHeadersMap, responseBody);

				if(!keepConnectionOpen)
				{
					close();
				}
			}
			catch (IOException e)
			{
				close();
			}
		}
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

	private String getParametersAsString(Map<String, String> parametersMap) throws UnsupportedEncodingException
	{
		StringJoiner parameterString = new StringJoiner("&");
		if(parametersMap != null)
		{
			Set<String> keySet = parametersMap.keySet();
			for (String key : keySet)
			{
				String k = URLEncoder.encode(key, "UTF-8");
				String v = URLEncoder.encode(parametersMap.get(key), "UTF-8");
				parameterString.add(k + "=" + v);
			}
		}
		return parameterString.toString();
	}

	private String getRequestLine(String method, String location, Map<String, String> parametersMap, String version) throws UnsupportedEncodingException
	{
		String parametersAsString = getParametersAsString(parametersMap);
		StringJoiner requestLine = new StringJoiner(" ");
		requestLine.add(method);
		if(parametersAsString.length() == 0)
		{
			requestLine.add(location);
		}
		else
		{
			requestLine.add(location + "?" + parametersAsString);
		}
		requestLine.add(version);
		return requestLine.toString();
	}
}