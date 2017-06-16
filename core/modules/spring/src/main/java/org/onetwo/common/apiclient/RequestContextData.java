package org.onetwo.common.apiclient;

import java.util.Map;
import java.util.function.Consumer;

import org.springframework.http.HttpMethod;
import org.springframework.http.client.ClientHttpRequest;
import org.springframework.web.bind.annotation.RequestMethod;

/**
 * @author wayshall
 * <br/>
 */
public class RequestContextData {
	final private HttpMethod httpMethod;
	final private String requestUrl;
	final private Class<?> responseType;
	private Object requestBody;
	final private Map<String, Object> uriVariables;
	private Consumer<ClientHttpRequest> requestCallback;
	
	public RequestContextData(RequestMethod requestMethod, String requestUrl, Map<String, Object> uriVariables, Class<?> responseType) {
		super();
		this.httpMethod = HttpMethod.resolve(requestMethod.name());
		this.requestUrl = requestUrl;
		this.uriVariables = uriVariables;
		this.responseType = responseType;
	}
	
	public Class<?> getResponseType() {
		return responseType;
	}

	public HttpMethod getHttpMethod() {
		return httpMethod;
	}
	public String getRequestUrl() {
		return requestUrl;
	}
	public Object getRequestBody() {
		return requestBody;
	}
	public Map<String, Object> getUriVariables() {
		return uriVariables;
	}
	public void setRequestBody(Object requestBody) {
		this.requestBody = requestBody;
	}
	public RequestContextData doWithRequestCallback(Consumer<ClientHttpRequest> requestCallback) {
		this.requestCallback = requestCallback;
		return this;
	}
	public Consumer<ClientHttpRequest> getRequestCallback() {
		return requestCallback;
	}

	@Override
	public String toString() {
		return "RequestContextData [httpMethod=" + httpMethod
				+ ", requestUrl=" + requestUrl + ", responseType="
				+ responseType + ", requestBody=" + requestBody
				+ ", uriVariables=" + uriVariables + ", requestCallback="
				+ requestCallback + "]";
	}

}