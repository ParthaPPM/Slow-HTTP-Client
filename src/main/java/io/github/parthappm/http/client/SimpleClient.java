package io.github.parthappm.http.client;

import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.*;

public class SimpleClient
{
	public static Response get(String url)
	{
		return get(url, null);
	}

	public static Response get(String url, Map<String, String> parametersMap)
	{
		return get(url, parametersMap, null);
	}

	public static Response get(String url, Map<String, String> parametersMap, Map<String, String> extraHeaders)
	{
		return request("GET", url, parametersMap, extraHeaders, null);
	}

	public static Response post(String url)
	{
		return post(url, "");
	}

	public static Response post(String url, String body)
	{
		return post(url, null, body);
	}

	public static Response post(String url, Map<String, String> parametersMap)
	{
		return post(url, null, getParametersAsString(parametersMap));
	}

	public static Response post(String url, Map<String, String> parametersMap, Map<String, String> extraHeaders)
	{
		return post(url, extraHeaders, getParametersAsString(parametersMap));
	}

	public static Response post(String url, Map<String, String> extraHeaders, String body)
	{
		return post(url, extraHeaders, body.getBytes(StandardCharsets.UTF_8));
	}

	public static Response post(String url, Map<String, String> extraHeaders, byte[] body)
	{
		return request("POST", url, null, extraHeaders, body);
	}

	private static Response request(String requestMethod, String url, Map<String, String> extraParameters, Map<String, String> extraHeaders, byte[] body)
	{
		try
		{
			// extracting the request parameters
			URL parsedUrl = new URL(url);
			String protocol = parsedUrl.getProtocol();
			String host = parsedUrl.getHost();
			int port = parsedUrl.getPort();
			if(port == -1)
			{
				if(protocol.equals("https"))
				{
					port = 443;
				}
				else
				{
					port = 80;
				}
			}
			String path = parsedUrl.getPath();
			if(path.equals(""))
			{
				path = "/";
			}
			Map<String, String> parametersMap = getParametersAsMap(parsedUrl.getQuery());
			if(extraParameters != null)
			{
				parametersMap.putAll(extraParameters);
			}

			// making the request
			if(protocol.equals("http"))
			{
				Client client = new HttpClient(host, port);
				client.setMethod(requestMethod);
				client.setPath(path);
				client.setParameters(parametersMap);
				client.setHeader(extraHeaders);
				client.setBody(body);
				Response response = client.request();
				client.close();
				return response;
			}
			else if(protocol.equals("https"))
			{
				Client client = new HttpsClient(host, port);
				client.setMethod(requestMethod);
				client.setPath(path);
				client.setParameters(parametersMap);
				client.setHeader(extraHeaders);
				client.setBody(body);
				Response response = client.request();
				client.close();
				return response;
			}
			else
			{
				System.out.println(protocol + " not supported.");
				return new Response();
			}
		}
		catch (MalformedURLException e)
		{
			return new Response();
		}
	}

	private static Map<String, String> getParametersAsMap(String query)
	{
		Map<String, String> parametersMap = new HashMap<>();
		if(query != null)
		{
			Scanner sc = new Scanner(query);
			sc.useDelimiter("&");
			while (sc.hasNext())
			{
				String s = sc.next();
				int indexOfEquals = s.indexOf('=');
				String key = s.substring(0, indexOfEquals).trim();
				String value = s.substring(indexOfEquals + 1).trim();
				parametersMap.put(key, value);
			}
		}
		return parametersMap;
	}

	private static String getParametersAsString(Map<String, String> parametersMap)
	{
		StringJoiner parameterString = new StringJoiner("&");
		if(parametersMap != null)
		{
			Set<String> keySet = parametersMap.keySet();
			for (String key : keySet)
			{
				String k = URLEncoder.encode(key, StandardCharsets.UTF_8);
				String v = URLEncoder.encode(parametersMap.get(key), StandardCharsets.UTF_8);
				parameterString.add(k + "=" + v);
			}
		}
		return parameterString.toString();
	}
}