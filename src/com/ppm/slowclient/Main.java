package com.ppm.slowclient;

import com.ppm.slowclient.http.Request;
import com.ppm.slowclient.http.Response;
import com.ppm.slowclient.https.HttpsClient;

import java.util.Map;

public class Main
{
	public static void main(String[] args)
	{
		String host = "www.google.com";
		Request request = new Request("GET");
		request.addParameter("gws_rd", "ssl");

		HttpsClient client = new HttpsClient(host);
		client.createRequest(request);
		client.makeRequest();
		Response response = client.getResponse();
		Map<String, String> headers = response.getHeaders();
		byte[] body = response.getBody();
		String bodyString = new String(body);
		System.out.println(response.getVersion()+" "+response.getResponseCode()+" "+response.getResponseCodeText());
		for (String key : headers.keySet())
		{
			System.out.println(key+": "+headers.get(key));
		}
		System.out.println("Body Length = "+body.length);
		System.out.println("Body string length = "+bodyString.length());
		System.out.println(bodyString);
	}
}