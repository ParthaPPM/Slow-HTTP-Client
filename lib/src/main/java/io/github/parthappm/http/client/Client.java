package io.github.parthappm.http.client;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.StringJoiner;

/**
 * A client class to implement the HTTP client functionality.
 * The object of this class cannot be created directly, either create an object of HttpClient of HttpsClient class.
 */
public class Client
{
	private boolean suppressException;
	private Socket socket;
	private boolean keepConnectionOpen;
	private boolean followRedirect;
	private String method;
	private String path;
	private final Map<String, String> parameters;
	private final Map<String, String> headers;
	private byte[] body;

	Client()
	{
		this.suppressException = true;
		this.keepConnectionOpen = false;
		this.followRedirect = false;
		this.method = "GET";
		this.path = "/";
		this.parameters = new HashMap<>();
		this.headers = new HashMap<>();
		this.body = new byte[0];
	}

	/**
	 * Setter method for socket object.
	 * @param socket The socket that will be used for HTTP protocol
	 */
	protected void setSocket(Socket socket)
	{
		this.socket = socket;
	}

	/**
	 * Setter method for flag to suppress any kind of exception.
	 * @param suppressException The flag to suppress exceptions
	 * @return The reference of the current object for chaining
	 */
	public Client suppressException(boolean suppressException)
	{
		this.suppressException = suppressException;
		return this;
	}

	/**
	 * Setter method for flag to keep the connection open for the socket.
	 * @param keepConnectionOpen The flag whether to keep the connection open or close the socket
	 * @return The reference of the current object for chaining
	 */
	public Client keepConnectionOpen(boolean keepConnectionOpen)
	{
		this.keepConnectionOpen = keepConnectionOpen;
		return this;
	}

	/**
	 * Setter method for flag to whether follow redirect HTTP response.
	 * @param followRedirect The flag whether to make the next request depending on HTTP response
	 * @return The reference of the current object for chaining
	 */
	public Client followRedirect(boolean followRedirect)
	{
		this.followRedirect = followRedirect;
		return this;
	}

	/**
	 * Setter method to set the HTTP Method.
	 * @param method The HTTP method to be used
	 * @return The reference of the current object for chaining
	 */
	public Client setMethod(String method)
	{
		if (method != null)
		{
			this.method = method.toUpperCase();
		}
		return this;
	}

	/**
	 * Setter method to set the path to be used after the host in url.
	 * @param path The url path
	 * @return The reference of the current object for chaining
	 */
	public Client setPath(String path)
	{
		if (path != null && !path.equals(""))
		{
			this.path = path;
		}
		return this;
	}

	/**
	 * Setter method to add or modify a single parameter to the existing list of parameters. To delete a parameter, set the value to null.
	 * @param key The parameter key
	 * @param value The parameter value
	 * @return The reference of the current object for chaining
	 */
	public Client addParameter(String key, String value)
	{
		if (key != null)
		{
			parameters.put(key, value);
		}
		return this;
	}

	/**
	 * Setter method to add or modify multiple parameters to the existing list of parameters. To delete a parameter, set the value to null.
	 * @param parameters The map of new or extra parameters that are to be added to the existing list of parameters
	 * @return The reference of the current object for chaining
	 */
	public Client setParameters(Map<String, String> parameters)
	{
		if (parameters != null)
		{
			this.parameters.putAll(parameters);
		}
		return this;
	}

	/**
	 * Setter method to add or modify a single header to the existing list of headers. To delete a header, set the value to null.
	 * @param key The header name
	 * @param value The header value
	 * @return The reference of the current object for chaining
	 */
	public Client addHeader(String key, String value)
	{
		if (key != null)
		{
			headers.put(key, value);
		}
		return this;
	}

	/**
	 * Setter method add or modify multiple headers to the existing list of parameters. To delete a parameter, set the value to null.
	 * @param headers The map of new or extra headers that are to be added to the existing list of headers
	 * @return The reference of the current object for chaining
	 */
	public Client setHeader(Map<String, String> headers)
	{
		if (headers != null)
		{
			this.headers.putAll(headers);
		}
		return this;
	}

	/**
	 * Setter method to set the request body
	 * @param body Bytes array representing the request body
	 * @return The reference of the current object for chaining
	 */
	public Client setBody(byte[] body)
	{
		if (body != null)
		{
			this.body = body;
		}
		return this;
	}

	/**
	 * This method has to be called to make the HTTP request to the server after setting the required parameters
	 * @return Response object that contains all the details of the HTTP response for this particular request
	 * @throws IOException If any IOException occurs in the socket connection
	 */
	public Response request() throws IOException
	{
		// setting some string values
		String LINE_SEPARATOR = "\r\n";
		String CONTENT_LENGTH = "Content-Length";
		String CONNECTION = "Connection";
		String CONNECTION_KEEP_ALIVE = "keep-alive";
		String CONNECTION_CLOSE = "close";
		String CONNECTION_CLOSE_RESPONSE = "Closed";
		String USER_AGENT = "User-Agent";
		String userAgent = "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/92.0.4515.131 Safari/537.36 Edg/92.0.902.67";

		// building the headers
		headers.put(CONTENT_LENGTH, String.valueOf(body.length));
		headers.put("Connection", keepConnectionOpen ? CONNECTION_KEEP_ALIVE : CONNECTION_CLOSE);
		headers.put(USER_AGENT, userAgent);

		try
		{
			// sending and receiving the data
			OutputStream os = socket.getOutputStream();
			InputStream is = socket.getInputStream();

			// sending the request line
			String requestLine = getRequestLine();
			os.write((requestLine + LINE_SEPARATOR).getBytes(StandardCharsets.UTF_8));

			//sending the headers
			for (String key : headers.keySet())
			{
				if (key != null)
				{
					String value = headers.get(key);
					if (value != null)
					{
						String line = key + ": " + value + LINE_SEPARATOR;
						os.write(line.getBytes(StandardCharsets.UTF_8));
					}
				}
			}
			os.write(LINE_SEPARATOR.getBytes(StandardCharsets.UTF_8));

			// sending the body
			os.write(body);

			// reading the response status line
			String version = readInputStream(is, ' ');
			int statusCode = Integer.parseInt(readInputStream(is, ' '));
			String statusText = readInputStream(is, '\n');

			// reading the response headers
			Map<String, String> responseHeaders = new HashMap<>();
			do
			{
				String header = readInputStream(is, '\n');
				if (header.equals(""))
				{
					break;
				}
				else
				{
					int colonIndex = header.indexOf(':');
					String key = header.substring(0, colonIndex).trim();
					String value = header.substring(colonIndex + 1).trim();
					responseHeaders.put(key, value);
				}
			} while (true);

			// reading the response body
			byte[] responseBody;
			int bytesRead;
			String contentLength = responseHeaders.get(CONTENT_LENGTH);
			if (contentLength != null)
			{
				int responseContentLength = Integer.parseInt(contentLength);
				responseBody = new byte[responseContentLength];
				bytesRead = is.read(responseBody, 0, responseContentLength);
			}
			else
			{
				responseBody = new byte[0];
				bytesRead = 0;
			}

			// reading the connection information
			String connection = responseHeaders.get(CONNECTION);
			if (connection != null && connection.equalsIgnoreCase(CONNECTION_CLOSE_RESPONSE))
			{
				keepConnectionOpen = false;
			}
			close();

			return new Response(version, statusCode, statusText, responseHeaders, Arrays.copyOfRange(responseBody, 0, bytesRead));
		}
		catch (Exception e)
		{
			if (!suppressException)
			{
				throw e;
			}
			else
			{
				return new Response();
			}
		}
	}

	/**
	 * Closes the socket connection to the server
	 * @throws IOException If any IOException occurs in the process of closing the connection
	 */
	public void close() throws IOException
	{
		if(socket!=null)
		{
			socket.close();
		}
		socket = null;
	}

	private String readInputStream(InputStream is, char endChar) throws IOException
	{
		StringBuilder temp = new StringBuilder();
		do
		{
			int b = is.read();
			temp.append((char) b);
			if (b == endChar)
			{
				break;
			}
		} while (true);
		return temp.toString().trim();
	}

	private String getRequestLine()
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
		for (String key : parameters.keySet())
		{
			if (key != null)
			{
				String value = parameters.get(key);
				if (value != null)
				{
					String k = URLEncoder.encode(key, StandardCharsets.UTF_8);
					String v = URLEncoder.encode(value, StandardCharsets.UTF_8);
					parameterString.add(k + "=" + v);
				}
			}
		}
		return parameterString.toString();
	}
}
