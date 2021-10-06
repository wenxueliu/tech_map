



### 大数据历史

2003 年：google 三驾马车（GFS，Big Table，MapReduce）

2008 年：搜索引擎时代

2010 年：数据仓库时代

2012 年：数据挖掘时代

2016 年：机器学习时代



### 背景

移动互联网出现，数据量每年逐步增长



### 解决了什么问题

1、扩展了提高数据决策的边界。传统BI能依赖的数据非常有限，大数据可以依赖更多的数据，通过数据驱动决策。



### 客户

老板

产品经理

运营人员

工程师

### 需要解决的问题

1、如何存储大数据

2、如何快速存储大数据

3、数据安全



### 现状





### 生态

离线大数据离线计算：

Hadoop

Pig

Hive

大数据实时计算：

Spark

Flink

Spark Streaming

Storm

HBase





### 思考

1、大数据的计算从 Hadoop、Spark、Flink，大数据存储却一直是 HDFS

2、大数据改变了产品开发流程，需求的来源增加了根据数据分析结果。





### 应用场景

1、医学图像识别

2、病例大数据诊疗

3、AI 外语老师

4、大数据风控

5、无人驾驶

6、智能解题

7、舆情监控与分析

8、新零售





### 架构图

https://github.com/bethunebtj/flink_tutorial


### 组件

数据采集

collectd

fluentd

flume

telegraf

数据传输

kafka

rabbitmq

数据存储

[Apache HDFS](http://hadoop.apache.org/)

[Apache HBase](http://hbase.apache.org/)

数据计算

[Apache Beam](https://beam.apache.org/)

[Apache Flink](http://flink.apache.org/)

[Spark](http://spark.apache.org/)

https://blog.csdn.net/tanliqing2010/article/details/81482337

[Spark Streaming](http://spark.apache.org/docs/0.7.3/streaming-programming-guide.html)

数据展示

kibana

grafana

数据分析

[Apache Hive](http://hive.apache.org/)

[Apache Pig](https://pig.apache.org/)

集群调度

[Apache Yarn](https://hortonworks.com/hadoop/yarn/)





#### 学习建议

大数据是一个生态，各个模块掌握核心即可， Spark，Kafka 必选。Hbase 可选。其他会用即可。

#### 必读书目

#### 参考书目





参考

<https://github.com/onurakpolat/awesome-bigdata>

<https://github.com/zenkay/bigdata-ecosystem>
