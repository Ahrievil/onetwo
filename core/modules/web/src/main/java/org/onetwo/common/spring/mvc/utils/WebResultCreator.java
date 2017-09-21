package org.onetwo.common.spring.mvc.utils;

import java.util.List;
import java.util.Map;
import java.util.stream.Stream;

import org.onetwo.common.data.AbstractDataResult.SimpleDataResult;
import org.onetwo.common.data.LazyValue;
import org.onetwo.common.exception.ErrorType;
import org.onetwo.common.spring.validator.ValidatorUtils;
import org.onetwo.common.utils.CUtils;
import org.onetwo.common.utils.LangUtils;
import org.springframework.validation.BindingResult;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;

final public class WebResultCreator {
	
	private final static WebResultCreator CREATOR = new WebResultCreator();

	private WebResultCreator() {
	}

	public static WebResultCreator creator(){
		return CREATOR;
	}

	public SimpleResultBuilder error(String message){
		return SimpleResultBuilder.builder().error(message);
	}

	public SimpleResultBuilder error(ErrorType errorType){
		return SimpleResultBuilder.builder().error(errorType);
	}

	public SimpleResultBuilder error(BindingResult bindingResult){
		String message = ValidatorUtils.asString(bindingResult);
		return error(message);
	}

	public SimpleResultBuilder success(String message){
		return SimpleResultBuilder.builder().success(message);
	}
	/***
	 * SimpleResultBuilder
	 * 把数据包装为{@linkplain org.onetwo.common.data.DataResult Result} ({@linkplain SimpleDataResult SimpleDataResult}) 类型返回
	 * 
	 * @return
	 */
	public SimpleResultBuilder simple(){
		return simple(null);
	}
	public SimpleResultBuilder simple(Object data){
		return data(data);
	}
	public SimpleResultBuilder data(Object data){
		return SimpleResultBuilder.builder().success().data(data);
	}
	
	/***
	 * 把data付给result的data属性，并设置extractableData属性为true
	 * @param data
	 * @return
	 */
	public SimpleResultBuilder extractableData(Object data){
		return simple(data).extractableData(true);
	}
	/****
	 * 
	 * ListResultBuilder
	 * 把数据包装为{@linkplain org.onetwo.common.data.DataResult Result} ({@linkplain org.onetwo.common.data.AbstractDataResult.ListResult ListResult}) 类型返回
	 * 
	 * @return
	 */
	public ListResultBuilder list(Object...elements){
		ListResultBuilder builder = new ListResultBuilder().success();
		builder.addElements(elements);
		return builder;
	}
	public ListResultBuilder list(List<?> list){
		ListResultBuilder builder = new ListResultBuilder().success();
		builder.addList(list);
		return builder;
	}
	public ListResultBuilder extractedList(List<Object> list){
		return list(list).extractableData(true);
	}
	/***
	 * 
	 * MapResultBuilder
	 * 把数据包装为{@linkplain org.onetwo.common.data.DataResult Result} ({@linkplain org.onetwo.common.result.MapResult MapResult}) 类型返回
	 * 
	 * @return
	 */
	public MapResultBuilder map(Object...objects){
		MapResultBuilder builder = new MapResultBuilder().success();
		builder.puts(objects);
		return builder;
	}
	public MapResultBuilder extractedMap(Object...objects){
		return map(objects).extractableData(true);
	}
	
	public LazyResultBuilder lazy(LazyValue data){
		LazyResultBuilder builder = new LazyResultBuilder().success();
		builder.data(data);
		return builder;
	}



	public static class SimpleResultBuilder extends AbstractResultBuilder<Object, SimpleResultBuilder> {
		public static SimpleResultBuilder builder(){
			return new SimpleResultBuilder();
		}
	}
	
	public static class BaseResultBuilder<T> extends AbstractResultBuilder<T, BaseResultBuilder<T>> {
		public static <T> BaseResultBuilder<T> builder(){
			return new BaseResultBuilder<T>();
		}
	}
	
	public static class LazyResultBuilder extends AbstractResultBuilder<LazyValue, LazyResultBuilder> {
		public static LazyResultBuilder builder(){
			return new LazyResultBuilder();
		}
	}
	

	public static class ListResultBuilder extends AbstractResultBuilder<List<Object>, ListResultBuilder> {
		public static ListResultBuilder builder(){
			return new ListResultBuilder();
		}
		
		public ListResultBuilder() {
//			super(ListResultBuilder.class);
			this.data = Lists.newArrayList();
		}
		
		public ListResultBuilder addElements(Object... elements){
			Stream.of(elements).forEach(e->data.add(e));
			return this;
		}
		
		public ListResultBuilder addList(List<?> list){
			data.addAll(list);
			return this;
		}
	}
	
	public static class MapResultBuilder extends AbstractResultBuilder<Map<Object, Object>, MapResultBuilder> {
		
		public MapResultBuilder() {
//			super(MapResultBuilder.class);
			this.data = Maps.newHashMap();
		}
		
		public MapResultBuilder put(Object key, Object value){
			data.put(key, value);
			return this;
		}
		
		public MapResultBuilder puts(Object...objs){
			if(objs.length==0)
				return this;
			data.putAll(CUtils.asMap(objs));
			return this;
		}
		
		public MapResultBuilder putAll(Map<?, ?> datas){
			if(LangUtils.isEmpty(datas))
				return this;
			data.putAll(datas);
			return this;
		}
	}

	
}
