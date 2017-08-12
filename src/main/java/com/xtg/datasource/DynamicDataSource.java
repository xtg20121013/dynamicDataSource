package com.xtg.datasource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.jdbc.datasource.lookup.AbstractRoutingDataSource;
import org.springframework.util.StringUtils;

public class DynamicDataSource extends AbstractRoutingDataSource{

    private static final Logger LOGGER = LoggerFactory.getLogger(DynamicDataSource.class);

    private final ThreadLocal<String> holder = new ThreadLocal<>();

    public void setDataSourceKey(String name){
        LOGGER.info("[DynamicDataSource] set datasource name {}", name);
        holder.set(name);
    }

    @Override
    protected Object determineCurrentLookupKey() {
        String name = holder.get();
        if(!StringUtils.isEmpty(name)) {
            LOGGER.debug("[DynamicDataSource] use datasource by name {}", name);
        }
        return name;
    }

    public void clearDataSourceKey(){
        LOGGER.info("[DynamicDataSource] clear datasource name");
        holder.set(null);
    }
}
