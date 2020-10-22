spring-boot-config



https://blog.csdn.net/feigeswjtu/article/details/74451973

https://github.com/hryou0922/spring_boot/tree/master/core/src/main/java/com/hry/spring/configinject

https://mp.weixin.qq.com/s?__biz=MzUzNTY4NTYxMA==&mid=2247484448&idx=1&sn=88aa5301b3f14b434370560cf627305f&chksm=fa80f4a5cdf77db37e7287eeae81813eab96ea709feace2392e70380c8ba73bb14433e90bbaf&scene=21#wechat_redirect

https://mp.weixin.qq.com/s?__biz=MzUzNTY4NTYxMA==&mid=2247484459&idx=1&sn=bab8aa54eed82cfe46ba0234fb7e58b8&chksm=fa80f4aecdf77db834e04ec50fe3e07b356402a9cecd707f37e95b964d592a7d2ab549246221&scene=21#wechat_redirect

https://github.com/hryou0922/spring_boot

https://blog.csdn.net/IT_faquir/article/details/80869578

https://blog.csdn.net/qq_33524158/article/details/79600434

https://blog.csdn.net/qq_33524158/article/details/79600434

https://blog.csdn.net/IT_faquir/article/details/80869578



Spring开发过程中经常遇到需要把特殊的值注入到成员变量里，比如普通值、文件、网址、配置信息、系统 变量等等。

Spring主要使用注解@Value把对应的值注入到变量中。 
常用的注入类型有以下几种: 

- 注入普通字符串
- 注入操作系统属性
- 注入表达式结果
- 注入其他Bean属性：注入beanInject对象的属性another
- 注入文件资源
- 注入URL资源



```java
@Configuration
@ComponentScan("ch2.value")
@PropertySource("classpath:ch2/value/test.properties")
public class Config {
    @Value("我是个普通字符串")
    private String nornal;

    @Value("#{systemEnvironment['os.name']}")
    private String osName;

    @Value("#{T(java.lang.Math).random()*1000.0}")
    private double randomNumber;

    @Value("#{demoService.anotherValue}")
    private String anotherValue;

    @Value("classpath:ch2/value/test.txt")
    private Resource testFile;

    @Value("http://www.baidu.com")
    private Resource testUrl;

    @Value("${book.name}")
    private String bookName;

    @Autowired
    private Environment environment;


    public void outSource(){
        System.out.println(nornal);
        System.out.println(osName);
        System.out.println(randomNumber);
        System.out.println(anotherValue);
        try {
        System.out.println(IOUtils.toString(testFile.getInputStream()));
        System.out.println(IOUtils.toString(testUrl.getInputStream()));
        }catch (Exception e){
            e.printStackTrace();
        }
        System.out.println(bookName);
        System.out.println(environment.getProperty("book.author"));
    }
}
```





```
import java.util.List;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.PropertySource;
import org.springframework.stereotype.Component;

/**
 * 比较复杂的注入应用
 * @author hry
 *
 */
@Component
@PropertySource({"classpath:com/hry/spring/configinject/advance_value_inject.properties"})
public class AdvanceValueInject {
	// SpEL：调用字符串Hello World的concat方法
	@Value("#{'Hello World'.concat('!')}")
	private String helloWorld;
	
	// SpEL: 调用字符串的getBytes方法，然后调用length属性
	@Value("#{'Hello World'.bytes.length}")
	private String helloWorldbytes;
	
	// SpEL: 传入一个字符串，根据","切分后插入列表中， #{}和${}配置使用(注意单引号，注意不能反过来${}在外面，#{}在里面)
	@Value("#{'${server.name}'.split(',')}")
	private List<String> servers;
	
//	// SpEL: 注意不能反过来${}在外面，#{}在里面，这个会执行失败
//	@Value("${#{'HelloWorld'.concat('_')}}")
//	private List<String> servers2;
	
	// 使用default.value设置值，如果不存在则使用默认值
	@Value("${spelDefault.value:127.0.0.1}")
	private String spelDefault;
	
	// 如果属性文件没有spelDefault.value，则会报错
//	@Value("${spelDefault.value}")
//	private String spelDefault2;
	
	public String toString(){
		StringBuilder sb = new StringBuilder();
		sb.append("helloWorld=").append(helloWorld).append("\r\n")
		.append("helloWorldbytes=").append(helloWorldbytes).append("\r\n")
		.append("servers=").append(servers).append("\r\n")
		.append("spelDefault=").append(spelDefault).append("\r\n")
	//	.append("spelDefault2=").append(spelDefault2).append("\r\n")
		;
		return sb.toString();
	}
}
```



```
import java.io.IOException;

import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Component;

/**
 * 通过@Value注入bean的属性
 * @author hry
 *
 */
@Component
public class BaseValueInject {
	private static final Logger log = LoggerFactory.getLogger(BaseValueInject.class);
	
	@Value("normal")
	private String normal; // 注入普通字符串
	
	@Value("#{systemProperties['os.name']}")
	private String systemPropertiesName; // 注入操作系统属性
	
	@Value("#{ T(java.lang.Math).random() * 100.0 }")
	private double randomNumber; //注入表达式结果
	
	@Value("#{beanInject.another}")
	private String fromAnotherBean; // 注入其他Bean属性：注入beanInject对象的属性another
	
	@Value("classpath:com/hry/spring/configinject/config.txt")
	private Resource resourceFile; // 注入文件资源
	
	@Value("http://www.baidu.com")
	private Resource testUrl; // 注入URL资源
	
	@Override
	public String toString(){
		StringBuilder sb = new StringBuilder();
		try {
			sb.append("normal=").append(normal).append("\r\n")
			.append("systemPropertiesName=").append(systemPropertiesName).append("\r\n")
			.append("randomNumber=").append(randomNumber).append("\r\n")
			.append("fromAnotherBean=").append(fromAnotherBean).append("\r\n")
			.append("resourceFile=").append(IOUtils.toString(resourceFile.getInputStream())).append("\r\n")
			.append("testUrl=").append(IOUtils.toString(testUrl.getInputStream())).append("\r\n");
		} catch (IOException e) {
			log.error("e={}", e);
		}
		return sb.toString();
	}	
	
}
```

