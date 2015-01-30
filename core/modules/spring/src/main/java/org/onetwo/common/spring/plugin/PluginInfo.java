package org.onetwo.common.spring.plugin;

import java.util.List;

import org.onetwo.common.utils.LangUtils;
import org.onetwo.common.utils.StringUtils;
import org.onetwo.common.utils.propconf.JFishProperties;

public class PluginInfo {
	
	public static class PKeys {
		public static final String NAME = "name";
		public static final String VERSION = "version";
		public static final String PLUGIN_CLASS = "pluginClass";
		public static final String DESC = "desc";
		public static final String DEPENDENCY = "dependency";
		public static final String SCOPE = "scope";
		public static final String WEBAPP_PLUGIN = "webapp.plugin";
		public static final String WEBAPP_PLUGIN_SERVER_LISTENER = "webapp.plugin.server.listener";
	}
	
	public void init(JFishProperties prop){
		PluginInfo info = this;
		info.name = prop.getAndThrowIfEmpty(PKeys.NAME);
		info.version = prop.getProperty(PKeys.VERSION, "1.0");
		info.pluginClass = prop.getProperty(PKeys.PLUGIN_CLASS);
//		info.pluginInstance = ReflectUtils.newInstance(cls);
//		info.contextPath = prop.getAndThrowIfEmpty("contextPath");
		info.contextPath = info.name;
		if(!info.contextPath.startsWith("/"))
			info.contextPath = "/" + info.contextPath;
		info.contextPath = StringUtils.trimEndWith(info.contextPath, "/");
		info.desc = prop.getProperty(PKeys.DESC);
		List<String> dependency = prop.getPropertyWithSplit(PKeys.DEPENDENCY, ",");
		info.dependency = dependency;
		info.scopes = prop.getPropertyWithSplit(PKeys.SCOPE, ",");
		
		info.webappPlugin = prop.getBoolean(PKeys.WEBAPP_PLUGIN, false);
		info.webappPluginServerListener = prop.getProperty(PKeys.WEBAPP_PLUGIN_SERVER_LISTENER, "");
		
		prop.remove(PKeys.NAME);
		prop.remove(PKeys.VERSION);
		prop.remove(PKeys.PLUGIN_CLASS);
		prop.remove(PKeys.DESC);
		prop.remove(PKeys.SCOPE);
		info.properties = prop;
	}

	private String version;
	private String name;
	private String pluginClass;
	private String contextPath;
	private String desc;
	private List<String> scopes;
	private JFishProperties properties;
	
	private List<String> dependency;
	
	private boolean initialized;
	
	private boolean webappPlugin;
	private String webappPluginServerListener;

	public String getVersion() {
		return version;
	}

	public String getName() {
		return name;
	}


	public String getContextPath() {
		return contextPath;
	}
	
	public String getPluginClass() {
		return pluginClass;
	}

	public String toString(){
		return LangUtils.append("plugin{name:", name, ", version:", version, "}");
	}

	public JFishProperties getProperties() {
		return properties;
	}

	public String getDesc() {
		return desc;
	}

	public List<String> getDependency() {
		return dependency;
	}

	public boolean isInitialized() {
		return initialized;
	}

	public void setInitialized() {
		this.initialized = true;
	}

	public List<String> getScopes() {
		return scopes;
	}

	public boolean applyOnScope(String scope){
		if(LangUtils.isEmpty(scopes)){
			return true;
		}
		return scopes.contains(scope);
	}
	
	public String wrapAsContextPath(String pluginContextUrl){
		String path = StringUtils.appendStartWith(pluginContextUrl, "/");
		return contextPath + path;
	}

	public boolean isWebappPlugin() {
		return webappPlugin;
	}

	public String getWebappPluginServerListener() {
		return webappPluginServerListener;
	}

}
