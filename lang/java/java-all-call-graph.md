

## 1、增加依赖
```xml
		<dependency>
			<groupId>com.github.adrninistrator</groupId>
			<artifactId>java-all-call-graph</artifactId>
			<version>2.0.8</version>
		</dependency>
		<dependency>
			<groupId>com.h2database</groupId>
			<artifactId>h2</artifactId>
			<version>2.1.214</version>
			<scope>runtime</scope>
		</dependency>
```

## 配置


### config.properties 文件

app.name=zpai
check.jar.file.updated=false

### config_db.properties
db.use.h2=true
db.h2.file.path=./build/jacg_h2db
db.driver.name=org.h2.Driver
db.url=jdbc:h2:~/test;MODE=MYSQL;CASE_INSENSITIVE_IDENTIFIERS=True
db.username=sa
db.password=

### ignore_class_keyword.properties
org.apache.commons
org.slf4j
io.unlogged
java.
javax.servlet
ServiceException

### jar_dir.properties
XXX.jar

配置要扫描的包

### method_class_4caller.properties
xxxx.XXXController:XXX

类全路径:方法名,详细参考配置文件示例

## 创建测试类

src/test/resource下创建如下类似

```java
public class AnalysisClass {
    public static void main(String[] args) {
        // 第一步：执行之后注释
        UnzipFile.main(args);
        // 第二步 初始数据库之后注释
        new RunnerWriteDb().run();
        // 第三步执行，不同的方法
        new RunnerGenAllGraph4Caller().run();
    }
}
```
