package org.onetwo.common.spring.web.mvc;

import java.util.Properties;

import org.onetwo.common.utils.propconf.JFishProperties;

public class MvcSetting {
	public static final String MAX_UPLOAD_SIZE = "max_upload_size";
	private JFishProperties wraper;
	private final Properties mvcSetting;
	
	public MvcSetting(Properties prop){
		this.mvcSetting = prop;
		this.wraper = new JFishProperties(prop);
	}
	
	public int getMaxUploadSize(){
		int maxUpload = this.wraper.getInt(MAX_UPLOAD_SIZE, 1024*1024*10);
		return maxUpload;
	}

	public Properties getMvcSetting() {
		return mvcSetting;
	}

}
