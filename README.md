### 一、使用方法

项目地址：[https://github.com/xtg20121013/dynamicDataSource](https://github.com/xtg20121013/dynamicDataSource)

代码很简单，在test下有个简单的web demo，可以参考。

##### 1. 在配置文件applicaiton.yml中加入数据源配置

a. 默认数据源：同spring默认配置，例如：

	spring:
	  mvc:
	    static-path-pattern: /static/**
	  datasource:
	    url: jdbc:mysql://localhost/db_master
	    username: root
	    password: 78451200
	    driver-class-name: com.mysql.jdbc.Driver
	    max-idle: 2

b. 其他数据源：可配置多个，务必指定name，需在项目中指定，db.datasources下其他配置同spring默认，例如：

	db:
	  datasources:
	    - name: s1
	      url: jdbc:mysql://localhost/db_slave
	      username: root
	      password: 78451200
	      driver-class-name: com.mysql.jdbc.Driver
	      max-idle: 3
	    - name: s2
	      url: jdbc:mysql://localhost/db_slave
	      username: root
	      password: 78451200
	      driver-class-name: com.mysql.jdbc.Driver
	      max-idle: 4


##### 2. 引入dynamicDataSource配置注册类
在@Configuration标注的配置类加上注解@import(DynamicDataSourceRegistrar.class)
##### 3. 在相关使用的地方加上注解@TargetDataSource
如@Transactional的用法相似，将@TargetDataSource("s1")标注在对应的类或者方法上即可指定相关方法内的所有同线程的db查询走指定的datasource
### 二、原理
1. 读取配置文件将配置中的所有配置的datasource（从库必填name字段）注册到routingDataSource的map中
2. 仅影响同一线程，通过AOP（在标注的方法前将所选择的datasource name存在ThreadLocal中，在方法结束后重置ThreadLocal的datasource name）
3. 通过routingDataSource替换原DataSource，重写getConnection方法，在该方法内根据datasource name来分发找到对应的datasource获取connection返回

### 三、适用场景
1. 同项目内，多个业务（可能基于历史原因，不太适合应用拆分）涉及多个不同DB，需要在项目分发
2. 个别耗时只读服务，希望落到从库中，这时建议标注在该服务方法最上层，保证同一线程下的该服务内的全部数据库查询都落在同一个db中，（不标注在下层dao，因为可以让其他服务调用时依然可以走主库）
3. 可做读写分离，将dao层的读方法标注走从库（注意在qps较高时，主从同步延迟较高可能导致的数据不一致，延迟相关建议查阅[http://www.cnblogs.com/olinux/p/6085405.html](http://www.cnblogs.com/olinux/p/6085405.html)
### 四、弊端
1. 必须在transactional事务之前调用，事务中无法切换db，因为spring事务中的connection是从事先预存的ThreadLocal中取得
2. 基于AOP，不适用于protect、private的方法(class.getMethod方法只能取到public的)，不适用于同类中的方法调用

ps:介绍blog [https://blog.52xtg.com/article/13](https://blog.52xtg.com/article/13)
***
欢迎来我的个人博客逛逛: [https://blog.52xtg.com](https://blog.52xtg.com)