package com.ppm.http.client;

import java.nio.charset.StandardCharsets;
import java.util.Map;

public class Response
{
	private final String version;
	private final int responseCode;
	private final String responseCodeText;
	private final Map<String, String> headers;
	private final byte[] body;

	Response()
	{
		this(null, 0, "Exception in Request", null, null);
	}

	Response(String version, int responseCode, String responseCodeText, Map<String, String> headers, byte[] body)
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

	public String getText()
	{
		String responseText;
		if(body == null)
		{
			responseText = null;
		}
		else
		{
			responseText = new String(body, StandardCharsets.UTF_8);
		}
		return responseText;
	}
}