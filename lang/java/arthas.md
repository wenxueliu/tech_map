


### TransformerManager

定义了一个TransformerManager类，用于管理Java类文件转换器(ClassFileTransformer)。它利用Instrumentation接口提供的功能，在类加载器加载类时对类文件进行转换。

### AdviceWeaver

一个工具类，用于监听器的注册、注销、查询、暂停和恢复

```java
public class AdviceWeaver {
    private final static Map<Long, AdviceListener> advices = new ConcurrentHashMap<Long, AdviceListener>();
```

### WatchAdviceListener

一个自定义的监听器类，继承自AdviceListenerAdapter，其主要功能是根据用户指定的WatchCommand（监控命令）
来监听方法调用的过程，并在满足特定条件时收集相关数据（如调用耗时、返回值、异常等），生成WatchModel对象，然后通过CommandProcess将监控结果输出或存储。

```java
class WatchAdviceListener extends AdviceListenerAdapter {
    private WatchCommand command;
    private CommandProcess process;
}
```

### CommandProcess



### AnnotatedCommand

```java
abstract class AnnotatedCommand
```

### EnhancerCommand

主要用于通过命令行方式对类进行增强操作，并提供了相应的配置选项和抽象方法，以便在具体实现中进行定制化操作。

```java
abstract class EnhancerCommand extends AnnotatedCommand {

    protected void enhance(CommandProcess process) {
        Session session = process.session();
        try {
            Instrumentation inst = session.getInstrumentation();
            AdviceListener listener = getAdviceListenerWithId(process);
            Enhancer enhancer = new Enhancer(listener, listener instanceof InvokeTraceable,
                skipJDKTrace, getClassNameMatcher(), getClassNameExcludeMatcher(), getMethodNameMatcher());
            // 注册通知监听器
            process.register(listener, enhancer);
            effect = enhancer.enhance(inst, this.maxNumOfMatchedClass);
            process.appendResult(new EnhancerModel(effect, true));
        }
    }
}
```

### WatchCommand

```
public class WatchCommand extends EnhancerCommand {}
```


### Enhancer

```
public synchronized EnhancerAffect enhance(final Instrumentation inst, int maxNumOfMatchedClass) {
    // 获取需要增强的类集合
    this.matchingClasses = GlobalOptions.isDisableSubClass
            ? SearchUtils.searchClass(inst, classNameMatcher)
            : SearchUtils.searchSubClass(inst, SearchUtils.searchClass(inst, classNameMatcher));

    if (matchingClasses.size() > maxNumOfMatchedClass) {
        affect.setOverLimitMsg(...); // 设置超限信息
        return affect;
    }

    // 过滤掉无法被增强的类

    // 设置增强器的转换器
    affect.setTransformer(this);

    // 将转换器添加到转换器管理器中
    ArthasBootstrap.getInstance().getTransformerManager().addTransformer(this, isTracing);

    // 批量增强或逐个增强
    if (GlobalOptions.isBatchReTransform) {
        final Class<?>[] classArray = matchingClasses.toArray(new Class<?>[0]);
        inst.retransformClasses(classArray);
    } else {
        for (Class<?> clazz : matchingClasses) {
            inst.retransformClasses(clazz);
        }
    }

    return affect;
}
```


### SpyImpl



```java
interface AdviceListener {
    long id();
    void create();
    void destroy();
    void before(Class<?> clazz, String methodName, String methodDesc, Object target, Object[] args) throws Throwable;
    void afterReturning( Class<?> clazz, String methodName, String methodDesc, Object target, Object[] args, Object returnObject) throws Throwable;
    void afterThrowing( Class<?> clazz, String methodName, String methodDesc, Object target, Object[] args, Throwable throwable) throws Throwable;
}

abstract class AdviceListenerAdapter implements AdviceListener, ProcessAware { 
    private static final  AtomicLong ID_GENERATOR = new AtomicLong(0);
    private Process process;
    private long id = ID_GENERATOR.addAndGet(1);
    private boolean verbose;
}

class AbstractTraceAdviceListener extends AdviceListenerAdapter {
    protected final ThreadLocalWatch threadLocalWatch = new ThreadLocalWatch();
    protected TraceCommand command;
    protected CommandProcess process;
}
class TraceAdviceListener extends AbstractTraceAdviceListener implements InvokeTraceable {
}
```

### TraceCommand 

```java
class TraceCommand extends EnhancerCommand {
    @Override
    protected AdviceListener getAdviceListener(CommandProcess process) {
        if (pathPatterns == null || pathPatterns.isEmpty()) {
            return new TraceAdviceListener(this, process, GlobalOptions.verbose || this.verbose);
        } else {
            return new PathTraceAdviceListener(this, process);
        }
    }
```


SpyInterceptors

