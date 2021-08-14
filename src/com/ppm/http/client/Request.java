package com.ppm.http.client;

import java.util.HashMap;
import java.util.Map;

class Request
{
	private final String method;
	private final String location;
	private final String version;
	private final Map<String, String> parameters;
	private final Map<String, String> headers;
	private final byte[] body;

	Request(String method, String location, Map<String, String> parameters, Map<String, String> headers, byte[] body)
	{
		this.method = method.toUpperCase();
		this.location = location;
		this.version = "HTTP/1.1";
		if(parameters != null)
		{
			if(parameters.size() == 0)
			{
				this.parameters = null;
			}
			else
			{
				this.parameters = parameters;
			}
		}
		else
		{
			this.parameters = null;
		}
		this.headers = new HashMap<>();
		this.headers.putAll(headers);
		if(body != null)
		{
			if(body.length != 0)
			{
				this.body = body;
				this.headers.put("Content-Length", String.valueOf(body.length));
			}
			else
			{
				this.body = null;
				this.headers.put("Content-Length", "0");
			}
		}
		else
		{
			this.body = null;
			this.headers.put("Content-Length", "0");
		}
	}

	String getMethod()
	{
		return method;
	}

	String getLocation()
	{
		return location;
	}

	String getVersion()
	{
		return version;
	}

	Map<String, String> getParameters()
	{
		return parameters;
	}

	Map<String, String> getHeaders()
	{
		return headers;
	}

	byte[] getBody()
	{
		return body;
	}
}