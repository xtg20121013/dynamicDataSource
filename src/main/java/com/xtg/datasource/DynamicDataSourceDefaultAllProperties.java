package com.xtg.datasource;

import org.springframework.boot.context.properties.ConfigurationProperties;

import java.util.HashMap;
import java.util.Map;

@ConfigurationProperties(DynamicDataSourceProperties.DEFAULT_PREFIX)
public class DynamicDataSourceDefaultAllProperties {

    private Map<String, String> datasource = new HashMap<>();

    public Map<String, String> getDatasource() {
        return datasource;
    }

    public Map<String, String> getPropertiesMap() {
        return datasource;
    }

}
