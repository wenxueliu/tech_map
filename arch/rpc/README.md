


## 概念

 远程服务调用是指位于互不重合的内存地址空间中的两个程序，在语言层面上，以同步的方式使用带宽有限的信道来传输程序控制信息

## 进程间通信

IPC 是低层次的或系统层次的特征

* 管道
* 信号量
* 信号
* 消息队列
* 共享内存
* 本地套接字接口

## 分布式计算的 8 宗罪

RPC 应该是一种高层次的或者说语言层次的特征

1、网络是可靠的（The network is reliable）
2、延迟是不存在的（Latency is zero）
3、带宽是无限的（Brandwidth is infiniite）
4、网络是安全的（The network is secure）
5、拓扑结构是一成不变的（Topology doesn‘t change）
6、总会有一个管理员（There is one administrator）
7、不必要考虑传输成本（Transport cost is zero）
8、网络都是同质化的（The network is homogeneous）

参考自[Fallacies of distributeed computing](https://en.wikipedia.org/wiki/Fallacies_of_distributed_computing)

## 三个基本问题

如何表示数据：序列化反序列化
如何传输数据：应用协议
如何表示方法: IDL

## 三个阶段

1、操作系统绑定：DEC/RPC、ONC RPC
2、设计复杂：CORBA
3、性能底下：W3C Web Service

因此，简单、普适、高性能是RPC追求的目标

## 百花齐放的 RPC

RMI
Thrift
Dubbo
gRPC
Motan1/2
Finagle
brpc
.NET Remotiing
Avro
JSON-RPC
Tars

RPC在解决基本RPC问题之后，朝插件化和解决更加高层次的问题（服务发现，负载均衡等）
的方向发展

## RPC 技术栈

序列化与反序列化


协议设计
JSON
XML
二进制
私有协议


## RPC 不是透明代理

[A Critique of The Remote Procedure CallParadigm](https://www.cs.vu.nl/~ast/Publications/Papers/euteco-1988.pdf)
提出把本地调用与远程调用当作同样的调用来处理，这是犯了方向性的错误，把系统间的调用透明化，反而会增加程序员工作的复杂度。

* 两个进程通信，谁作为服务端，谁作为客户端？
* 怎样进行异常处理？
* 异常该如何让调用者获知？
* 服务端出现多线程竞争之后怎么办？
* 如何提高网络利用的效率？
* 连接是否可被多个请求复用以减少开销？
* 是否支持多播？
* 参数、返回值如何表示？
* 应该有怎样的字节序？
* 如何保证网络的可靠性？
* 调用期间某个链接忽然断开了怎么办？
* 发送的请求服务端收不到回复怎么办？

## 参考

凤凰架构：构建可靠的分布式架构
