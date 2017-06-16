package org.onetwo.boot.core.web.view;

import java.util.Map;
import java.util.Map.Entry;

import org.onetwo.boot.core.web.utils.ModelAttr;
import org.onetwo.common.data.AbstractDataResult.SimpleDataResult;
import org.onetwo.common.data.DataResultWrapper;
import org.onetwo.common.data.Result;
import org.onetwo.common.spring.mvc.utils.WebResultCreator;
import org.onetwo.common.spring.mvc.utils.WebResultCreator.MapResultBuilder;
import org.springframework.validation.BindingResult;

/**
 * @author wayshall
 * <br/>
 */
public class DefaultDataResultWrapper implements DataResultWrapper {

	@Override
	public Result<?, ?> wrapResult(Object data) {
		return wrapAsDataResultIfNeed(data);
	}
	
	private Result<?, ?> wrapAsDataResultIfNeed(Object result){
		if(Result.class.isInstance(result)){
			return (Result<?, ?>)result;
		}
		if(Map.class.isInstance(result)){
			@SuppressWarnings("unchecked")
			MapResultBuilder dataResult = createMapResultBuilder((Map<String, Object>) result);
			return dataResult.buildResult();
		}else{
			SimpleDataResult<Object> dataResult = SimpleDataResult.success("", result);
			return dataResult;
		}
	}
	
	private MapResultBuilder createMapResultBuilder(Map<String, Object> map){
		MapResultBuilder dataResult = WebResultCreator.creator().map().success();
		for(Entry<String, Object> entry : map.entrySet()){
			if(BindingResult.class.isInstance(entry.getValue())){
				BindingResult br = (BindingResult) entry.getValue();
				if(br.hasErrors()){
					dataResult.error();
				}
				
			}else if(ModelAttr.MESSAGE.equalsIgnoreCase(entry.getKey())){
				dataResult.success(entry.getValue().toString());
				
			}else if(ModelAttr.ERROR_MESSAGE.equalsIgnoreCase(entry.getKey())){
				dataResult.error(entry.getValue().toString());
				
			}else{
				dataResult.put(entry.getKey(), entry.getValue());
			}
		}
		return dataResult;
	}

}