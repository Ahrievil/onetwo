package org.onetwo.common.db.dquery;

import java.lang.reflect.Method;
import java.util.Map;
import java.util.Map.Entry;

import org.onetwo.common.db.DataBase;
import org.onetwo.common.db.filequery.NamespacePropertiesManager;
import org.onetwo.common.db.filequery.SpringBasedSqlFileScanner;
import org.onetwo.common.db.filequery.SqlFileScanner;
import org.onetwo.common.log.JFishLoggerFactory;
import org.onetwo.common.propconf.ResourceAdapter;
import org.onetwo.common.reflect.ReflectUtils;
import org.onetwo.common.spring.SpringUtils;
import org.slf4j.Logger;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.support.BeanDefinitionBuilder;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.context.ApplicationContext;
import org.springframework.util.ClassUtils;

import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;

public class FileScanBasicDynamicQueryObjectRegister implements DynamicQueryObjectRegister {
	protected final Logger logger = JFishLoggerFactory.getLogger(this.getClass());

	private SqlFileScanner sqlFileScanner = new SpringBasedSqlFileScanner(ClassUtils.getDefaultClassLoader());

	private LoadingCache<Method, DynamicMethod> methodCache = CacheBuilder.newBuilder()
																.build(new CacheLoader<Method, DynamicMethod>() {
																	@Override
																	public DynamicMethod load(Method method) throws Exception {
																		return DynamicMethod.newDynamicMethod(method);
																	}
																});

	private DataBase database;
	private BeanDefinitionRegistry registry;
//	private ApplicationContext applicationContext;
	
	public FileScanBasicDynamicQueryObjectRegister(BeanDefinitionRegistry registry) {
		this.registry = registry;
	}

	public FileScanBasicDynamicQueryObjectRegister(ApplicationContext applicationContext) {
		this.registry = SpringUtils.getBeanDefinitionRegistry(applicationContext);
	}

	public void setDatabase(DataBase database) {
		this.database = database;
	}


	@Override
	public boolean registerQueryBeans() {
		logger.info("start to register dao bean ....");
		Map<String, ResourceAdapter<?>> sqlfiles = sqlFileScanner.scanMatchSqlFiles(database.getName());
		boolean scaned = false;
		for(Entry<String, ResourceAdapter<?>> f: sqlfiles.entrySet()){
			String className = f.getKey();
			if(NamespacePropertiesManager.GLOBAL_NS_KEY.equalsIgnoreCase(className)){
				continue;
			}
			if(registry.containsBeanDefinition(className)){
				continue;
			}
			final Class<?> interfaceClass = ReflectUtils.loadClass(className);
			BeanDefinition beandef = BeanDefinitionBuilder.rootBeanDefinition(JDKDynamicProxyCreator.class)
								.addConstructorArgValue(interfaceClass)
								.addConstructorArgValue(methodCache)
								.addPropertyValue("sqlFile", f.getValue())
								.setScope(BeanDefinition.SCOPE_SINGLETON)
//								.setRole(BeanDefinition.ROLE_APPLICATION)
								.getBeanDefinition();
			registry.registerBeanDefinition(className, beandef);
			logger.info("register dao bean: {} -> {}", className, f.getValue().getFile());
			if(!scaned){
				scaned = true;
			}
		}
		return scaned;
		
	}
}