package org.onetwo.plugins.jsonrpc.client.proxy;

import java.lang.reflect.Method;

import org.onetwo.common.jsonrpc.ClientResponseParser;
import org.onetwo.common.jsonrpc.RpcMethodResolver;
import org.onetwo.common.jsonrpc.exception.JsonRpcError;
import org.onetwo.common.jsonrpc.exception.JsonRpcException;
import org.onetwo.common.jsonrpc.protocol.JsonRpcRequest;
import org.onetwo.common.jsonrpc.protocol.JsonRpcResponse;
import org.onetwo.common.proxy.BaseMethodParameter;
import org.onetwo.common.proxy.CacheableDynamicProxyHandler;
import org.onetwo.common.spring.rest.JFishRestTemplate;
import org.springframework.web.client.RestTemplate;

import com.google.common.cache.Cache;

public class RpcClientHandler extends CacheableDynamicProxyHandler<BaseMethodParameter, RpcMethodResolver> {

//	private final Cache<Method, RestMethodResolver> methodCaches;

	final private String baseUrl;
	private RestTemplate restTemplate;
	
	public RpcClientHandler(String baseUrl, Cache<Method, RpcMethodResolver> methodCaches, Class<?>... proxiedInterfaces) {
		this(baseUrl, null, methodCaches, proxiedInterfaces);
	}
	
	public RpcClientHandler(String baseUrl, RestTemplate restTemplate, Cache<Method, RpcMethodResolver> methodCaches, Class<?>... proxiedInterfaces) {
		super(methodCaches, proxiedInterfaces);
		this.restTemplate = restTemplate==null?new JFishRestTemplate():restTemplate;
		this.baseUrl = baseUrl;
	}

	@Override
	protected Object invokeMethod(Object proxy, RpcMethodResolver method, Object[] args) throws Throwable {
		JsonRpcRequest request = new JsonRpcRequest();
		String methodName = method.getDeclaringClass().getName()+"."+method.getMethod().getName();
		request.setMethod(methodName);
		if(method.isNamedParam()){
			request.setParams(method.toMapByArgs(args));
		}else{
			request.setParams(method.toListByArgs(args));
		}
		String jsonstr = this.restTemplate.postForObject(baseUrl, request, String.class);
		ClientResponseParser parser = new ClientResponseParser(jsonstr);
		JsonRpcResponse baseResponse = parser.parseBase().getResponse();
		if(baseResponse.getError()!=null){
			throw new JsonRpcException(JsonRpcError.valueOf(baseResponse.getError()));
		}
		Object result = parser.parseResult(method).getResponse().getResult();
//		this.restTemplate.postForEntity(baseUrl, request, method.getResponseType());
		return result;
	}

	@Override
	protected RpcMethodResolver createMethodResolver(Method method) {
		return new RpcMethodResolver(method);
	}


}
