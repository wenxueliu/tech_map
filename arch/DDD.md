

DDD

## 本质

解决业务复杂度，不能降低质量复杂度


前提

业务较复杂，典型特征说就是有很多专业术语，流程有很多套，根据不同的情况之下不同的流程

## 战略设计


## 战术设计




## 概念

实体：
值对象：

## 分层

Uer Interface
Application
Domain
Infrastructure

1. 上层依赖下层
2. Application和Domain与框架无关


## 源码结构

application
    dto : 参数传输对象
    service: 粗粒度的服务接口，依赖infrastureture对domain的编排
    util: 工具类
domain
    bc1：某个业务的界限上下文(Bound Context)
        event：不同界限上下文的交互，建议采用基于事件的方式。也是可以是服务调用的方式等。
            handler：处理其他界限上下文发送的事件
            producer：发送事件
        exception：具体业务的异常
        model： 充血模型
        repository：
        service
        util
facade：没有业务规则，只是完成数据的转换
    rest：对外暴露的控制器接口。 对外部数据进行处理之后转换为application中的dto， 并调用application层提供的服务。
    ws：
infrastureture
    datasource
    graphql
    jpa
    dao
    logging
    repository

问题

1、繁琐的数据转换：Model Mapper
2、事务问题：
