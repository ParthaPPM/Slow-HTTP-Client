package com.ppm.slowclient.http;

import java.util.Map;

public class Response
{
	private final String version;
	private final int responseCode;
	private final String responseCodeText;
	private final Map<String, String> headers;
	private final byte[] body;

	public Response(String version, int responseCode, String responseCodeText, Map<String, String> headers, byte[] body)
	{
		this.version = version;
		this.responseCode = responseCode;
		this.responseCodeText = responseCodeText;
		this.headers = headers;
		if(body != null)
		{
			if(body.length != 0)
			{
				this.body = body;
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

	public String getVersion()
	{
		return version;
	}

	public int getResponseCode()
	{
		return responseCode;
	}

	public String getResponseCodeText()
	{
		return responseCodeText;
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