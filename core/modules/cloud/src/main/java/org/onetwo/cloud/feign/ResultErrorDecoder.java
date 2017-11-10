package org.onetwo.cloud.feign;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Map;

import lombok.extern.slf4j.Slf4j;

import org.onetwo.common.data.AbstractDataResult.SimpleDataResult;
import org.onetwo.common.exception.BaseException;
import org.springframework.boot.autoconfigure.web.HttpMessageConverters;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.web.client.HttpMessageConverterExtractor;

import com.netflix.hystrix.exception.HystrixBadRequestException;

import feign.Response;
import feign.codec.ErrorDecoder;

/** 除了2xx和404，都算错误，会调用，详见：
 * SynchronousMethodHandler#executeAndDecode
 * @author wayshall
 * <br/>
 */
@Slf4j
public class ResultErrorDecoder implements ErrorDecoder {
	
	final private Default defaultDecoder = new Default();
	private HttpMessageConverters httpMessageConverters;
	
    public ResultErrorDecoder(HttpMessageConverters httpMessageConverters) {
		super();
		this.httpMessageConverters = httpMessageConverters;
	}

	@Override
    public Exception decode(String methodKey, Response response) {
		if(response.status()>=200){
			try {
				HttpMessageConverterExtractor<SimpleDataResult<Object>> extractor = new HttpMessageConverterExtractor<SimpleDataResult<Object>>(SimpleDataResult.class, this.httpMessageConverters.getConverters());
				SimpleDataResult<Object> result = extractor.extractData(new FeignResponseAdapter(response));
				log.error("error code: {}, result: {}", response.status(), result);
				//防止普通异常也被熔断,if not convert as HystrixBadRequestException and fallback also throws error, it will be enabled short-circuited get "Hystrix circuit short-circuited and is OPEN" when client frequently invoke
				return new HystrixBadRequestException(result.getMessage());
			} catch (IOException e) {
				throw new BaseException("error feign response : " + e.getMessage(), e);
			}
		}
		
    	Exception exception = defaultDecoder.decode(methodKey, response);
    	return exception;
    }
	
	private class FeignResponseAdapter implements ClientHttpResponse {

		private final Response response;

		private FeignResponseAdapter(Response response) {
			this.response = response;
		}

		@Override
		public HttpStatus getStatusCode() throws IOException {
			return HttpStatus.valueOf(this.response.status());
		}

		@Override
		public int getRawStatusCode() throws IOException {
			return this.response.status();
		}

		@Override
		public String getStatusText() throws IOException {
			return this.response.reason();
		}

		@Override
		public void close() {
			try {
				this.response.body().close();
			}
			catch (IOException ex) {
				// Ignore exception on close...
			}
		}

		@Override
		public InputStream getBody() throws IOException {
			return this.response.body().asInputStream();
		}

		@Override
		public HttpHeaders getHeaders() {
			return getHttpHeaders(this.response.headers());
		}

	}

	static HttpHeaders getHttpHeaders(Map<String, Collection<String>> headers) {
		HttpHeaders httpHeaders = new HttpHeaders();
		for (Map.Entry<String, Collection<String>> entry : headers.entrySet()) {
			httpHeaders.put(entry.getKey(), new ArrayList<>(entry.getValue()));
		}
		return httpHeaders;
	}
}