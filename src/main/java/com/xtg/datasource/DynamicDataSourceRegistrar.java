package com.xtg.datasource;

import org.springframework.beans.MutablePropertyValues;
import org.springframework.beans.PropertyValues;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.jdbc.DataSourceBuilder;
import org.springframework.boot.autoconfigure.jdbc.DataSourceProperties;
import org.springframework.boot.bind.RelaxedDataBinder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.convert.ConversionService;
import org.springframework.core.convert.support.DefaultConversionService;

import javax.sql.DataSource;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Configuration
public class DynamicDataSourceRegistrar {

    @Autowired(required = false)
    ConversionService conversionService;

    @Bean
    public DynamicDataSourceProperties dynamicDataSourceProperties(){
        return new DynamicDataSourceProperties();
    }

    @Bean
    public DynamicDataSourceDefaultAllProperties defaultAllProperties(){
        return new DynamicDataSourceDefaultAllProperties();
    }

    @Bean
    public DynamicDataSourceSlaveAllProperties slaveAllProperties(){
        return new DynamicDataSourceSlaveAllProperties();
    }

    @Bean
    public DynamicDataSourceAspect dynamicDataSourceAspect(){
        return new DynamicDataSourceAspect();
    }

    public Map<String, String> getDefaultAllProperties(){
        return defaultAllProperties().getPropertiesMap();
    }

    public Map<String, Map<String, String>> getSlaveAllProperties(){
        return slaveAllProperties().getPropertiesMap();
    }

    public DataSource generateDefaultDataSource() {
        DynamicDataSourceProperties dynamicDataSourceProperties = dynamicDataSourceProperties();
        return buildDataSource(dynamicDataSourceProperties.getDefaultDataSourceProperties(),
                getDefaultAllProperties());
    }

    public Map<Object, Object> generateSlaveDataSourceMap(){
        Map<Object, Object> slaveDataSourceMap = new HashMap<>();
        Map<String, Map<String, String>> slaveAllProperties = getSlaveAllProperties();
        List<DataSourceProperties> dataSourcePropertiesList =
                dynamicDataSourceProperties().getSlaveDataSourceProperties();
        if(dataSourcePropertiesList != null && dataSourcePropertiesList.size() > 0){
            for(DataSourceProperties dataSourceProperties : dataSourcePropertiesList){
                String name = dataSourceProperties.getName();
                DataSource dataSource = buildDataSource(dataSourceProperties, slaveAllProperties.get(name));
                slaveDataSourceMap.put(name, dataSource);
            }
        }
        return slaveDataSourceMap;
    }

    @Bean
    public DataSource generateDynamicDataSource(){
        DynamicDataSource dynamicDataSource = new DynamicDataSource();
        dynamicDataSource.setDefaultTargetDataSource(generateDefaultDataSource());
        dynamicDataSource.setTargetDataSources(generateSlaveDataSourceMap());
        return dynamicDataSource;
    }

    public DataSource buildDataSource(DataSourceProperties properties, Map<String, String> valueMap){
        DataSourceBuilder factory = DataSourceBuilder
                .create(properties.getClassLoader())
                .driverClassName(properties.getDriverClassName())
                .url(properties.getUrl()).username(properties.getUsername())
                .password(properties.getPassword());
        if (properties.getType() != null) {
            factory.type(properties.getType());
        }
        DataSource dataSource = factory.build();
        RelaxedDataBinder dataBinder = new RelaxedDataBinder(dataSource);
        dataBinder.setConversionService(conversionService == null ? new DefaultConversionService() : conversionService);
        dataBinder.setIgnoreNestedProperties(false);
        dataBinder.setIgnoreInvalidFields(false);
        dataBinder.setIgnoreUnknownFields(true);
        PropertyValues values = new MutablePropertyValues(valueMap);
        dataBinder.bind(values);
        return dataSource;
    }

}
