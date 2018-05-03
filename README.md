[TOC]

# uw-log-es

#### 项目简介

Elasticsearch(下文简称es)是一个基于Lucene的搜索服务器,其强大的索引功能,自动支持分片、压缩、负载均衡、自动数据恢复,复制功能,可靠的集群功能,基本上是开箱即用。uw-log-es的出现,就是利用es来替代传统的MySql数据库。随着业务日志量的增加,传统的MySql记录重要的业务日志基本上增查性能都会出现严重的耗时,复杂的查询基本上没法使用。比如,uw-task框架的log_interface表,酒店组的Runner基本上每一天有5~7KW的数据写入,表的查询功能基本废了。现在uw-task直接在应用端使用REST API向es发起写日志请求,而且还可以批量的写,利用es的性能,不但查询快,写的速度也很快。定时任务列表和队列任务列表查询也明显快多了。
    
#### 基本使用

uw-log-es原理很简单,因为es所有操作都可以用REST API的形式表达,uw-log-es就是利用uw-httpclient发起REST API请求操作es进行读写基本操作的封装。
uw-log-es提供了一个核心业务的Spring Bean ---- uw.log.es.LogClient,在你的项目中直接注入LogClient就可以使用了。

##### 初始化注册日志对象
笔者认为使用Spring CommandLineRunner Bean是一个不错的初始化地方。当然,根据你的应用启动方式,你也可以把它放在main方法内,甚至任一依赖LogClient的Spring Bean Construct内。
```java

/**
* Spring boot Configuration
*/
public class ApplicationOrConfig {
    @Bean
    public CommandLineRunner initLogClient(final LogClient logClient) {
        return args -> logClient.regLogObject(MscLoginLog.class);
    }
}

/**
* 演示Controller
*/
@RestController
public class LogDemoController {
    @Autowired
    public LogDemoController(final LogClient logClient){
        logClient.regLogObject(MyLog.Class);
    }
}
```
##### 日志写入
在调用logClient.log 或者 logClient.bulkLog之间必须对日志对象进行注册,否者无法进行日志的读写操作! log方法为写单条记录,bulkLog可以批量写日志,适合多条日志记录的业务场合。

##### 日志查询
在构建查询前,先选择你熟悉的查询方式: uw-log-es目前支持 1: 简单查询日志; 2: DSL(Domain Specific Language)查询; 3: SQL(Structured Query Language)查询;  
前两种方式要求使用者必须注册被查询的日志对象,SQL查询时(select * from table)table就是日志在ES中的索引,同时日志对象中的字段就是fields,其中简单查询效率最高,适合单条
记录查询,后面两种适合复杂查询。复杂的聚合查询时,建议使用DSL查询,能提高效率(ES内部有优化)。

#### 项目引入

```xml
<dependency>
    <groupId>com.umtone</groupId>
    <artifactId>uw-log-es</artifactId>
    <version>${project.version}</version>
</dependency>
```

#### 常见问题
1.Q: 日志没有被记录到es?

A: 是否在uw-log-es初始化时调用LogClient.regLogObject方法注册日志对象

2.Q: uw-log-es写日志失败401?

A: uw-log-es在es服务端配置了Http Basic验证,需要配置用户名和密码。