package com.xtg.datasouece.test;

import com.xtg.datasource.DynamicDataSourceRegistrar;
import com.xtg.datasource.annotation.TargetDataSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Import;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

@RestController
@SpringBootApplication
@Import(DynamicDataSourceRegistrar.class)
public class Application {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @RequestMapping("/*")
    public Map masterdb(){
        int count = jdbcTemplate.update("INSERT INTO `table` (`name`) VALUES (?)", "master");
        Map<String, String> res = new HashMap<>();
        if(count > 0) {
            res.put("saved", "master");
        }
        return res;
    }

    @RequestMapping("/slavedb")
    @TargetDataSource("s1")
    public Map slavedb(){
        int count = jdbcTemplate.update("INSERT INTO `table` (`name`) VALUES (?)", "slave");
        Map<String, String> res = new HashMap<>();
        if(count > 0) {
            res.put("saved", "slave");
        }
        return res;
    }

    public static void main(String[] args) {
        SpringApplication sa = new SpringApplication(Application.class);
        sa.setWebEnvironment(true);
        sa.run(args);
    }
}