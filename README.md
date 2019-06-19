## 技术图谱

### 原则

入选技术、库、方法都是已经被大规模或者正在被大规模使用。这也是与所有 awesome 最大的区别。

### 备注

1. 所有技术，如果有官方文档，官方文档都是第一手资料，不再单独列出。
2. 由于内容 庞大，无法用思维导图输出，部分子项目考虑采用思维导图。

### 每一个技术领域

1. 预备知识
2. 主题
3. 学习方法建议：学习优先级，难易程度
4. 必读书目
5. 可选书目
6. 开源代码
7. 最佳实践
8. 附录



欢迎 PR



### 基础

数据结构

算法

### 前端

html

css

javascript

### 系统

#### CPU

####内存

####磁盘

####Linux 操作系统

内核

* 内存：虚拟分页、分页分段、内存调度算法

* 进程线程：数据结构、调度算法

* 网络：socket：tcp/ip,  epoll

* 文件系统：

Shell

####计算机网络

tcp/ip 协议

http 协议：http1.1、http2、https  参考书目：《图解 HTTP》，http1.1 规范，http2.0 规范

网络协议：arp，dns，route，icmp，ospf, vlan, vxlan，dns，dhcp，

网关：

nginx： <https://github.com/fcambus/nginx-resources> 参考《深入理解 Nginx》陶辉

openresty：nginx + lua

haproxy（源码）

memcached : https://blog.csdn.net/luotuo44/article/list/1?

libevent : https://blog.csdn.net/luotuo44/article/list/1?


《Tcp/Ip详解》《Wireshark》

IO模型：同步、异步、阻塞、非阻塞、事件驱动

### 语言

#### Go

#### python

<https://github.com/vinta/awesome-python>

#### C++

<https://github.com/fffaraz/awesome-cpp>

#### C

<https://github.com/kozross/awesome-c>

#### Java

参考 java 文件夹

<https://github.com/akullpp/awesome-java>

### 架构

架构演变：单体，SOA，分布式、微服务

微服务：ServiceMesh(istio)   参考书目《微服务设计》

Serverless：

DDD：CQRS。参考书目《领域驱动设计》 <https://github.com/wkjagt/awesome-ddd>

Actor模式

响应式编程：Reactor

敏捷：参考书目《敏捷革命》

设计模式：设计原则（单一职责、里氏替换、依赖倒置、接口隔离、迪米特法则、开闭原则）。常用设计模式（不止 23 种）、反模式场景。参考书目《设计模式之禅》秦小波 《大话设计模式》《Head First设计模式》

https://www.cnblogs.com/yulinfeng/category/877773.html
https://blog.csdn.net/uftjtt/article/category/7477267

### 系统架构

### 通用组件

正则表达式：

RPC：thrift，ProtocalBuffer  https://github.com/grpc/grpc-java

消息队列：RabbitMq，Kafka

字符编码：

国际化：

### 分布式系统

基础理论：BASE，CAP，拜占庭问题

一致性算法：2PC（及改进），3PC，poaxs，zookeeper，raft，最终一致性(gossip)

分布式事务：TCC(Try/Cancel/Commit)柔性事务

一致性哈希：

#### 实战

唯一ID生成



#### 参数实现

zookeeper，etcd

#### 必读书目

 《从 Paxos 到 Zookeeper：分布式一致性原理与实践》倪超



### 项目管理

Code Review

代码规范：参考《代码整洁之道》、《编写可读代码的艺术》

数据库规范

日志规范

文档：swagger

开发环境：idea，virtualbox，vagrant

API规范 : <https://github.com/Kikobeats/awesome-api>



参考

https://github.com/narkoz/guides

<https://github.com/RichardLitt/awesome-styleguides>



### 大数据

架构图

组件

### 存储

### 性能调优

火焰图：Go，Java，NodeJS

systemTap：系统

perf：系统

Arthas ： Java

### 运维

###测试

黑盒测试

白盒测试

契约测试

单元测试

冒烟测试

测试指标

集成测试

端到端测试

性能测试：

Jmeter

### 安全

### 云计算

#### IaaS：openstack

存储

计算：[Nova](https://github.com/openstack/nova)（KVM）

网络：[Neutron](https://github.com/openstack/neutron)，[openvswitch](  [onos](https://github.com/opennetworkinglab/onos)

### Pass

标准与组织

k8s:

docker：参考书目 《Docker 源码分析》



###人工智能

#### 框架

tensorflow

pytorch

#### GPU

购买指南

基本知识

核心实现库：cuda

####机器学习

算法

####深度学习

领域

CV

NLP



<https://github.com/ChristosChristofidis/awesome-deep-learning>

<https://github.com/guillaume-chevalier/awesome-deep-learning-resources>

<https://github.com/tigerneil/awesome-deep-rl>

<https://github.com/josephmisiti/awesome-machine-learning>

<https://github.com/ChristosChristofidis/awesome-deep-learning>





### 区块链

比特币：挖矿、闪电网络、侧链、主链、分叉 

零知识证明

工作量证明

哈希算法、Merkle树、公钥密码算法、共识算法、拜占庭问题与算法、消息认证码与数字签名

POS

DPOS

ICO

IEO

IFO

IPFS

Bitcoin：https://github.com/igorbarinov/awesome-bitcoin#readme

Ethereum：https://github.com/Tom2718/Awesome-Ethereum#readme

EOS：https://github.com/DanailMinchev/awesome-eosio#readme

参考书目《区块链原理、设计与应用》杨宝华、陈昌

TODO

<https://github.com/kasketis/awesome-cryptocurrencies>



### 参考

https://github.com/xingshaocheng/architect-awesome

https://github.com/rootsongjc/awesome-cloud-native

https://github.com/sindresorhus/awesome 非常全，让人望而生畏

https://github.com/bayandin/awesome-awesomeness

<https://github.com/oyvindrobertsen/awesome-awesome>

<https://github.com/erichs/awesome-awesome> 

待定参考书目

《架构即未来》

 《深入分析Java Web技术内幕》

《架构整洁之道》

《聊聊架构》
