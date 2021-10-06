

## 目标

1、降本增效

2、满足业务

## 软件设计

高内聚低耦合

### 正交四原则

1、消除重复

2、缩小依赖范围

3、分离变化

4、向着稳定的方向演进

### 设计原则

1. 单一职责原则 (Single Responsibility Principle)
2. 开放-关闭原则 (Open-Closed Principle)
3. 里氏替换原则 (Liskov Substitution Principle)
4. 依赖倒转原则 (Dependence Inversion Principle)
5. 接口隔离原则 (Interface Segregation Principle)
6. 迪米特法则（Law Of Demeter）
7. 组合/聚合复用原则 (Composite/Aggregate Reuse Principle)
8. KISS原则( Keep it simple, stupid)
9. YAGNI原则(You Ain’t Gonna Need It)
10. DRP 原则(Don’t Repeat Yourself)

## 架构设计

### 范围

功能属性

质量属性：可靠性、可用性、安全性、可扩展性、性能、伸缩性

生命周期：设计、开发、测试、运维、运营、兼容、演进、可调试性

## 架构思想

领域设计（DDD)

### 方法论

4R 分析法

### 架构原则

2. 麻省理工方法与新泽西方法(MIT Approach vs. New Jersey Approach)

“The Rise of ‘Worse is Better”对比了以 LISP 系统为代表的麻省理工方法和以 Unix/C为代表的新泽西(贝尔实验室)方法。
Gabriel 发现相比于 LISP/CLOS 系统完美的设计，Unix/C只是一味追求实现简单，但事实却证明 Unix/C 像终极计算机病毒那样快速蔓延，奠定了今天计算机系统的基础。



### 架构思维

1、拆分法则

2、叠加法则



### 架构视图

#### 用例视图

上下文模型

用例图

#### 逻辑视图

逻辑模型

技术模型

领域视图

### 开发视图

代码模型

构建模型

#### 部署视图

部署模型

#### 运行视图



#### 扩展视图

扩展视图仍然从 4+1的基础上，从另外一个维度来描述架构



### 架构模式

分布式架构：微服务架构

演进式架构：

DCI 架构

六边形架构

整洁架构

洋葱架构

微服务架构

无服务架构

文档化设计

代码可读性设计



API 设计

1、容错

2、兼容性

3、复用

组件化设计



### 架构元素

CDN：

均衡均衡：四层、七层、客户端负载均衡

缓存：缓存穿透等问题

降级

限流

熔断

<https://github.com/DovAmir/awesome-design-patterns>

#### 实战

秒杀系统设计：https://github.com/ggj2010/SecKill，

参考书目

《企业 IT 架构转型之道》钟华

《大型网站技术架构》

[互联网公司技术架构](https://github.com/davideuler/architecture.of.internet-product)

https://github.com/hollischuang/Architecture-Evolution

https://github.com/donnemartin/system-design-primer/blob/master/README-zh-Hans.md、



## 附录



故障：

不符合服务的运营标准且引发了或者可能引发服务中断或服务质量下降的事件

问题：

造成一个或多个故障的不明起因
