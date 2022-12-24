package io.github.parthappm.http.client;

import java.util.Map;

public interface Client
{
	void createRequest(String method, String location, Map<String, String> parameters, Map<String, String> extraHeaders, byte[] body);
	Response makeRequest();
	void close();
}
