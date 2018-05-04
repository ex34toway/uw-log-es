[TOC]

# uw-log-es

Elasticsearch(下文简称es)是一个基于Lucene的搜索服务器,其强大的索引功能,自动支持分片、压缩、负载均衡、自动数据恢复,复制功能,可靠的集群功能,基本上是开箱即用。

#### 项目简介

uw-log-es的主要特性:
1. 使用uw-httpclient做REST构建查询,支持Http Basic 验证;
2. 支持多种方式批量写入,完全自动化构建日志内容;
3. 支持多种方式查询,并映自动射到日志对象;

#### 项目引入

```xml
<dependency>
    <groupId>com.umtone</groupId>
    <artifactId>uw-log-es</artifactId>
    <version>${project.version}</version>
</dependency>
```
#### 项目配置
```yaml
uw:
  log:
    es:
      # 如果不配置 clusters 地址,将不会把日志发送到es服务端
      clusters: http://localhost:9200
      # 如果不配置用户名和密码,将不会有Http Basic验证头
      username: admin
      password: admin

```

#### 基本使用

##### 初始化注册日志对象
```java
/**
 * uw-log-es配置,主要用来注册日志对象
 *
 * @author liliang
 * @since 2018-05-03
 */
@Service
public class MyLoginLogService {
    
    @PostConstruct
    public void initMyLoginLogService(final LogClient logClient) {
        logClient.regLogObject(MscLoginLog.class);
    }
}
```
##### 日志写入
```java
public class DemoWriteLog {
    
    @org.springframework.beans.factory.annotation.Autowired
    private uw.log.es.LogClient logClient;
    
    /**
    * 写单条日志 
    */
    public void log() {
        MscLoginLog loginLog = new MscLoginLog();
        // 写日志...
        logClient.log(loginLog);
    }
    
    /**
    * 批写日志 
    */
    public void bulkLog() {
        List<MscLoginLog> dataLists = new ArrayList<MscLoginLog>();
        // 写日志...
        logClient.bulkLog(loginLog);
    }
}
```

##### 日志查询
在构建查询前,先选择你熟悉的查询方式,uw-log-es目前支持:
1. 简单查询日志:


```java
public class DemoSimpleQuery {
    /**
    * 简单查询,适合按唯一关键字查询的场合,但是也建议带分页,除非能明确知道该关键字对应一条记录。
    * PS: 对于在查询应用中,此方法查询要求先注册日志对象
    */
    public void queryLogBySimpleQuery() {
        // q为查询参数,查询的关键字用:分隔,更多语法请参考
        // @see https://www.elastic.co/guide/en/elasticsearch/reference/current/query-dsl-query-string-query.html#query-string-syntax
        // size,from为分页参数
        List<MscLoginLog> dataList = logClient.simpleQueryLog(MscLoginLog.class,"q=userId:926270&size=1&from=0");
        dataList.size();
    }
}
```
2. DSL(Domain Specific Language)查询:


```java
public class DemoDSLQuery {
    /**
    * DSL查询,注意要带分页from,size参数。
    * PS: 对于在查询应用中,此方法查询要求先注册日志对象
    */
    @Test
    public void testQueryLogByDSLQuery() {
        // DSL适合固定的组合查询(效率高),变参查询建议使用SQL查询
        String dslQuery = "{" +
                "\"query\": {" +
                "\"bool\": {" +
                "\"must\": [" +
                "{\n" +
                "\"term\": {" +
                "\"productType\": 10" +
                "}\n" +
                "}\n" +
                "],\n" +
                "\"must_not\": [ ]," +
                "\"should\": [ ]" +
                "}" +
                "}," +
                "\"from\": 0," +
                "\"size\": 10," +
                "\"sort\": [\"requestDate\"]," +
                "\"aggs\": { }" +
                "}";
        List<MscLoginLog> dataList = logClient.dslQueryLog(MscLoginLog.class,dslQuery);
        dataList.size();
    }
}
```
3. SQL(Structured Query Language)查询:


```java
public class DemoSQLQuery {
    /**
    * SQL查询,注意要带分页
    */
    @Test
    public void testQueryLogBySql() {
        // SQL查询句法,与MySql类似,并且支持部分ES函数功能,适合变参查询,方便拼接。详情文档请参考elasticsearch-sql官网: https://github.com/NLPchina/elasticsearch-sql
        SearchResponse<MscLoginLog> response = logClient.sqlQueryLogSearchResponse(LogInterface.class,
                "select * from "+logClient.getLogObjectIndex(MscLoginLog.class)+" where loginDate > 1524666600000 limit 0,10 ");
        response.getHisResponse();
    }
}
```

##### 日志分页查询
es本身对分页查询支持良好,使用简单查询和DSL查询默认是取10条,可以在es服务端配置。

项目中常见的分页查询是前端的列表查询,可以使用带SearchResponse后缀的方法将数据查出来,然后使用[uw-dao](http://192.168.88.88:10080/uw/uw-dao "数据库操作的类库")提供的DataList工具类进行包装即可,比如
```java
public class DemoPaginationQuery {
    
    /**
    * SQL分页查询示例
    */
    public DataList<MscLoginLog> list(@RequestParam(name = "page", defaultValue = "1") int page,
                                      			@RequestParam(name = "resultNum", defaultValue = "10") int resultNum){
        SearchResponse<MscLoginLog> response = logClient.sqlQueryLogSearchResponse(LogInterface.class,
                "select * from "+logClient.getLogObjectIndex(MscLoginLog.class)+" where loginDate > 1524666600000 limit "+(page-1)*resultNum+","+resultNum);
        // 组装分页参数
        if(response != null && response.getHisResponse() != null) {
            int total = response.getHisResponse().getTotal();
            List<SearchResponse.Hits<TaskRunnerLog>> hitsList = response.getHisResponse().getHits();
            if (!hitsList.isEmpty()) {
                List<TaskRunnerLog> dataList = Lists.newArrayList();
                for (SearchResponse.Hits<TaskRunnerLog> hits : hitsList) {
                    dataList.add(hits.getSource());
                }
                return new DataList<TaskRunnerLog>(dataList, startIndex, resultNum, total);
            }
        }
    }
}
```
##### 学习的es的建议
因为es版本目前迭代非常快。。。不要上百度搜文档了,搜出来的可能解决不了你的问题(因为版本不一致),建议参考[官方文档](https://www.elastic.co/guide/en/elasticsearch/guide/current/index.html "Elasticsearch: The Definitive Guide"),[中文版](https://github.com/elasticsearch-cn/elasticsearch-definitive-guide "Elasticsearch: The Definitive Guide"),中文文档翻译可能与最新版的原文档有出入,但是基本够用。

#### 常见问题
1.Q: 日志没有被记录到es?

A: 是否在uw-log-es初始化时调用LogClient.regLogObject方法注册日志对象

2.Q: uw-log-es写日志失败401?

A: uw-log-es在es服务端配置了Http Basic验证,需要配置用户名和密码。