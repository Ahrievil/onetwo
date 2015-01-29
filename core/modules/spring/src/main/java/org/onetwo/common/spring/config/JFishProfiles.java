package org.onetwo.common.spring.config;

import org.onetwo.common.spring.SpringUtils;
import org.onetwo.common.utils.propconf.Environment;
import org.springframework.beans.factory.config.PropertyPlaceholderConfigurer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.ImportResource;
import org.springframework.context.annotation.Profile;

@Configuration
@ImportResource({"classpath:webconf/applicationContext-base.xml"})
public class JFishProfiles {
	
		public static final String APP_CONFIG_NAME = "applicationConfig";

        @Configuration
        @Profile(Environment.PRODUCT)
//        @PropertySource(name=APP_CONFIG_NAME, value={"webconf/application.properties", "webconf/application-product.properties"})
        static class ProductConcfig {

                @Bean(name=APP_CONFIG_NAME)
                public static PropertyPlaceholderConfigurer applicationConfig() {
                        return SpringUtils.newApplicationConf("webconf/application.properties", "webconf/application-product.properties");
                }

        }

        @Configuration
        @Profile(Environment.TEST)
//        @PropertySource(name=APP_CONFIG_NAME, value={"webconf/application.properties", "webconf/application-test.properties"})
        static class TestConcfig {

            	@Bean(name=APP_CONFIG_NAME)
                public static PropertyPlaceholderConfigurer applicationConfig() {
                        return SpringUtils.newApplicationConf("webconf/application.properties", "webconf/application-test.properties");
                }
        }

        @Configuration
        @Profile(Environment.TEST_LOCAL)
//        @PropertySource(name=APP_CONFIG_NAME, value={"webconf/application.properties", "webconf/application-test-local.properties"})
        static class TestLocalConcfig {

            	@Bean(name=APP_CONFIG_NAME)
                public static PropertyPlaceholderConfigurer applicationConfig() {
                        return SpringUtils.newApplicationConf("webconf/application.properties", "webconf/application-test-local.properties");
                }

        }

        @Configuration
        @Profile(Environment.DEV)
//        @PropertySource(name=APP_CONFIG_NAME, value={"webconf/application.properties", "webconf/application-dev.properties"})
        static class DevConcfig {

            	@Bean(name=APP_CONFIG_NAME)
                public static PropertyPlaceholderConfigurer applicationConfig() {
                        PropertyPlaceholderConfigurer dev = SpringUtils.newApplicationConf("webconf/application.properties", "webconf/application-dev.properties");
                        return dev;
                }

        }

        @Configuration
        @Profile(Environment.DEV_LOCAL)
//        @PropertySource(name=APP_CONFIG_NAME, value={"webconf/application.properties", "webconf/application-dev-local.properties"})
        static class DevLocalConcfig {

            	@Bean(name=APP_CONFIG_NAME)
                public static PropertyPlaceholderConfigurer applicationConfig() {
                        return SpringUtils.newApplicationConf("webconf/application.properties", "webconf/application-dev-local.properties");
                }

        }
}