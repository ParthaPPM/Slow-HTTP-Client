package io.github.parthappm.http.client;

import java.nio.charset.StandardCharsets;
import java.util.Map;

/**
 * The Response class contains all the data related to HTTP response returned by the server
 */
public class Response
{
	private final String version;
	private final int statusCode;
	private final String statusText;
	private final Map<String, String> headers;
	private final byte[] body;

	Response()
	{
		this(null, 0, "Exception in Request", null, null);
	}

	Response(String version, int statusCode, String statusText, Map<String, String> headers, byte[] body)
	{
		this.version = version;
		this.statusCode = statusCode;
		this.statusText = statusText;
		this.headers = headers;
		this.body = body;
	}

	/**
	 * Getter method to get the HTTP version specified by the server in the response.
	 * @return The HTTP version specified by the server in the response
	 */
	public String getVersion()
	{
		return version;
	}

	/**
	 * Getter method to get the response status code.
	 * @return The response status code
	 */
	public int getStatusCode()
	{
		return statusCode;
	}

	/**
	 * Getter method to get the response status text.
	 * @return The response status text
	 */
	public String getStatusText()
	{
		return statusText;
	}

	/**
	 * Getter method to get the response headers.
	 * @return The response headers
	 */
	public Map<String, String> getHeaders()
	{
		return headers;
	}

	/**
	 * Getter method to get the response body as bytes array.
	 * @return The response body as byte array
	 */
	public byte[] getBody()
	{
		return body;
	}

	/**
	 * Getter method to get the response body as String representation decoded by UTF 8 standard.
	 * @return The response body as String representation decoded by UTF 8 standard
	 */
	public String getText()
	{
		return new String(body, StandardCharsets.UTF_8);
	}
}