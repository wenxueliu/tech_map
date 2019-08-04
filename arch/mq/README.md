

### 标准

JMS

AMQP

OpenMessaging


### 实现技术

网络通信

序列化

一致性协议

分布式事务

异步编程模型

数据压缩

内存管理

文件与高性能 IO

高可用分布式系统

缓存策略


### 应用场景

日志采集

收集监控

服务解耦

连接流计算任务

消息驱动任务

蓄洪、削峰

ETL: 异步数据库间数据同步

IoT：连接海量 IoT 设备

### 消息队列

Kafka

RabbitMQ

RocketMQ

### 关键模型

#### 队列模型
* 队列
* 生产者
* 消费者

#### 发布订阅模型
* 主题
* 发布者
* 订阅者

#### 消息分片
* 分区
* 队列

#### 消费
* 消费位置
* 消费分组
* 消费幂等

#### 服务质量
* At most onece
* At least once
* Exactly once

### 关键点

1. 选型：当前业务需要什么样的消息队列？
2. 如何保证消息不丢失？
2. 如何处理消息重复问题？
4. 消息积压了如何处理？
3. 如何做到水平扩展


### 参考

https://rocketmq.apache.org/docs/quick-start/
https://rocketmq.cloud/zh-cn/
https://kafka.apache.org/documentation/
https://www.rabbitmq.com/documentation.html/

