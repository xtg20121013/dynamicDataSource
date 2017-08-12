package com.xtg.datasource;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@ConfigurationProperties(DynamicDataSourceProperties.PREFIX)
public class DynamicDataSourceSlaveAllProperties {

    private static final String PROPERTIES_MAP_KEY = "name";

    private List<Map<String, String>> datasources = new ArrayList<>();

    private Map<String, Map<String, String>> propertiesMap = null;

    public List<Map<String, String>> getDatasources() {
        return datasources;
    }

    public Map<String, Map<String, String>> buildPropertiesMap(){
        Map<String, Map<String, String>> propertiesMap = new HashMap<>();
        for(int i = 0; i < datasources.size(); i++){
            Map<String, String> map = datasources.get(i);
            String name = map.get(PROPERTIES_MAP_KEY);
            assert !StringUtils.isEmpty(name);
            propertiesMap.put(map.get(PROPERTIES_MAP_KEY), map);
        }
        return propertiesMap;
    }

    public Map<String, Map<String, String>> getPropertiesMap() {
        if(propertiesMap == null){
            propertiesMap = buildPropertiesMap();
        }
        return propertiesMap;
    }

}
