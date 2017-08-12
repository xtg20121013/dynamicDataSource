package com.xtg.datasource;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.jdbc.DataSourceProperties;
import org.springframework.boot.context.properties.ConfigurationProperties;

import java.util.ArrayList;
import java.util.List;

@ConfigurationProperties(DynamicDataSourceProperties.PREFIX)
public class DynamicDataSourceProperties {

    public static final String PREFIX = "db";

    public static final String DEFAULT_PREFIX = "spring";

    private List<DataSourceProperties> datasources = new ArrayList<>();

    @Autowired
    private DataSourceProperties dataSourceProperties;

    public List<DataSourceProperties> getDatasources() {
        return datasources;
    }

    public List<DataSourceProperties> getSlaveDataSourceProperties() {
        return datasources;
    }

    public DataSourceProperties getDefaultDataSourceProperties() {
        return dataSourceProperties;
    }

}
