https://github.com/gdhucoder/DesignPattern



## 继承

优点
1. 代码复用 : 减少代码量

缺点
1. 侵入性 : 拥有父类属性
2. 降低灵活度 : 拥有父类属性
3. 增强耦合性 : 修改父类


## Tips

如果子类不能完整地实现父类的方法，或者父类的某些方法在子类中发生“畸变”，则建议断开
父子关系，采样依赖，聚集，组合等关系替代继承

在子类中调用其他类时务必要使用父类或接口，如果不能使用父类或接口，则说明已经违背
了LSP 原则

只要做到抽象依赖，即使多层依赖传递也无所畏惧

有时候一个方法，放在本类中也可以，放在其他类也没有问题，怎么去衡量呢？坚持这样一个
原则：如果放在本类中，既不增加类间关系，也不对本类增加负面影响，就放在本类

父类调用子类方法的三种方式：
1. 把子类传递到父类的有参构造中，然后调用。
2. 使用反射的方式调用
3. 父类调用子类的静态方法

## 6 大原则

SOLID

Single Responsibility Principle : 一个类的职责：每个类只做一件事
Open Closed Principle : 类间关系：通过扩展来实现新功能而不是修改
Liskov Substitution Principle : 父类和子类的关系：子类必须实现父类的方法
Law of Demeter : 两个类之间关系越简单越好
Interface Segregation Principle(类间关系的程度) : 类间依赖应该尽可能小，少
Dependence Inversion Principle(类间关系的方式) : 通过接口或抽象建立依赖关系

* 抽象约束
* 元数据控制模块行为
* 项目章程非常重要：约定大于配置
* 封装变化

### 单一职责

职责的定义: 应该有且只有一个原因引起变更 There should never be more than one reason for a class to change

什么是类的职责 : 

怎么划分职责 : 没有统一的标准，根据具体情况来决定

好处

1. 降低类的复杂度
2. 可读性提高
3. 可维护性提高
4. 变更风险降低

### 里式替换原则

定义1
```
if for each object o1 of type S there is an object o2 of type T such that for
all programs P defined in term of T, the behavior of P is unchanged when o1 is
substituted for o2 then S is a subtype of of T
```

定义2

```
Functions that use pointer or reference to base classes must be able to use
object of derived class without knowing it.
```

解读

1. 子类必须完全实现父类的方法
2. 子类可以有自己的个性
3. 覆写或实现父类的方法时，输入参数可以放大，输出结果可以缩小。


#### 子类中方法的前置条件必须与超类中被覆写的方法的前置条件相同或更宽松。

不期望的结果
```
class Father {
    public Collection doSth(Map map) {
        System.out.println("Father do sth");
    }
}

class Son extends Father {
    public Collection doSth(HashMap map) {
        System.out.println("Son do sth");
    }
}

public class Client {
    public static void main(String []args) {
        Father f = new Father();
        HashMap map = new HashMap();
        f.doSth(map);
        Father f = new Son();
        HashMap map = new HashMap();
        f.doSth(map);
    }
}
```

期望的结果

```
class Father {
    public Collection doSth(HashMap map) {
        System.out.println("Father do sth");
    }
}

class Son extends Father {
    public Collection doSth(Map map) {
        System.out.println("Son do sth");
    }
}

public class Client {
    public static void main(String []args) {
        Father f = new Father();
        HashMap map = new HashMap();
        f.doSth(map);
        Father f = new Son();
        HashMap map = new HashMap();
        f.doSth(map);
    }
}
```

在实践中往往存在矛盾，把子类当做父类使用，会让子类的个性化特性无法显现，
而直接用子类，又让代码间耦合过重

### 依赖倒置原则

DIP(dependence inversion principle)

High level modules should not depend upon low level modules, both should
depend upon abstraction. Abstraction shoud not depend upon details, Details
should depend on abstraction.

翻译为 Java 语义

1. 模块间的依赖通过抽象发生，实现类不发生直接的依赖关系，其依赖关系通过接口或
者抽象类产生的
2. 接口或抽象不依赖于实现
3. 实现类依赖于接口

即所谓的面向接口设计(ODD)

#### 依赖注入的三种方式


1. 构造函数传递依赖

```
public interface IDriver {
    public void drive();
}

public class Driver implements IDriver {
    ICar car;

    Driver(ICar car) {
        this.car = car;
    }

    public void drive() {
        this.car.run();
    }
}
```

2. Setter 方法传递依赖

```
public interface IDriver {
    public void setCar(ICar car);
    public void drive();
}

public class Driver implements IDriver {
    ICar car;

    public void setCar(ICar car) {
        this.car = car;
    }

    public void drive() {
        this.car.run();
    }
}
```

3. 接口声明依赖

```
public interface IDriver {
    public void drive(ICar car);
}

public class Driver implements IDriver {
    public void drive(ICar car) {
        car.run();
    }
}
```

最佳实践

1. 每个类尽量都有抽象或接口类，或者两者兼而有之
2. 变量的类型尽可能是接口或抽象类
3. 任何类都不应该从具体类派生
4. 尽量不要覆写基类的方法
5. 结合里氏替换原则使用

注意以上措词，尽量就意味着不是绝对的，难点也在这里，什么时候遵循，什么时候打破，需要经验。

### 接口隔离原则

定义

Clients should not be forced to depend upon interfaces that they don't use

The dependence of one class to another one should depend on the smallest
possible interface

* 接口要尽量小
* 接口要高内聚
* 定制服务
* 接口设计是有限度的
* 一个接口只服务一个子模块或业务逻辑
* 已经污染的接口，尽量去修改，若变更的风险大，采用适配器进行转换处理
* 了解环境，拒绝盲从

### 迪米特法则

LKP(least knowledge principle)

一个对象应该对另一个对象有最少的了解

只和朋友交流

```
public class Teacher {
    List girls;

    Teacher(Girl girls) {
        this.girls = girls;
    }
    public void commond(GroupLeader groupLeader) {
        groupLeader.countGirls(this.girls);
    }
}

public class GroupLeader {
    public void countGirls(List<Girls> listGirls) {
        System.out.println("girls count is " + listGirls.size());
    }
}

public class Girl {
}

public class Client {
    public void static main(String []args) {
        List<Girl> girls = new ArrayList<Girl>();
        for (int i = 0; i< 20; i++) {
            girls.add(new Girl());
        }
        Teacher teacher = new Teacher(girls);
        teacher.commond(new GroupLeader());
    }
}
```

```
public class Teacher {

    public void commond(GroupLeader groupLeader) {
        groupLeader.countGirls();
    }
}

public class GroupLeader {
    List girls;
    GroupLeader(Girl girls) {
        this.girls = girls;
    }
    public void countGirls() {
        System.out.println("girls count is " + listGirls.size());
    }
}

public class Girl {
}

public class Client {
    public void static main(String []args) {
        List<Girl> girls = new ArrayList<Girl>();
        for (int i = 0; i< 20; i++) {
            girls.add(new Girl());
        }
        Teacher teacher = new Teacher();
        teacher.commond(new GroupLeader(girls));
    }
}
```

对比以上，很难定义孰好孰坏。得看场景

朋友间也是有距离的

```
public Wizard {
    private Random rand = new Random(System.currentTimeMillis());

    public int first() {
        System.out.println("first");
        return rand.nextInt(100);
    }
    public int second() {
        System.out.println("second");
        return rand.nextInt(100);
    }
    public int third() {
        System.out.println("third");
        return rand.nextInt(100);
    }
}

public class InstallSoftware {
    public void installWizward(Wizard wizard) {
        int first = wizard.first();
        if (first > 50) {
            int second = wizard.second();
            if (second > 50) {
                int third = wizard.third();
                if (third > 50) {
                    wizard.third();
                }
            }
        }
    }
}

public class Client {
    public void main(String []args) {
        InstallSoftware invoker = new InstallSoftware();
        invoker.installWizward(new Wizard());
    }
}
```

这个类的问题在于 InstallSoftware 的 installWizward 方法中
Wizard 的太多方法暴露给 InstallSoftware, 违背了松耦合的原则。
想想如果要把 first, second, third 的返回值由 int 改为 boolean，
那么，要改动的地方就非常多。

```
public Wizard {
    private Random rand = new Random(System.currentTimeMillis());

    public int first() {
        System.out.println("first");
        return rand.nextInt(100);
    }
    public int second() {
        System.out.println("second");
        return rand.nextInt(100);
    }
    public int third() {
        System.out.println("third");
        return rand.nextInt(100);
    }

    public void installWizward() {
        int first = wizard.first();
        if (first > 50) {
            int second = wizard.second();
            if (second > 50) {
                int third = wizard.third();
                if (third > 50) {
                    wizard.third();
                }
            }
        }
    }
}

public class InstallSoftware {
    public void installWizward(Wizard wizard) {
        wizard.installWizward();
    }
}

public class Client {
    public void main(String []args) {
        InstallSoftware invoker = new InstallSoftware();
        invoker.installWizward(new Wizard());
    }
}
```

自己的就是自己的

谨慎使用 Serializable

### 开闭原则

Software entities like classes, modules and functions should be open for
extension but closed for modification

总结就是通过扩展来实现新的功能而不是修改原有的程序。原因在于

1. 模块，类之间的相互是耦合的，牵一发动全身
2. 测试也需要重新测试，而有些测试可能失效，有些测试需要修改，需要修改的地方也非常多
3. 人员在变动，没有一个人可以掌握全局，有时候无法评估影响。

## 单例模式

定义

Ensure a class has only one instance, and provide a global point of access to it

优点：
1. 在内存中只有一份，减少内存消耗
2. 如果单例对象依赖的资源比较多的时候，可以在启动时直接产生一个单例对象，常驻内存

缺点：
1. 对测试不友好

使用场景

1. 唯一 ID
2. 共享访问点
3. 一个对象需要消耗资源过多，而且频繁操作
4. 需要定义大量静态常量和静态方法的环境

需要考虑的因素
1. 多线程环境
2. 对象复制(不要实现 Cloneable 接口)

## 工厂模式

定义

define an interface for creating an object; but let subclasses decide which to
instantiate. Factory Method lets a class defer instantiate to subclasses

优点
1. 解耦：只需要知道类名即可
2. 扩展性非常好
3. 符合迪米特法则，依赖倒置原则，符合里氏替换原则

缺点


使用场景

1. 一个产品有多个实现时使用

## 抽象工厂

定义

Provide an interface for create families of related or dependent objects without
specifying their concrete classes

优点

封装性

缺点

纵向扩展非常困难，横向扩展容易 : 想想增加一个新的产品需要做什么改动

场景

不同操作系统下的同一软件



### 门面模式

关键点

门面模式与工厂类模式非常相似，可以理解为将多个工厂模式组合起来，提供对外访问接口。

## 模板模式

定义

Define the skeleton of an algorithm in an operation, deferring some steps to
subclasses. Template Method let subclasses redefine certain steps of an
algorithm without changing the algorithm's structure.

为了防止恶意操作，一般模板方法都加 final 关键字，不允许被覆写。

优点

封装不变性，扩展可变性
提取公共部分，便于维护
行为由父类定义，子类实现

使用场景

多个子类的共有方法，并且逻辑相同

关键点

1. 抽象类定义好步骤，具体步骤由子类实现。

## 建造者模式

serperate the construction of a complex object from its representation so that
same construction process can create different representation

优点

封装性
建造者独立，容易扩展
便于控制风险

使用场景

相同方法，不同执行顺序，产生不同结果

## 代理模式

Provide a surrogate or placeholder for another object to control access to it

优点

职责清晰：真实的角色做好自己的本职工作即可
高扩展性：具体实现可以变化，需要改动非常小, 此外可以在具体的实现之上做一些额外工作, 比如拦截，过滤，增强等等
智能化：

一个代理类可以代理多个真实角色，并且真实角色之间可以有耦合关系

分类

普通代理
强制代理


### 静态代理

优点

代理使客户端不需要知道实现类是什么，怎么做的，而客户端只需知道代理即可（解耦合）

缺点

1. 代理类和委托类实现了相同的接口，代理类通过委托类实现了相同的方法。如果接口增加一个方法，除了所有实现类需要实现这个方法外，所有代理类也需要实现此方法。增加了代码维护的复杂度。
2. 代理对象只服务于一种类型的对象，如果要服务多类型的对象。势必要为每一种对象都进行代理，静态代理在程序规模稍大时就无法胜任了。


### 动态代理

为了解决静态代理每个代理类只能为一个接口服务的缺点，要可以通过一个代理类完成全部的代理功能，就需要用动态代理

动态代理是在运行时，通过反射机制（或字节码注入）实现动态代理，并且能够代理各种类型的对象。

#### InvocationHandler，它到底是用来干嘛的？为什么要用它？

首先，在动态代理中，Proxy代理类在编译期是不存在的，而是在程序运行时被动态生成的，因为有了反射，可以根据传入的参数，生成你想要的代理（如你想代理A就代理A，想代理B就代理B），实现原理就是在生成Proxy的时候你需要传入被代理类的所有接口（如果没有接口是另一种方式，下文会提），反射机制会根据你传入的所有接口，帮你生成一个也实现这些接口的代理类出来。之后，代理对象每调用一个方法，都会把这个请求转交给InvocationHandler来执行，而在InvocationHandler里则通过反射机制，继续转发请求给真正的目标对象，最后由目标对象来返回结果。或许还有读者疑惑，为什么Proxy不能直接转发请求给目标对象，而是先交给InvocationHandler，（如果你会反射很容易明白）这是因为，要通过反射来转为目标的对象的调用，需要传入调用的方法名，方法所属的类对象以及方法的参数，Proxy不直接转发给目标对象是因为，在程序运行时，Proxy是方法的直接调用者，然后将调用的信息传给InvocationHandler，InvocationHandler再根据传过来的信息并利用反射转为目标对象的最终调用，这个过程中，如果没有Proxy对象的调用，又怎么有运行时的动态信息给反射机制来处理呢，只有Proxy对象调用了，才能够知道有哪些动态信息，而InvocationHandler的作用就是用来接收这些信息，通过反射，进而转为目标对象的调用。

动态代理与静态代理相比较，最大的好处是接口中声明的所有方法都被转移到调用处理器一个集中的方法中处理（InvocationHandler.invoke）。这样，在接口方法数量比较多的时候，我们可以进行灵活处理，而不需要像静态代理那样每一个方法进行中转。而且动态代理的应用使我们的类职责更加单一，复用性更强。

## 原型模式

Specify the kinds of objects to create using a prototypical instance, and create
new objects by copying this prototype

优点：

性能优良 : 内存二进制拷贝，比 new 要好很多，尤其是在循环体内复制大量对象
逃避构造函数的约束

## 中介者模式

定义

Define an object that encapsulates how a set of objects interact. Mediator
promotes loss coupling by keeping objects from refering to each other
explicitly, and it lets you vary their interaction independently

多个类之间的交互通过中介者来处理，有网状变为星型，各类类涉及与其他类交互都
通过中介者完成。

优点：
将类间关系由一对多转换为一对一。减少依赖

缺点
中介者的逻辑为极端复杂

注：不是涉及类的交互都需要中介者模式，只有当多个类相互交织在一起，具体的
度根据实际情况决定。

使用场景

机场调度中心
中介服务
MVC框架
媒体网关: QQ, weichat

## 命令模式

Encapsulate a request as an object, thereby letting you parameterize clients
with different queue or log requests, and support undoable operations.

优点

调用者与接收者之间解耦：实际是通过 Command 代理各个实体类的操作
可扩展性：

缺点
类膨胀问题

场景
政府发布命令


## 责任链模式

定义

Avoid coupling the sender of a request to its receviver by giving more than
one object a chance to handle the request. Chain the receiving objects and
pass the request alone the chain untill an object handles it

优点
请求与处理的解耦：请求者不需要知道所有的处理者，只需要知道第一个处理或者默认处理者即可。
处理者也不需要知道所有处理者，只需要知道下一个处理者

缺点
性能问题：链很长
调试问题：很难定位当前在链的哪个地方出现错误

## 装饰模式

定义

Attach additional responsibilities to an object dynamically keeping the
same interface. Decorators provide a flexible alternative to subclassing
for extending functions.

装饰器与代理模式非常相似，只是在代理模式的基础上，进一步扩展，在需要更加
灵活的方式时，装饰器比代理模式更灵活。但目的不一样，代理模式目的是为了增加
过滤，处理等非实体类本身的功能，而装饰器的目的是增加实体类相关的功能。

优点
1. 动态地增加功能(是对继承的替代方案, 继承是静态地增加功能)

缺点
1. 多层装饰器比较复杂

场景
动态地给已经存在的类增加功能

关键点：

1. 实现代理模式
2. 装饰器类继承代理类实现自己的功能



### 桥梁模式

关键点

1. 与装饰器模式非常相似，区别在于装饰器模式是继承，而桥梁是组合。
2. 与策略模式非常相似，可以认为是策略模式的升级版。



### 命令模式

关键点

1. 与桥梁模式非常相似，不同点在于命令模式是子类组合抽象类，而桥梁模式是抽象类之间的组合。

## 策略模式

定义

Define a family of algorithms, encapsulate each one, and make them
interchangable

与工厂模式区别在于用于调用对象方法，而工厂模式用于创建对象。
与代理模式区别，在于代理模式通过继承，而策略通过组合。

代理模式和策略模式是否可以通用？目的不一样，代理是在原类基础上做一些工作，
而策略只是算法的选项，至于将两者混用会有什么问题？

优点
灵活切换实现

缺点
将具体实现暴露给用户

关键点

1. 工厂类 A 组合到类 B

## 适配器模式

convert the interface of a class into another interface clients expect,
Adapter let classes work together that couldn't otherwise because of
incompatible interface.

优点
提供类的复用
增加类的透明性
灵活性好

缺点

场景
1. 云平台之间接口的互操作。
2. 两个数据库之间的操作的转换，比如 redis 模拟 sql 语句
3. 将两个已有系统进行对接。

与策略模式的区别是策略模式更加倾向于同一接口的不同实现；

注意强调的是将两个功能相同但是接口不同的类转换为其中一个类。比如你一直
用 sql 语句来操作数据库，新的 nosql 数据库不支持 sql
语句，因此，你需要对 nosql 语句进行封装，用起来和 sql
语句一样，这个时候就可以用适配器模式

另外一个例子就是账号体系，你内部引入一个外部的账号体系，为了将其纳入现有账号体系，适配器模式很适应。

关键实现点：

1. 如果是类适配器（C 继承 A 实现 B）；如果是对象适配器（ C 组合 A 实现 B）。
2. 将 A 转为 B 的格式
3. 客户端可以通过 C 来访问 A 

## 迭代器模式

Provide an way to access the elements of an aggregate object sequentially
without exposing its underlying representation


## 组合模式

Compose objects into tree structures to represent part-whole hierarchies.
Composite lets clients treat individual objects and compositions of objects
uniformly

优点
节点自由增加

缺点
违背了依赖倒置原则

场景
菜单和文件
从整体中独立出部分模块或功能的场景

分为
透明模式：抽象类包含所有操作
安全模式：实现类包含具体操作, 抽象类只提供公共操作

## 观察者模式

定义

Define a one-to-many dependency between objects so that when
one object update changes state, all its dependents are notified
and updated automatically.

优点
观察者与被观察者解耦
建立触发机制

缺点
一个观察者的阻塞会干扰其他观察者
观察者的广播链

扩展；
异步通知机制
跨主机的通知机制
观察者处理能力不如被观察者，导致待处理消息累积, 通过队列或多线程

场景
文件系统改变

## 门面模式

provide a unified interface to a set of interface in a subsystem.
Facade defines a higher-level interface that make the subsystem easier
to use

可以理解为系统级别的，对一个系统对外暴露同意的接口，供客户的调用。
与中介模式类似，但是中介模式解决的是系统内部模块之间的相互调用问题。
与模板模式区别在于模板模式针对的是一个类，而门面针对的一组类。因此，
实现上稍微有区别。

优点
避免暴露系统内部细节给用户，简化操作，增加易用性。

缺点
不符合开闭原则

注意事项
* 一个系统可以有多个门面
* 门面不参与子系统的内部逻辑

## 备忘录模式

Without voliating encapsulation, capture and externalize an object's
internal state so that the object can be restore to this state later

无法理解 CareTaker 存在的必要性？

扩展
多个状态的备份
多种状态的备份
备份数量的控制

## 访问者模式

represent an operation to be performed on the elements of object structure.
Visitor lets you define a new operation without changing the classes of the
elements on which it operates.

在不改变原有类的情况下，定义类的新的操作。
对一个类提供一个访问者。针对的是一个类。而不是一组类。访问者需要访问的类一般
需要频繁修改，但为了避免修改原类而引申出访问者模式。

优点
符合单一职责原则
灵活性较高

缺点
违背迪米特法则:对访问者公布细节
违背依赖倒置原则

场景
1. 对不同的类执行不同的操作，是对迭代器的有力补充。
2. 拦截器：TODO
3. 统计工作

扩展：
多个访问者，不同访问者做不同的工作


## 状态模式

定义

allow an object ot alter its behavior when its internal state changes,
The object will appear to change its state.

优点
结构清晰，可扩展性好 : 避免了大量的 if else 语句，使得增加新的状态变得非常容易。


缺点
类膨胀


与责任链的区别在于，责任链自动建立了实体类之间的关系，调用方只要调用默认行为即可。
而状态模式目的在于防止用户对状态使用不当导致出错，减少了错误的可能性，当然还有
其他优点，但相对于责任链区别在此。还有一点是责任链是线性的，而状态模式可以是非
线性的，一个状态可能到多个状态。

非常常用的模式，需要掌握


## 总结

三个对象

* 客户端
* 中介类
* 实体类

#### 涉及单一或多个实体类

* 单例模式：全局唯一访问点
* 原型模式：通过拷贝而不是构造函数创建对象
* 模板模式：主要涉及实体类，将共性在实体的抽象类中实现，不同子类个性化实现每个步骤。
* 中介者模式：将网状的关系变为星型关系, 主要解决各个实体类之间的关系
* 责任链模式：将各个实体类串起来，形成链, 主要解决实体类之间处理连接问题
* 装饰器模式：与代理模式非常相似，动态地扩展功能
* 适配器模式：将两个本质上功能相同但是接口不同的类做转换，使得调用者完全没有迁移的负担。
* 组合模式：如何简单灵活地组织树形结构的一种做法
* 备忘录模式：保存一个对象的状态，以期在未来某个时刻恢复

#### 增加中介类，解决客户端与实体类访问问题。

这类设计模式都是解决客户端的易用问题，将实际的需求经过包装，处理之后提供给客户端使用。

* 工厂模式：一个对象有多种实现方法，通过一个单独的类，将创建不同种类的对象封装起来，提高易用性，屏蔽差异性,提取共性。解决的是对象创建问题。
* 建造者模式：如果需要将同样的组件，进行各种组合，由于存在多种组合过程，将组合过程用单独的类来实现。与模板模式结合使用
* 代理模式：实现同样的接口，其中一个实现调用其他实现来完成功能，因此成为代理。解决对象调用问题。
* 命名模式：解决的客户端同时与多个实体类交互的问题，将每一个操作封装为一个命令对象。 解决对象调用问题。
* 策略模式：解决客户端有多个选择时，算法切换问题
* 观察者模式：将一根对象的变化通知给所有所有关注该对象变化的对象。该模式非常常用。
* 门面模式: 可以理解为系统级别的，对一个系统对外暴露同意的接口，供客户的调用。

问题：是否在用命令模式的地方都可以用代理模式

命令模式与代理的区别：命令模式可以将多个类的相同操作代理执行，而代理模式只是代理一个类的全部操作。命令模式是横向的。代理模式是纵向的。

## 术语

* TDD
* ODD

