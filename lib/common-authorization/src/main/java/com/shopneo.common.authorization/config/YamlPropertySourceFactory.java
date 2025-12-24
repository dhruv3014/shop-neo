package com.shopneo.common.authorization.config;


import org.springframework.beans.factory.config.YamlPropertiesFactoryBean;
import org.springframework.core.env.PropertiesPropertySource;
import org.springframework.core.env.PropertySource;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.EncodedResource;
import org.springframework.core.io.support.PropertySourceFactory;

import java.util.Objects;
import java.util.Properties;

public class YamlPropertySourceFactory implements PropertySourceFactory {
  public PropertySource<?> createPropertySource(final String name, final EncodedResource encodedResource) {
    YamlPropertiesFactoryBean factory = new YamlPropertiesFactoryBean();
    factory.setResources(new Resource[]{encodedResource.getResource()});
    Properties properties = factory.getObject();
    return new PropertiesPropertySource((String) Objects.requireNonNull(encodedResource.getResource().getFilename()), (Properties)Objects.requireNonNull(properties));
  }
}