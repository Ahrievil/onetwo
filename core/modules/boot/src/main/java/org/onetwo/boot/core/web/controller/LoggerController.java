package org.onetwo.boot.core.web.controller;

import org.onetwo.boot.core.web.service.impl.SimpleLoggerManager;
import org.onetwo.boot.core.web.utils.BootWebUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

/**
 * @author wayshall
 * <br/>
 */
@RequestMapping(BootWebUtils.CONTROLLER_PREFIX+"/logger")
public class LoggerController extends AbstractBaseController {
	
	@Autowired
	private SimpleLoggerManager loggerManager;
	
	@PutMapping("/{logger}/changeLevel")
	@ResponseBody
	public Object changeLevel(@PathVariable("logger")String logger, @RequestParam String level){
		loggerManager.changeLevel(logger, level);
		return result().success("change logger level success!").buildResult();
	}
	
	@PutMapping("/resetLevels")
	@ResponseBody
	public Object resetLevels(){
		loggerManager.resetLevels();
		return result().success("reset logger level success!").buildResult();
	}


}
