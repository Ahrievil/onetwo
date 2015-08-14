package org.onetwo.common.spring.sql;

import org.onetwo.common.db.filequery.SimpleSqlFileScanner;
import org.onetwo.common.exception.BaseException;
import org.onetwo.common.spring.utils.SpringResourceAdapterImpl;
import org.onetwo.common.utils.propconf.ResourceAdapter;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.core.io.support.ResourcePatternResolver;

public class SpringBasedSqlFileScanner extends SimpleSqlFileScanner {
	
	public SpringBasedSqlFileScanner(ClassLoader classLoader) {
		super(classLoader);
	}

	@Override
	public ResourceAdapter<?>[] scanMatchSqlFiles(){
		ResourcePatternResolver resourcePatternResolver = new PathMatchingResourcePatternResolver();
		
		String locationPattern = ResourcePatternResolver.CLASSPATH_ALL_URL_PREFIX + dir;
		String sqldirPath = locationPattern+"/**/*"+postfix;
		
		ResourceAdapter<?>[] allSqlFiles = null;
		try {
			Resource[] sqlfileArray = resourcePatternResolver.getResources(sqldirPath);
//			if(StringUtils.isNotBlank(conf.getOverrideDir())){
//				sqldirPath = locationPattern+"/"+conf.getOverrideDir()+"/**/*"+conf.getPostfix();
//				logger.info("scan database dialect dir : " + sqldirPath);
//				Resource[] dbsqlfiles = resourcePatternResolver.getResources(sqldirPath);
//				if(!LangUtils.isEmpty(dbsqlfiles)){
//					sqlfileArray = (Resource[]) ArrayUtils.addAll(sqlfileArray, dbsqlfiles);
//				}
//			}
			allSqlFiles = new ResourceAdapter[sqlfileArray.length];
			int index = 0;
			for(Resource rs : sqlfileArray){
				allSqlFiles[index++] = new SpringResourceAdapterImpl(rs);
			}
		} catch (Exception e) {
			throw new BaseException("scan sql file error: " + e.getMessage());
		}
		
		return allSqlFiles;
	}
}
