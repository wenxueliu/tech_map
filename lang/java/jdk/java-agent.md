



主要的功能如下：

- 可以在加载class文件之前做拦截把字节码做修改
- 可以在运行期将已经加载的类的字节码做变更，但是这种情况下会有很多的限制，后面会详细说
- 还有其他的一些小众的功能
  - 获取所有已经被加载过的类
  - 获取所有已经被初始化过了的类（执行过了clinit方法，是上面的一个子集）
  - 获取某个对象的大小
  - 将某个jar加入到bootstrapclasspath里作为高优先级被bootstrapClassloader加载
  - 将某个jar加入到classpath里供AppClassloard去加载
  - 设置某些native方法的前缀，主要在查找native方法的时候做规则匹配



### JVMTI 与 Java Instrument

Java平台调试器架构（Java Platform Debugger Architecture，JPDA）是一组用于调试Java代码的API（摘自维基百科）：

* Java调试器接口（Java Debugger Interface，JDI）：定义了一个高层次Java接口，开发人员可以利用JDI轻松编写[远程调试](https://cloud.tencent.com/product/rd?from_column=20065&from=20065)工具
* Java虚拟机工具接口（Java Virtual Machine Tools Interface，JVMTI）：定义了一个原生（native）接口，可以对运行在Java虚拟机的应用程序检查状态、控制运行
* Java虚拟机调试接口（JVMDI）：JVMDI在J2SE 5中被JVMTI取代，并在Java SE 6中被移除
* Java调试线协议（JDWP）：定义了调试对象（一个 Java 应用程序）和调试器进程之间的通信协议

JVMTI 提供了一套"代理"程序机制，可以支持第三方工具程序以代理的方式连接和访问 JVM，并利用 JVMTI 提供的丰富的编程接口，完成很多跟 JVM 相关的功能。JVMTI是基于事件驱动的，JVM每执行到一定的逻辑就会调用一些事件的回调接口（如果有的话），这些接口可以供开发者去扩展自己的逻辑。

JVMTI Agent是一个利用JVMTI暴露出来的接口提供了代理启动时加载(agent on load)、代理通过attach形式加载(agent on attach)和代理卸载(agent on unload)功能的动态库。

Instrument Agent可以理解为一类JVMTI Agent动态库，别名是JPLISAgent(Java Programming Language Instrumentation Services Agent)，是专门为java语言编写的插桩服务提供支持的代理。



### Instrument Agent 两种加载方式

在官方API文档[1]中提到，有两种获取Instrumentation接口实例的方法 ： 

1. JVM在指定代理的方式下启动，此时Instrumentation实例会传递到代理类的premain方法。

2. JVM提供一种在启动之后的某个时刻启动代理的机制，此时Instrumentation实例会传递到代理类代码的agentmain方法。

premain：对应的就是VM启动时的Instrument Agent加载，即`agent on load`，

agentmain：对应的是VM运行时的Instrument Agent加载，即`agent on attach`。

两种加载形式所加载的`Instrument Agent`都关注同一个`JVMTI`事件 – `ClassFileLoadHook`事件，这个事件是在读取字节码文件之后回调时用，也就是说premain和agentmain方式的回调时机都是类文件字节码读取之后（或者说是类加载之后），之后对字节码进行重定义或重转换，不过修改的字节码也需要满足一些要求，在最后的局限性有说明。





### **premain 与 agentmaiin 的区别**

`premain`和`agentmain`两种方式最终的目的都是为了回调`Instrumentation`实例并激活`sun.instrument.InstrumentationImpl#transform()`（InstrumentationImpl是Instrumentation的实现类）从而回调注册到`Instrumentation`中的`ClassFileTransformer`实现字节码修改，本质功能上没有很大区别。两者的非本质功能的区别如下：

* premain方式是JDK1.5引入的，agentmain方式是JDK1.6引入的，JDK1.6之后可以自行选择使用`premain`或者`agentmain`。

* `premain`需要通过命令行使用外部代理jar包，即`-javaagent:代理jar包路径`；`agentmain`则可以通过`attach`机制直接附着到目标VM中加载代理，也就是使用`agentmain`方式下，操作`attach`的程序和被代理的程序可以是完全不同的两个程序。
* `premain`方式回调到`ClassFileTransformer`中的类是虚拟机加载的所有类，这个是由于代理加载的顺序比较靠前决定的，在开发者逻辑看来就是：所有类首次加载并且进入程序`main()`方法之前，`premain`方法会被激活，然后所有被加载的类都会执行`ClassFileTransformer`列表中的回调。
* `agentmain`方式由于是采用`attach`机制，被代理的目标程序VM有可能很早之前已经启动，当然其所有类已经被加载完成，这个时候需要借助`Instrumentation#retransformClasses(Class... classes)`让对应的类可以重新转换，从而激活重新转换的类执行`ClassFileTransformer`列表中的回调。
* 通过premain方式的代理Jar包进行了更新的话，需要重启服务器，而agentmain方式的Jar包如果进行了更新的话，需要重新attach，但是agentmain重新attach还会导致重复的字节码插入问题，不过也有`Hotswap`和`DCE VM`方式来避免。

permain`方法只能在java程序启动之前执行，并不能程序启动之后再执行，但是在实际的很多的情况下，我们没有办法在虚拟机启动之时就为其设定代理，这样实际上限制了instrument的应用。而Java SE 6的新特性改变了这种情况，可以通过Java Tool API中的attach方式来达到这种程序启动之后设置代理的效果。

Attach API 不是 Java 的标准 API，而是 Sun 公司提供的一套扩展 API，用来向目标 JVM “附着”（Attach）代理工具程序的。有了它，开发者可以方便的监控一个 JVM，运行一个外加的代理程序。



### Instrumentation的局限性
大多数情况下，我们使用Instrumentation都是使用其字节码插桩的功能，或者笼统说就是类重定义(Class Redefine)的功能，但是有以下的局限性：

1. premain和agentmain两种方式修改字节码的时机都是类文件加载之后，也就是说必须要带有Class类型的参数，不能通过字节码文件和自定义的类名重新定义一个本来不存在的类。
2. 类的字节码修改称为类转换(Class Transform)，类转换其实最终都回归到类重定义Instrumentation#redefineClasses()方法，此方法有以下限制：
   2.1、新类和老类的父类必须相同；
   2.2、新类和老类实现的接口数也要相同，并且是相同的接口；
   2.3、新类和老类访问符必须一致。 新类和老类字段数和字段名要一致；
   2.4、新类和老类新增或删除的方法必须是private static/final修饰的；
   2.5、可以修改方法体。

除了上面的方式，如果想要重新定义一个类，可以考虑基于类加载器隔离的方式：创建一个新的自定义类加载器去通过新的字节码去定义一个全新的类，不过也存在只能通过反射调用该全新类的局限性。



#### premain 定义

1、定义一个 MANIFEST.MF 文件，必须包含 Premain-Class 选项，通常也会加入Can-Redefine-Classes 和 Can-Retransform-Classes 选项。

```
Manifest-Version: 1.0
Can-Redefine-Classes: true
Can-Retransform-Classes: true
Premain-Class: com.longofo.PreMainAgent // 定义 premain 函数的类
```

* Premain-Class ：包含 premain 方法的类（类的全路径名）main方法运行前代理

* Can-Redefine-Classes ：true表示能重定义此代理所需的类，默认值为 false（可选）

* Can-Retransform-Classes ：true 表示能重转换此代理所需的类，默认值为 false （可选）

* Can-Set-Native-Method-Prefix： true表示能设置此代理所需的本机方法前缀，默认值为 false（可选）

2、创建一个Premain-Class 指定的类，类中包含 premain 方法，premain函数，包含下面两个方法的其中之一，方法逻辑由用户自己确定。

```java
   public static void premain(String agentArgs, Instrumentation inst);
   public static void premain(String agentArgs);
```

注：两个方法都被实现了，那么带Instrumentation参数的优先级高一些，会被优先调用。`agentArgs`是`premain`函数得到的程序参数，通过命令行参数传入

3、将 premain 的类和 MANIFEST.MF 文件打成 jar 包。

```xml
<plugin>
    <groupId>org.apache.maven.plugins</groupId>
    <artifactId>maven-jar-plugin</artifactId>
    <version>3.1.0</version>
    <configuration>
        <archive>
            <!--自动添加META-INF/MANIFEST.MF -->
            <manifest>
                <addClasspath>true</addClasspath>
            </manifest>
            <manifestEntries>
                <Premain-Class>premain类的全路径</Premain-Class>
                <Can-Redefine-Classes>true</Can-Redefine-Classes>
                <Can-Retransform-Classes>true</Can-Retransform-Classes>
            </manifestEntries>
        </archive>
    </configuration>
</plugin>
```



4、使用参数 -javaagent: jar包路径 启动要代理的方法。

```
java -javaagent:agent1.jar -javaagent:agent2.jar -jar MyProgram.jar
```

注：可以定义多个agent，按指定顺序执行



#### premain 执行流程

1. 创建并初始化 JPLISAgent 

2. MANIFEST.MF 文件的参数，并根据这些参数来设置 JPLISAgent 里的一些内容 

3. 监听 `VMInit` 事件，在 JVM 初始化完成之后做下面的事情： 

   （1）创建 InstrumentationImpl 对象 ； 

   （2）监听 ClassFileLoadHook 事件 ； 

   （3）调用 InstrumentationImpl 的`loadClassAndCallPremain`方法，在这个方法里会去调用 javaagent 中 MANIFEST.MF 里指定的Premain-Class 类的 premain 方法

4. premain方法是在 main 方法启动前拦截大部分类（系统类除外）的加载活动，既然可以拦截类的加载，那么就可以去做重写类这样的操作



### agentmain 定义

agentmain方式编写步骤简单如下：

1、定义一个 MANIFEST.MF 文件，必须包含 Premain-Class 选项，通常也会加入Can-Redefine-Classes 和 Can-Retransform-Classes 选项。

```
Manifest-Version: 1.0
Can-Redefine-Classes: true
Can-Retransform-Classes: true
Agent-Class: com.longofo.SufMainAgent
```

2、创建一个Premain-Class 指定的类，类中包含 premain 方法，agentmain函数，包含下面两个方法的其中之一，方法逻辑由用户自己确定。

```java
   public static void agentmain(String agentArgs, Instrumentation inst);
   public static void agentmain(String agentArgs);
```

注：两个方法都被实现了，那么带Instrumentation参数的优先级高一些，会被优先调用。`agentArgs`是`agentmain`函数得到的程序参数，通过命令行参数传入

3、将 agentmain 的类和 MANIFEST.MF 文件打成 jar 包。

```xml
 		<build>
        <plugins>
            <plugin>
                <groupId>org.apache.maven.plugins</groupId>
                <artifactId>maven-jar-plugin</artifactId>
                <version>3.1.0</version>
                <configuration>
                    <archive>
                        <!--自动添加META-INF/MANIFEST.MF -->
                        <manifest>
                            <addClasspath>true</addClasspath>
                        </manifest>
                        <manifestEntries>
                            <Agent-Class>agentmain类的全路径</Agent-Class>
                            <Can-Redefine-Classes>true</Can-Redefine-Classes>
                            <Can-Retransform-Classes>true</Can-Retransform-Classes>
                        </manifestEntries>
                    </archive>
                </configuration>
            </plugin>
        </plugins>
    </build>
```

4、将 jar 包注入对应的服务

```java
public class AttachTest {
    public static void main(String[] args) throws IOException, AttachNotSupportedException, AgentLoadException, AgentInitializationException {
        String mainClassPath = "要注入的服务中，main 方法所在的类";
        // 哪些类需要进行增强
        List<String> targetClassNames = Arrays.asList(
                "javax.servlet.http.HttpServletRequest");
        String jarPath = "打包的jar文件全路径.jar";
        for (VirtualMachineDescriptor descriptor : VirtualMachine.list()) {
            System.out.println(descriptor.displayName());
            if (descriptor.displayName().equals(mainClassPath)) {
                VirtualMachine virtualMachine = VirtualMachine.attach(descriptor.id());
                for (String targetClassName : targetClassNames) {
                    virtualMachine.loadAgent(jarPath, targetClassName);
                }
            }
        }
    }
}
```





#### agentmain 执行流程

1. 创建并初始化JPLISAgent 

2. 解析MANIFEST.MF 里的参数，并根据这些参数来设置 JPLISAgent 里的一些内容 

   ```
   Manifest-Version: 1.0
   Can-Redefine-Classes: true
   Can-Retransform-Classes: true
   Agent-Class: com.longofo.SufMainAgent
   ```

3. 创建 InstrumentationImpl 对象

4. 监听 ClassFileLoadHook 事件

5. 调用 InstrumentationImpl 的`loadClassAndCallAgentmain`方法，在这个方法里会去调用javaagent里 MANIFEST.MF 里指定的`Agent-Class`类的`agentmain`方法。

通过VirtualMachine类的`attach(pid)`方法，便可以attach到一个运行中的java进程上，之后便可以通过`loadAgent(agentJarPath)`来将agent的jar包注入到对应的进程，然后对应的进程会调用agentmain方法。既然是两个进程之间通信那肯定的建立起连接，VirtualMachine.attach动作类似TCP创建连接的三次握手，目的就是搭建attach通信的连接。而后面执行的操作，例如vm.loadAgent，其实就是向这个socket写入数据流，接收方target VM会针对不同的传入数据来做不同的处理。



## javaagent清单属性

| 属性                         | 说明                                 | 是否必选     | 默认值 |
| ---------------------------- | ------------------------------------ | ------------ | ------ |
| Premain-Class                | 包含premain方法的类                  | 依赖启动方式 | 无     |
| Agent-Class                  | 包含agentmain方法的类                | 依赖启动方式 | 无     |
| Boot-Class-Path              | 启动类加载器搜索路径                 | 否           | 无     |
| Can-Redefine-Classes         | 是否可以重定义代理所需的类           | 否           | false  |
| Can-Retransform-Classes      | 是否能够重新转换此代理所需的类       | 否           | false  |
| Can-Set-Native-Method-Prefix | 是否能够设置此代理所需的本机方法前缀 | 否           | false  |



### 调试

主程序（被 agentmain attach 的进程）增加如下参数

```javascript
-Xrunjdwp:transport=dt_socket,server=y,address=8001  
```

此时程序启动，将会被阻塞，直到远程调试程序连接上 8001 端口。输出如下内容，并阻塞

```
Listening for transport dt_socket at address: 8001
```





然后在有  `agentmain`  工程增加一个 remote 调试。如下配置

```javascript
-agentlib:jdwp=transport=dt_socket,server=y,suspend=n,address=8001
```

在 `agentmain` 工程打上断点，运行远程调试， 主程序将会被启动。

最后在命令行窗口运行 `Agent-main` 程序，远程调试将会暂停到相应断点处，接下来调试就跟普通 Debug 模式一样



### FQA

由于 Attach API 位于 tools.jar 中，而在 JDK8 之前 tools.jar 与我们常用JDK jar 包并不在同一个位置，所以编译与运行过程可能找不到该 jar 包，从而导致报错。

如果 maven 编译与运行都使用 JDK9 之后，不用担心下面问题。

#### maven 编译问题

```
        <dependency>
            <groupId>com.github.olivergondza</groupId>
            <artifactId>maven-jdk-tools-wrapper</artifactId>
            <version>0.1</version>
            <scope>provided</scope>
            <optional>true</optional>
        </dependency>
```



**程序运行过程 tools.jar 找不到**

运行程序时抛出 `java.lang.NoClassDefFoundError`，主要原因还是系统未找到 tools.jar 导致。

在运行参数加入 `-Xbootclasspath/a:${java_home}/lib/tools.jar`，完整运行命令如下：



## 参考

[手把手教你实现热更新功能，带你了解 Arthas 热更新背后的原理](https://cloud.tencent.com/developer/article/1540142?areaId=106001)

[javaagent使用指南](https://www.cnblogs.com/rickiyang/p/11368932.html)

[Java的Instrumentation类原理分析](https://cloud.tencent.com/developer/article/2315808?areaId=106001)

[JVM Attach机制实现](http://lovestblog.cn/blog/2014/06/18/jvm-attach/)

[JavaAgent源码分析](https://blog.csdn.net/jinxin70/article/details/85690904)