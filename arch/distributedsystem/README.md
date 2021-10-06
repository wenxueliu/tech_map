
### 定义

在《分布式系统概念与设计》一书中，对分布式系统的定义如下：

分布式系统是一个硬件或软件组件分布在不用的网络计算机上，彼此之间仅仅通过消息传递进行通信和协调的系统。

### 特征

#### 分布性

硬件或软件的空间随意分布，随时间变化，空间分布也可能变化

#### 对等性

系统之家没有主从，完全对等，每个节点都可以提供服务

#### 并发性

多个节点同时发送请求，分布式系统需要并发处理各个请求

#### 缺乏全局时钟

#### 故障总是会发生


### 分布式系统的异常

1. 通信异常

由于通信组件异常（DNS 异常，路由器故障）导致的网络不可用

消息丢失

消息延迟

2. 网络分区

3. 三态

每次请求与响应包括成功、失败与超时三个状态

4. 节点故障

节点宕机或僵死

### 机制

解决分布式系统问题的机制

#### 副本机制

包括数据副本和服务副本



### 理论

BASE

CAP

### 协议

RAFT : https://mp.weixin.qq.com/s/4Da9NEtDzNA70dU6e7CWGw

Paxos

Zab : https://mp.weixin.qq.com/s/5B5lWq8VNfik6RWypkCOEA

Gossip : https://mp.weixin.qq.com/s/vziFrW-DUhcVpHXjS4h_dw



### 实例

#### ID 生成

https://tech.meituan.com/2019/03/07/open-source-project-leaf.html

https://www.jianshu.com/p/bd6b00e5f5ac

https://mp.weixin.qq.com/s/8NsTXexf03wrT0tsW24EHA
