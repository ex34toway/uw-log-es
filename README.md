[TOC]

# uw-log-es

#### 更新日志
    0.0.5 添加Http Basic认证支持,生产环境直接在es前挂nginx
    0.0.4 注释完善,没有配置直接不写入日志
    0.0.3 失败错误日志记录
    0.0.2 优化批量提交日志方法,使用okio.Buffer提升性能
    0.0.1 项目初始化

#### 项目简介

    Elasticsearch(下文简称es)是一个基于Lucene的搜索服务器,其强大的索引功能,自动支持分片、压缩、负载均衡、自动数据恢复,复制功能,可靠的集群功能,基本
    上是开箱即用。uw-log-es的出现,就是利用es来替代传统的MySql数据库。随着业务日志量的增加,传统的MySql记录重要的业务日志基本上增查性能都会出现严重的
    耗时,复杂的查询基本上没法使用。比如,uw-task框架的log_interface表,酒店组的Runner基本上每一天有5~7KW的数据写入,表的查询功能基本废了。现在uw-task
    直接在应用端使用REST API向es发起写日志请求,而且还可以批量的写,利用es的性能,不但查询快,写的速度也很快。定时任务列表和队列任务列表查询也明显快多了。
    
    
#### 基本使用

    uw-log-es原理很简单,因为es所有操作都可以用REST API的形式表达,uw-log-es就是利用uw-httpclient发起REST API请求操作es进行读写基本操作的
    封装。
    
#### 项目引入

```xml
<dependency>
    <groupId>com.umtone</groupId>
    <artifactId>uw-log-es</artifactId>
    <version>${project.version}</version>
</dependency>
```

uw-log-es提供了一个核心业务的Spring Bean ---- uw.log.es.LogClient,在你的项目中直接注入LogClient就可以使用了。


#### 常见问题
Q: 日志没有被记录到es?
A: 是否在uw-log-es初始化时调用LogClient.regLogObject方法注册日志对象?

Q: uw-log-es写日志失败401?
A: uw-log-es在es服务端配置了Http Basic验证,需要配置用户名和密码。