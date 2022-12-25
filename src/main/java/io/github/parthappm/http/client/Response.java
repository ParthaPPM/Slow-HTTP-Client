package io.github.parthappm.http.client;

import java.nio.charset.StandardCharsets;
import java.util.Map;

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

	public String getVersion()
	{
		return version;
	}

	public int getStatusCode()
	{
		return statusCode;
	}

	public String getStatusText()
	{
		return statusText;
	}

	public Map<String, String> getHeaders()
	{
		return headers;
	}

	public byte[] getBody()
	{
		return body;
	}

	public String getText()
	{
		return new String(body, StandardCharsets.UTF_8);
	}
}