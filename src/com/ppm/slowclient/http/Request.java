package com.ppm.slowclient.http;

import java.util.HashMap;
import java.util.Map;

public class Request
{
	private final String method;
	private final String location;
	private final String version;
	private final Map<String, String> parameters;
	private final Map<String, String> headers;
	private final byte[] body;

	public Request(String method)
	{
		this(method, "/");
	}

	public Request(String method, String location)
	{
		this(method, location, null);
	}

	public Request(String method, String location, byte[] body)
	{
		this.method = method.toUpperCase();
		this.location = location;
		this.version = "HTTP/1.1";
		this.parameters = new HashMap<>();
		this.headers = new HashMap<>();
		if(body != null)
		{
			if(body.length != 0)
			{
				this.body = body;
				headers.put("Content-Length", String.valueOf(body.length));
			}
			else
			{
				this.body = null;
			}
		}
		else
		{
			this.body = null;
		}
	}

	public void addParameter(String key, String value)
	{
		parameters.put(key, value);
	}

	public void addHeader(String key, String value)
	{
		headers.put(key, value);
	}

	public String getMethod()
	{
		return method;
	}

	public String getLocation()
	{
		return location;
	}

	public String getVersion()
	{
		return version;
	}

	public Map<String, String> getParameters()
	{
		return parameters;
	}

	public Map<String, String> getHeaders()
	{
		return headers;
	}

	public byte[] getBody()
	{
		return body;
	}
}