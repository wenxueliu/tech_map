## 创建业务对象

1、已有的业务对象可以放入向量数据库
2、如果没有新的业务对象需要创建，直接跳过
3、新的业务对象由用户手动创建

### 提示词

假设你是一名 Java 软件技术专家，根据如下 yaml 格式生成对应的实体对象，其中
1、最顶层为对象名
2、每个key 对应的 value 为对象属性的类型；类型是可选的，如果没有指定，您将根据上下文进行推断
3、可以根据需要补充兑现的属性
5、如果嵌套多层，对应类为内部类，类的修饰符为 public static
请根据如下 yaml 生成 Java 对象，并使用 lombok。
"""
{yaml个数的对象描述，详细参考示例}
"""

### 示例

假设你是一名 Java 软件技术专家，根据如下 yaml 格式生成对应的实体对象，其中
1、最顶层为对象名
2、每个key 对应的 value 为对象属性的类型；类型是可选的，如果没有指定，您将根据上下文进行推断
3、可以根据需要补充兑现的属性
5、如果嵌套多层，对应类为内部类，类的修饰符为 public static
请根据如下 yaml 生成 Java 对象，并使用 lombok。
"""
- instance:
    name: string
    url: string
    available: int
- lb:
    consistenthash:
      request:
        header:
          key: string
          regex: string
- healthcheck:
    interval: long
- microinstance:
    name: string
    lb: lb
    healthcheck: healthcheck
    instances:
      -  instance
- serviceproperties:
    - microinstance
- microserviceInfo:
    name: string
    namespace: string
    accessurl: string
    servicecontext: string
    context: string
    microinstances:
      - microinstance: microinstance
"""

生成对象示例

```java
@Data
public class Instance {

    private String name;

    private String url;

    private Integer available;
}


@Data
public class Lb {

    @Singular("consistentHash")
    private List<ConsistentHash> consistentHashes = new ArrayList<>();

    @Data
    public static class ConsistentHash {

        private Request request;

        @Data
        public static class Request {

            private Map<String, String> headers = new HashMap<>();
        }
    }
}


@Data
public class HealthCheck {

    private Long interval;
}


@Data
public class MicroInstance {

    private String name;

    private Lb lb;

    private HealthCheck healthcheck;

    private Set<Instance> instances = new HashSet<>();
}


@Data
public class ServiceProperties {

    @Singular
    private List<MicroInstance> microInstances = new ArrayList<>();
}


@Data
public class MicroServiceInfo {

    private String name;

    private String namespace;

    private String accessUrl;

    private String serviceContext;

    private String context;

    private ServiceProperties serviceProperties;

    private Set<MicroInstance> microInstances = new LinkedHashSet<>();
}
```

## 业务逻辑

1、依据业务对象描述业务逻辑

### 提示词

根据如上对象，你将根据如下业务描述，生成对应的Java代码
1、input 表示方法的输入参数
2、processor 表示方法具体的处理过程
3、output 表示方法的输出参数
3、对应的类自动实现接口类和实现类
约束
1、采用 spring boot、spring cloud gateway、caffeine、guava技术栈
2、包路径为 com.example.gateway
"""
{以yaml格式组织业务逻辑描述}
"""

### 示例

根据如上对象，你将根据如下业务描述，生成对应的Java代码
1、input 表示方法的输入参数
2、processor 表示方法具体的处理过程
3、output 表示方法的输出参数
3、对应的类自动实现接口类和实现类
约束
1、采用 spring boot、spring cloud gateway、caffeine、guava技术栈
2、包路径为 com.example.gateway
"""
- healthCheckService:
  checkservice:
    input: null
    processor:
      step1: 从配置文件 application.yanl 读取  spring.cloud.gateway.services 配置解析为 serviceproperties 对象
      step2: 遍历 serviceproperties 中的每个 microinstance，通过 webclient  的get
        请求访问microinstance.instances中的每个instance的 url，如果抛出异常或返回值为 Bad，就设置
        instance 的 available 为0，否则设置 instance的available为 1
    output: null
"""


根据如上对象，你将根据如下业务描述，生成对应的Java代码
1、input 表示方法的输入参数
2、processor 表示方法具体的处理过程
3、output 表示方法的输出参数
3、自动生成接口类和实现类
约束
1、采用 spring boot、spring cloud gateway、caffeine、guava技术栈
2、包路径为 com.example.gateway
"""
- guavaconsistenthashservice: class
  description: 一致性哈希负载均衡服务
  methods:
    consistenthash:
      input:
        val: string
        buckets: int
      processor:
        step1: 使用 guava 的 Hashing.consistentHash() 计算一致性 hash
      output:
        index: int
- consistentHashloadbalanceservice: class
  description: 一致性哈希负载均衡服务
  loadbalance: null
  input:
    exchange: ServerWebExchange
    microserviceinfo: microserviceInfo
  processor:
    step1: 从 microserviceInfo 读取 consistenthash，从consistenthash中读取 request，从 request 读取 header，从 header 读取 key 的值 val
    step2: 从 http header 中读取 val 对应的值 headervalue
    step3: 使用guavaconsistenthashservice计算一致性 hash，返回instanceIndex
    step4: 从microserviceinfo中的 instances  中读取instanceIndex对应的 instance，返回 instance
  output:
    instance: instance
"""

## 业务逻辑(版本2)

根据如上对象，你将根据如下业务描述，生成对应的 Java 代码，其中
1、id 表示功能点
2、processor 表示方法具体的处理过程
3、step 表示具体的步骤
技术栈
1、spring boot、spring cloud gateway、caffeine、guava
"""
- 健康检查服务: null
  id: 检查实例是否可用
  processor:
    step1: 从配置文件 application.yanl 读取配置 spring.cloud.gateway.services 解析为
      serviceproperties 对象
    step2: 遍历serviceproperties中的每个microinstance
    step3: 遍历microinstance中的instances中的每个instance
    step4: 通过webclient的get请求访问instance的url，如果抛出异常或返回值为 Bad，就标记 instance
      状态为0，否则标记为状态为 1
- 一致性哈希负载均衡服务: null
  描述: 计算一致性哈希
  处理过程:
    step1: 从 microserviceInfo 读取 consistenthash，从consistenthash中读取 request，从 request
      读取 header，从 header 读取 key 的值 val
    step2: 从 http header 中读取 val 对应的值 headervalue
    step3: 使用guavaconsistenthashservice计算一致性 hash，返回instanceIndex
    step4: 从microserviceinfo中的 instances  中读取instanceIndex对应的 instance，返回 instance
- 一致性哈希负载均衡服务: null
  id: 计算一致性哈希
  processor:
    step1: 使用 guava 的 Hashing.consistentHash() 计算一致性 hash
"""

## 落地策略

1、验证需求
2、开始必须有相关的人进行提示词检视，训练大家的描述准确性
