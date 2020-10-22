spring -tips



Spring 的这些技巧你一定要了解下



### 领域对象

所有实体对象放到个 entity 包下（注意不是 domain）。

要分 PO 和 VO

PO 用于与数据库交互

VO 用于展示

VO 与 PO 的转换：继承 Convert 实现 VO 和 PO 的转换，转换用 Spring 自带的 BeanUtils 工具

### lombok

用 lombok 减少 setter，getter 的代码

如果直接继承自 Object 类，用 @Data

如果是基础自非 Object 类，用 @EqualsAndHashCode(callsuper = true)

### 入参要校验 jsr 303 

hibernate-valiation 来校验传入的参数



### 减少 if-else 的方法



1. 抽象出子方法
2. 多态，工厂模式，策略模式，模板模式
3. 



### 异常

统一异常处理

异常要分层



### 使用 Java 8

Java 8 的 lambda，stream，等新的基础设施可以简化代码，增加可读性，提高性能



熟悉常用的第三库

guava，common-io，common-lang



### 了解常用设计模式

工厂模式、策略模式、代理模式、模板模式、建造者模式等等