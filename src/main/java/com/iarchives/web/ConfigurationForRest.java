package com.iarchives.web;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.data.rest.core.config.RepositoryRestConfiguration;
import org.springframework.data.rest.webmvc.config.RepositoryRestMvcConfiguration;

import com.iarchives.web.domain.Container;
import com.iarchives.web.domain.Project;

@Configuration
@PropertySource({"classpath:application.properties"})
public class ConfigurationForRest extends RepositoryRestMvcConfiguration {

	@Value("${spring.data.rest.baseUri}")
	private String baseUri;

    @Override
    protected void configureRepositoryRestConfiguration(RepositoryRestConfiguration config) {
        config.exposeIdsFor(Project.class, Container.class);
        config.setBaseUri(baseUri);
        config.setReturnBodyOnCreate(true);
    }
}
