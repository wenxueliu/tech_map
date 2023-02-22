Spring动态多数据源是一个我们在项目中常用到的组件，尤其是做项目重构，有多种数据库，不同的请求可能会调用不同的数据源。这时，就需要动态调用指定的数据源。

完整的动态数据源包括：配置、注解配置，数据源提供者、数据源创建、动态数据源路由5 部分组成。下面对各个部分进行详细说明。

### 动态配置
通过在META-INF/spring.factories 配置

```
org.springframework.boot.autoconfigure.EnableAutoConfiguration=\
com.baomidou.dynamic.datasource.spring.boot.autoconfigure.DynamicDataSourceAutoConfiguration
```
spring动态加载的时候会自动扫描的类DynamicDataSourceAutoConfiguration


### 动态配置

```java
@Slf4j
@Configuration
@AllArgsConstructor
@EnableConfigurationProperties(DynamicDataSourceProperties.class)
@AutoConfigureBefore(DataSourceAutoConfiguration.class)
@Import(value = {DruidDynamicDataSourceConfiguration.class, DynamicDataSourceCreatorAutoConfiguration.class})
@ConditionalOnProperty(prefix = DynamicDataSourceProperties.PREFIX, name = "enabled", havingValue = "true", matchIfMissing = true)
public class DynamicDataSourceAutoConfiguration {

    private final DynamicDataSourceProperties properties;

    @Bean
    @ConditionalOnMissingBean
    public DynamicDataSourceProvider dynamicDataSourceProvider() {
        Map<String, DataSourceProperty> datasourceMap = properties.getDatasource();
        return new YmlDynamicDataSourceProvider(datasourceMap);
    }

    @Bean
    @ConditionalOnMissingBean
    public DataSource dataSource(DynamicDataSourceProvider dynamicDataSourceProvider) {
        DynamicRoutingDataSource dataSource = new DynamicRoutingDataSource();
        dataSource.setPrimary(properties.getPrimary());
        dataSource.setStrict(properties.getStrict());
        dataSource.setStrategy(properties.getStrategy());
        dataSource.setProvider(dynamicDataSourceProvider);
        dataSource.setP6spy(properties.getP6spy());
        dataSource.setSeata(properties.getSeata());
        return dataSource;
    }

    @Bean
    @ConditionalOnMissingBean
    public DynamicDataSourceAnnotationAdvisor dynamicDatasourceAnnotationAdvisor(DsProcessor dsProcessor) {
        DynamicDataSourceAnnotationInterceptor interceptor = new DynamicDataSourceAnnotationInterceptor();
        interceptor.setDsProcessor(dsProcessor);
        DynamicDataSourceAnnotationAdvisor advisor = new DynamicDataSourceAnnotationAdvisor(interceptor);
        advisor.setOrder(properties.getOrder());
        return advisor;
    }

    /**
     * 依次是headerProcessor、sessionProcessor、spelExpressionProcessor
     **/
    @Bean
    @ConditionalOnMissingBean
    public DsProcessor dsProcessor() {
        DsHeaderProcessor headerProcessor = new DsHeaderProcessor();
        DsSessionProcessor sessionProcessor = new DsSessionProcessor();
        DsSpelExpressionProcessor spelExpressionProcessor = new DsSpelExpressionProcessor();
        headerProcessor.setNextProcessor(sessionProcessor);
        sessionProcessor.setNextProcessor(spelExpressionProcessor);
        return headerProcessor;
    }

    @Bean
    @ConditionalOnBean(DynamicDataSourceConfigure.class)
    public DynamicDataSourceAdvisor dynamicAdvisor(DynamicDataSourceConfigure dynamicDataSourceConfigure, DsProcessor dsProcessor) {
        DynamicDataSourceAdvisor advisor = new DynamicDataSourceAdvisor(dynamicDataSourceConfigure.getMatchers());
        advisor.setDsProcessor(dsProcessor);
        advisor.setOrder(Ordered.HIGHEST_PRECEDENCE);
        return advisor;
    }
}
```

1、DynamicDataSourceProperties：动态数据源属性类
2、DynamicDataSourceProvider：数据源提供者Provider，这是动态初始化数据源，读取yml配置文件，在配置文件中可配置1个或多个数据源。Spring动态多数据源支持数据源的嵌套。3、DsProcessor：数据源处理器，ds就是datasource的简称，采用责任链设计模式3种方法加载ds
4、DynamicDataSourceAnnotationAdvisor：动态数据源注解类，包括前置通知，切面类，切点的加载
5、DataSource：默认的 DataSource

### 数据源创建
DataSourceCreator：在 DynamicDataSourceCreatorAutoConfiguration 中创建数据源。可以选择4种类型的数据源进行创建，分别是：basic、jndi、druid、hikari。这四种数据源通过组合设计模式被set到DataSourceCreator中。

```java
@Slf4j
@Configuration
@AllArgsConstructor
@EnableConfigurationProperties(DynamicDataSourceProperties.class)
public class DynamicDataSourceCreatorAutoConfiguration {

    private final DynamicDataSourceProperties properties;

    @Bean
    @ConditionalOnMissingBean
    public DataSourceCreator dataSourceCreator() {
        DataSourceCreator dataSourceCreator = new DataSourceCreator();
        dataSourceCreator.setBasicDataSourceCreator(basicDataSourceCreator());
        dataSourceCreator.setJndiDataSourceCreator(jndiDataSourceCreator());
        dataSourceCreator.setDruidDataSourceCreator(druidDataSourceCreator());
        dataSourceCreator.setHikariDataSourceCreator(hikariDataSourceCreator());
        dataSourceCreator.setGlobalPublicKey(properties.getPublicKey());
        return dataSourceCreator;
    }

    @Bean
    @ConditionalOnMissingBean
    public BasicDataSourceCreator basicDataSourceCreator() {
        return new BasicDataSourceCreator();
    }

    @Bean
    @ConditionalOnMissingBean
    public JndiDataSourceCreator jndiDataSourceCreator() {
        return new JndiDataSourceCreator();
    }

    @Bean
    @ConditionalOnMissingBean
    public DruidDataSourceCreator druidDataSourceCreator() {
        return new DruidDataSourceCreator(properties.getDruid());
    }

    @Bean
    @ConditionalOnMissingBean
    public HikariDataSourceCreator hikariDataSourceCreator() {
        return new HikariDataSourceCreator(properties.getHikari());
    }
}
```

### 获取匹配的数据源
通过责任链设计模式的方式获取数据源。

```java
import org.aopalliance.intercept.MethodInvocation;


public abstract class DsProcessor {

    private DsProcessor nextProcessor;

    public void setNextProcessor(DsProcessor dsProcessor) {
        this.nextProcessor = dsProcessor;
    }

    /**
     * 抽象匹配条件 匹配才会走当前执行器否则走下一级执行器
     *
     * @param key DS注解里的内容
     * @return 是否匹配
     */
    public abstract boolean matches(String key);

    /**
     * 决定数据源
     * <pre>
     *     调用底层doDetermineDatasource，
     *     如果返回的是null则继续执行下一个，否则直接返回
     * </pre>
     *
     * @param invocation 方法执行信息
     * @param key        DS注解里的内容
     * @return 数据源名称
     */
    public String determineDatasource(MethodInvocation invocation, String key) {
        if (matches(key)) {
            String datasource = doDetermineDatasource(invocation, key);
            if (datasource == null && nextProcessor != null) {
                return nextProcessor.determineDatasource(invocation, key);
            }
            return datasource;
        }
        if (nextProcessor != null) {
            return nextProcessor.determineDatasource(invocation, key);
        }
        return null;
    }

    /**
     * 抽象最终决定数据源
     *
     * @param invocation 方法执行信息
     * @param key        DS注解里的内容
     * @return 数据源名称
     */
    public abstract String doDetermineDatasource(MethodInvocation invocation, String key);
}

```
Spring动态多数据源， 获取数据源名称的方式有3种，这3中方式采用的是责任链方式连续获取的。依次执行
1. DsHeaderProcessor: 从请求的header中获取ds数据源名称。
2. DsSessionProcessor: 从session中获取数据源d名称
3. DsSpelExpressionProcessor: 通过spel表达式获取ds数据源名称

### 注解通知

主要有三部分：

1、切面类：DynamicDataSourceAdvisor，DynamicDataSourceAnnotationAdvisor
2、切点类：DynamicAspectJExpressionPointcut，DynamicJdkRegexpMethodPointcut
3、前置通知类：DynamicDataSourceAnnotationInterceptor

#### 自定义的前置通知类
```java
public class DynamicDataSourceAnnotationInterceptor implements MethodInterceptor {

    /**
     * The identification of SPEL.
     */
    private static final String DYNAMIC_PREFIX = "#";
    private static final DataSourceClassResolver RESOLVER = new DataSourceClassResolver();
    @Setter
    private DsProcessor dsProcessor;

    @Override
    public Object invoke(MethodInvocation invocation) throws Throwable {
        try {
            DynamicDataSourceContextHolder.push(determineDatasource(invocation));
            return invocation.proceed();
        } finally {
            DynamicDataSourceContextHolder.poll();
        }
    }

    private String determineDatasource(MethodInvocation invocation) throws Throwable {
        Method method = invocation.getMethod();
        DS ds = method.isAnnotationPresent(DS.class) ? method.getAnnotation(DS.class)
                : AnnotationUtils.findAnnotation(RESOLVER.targetClass(invocation), DS.class);
        String key = ds.value();
        return (!key.isEmpty() && key.startsWith(DYNAMIC_PREFIX)) ? dsProcessor.determineDatasource(invocation, key) : key;
    }
}
```

这里入参中有一个是DsProcessor，也就是ds处理器。在determineDatasource中看看DS的value值是否包含#，如果包含就经过dsProcessor处理后获得key，如果不包含#则直接返回注解的value值。

#### 切面类
```java
public class DynamicDataSourceAnnotationAdvisor extends AbstractPointcutAdvisor implements
        BeanFactoryAware {

    private Advice advice;

    private Pointcut pointcut;

    public DynamicDataSourceAnnotationAdvisor(@NonNull DynamicDataSourceAnnotationInterceptor dynamicDataSourceAnnotationInterceptor) {
        this.advice = dynamicDataSourceAnnotationInterceptor;
        this.pointcut = buildPointcut();
    }

    @Override
    public Pointcut getPointcut() {
        return this.pointcut;
    }

    @Override
    public Advice getAdvice() {
        return this.advice;
    }

    @Override
    public void setBeanFactory(BeanFactory beanFactory) throws BeansException {
        if (this.advice instanceof BeanFactoryAware) {
            ((BeanFactoryAware) this.advice).setBeanFactory(beanFactory);
        }
    }

    private Pointcut buildPointcut() {
        Pointcut cpc = new AnnotationMatchingPointcut(DS.class, true);
        Pointcut mpc = AnnotationMatchingPointcut.forMethodAnnotation(DS.class);
        return new ComposablePointcut(cpc).union(mpc);
    }
}
```
在切面类的构造函数中设置了前置通知和切点。这个类在项目启动的时候就会被加载。所有带有DS注解的方法都会被扫描，在方法被调用的时候触发前置通知。

### 数据源创建器
DataSourceCreator类组合创建数据源，这里面使用了简单工厂模式创建类

```java
@Slf4j
@Setter
public class DataSourceCreator {

    /**
     * 是否存在druid
     */
    private static Boolean druidExists = false;
    /**
     * 是否存在hikari
     */
    private static Boolean hikariExists = false;

    static {
        try {
            Class.forName(DRUID_DATASOURCE);
            druidExists = true;
            log.debug("dynamic-datasource detect druid,Please Notice \n " +
                    "https://github.com/baomidou/dynamic-datasource-spring-boot-starter/wiki/Integration-With-Druid");
        } catch (ClassNotFoundException ignored) {
        }
        try {
            Class.forName(HIKARI_DATASOURCE);
            hikariExists = true;
        } catch (ClassNotFoundException ignored) {
        }
    }

    private BasicDataSourceCreator basicDataSourceCreator;
    private JndiDataSourceCreator jndiDataSourceCreator;
    private HikariDataSourceCreator hikariDataSourceCreator;
    private DruidDataSourceCreator druidDataSourceCreator;
    private String globalPublicKey;

    /**
     * 创建数据源
     *
     * @param dataSourceProperty 数据源信息
     * @return 数据源
     */
    public DataSource createDataSource(DataSourceProperty dataSourceProperty) {
        DataSource dataSource;
        //如果是jndi数据源
        String jndiName = dataSourceProperty.getJndiName();
        if (jndiName != null && !jndiName.isEmpty()) {
            dataSource = createJNDIDataSource(jndiName);
        } else {
            Class<? extends DataSource> type = dataSourceProperty.getType();
            if (type == null) {
                if (druidExists) {
                    dataSource = createDruidDataSource(dataSourceProperty);
                } else if (hikariExists) {
                    dataSource = createHikariDataSource(dataSourceProperty);
                } else {
                    dataSource = createBasicDataSource(dataSourceProperty);
                }
            } else if (DRUID_DATASOURCE.equals(type.getName())) {
                dataSource = createDruidDataSource(dataSourceProperty);
            } else if (HIKARI_DATASOURCE.equals(type.getName())) {
                dataSource = createHikariDataSource(dataSourceProperty);
            } else {
                dataSource = createBasicDataSource(dataSourceProperty);
            }
        }
        this.runScrip(dataSourceProperty, dataSource);
        return dataSource;
    }

    private void runScrip(DataSourceProperty dataSourceProperty, DataSource dataSource) {
        String schema = dataSourceProperty.getSchema();
        String data = dataSourceProperty.getData();
        if (StringUtils.hasText(schema) || StringUtils.hasText(data)) {
            ScriptRunner scriptRunner = new ScriptRunner(dataSourceProperty.isContinueOnError(), dataSourceProperty.getSeparator());
            if (StringUtils.hasText(schema)) {
                scriptRunner.runScript(dataSource, schema);
            }
            if (StringUtils.hasText(data)) {
                scriptRunner.runScript(dataSource, data);
            }
        }
    }

    /**
     * 创建基础数据源
     *
     * @param dataSourceProperty 数据源参数
     * @return 数据源
     */
    public DataSource createBasicDataSource(DataSourceProperty dataSourceProperty) {
        if (StringUtils.isEmpty(dataSourceProperty.getPublicKey())) {
            dataSourceProperty.setPublicKey(globalPublicKey);
        }
        return basicDataSourceCreator.createDataSource(dataSourceProperty);
    }

    /**
     * 创建JNDI数据源
     *
     * @param jndiName jndi数据源名称
     * @return 数据源
     */
    public DataSource createJNDIDataSource(String jndiName) {
        return jndiDataSourceCreator.createDataSource(jndiName);
    }

    /**
     * 创建Druid数据源
     *
     * @param dataSourceProperty 数据源参数
     * @return 数据源
     */
    public DataSource createDruidDataSource(DataSourceProperty dataSourceProperty) {
        if (StringUtils.isEmpty(dataSourceProperty.getPublicKey())) {
            dataSourceProperty.setPublicKey(globalPublicKey);
        }
        return druidDataSourceCreator.createDataSource(dataSourceProperty);
    }

    /**
     * 创建Hikari数据源
     *
     * @param dataSourceProperty 数据源参数
     * @return 数据源
     */
    public DataSource createHikariDataSource(DataSourceProperty dataSourceProperty) {
        if (StringUtils.isEmpty(dataSourceProperty.getPublicKey())) {
            dataSourceProperty.setPublicKey(globalPublicKey);
        }
        return hikariDataSourceCreator.createDataSource(dataSourceProperty);
    }
}
```

#### 基础数据源创建器
以 BasicDataSourceCreator 为例说明，数据源创建器的基本实现。

```java
@Data
@Slf4j
public class BasicDataSourceCreator {

    private static Method createMethod;
    private static Method typeMethod;
    private static Method urlMethod;
    private static Method usernameMethod;
    private static Method passwordMethod;
    private static Method driverClassNameMethod;
    private static Method buildMethod;

    static {
        //to support springboot 1.5 and 2.x
        Class<?> builderClass = null;
        try {
            builderClass = Class.forName("org.springframework.boot.jdbc.DataSourceBuilder");
        } catch (Exception ignored) {
        }
        if (builderClass == null) {
            try {
                builderClass = Class.forName("org.springframework.boot.autoconfigure.jdbc.DataSourceBuilder");
            } catch (Exception e) {
                log.warn("not in springBoot ENV,could not create BasicDataSourceCreator");
            }
        }
        if (builderClass != null) {
            try {
                createMethod = builderClass.getDeclaredMethod("create");
                typeMethod = builderClass.getDeclaredMethod("type", Class.class);
                urlMethod = builderClass.getDeclaredMethod("url", String.class);
                usernameMethod = builderClass.getDeclaredMethod("username", String.class);
                passwordMethod = builderClass.getDeclaredMethod("password", String.class);
                driverClassNameMethod = builderClass.getDeclaredMethod("driverClassName", String.class);
                buildMethod = builderClass.getDeclaredMethod("build");
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * 创建基础数据源
     *
     * @param dataSourceProperty 数据源参数
     * @return 数据源
     */
    public DataSource createDataSource(DataSourceProperty dataSourceProperty) {
        try {
            Object o1 = createMethod.invoke(null);
            Object o2 = typeMethod.invoke(o1, dataSourceProperty.getType());
            Object o3 = urlMethod.invoke(o2, dataSourceProperty.getUrl());
            Object o4 = usernameMethod.invoke(o3, dataSourceProperty.getUsername());
            Object o5 = passwordMethod.invoke(o4, dataSourceProperty.getPassword());
            Object o6 = driverClassNameMethod.invoke(o5, dataSourceProperty.getDriverClassName());
            return (DataSource) buildMethod.invoke(o6);
        } catch (Exception e) {
            throw new ErrorCreateDataSourceException(
                    "dynamic-datasource create basic database named " + dataSourceProperty.getPoolName() + " error");
        }
    }
}
```
这里包括两部分
一、类初始化的时候初始化成员变量， 
二、创建数据源。当被调用createDataSource的时候执行创建数据源，使用的反射机制创建数据源。


#### Hikari类型的数据源
实现如下

```java
@Data
@AllArgsConstructor
public class HikariDataSourceCreator {

    private HikariCpConfig hikariCpConfig;

    public DataSource createDataSource(DataSourceProperty dataSourceProperty) {
        HikariConfig config = dataSourceProperty.getHikari().toHikariConfig(hikariCpConfig);
        config.setUsername(dataSourceProperty.getUsername());
        config.setPassword(dataSourceProperty.getPassword());
        config.setJdbcUrl(dataSourceProperty.getUrl());
        config.setDriverClassName(dataSourceProperty.getDriverClassName());
        config.setPoolName(dataSourceProperty.getPoolName());
        return new HikariDataSource(config);
    }
}
```

### 数据源提供者
数据源提供者是连接配置文件和数据源创建器的桥梁。数据源提供者先去读取配置文件， 将所有的数据源读取到DynamicDataSourceProperties对象的datasource属性中，datasource是一个Map集合，可以用来存储多种类型的数据源。

#### 动态数据源提供者
```java
/**
 * 多数据源加载接口，默认的实现为从yml信息中加载所有数据源 你可以自己实现从其他地方加载所有数据源
 *
 */
public interface DynamicDataSourceProvider {

    /**
     * 加载所有数据源
     *
     * @return 所有数据源，key为数据源名称
     */
    Map<String, DataSource> loadDataSources();
}
```

#### 动态数据源提供者的的抽象类
```java
public abstract class AbstractDataSourceProvider implements DynamicDataSourceProvider {

    @Autowired
    private DataSourceCreator dataSourceCreator;

    protected Map<String, DataSource> createDataSourceMap(
            Map<String, DataSourceProperty> dataSourcePropertiesMap) {
        Map<String, DataSource> dataSourceMap = new HashMap<>(dataSourcePropertiesMap.size() * 2);
        for (Map.Entry<String, DataSourceProperty> item : dataSourcePropertiesMap.entrySet()) {
            DataSourceProperty dataSourceProperty = item.getValue();
            String pollName = dataSourceProperty.getPoolName();
            if (pollName == null || "".equals(pollName)) {
                pollName = item.getKey();
            }
            dataSourceProperty.setPoolName(pollName);
            dataSourceMap.put(pollName, dataSourceCreator.createDataSource(dataSourceProperty));
        }
        return dataSourceMap;
    }
}
```

这里的成员变量是数据源数据源创建者dataSourceCreator。提供了一个创建数据源的方法:createDataSourceMap(...), 这个方法的入参是属性配置文件datasources, 返回值是创建的数据源对象结合.

这里的主要逻辑思想是: 循环遍历从配置文件读取的多个数据源, 然后根据数据源的类型, 调用DataSourceCreator数据源创建器去创建(初始化)数据源, 然后返回已经初始化好的数据源,将其保存到map集合中.

#### 使用yml配置文件读取的方式的动态数据源提供者
```
@Slf4j
@AllArgsConstructor
public class YmlDynamicDataSourceProvider extends AbstractDataSourceProvider implements DynamicDataSourceProvider {

    /**
     * 所有数据源
     */
    private Map<String, DataSourceProperty> dataSourcePropertiesMap;

    @Override
    public Map<String, DataSource> loadDataSources() {
        return createDataSourceMap(dataSourcePropertiesMap);
    }
}
```
这个源码也是非常简单, 继承了AbstractDataSourceProvider抽象类, 实现了DynamicDataSourceProvider接口. 在loadDataSources()方法中, 创建了多数据源, 并返回多数据源的map集合.

这里指的一提的是他的成员变量dataSourcePropertiesMap. 这个变量是什么时候被赋值的呢? 是在项目启动, 扫描配置文件DynamicDataSourceAutoConfiguration的时候被初始化的.

### 动态路由数据源

为什么数据源可以嵌套呢,就是这里决定的, 这里一共有四个文件,
1.AbstractRoutingDataSource: 抽象的路由数据源, 这个类主要作用是在找到目标数据源的情况下,连接数据库.
2.DynamicGroupDataSource:动态分组数据源, 在一个请求链接下的所有数据源就是一组. 也就是一个请求过来, 可以嵌套数据源, 这样数据源就有多个, 这多个就是一组.
3、DynamicRoutingDataSource: 动态路由数据源, 第一类AbstractRoutingDataSource用来连接数据源,那么到底应该链接哪个数据源呢?在这个类里面查找, 如何找呢, 从DynamicDataSourceContextHolder里面获取当前线程的数据源. 然后链接数据库.
4、DynamicDataSourceConfigure: 基于多种策略的自动切换数据源.

#### 数据源路由抽象
```java
public abstract class AbstractRoutingDataSource extends AbstractDataSource {

    /**
     * 子类实现决定最终数据源
     *
     * @return 数据源
     */
    protected abstract DataSource determineDataSource();

    @Override
    public Connection getConnection() throws SQLException {
        return determineDataSource().getConnection();
    }

    @Override
    public Connection getConnection(String username, String password) throws SQLException {
        return determineDataSource().getConnection(username, password);
    }
}
```
给出一个抽象方法determineDataSource, 由子类实现决定最终数据源

#### 动态分组数据源
这里定义了分组的概念.



```java
public class DynamicGroupDataSource {

    private String groupName;

    private DynamicDataSourceStrategy dynamicDataSourceStrategy;

    private List<DataSource> dataSources = new LinkedList<>();

    public DynamicGroupDataSource(String groupName, DynamicDataSourceStrategy dynamicDataSourceStrategy) {
        this.groupName = groupName;
        this.dynamicDataSourceStrategy = dynamicDataSourceStrategy;
    }

    public void addDatasource(DataSource dataSource) {
        dataSources.add(dataSource);
    }

    public void removeDatasource(DataSource dataSource) {
        dataSources.remove(dataSource);
    }

    public DataSource determineDataSource() {
        return dynamicDataSourceStrategy.determineDataSource(dataSources);
    }

    public int size() {
        return dataSources.size();
    }
}
```

1、每一个组有一个组名，组里面有多个数据源用list存储
2、dataSources 是一个LinkedList，有顺序的，因为在调用数据库查询数据的时候，不能调混了，所以使用顺序列表集合。
3、选择数据源的策略, 有多个数据源,按照什么策略选择呢?由策略类型来决定。

#### 数据源上下文
```java
public final class DynamicDataSourceContextHolder {

    /**
     * 为什么要用链表存储(准确的是栈)
     * <pre>
     * 为了支持嵌套切换，如ABC三个service都是不同的数据源
     * 其中A的某个业务要调B的方法，B的方法需要调用C的方法。一级一级调用切换，形成了链。
     * 传统的只设置当前线程的方式不能满足此业务需求，必须使用栈，后进先出。
     * </pre>
     */
    private static final ThreadLocal<Deque<String>> LOOKUP_KEY_HOLDER = new NamedThreadLocal<Deque<String>>("dynamic-datasource") {
        @Override
        protected Deque<String> initialValue() {
            return new ArrayDeque<>();
        }
    };

    private DynamicDataSourceContextHolder() {
    }

    /**
     * 获得当前线程数据源
     *
     * @return 数据源名称
     */
    public static String peek() {
        return LOOKUP_KEY_HOLDER.get().peek();
    }

    /**
     * 设置当前线程数据源
     * <p>
     * 如非必要不要手动调用，调用后确保最终清除
     * </p>
     *
     * @param ds 数据源名称
     */
    public static void push(String ds) {
        LOOKUP_KEY_HOLDER.get().push(StringUtils.isEmpty(ds) ? "" : ds);
    }

    /**
     * 清空当前线程数据源
     * <p>
     * 如果当前线程是连续切换数据源 只会移除掉当前线程的数据源名称
     * </p>
     */
    public static void poll() {
        Deque<String> deque = LOOKUP_KEY_HOLDER.get();
        deque.poll();
        if (deque.isEmpty()) {
            LOOKUP_KEY_HOLDER.remove();
        }
    }

    /**
     * 强制清空本地线程
     * <p>
     * 防止内存泄漏，如手动调用了push可调用此方法确保清除
     * </p>
     */
    public static void clear() {
        LOOKUP_KEY_HOLDER.remove();
    }
}
```
保存了当前线程里面所有的数据源. 使用的是ThreadLocal<Deque>.这个类最主要的含义就是ThreadLocal, 保证每个线程获取的是当前线程的数据源。

#### 动态路由数据源
通过数据源提供器获取所有的数据源添加到 dataSourceMap 和 addGroupDataSource 中

```java
@Slf4j
public class DynamicRoutingDataSource extends AbstractRoutingDataSource implements InitializingBean, DisposableBean {

    private static final String UNDERLINE = "_";
    /**
     * 所有数据库
     */
    private final Map<String, DataSource> dataSourceMap = new LinkedHashMap<>();
    /**
     * 分组数据库
     */
    private final Map<String, DynamicGroupDataSource> groupDataSources = new ConcurrentHashMap<>();
    @Setter
    private DynamicDataSourceProvider provider;
    @Setter
    private String primary;
    @Setter
    private boolean strict;
    @Setter
    private Class<? extends DynamicDataSourceStrategy> strategy;
    private boolean p6spy;
    private boolean seata;

    @Override
    public DataSource determineDataSource() {
        return getDataSource(DynamicDataSourceContextHolder.peek());
    }

    private DataSource determinePrimaryDataSource() {
        log.debug("dynamic-datasource switch to the primary datasource");
        return groupDataSources.containsKey(primary) ? groupDataSources.get(primary).determineDataSource() : dataSourceMap.get(primary);
    }

    /**
     * 获取当前所有的数据源
     *
     * @return 当前所有数据源
     */
    public Map<String, DataSource> getCurrentDataSources() {
        return dataSourceMap;
    }

    /**
     * 获取的当前所有的分组数据源
     *
     * @return 当前所有的分组数据源
     */
    public Map<String, DynamicGroupDataSource> getCurrentGroupDataSources() {
        return groupDataSources;
    }

    /**
     * 获取数据源
     *
     * @param ds 数据源名称
     * @return 数据源
     */
    public DataSource getDataSource(String ds) {
        if (StringUtils.isEmpty(ds)) {
            return determinePrimaryDataSource();
        } else if (!groupDataSources.isEmpty() && groupDataSources.containsKey(ds)) {
            log.debug("dynamic-datasource switch to the datasource named [{}]", ds);
            return groupDataSources.get(ds).determineDataSource();
        } else if (dataSourceMap.containsKey(ds)) {
            log.debug("dynamic-datasource switch to the datasource named [{}]", ds);
            return dataSourceMap.get(ds);
        }
        if (strict) {
            throw new RuntimeException("dynamic-datasource could not find a datasource named" + ds);
        }
        return determinePrimaryDataSource();
    }

    /**
     * 添加数据源
     *
     * @param ds         数据源名称
     * @param dataSource 数据源
     */
    public synchronized void addDataSource(String ds, DataSource dataSource) {
        if (!dataSourceMap.containsKey(ds)) {
            dataSource = wrapDataSource(ds, dataSource);
            dataSourceMap.put(ds, dataSource);
            this.addGroupDataSource(ds, dataSource);
            log.info("dynamic-datasource - load a datasource named [{}] success", ds);
        } else {
            log.warn("dynamic-datasource - load a datasource named [{}] failed, because it already exist", ds);
        }
    }


    private void addGroupDataSource(String ds, DataSource dataSource) {
        if (ds.contains(UNDERLINE)) {
            String group = ds.split(UNDERLINE)[0];
            if (groupDataSources.containsKey(group)) {
                groupDataSources.get(group).addDatasource(dataSource);
            } else {
                try {
                    DynamicGroupDataSource groupDatasource = new DynamicGroupDataSource(group, strategy.newInstance());
                    groupDatasource.addDatasource(dataSource);
                    groupDataSources.put(group, groupDatasource);
                } catch (Exception e) {
                    log.error("dynamic-datasource - add the datasource named [{}] error", ds, e);
                    dataSourceMap.remove(ds);
                }
            }
        }
    }

    

    @Override
    public void afterPropertiesSet() throws Exception {
        Map<String, DataSource> dataSources = provider.loadDataSources();
        // 添加并分组数据源
        for (Map.Entry<String, DataSource> dsItem : dataSources.entrySet()) {
            addDataSource(dsItem.getKey(), dsItem.getValue());
        }
        // 检测默认数据源设置
        if (groupDataSources.containsKey(primary)) {
            log.info("dynamic-datasource initial loaded [{}] datasource,primary group datasource named [{}]", dataSources.size(), primary);
        } else if (dataSourceMap.containsKey(primary)) {
            log.info("dynamic-datasource initial loaded [{}] datasource,primary datasource named [{}]", dataSources.size(), primary);
        } else {
            throw new RuntimeException("dynamic-datasource Please check the setting of primary");
        }
    }

}
```
其中

```java
private void addGroupDataSource(String ds, DataSource dataSource) {
        if (ds.contains(UNDERLINE)) {
            String group = ds.split(UNDERLINE)[0];
            if (groupDataSources.containsKey(group)) {
                groupDataSources.get(group).addDatasource(dataSource);
            } else {
                try {
                    DynamicGroupDataSource groupDatasource = new DynamicGroupDataSource(group, strategy.newInstance());
                    groupDatasource.addDatasource(dataSource);
                    groupDataSources.put(group, groupDatasource);
                } catch (Exception e) {
                    log.error("dynamic-datasource - add the datasource named [{}] error", ds, e);
                    dataSourceMap.remove(ds);
                }
            }
        }
    }
```

### 最佳实践
以上动态数据源的实现来源于 [这里](https://github.com/baomidou/dynamic-datasource-spring-boot-starter)，如果要自行实现，也可以参考这里的设计思想。

### 总结
以上就是整个数据源源码的全部内容, 内容比较多, 部分功能描述不是特别详细。

### 参考
[一文读懂Spring动态配置多数据源---源码详细分析](https://www.cnblogs.com/ITPower/p/15110531.html)