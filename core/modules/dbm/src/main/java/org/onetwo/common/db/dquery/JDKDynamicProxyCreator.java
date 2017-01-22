package org.onetwo.common.db.dquery;

import java.lang.reflect.Method;

import javax.sql.DataSource;

import org.apache.commons.lang3.StringUtils;
import org.onetwo.common.db.dquery.annotation.QueryRepository;
import org.onetwo.common.db.filequery.JFishNamedFileQueryInfo;
import org.onetwo.common.db.filequery.JFishNamedSqlFileManager;
import org.onetwo.common.db.filequery.PropertiesNamespaceInfo;
import org.onetwo.common.db.filequery.QueryProvideManager;
import org.onetwo.common.log.JFishLoggerFactory;
import org.onetwo.common.propconf.ResourceAdapter;
import org.onetwo.common.spring.SpringUtils;
import org.onetwo.dbm.exception.DbmException;
import org.onetwo.dbm.exception.FileNamedQueryException;
import org.onetwo.dbm.jdbc.NamedJdbcTemplate;
import org.onetwo.dbm.support.Dbms;
import org.slf4j.Logger;
import org.springframework.beans.factory.BeanNameAware;
import org.springframework.beans.factory.FactoryBean;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.util.Assert;

import com.google.common.cache.LoadingCache;

public class JDKDynamicProxyCreator implements InitializingBean, ApplicationContextAware, FactoryBean<Object>, BeanNameAware {

	protected final Logger logger = JFishLoggerFactory.getLogger(this.getClass());
	
	private LoadingCache<Method, DynamicMethod> methodCache;
	private ApplicationContext applicationContext;
	protected Class<?> interfaceClass;
	private Object targetObject;
	private ResourceAdapter<?> sqlFile;

	private NamedJdbcTemplate namedJdbcTemplate;
//	private JFishNamedSqlFileManager namedSqlFileManager;
//	private QueryProvideManager queryProvideManager;
	private String beanName;
	
	public JDKDynamicProxyCreator(Class<?> interfaceClass, LoadingCache<Method, DynamicMethod> methodCache) {
		super();
		this.interfaceClass = interfaceClass;
		this.methodCache = methodCache;
	}

	@Override
	public void afterPropertiesSet() throws Exception {
		/*if(namedSqlFileManager==null){
			namedSqlFileManager = SpringUtils.getBean(applicationContext, JFishNamedSqlFileManager.class);
		}*/
		if(namedJdbcTemplate==null){
			namedJdbcTemplate = SpringUtils.getBean(applicationContext, NamedJdbcTemplate.class);
		}
		
		QueryProvideManager queryProvideManager;
		QueryRepository queryProvider = this.interfaceClass.getAnnotation(QueryRepository.class);
		if(queryProvider==null){
			queryProvideManager = SpringUtils.getBean(applicationContext, QueryProvideManager.class);
		}else{
			if(StringUtils.isNotBlank(queryProvider.provideManager())){
				queryProvideManager = SpringUtils.getBean(applicationContext, queryProvider.provideManager());
			}else if(StringUtils.isNotBlank(queryProvider.dataSource())){
				DataSource dataSource = SpringUtils.getBean(applicationContext, queryProvider.dataSource());
				if(dataSource==null){
					throw new DbmException("no dataSource found: " + queryProvider.dataSource());
				}
				queryProvideManager = Dbms.obtainBaseEntityManager(dataSource);
			}else{
				queryProvideManager = SpringUtils.getBean(applicationContext, QueryProvideManager.class);
			}
		}
		if(queryProvideManager==null){
			throw new FileNamedQueryException("no QueryProvideManager found!");
		}
		
		JFishNamedSqlFileManager namedSqlFileManager = (JFishNamedSqlFileManager)queryProvideManager.getFileNamedQueryManager().getNamespacePropertiesManager();
		Assert.notNull(namedSqlFileManager);
		Assert.notNull(namedJdbcTemplate);
		
		ResourceAdapter<?> sqlFile = getSqlFile(queryProvideManager.getDataSource());
		Assert.notNull(sqlFile);

		logger.info("initialize dynamic query proxy[{}] for : {}", beanName, sqlFile);
		PropertiesNamespaceInfo<JFishNamedFileQueryInfo> info = namedSqlFileManager.buildSqlFile(sqlFile);
//		interfaceClass = ReflectUtils.loadClass(info.getNamespace());
		if(!interfaceClass.getName().equals(info.getNamespace())){
			throw new FileNamedQueryException("namespace error:  interface->" + interfaceClass+", namespace->"+info.getNamespace());
		}
		targetObject = new DynamicQueryHandler(queryProvideManager, methodCache, namedJdbcTemplate, interfaceClass).getQueryObject();
	}
	
	protected ResourceAdapter<?> getSqlFile(DataSource dataSource){
		return sqlFile;
	}

	@Override
	public Object getObject() throws Exception {
		return targetObject;
	}

	@Override
	public Class<?> getObjectType() {
		return interfaceClass;
	}

	@Override
	public boolean isSingleton() {
		return true;
	}

	public void setApplicationContext(ApplicationContext applicationContext) {
		this.applicationContext = applicationContext;
	}

	public void setSqlFile(ResourceAdapter<?> sqlFile) {
		this.sqlFile = sqlFile;
	}

	public void setNamedJdbcTemplate(NamedJdbcTemplate namedJdbcTemplate) {
		this.namedJdbcTemplate = namedJdbcTemplate;
	}

	@Override
	public void setBeanName(String name) {
		this.beanName = name;
	}

	/*public void setQueryProvideManager(QueryProvideManager queryProvideManager) {
		this.queryProvideManager = queryProvideManager;
	}*/
	
}
