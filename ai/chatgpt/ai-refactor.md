
## 重构案例

### 命名


### 重复代码

#### 同一类的多个方法代码重复

```java
public class FruitsCost {
    public double computeMoneyWithoutPrivileges(String type, int numbers) {
        double prices;
        switch (type) {
            case "apple":
                prices = 5.5;
                break;
            case "banana":
                prices = 4.0;
                break;
            case "strawberry":
                prices = 10.5;
                break;
            default:
                throw new IllegalArgumentException("Illegal type : " + type);
        }
        return prices * numbers;
    }

    public double computeMoneyWithPrivileges(String type, double numbers, double discount) {
        double prices;
        switch (type) {
            case "apple":
                prices = 5.5;
                break;
            case "banana":
                prices = 4.0;
                break;
            case "strawberry":
                prices = 10.5;
                break;
            default:
                throw new IllegalArgumentException("Illegal type : " + type);
        }
        return prices * numbers * discount;
    }
}
```




#### 两个互为兄弟的子类内含相同的方法

```java
class Fruits {
    // 成本单价
    public double costPrices;

    // 出售单价
    public double prices;

    // 最小出货量
    public double minSaleableNum;
}

class Apple extends Fruits {
    public Apple(double costPrices, double prices, double minSaleableNum) {
        this.costPrices = costPrices;
        this.minSaleableNum = minSaleableNum;
        this.prices = prices;
    }

    public double profitMoney(int number) {
        return Math.max(0, number - minSaleableNum) * this.prices - this.costPrices * number;
    }
}

class Banana extends Fruits {
    public Banana(double costPrices, double prices, double minSaleableNum) {
        this.costPrices = costPrices;
        this.minSaleableNum = minSaleableNum;
        this.prices = prices;
    }

    public double profitMoney(int number) {
        return Math.max(0, number - minSaleableNum) * this.prices - this.costPrices * number;
    }
}
```

#### 两个毫不相关的类出现重复代码

```java
class MonthJudgement {
    public boolean judgeMonth() {
        Long timeStamp = System.currentTimeMillis();  // 获取当前时间戳
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String date = sdf.format(new Date(Long.parseLong(String.valueOf(timeStamp))));
        String month = date.split(" ")[0].split("-")[1];
        return "12".equals(month);
    }
}

class YearJudgement {
    public boolean judgeYear() {
        Long time = System.currentTimeMillis();  // 获取当前时间戳
        System.out.println("获得当前时间戳");
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String date = dateFormat.format(new Date(Long.parseLong(String.valueOf(time))));
        return date.startsWith("2021");
    }
}
```

### 过长函数

#### 案例1

```java
    public void destroy() {
        if (shellServer != null) {
            shellServer.close();
            shellServer = null;
        }
        if (sessionManager != null) {
            sessionManager.close();
            sessionManager = null;
        }
        if (this.httpSessionManager != null) {
            httpSessionManager.stop();
        }
        if (timer != null) {
            timer.cancel();
        }
        if (this.tunnelClient != null) {
            try {
                tunnelClient.stop();
            } catch (Throwable e) {
                logger().error("stop tunnel client error", e);
            }
        }
        if (executorService != null) {
            executorService.shutdownNow();
        }
        if (transformerManager != null) {
            transformerManager.destroy();
        }
        if (classLoaderInstrumentTransformer != null) {
            instrumentation.removeTransformer(classLoaderInstrumentTransformer);
        }
        // clear the reference in Spy class.
        cleanUpSpyReference();
        shutdownWorkGroup();
        UserStatUtil.destroy();
        if (shutdown != null) {
            try {
                Runtime.getRuntime().removeShutdownHook(shutdown);
            } catch (Throwable t) {
                // ignore
            }
        }
        logger().info("as-server destroy completed.");
        if (loggerContext != null) {
            loggerContext.stop();
        }
    }
```

重构后的代码

```java
public void destroy() {
    closeShellServer();
    closeSessionManager();
    stopHttpSessionManager();
    cancelTimer();
    stopTunnelClient();
    shutdownExecutorService();
    destroyTransformerManager();
    removeClassLoaderInstrumentTransformer();
    cleanUpSpyReference();
    shutdownWorkerGroup();
    destroyUserStatUtil();
    removeShutdownHook();
    stopLoggerContext();
    logDestroyCompletion();
}

private void closeShellServer() {
    if (shellServer != null) {
        shellServer.close();
        shellServer = null;
    }
}

private void closeSessionManager() {
    if (sessionManager != null) {
        sessionManager.close();
        sessionManager = null;
    }
}

private void stopHttpSessionManager() {
    if (httpSessionManager != null) {
        httpSessionManager.stop();
    }
}

private void cancelTimer() {
    if (timer != null) {
        timer.cancel();
    }
}

private void stopTunnelClient() {
    if (tunnelClient != null) {
        try {
            tunnelClient.stop();
        } catch (Throwable e) {
            logger().error("Stopping tunnel client error", e);
        }
    }
}

private void shutdownExecutorService() {
    if (executorService != null) {
        executorService.shutdownNow();
    }
}

private void destroyTransformerManager() {
    if (transformerManager != null) {
        transformerManager.destroy();
    }
}

private void removeClassLoaderInstrumentTransformer() {
    if (classLoaderInstrumentTransformer != null) {
        instrumentation.removeTransformer(classLoaderInstrumentTransformer);
    }
}

private void cleanUpSpyReference() {
    SpyAPI.cleanUpReference();
}

private void shutdownWorkerGroup() {
    if (workerGroup != null) {
        workerGroup.shutdownGracefully();
    }
}

private void destroyUserStatUtil() {
    UserStatUtil.destroy();
}

private void removeShutdownHook() {
    if (shutdown != null) {
        try {
            Runtime.getRuntime().removeShutdownHook(shutdown);
        } catch (Throwable t) {
            // Ignored
        }
    }
}

private void stopLoggerContext() {
    if (loggerContext != null) {
        loggerContext.stop();
    }
}

private void logDestroyCompletion() {
    logger().info("as-server destroy completed.");
}
```

#### 案例2

```java
    public void handleInstrument(InstrumentRequest instrumentRequest) throws Exception {
        Set<String> inputClassNames = instrumentRequest.toClassNames();
        Set<String> targetClassNames =
                inputClassNames.stream().map(ClazzUtils::getImplementClassNames).flatMap(Set::stream).collect(Collectors.toSet());
        logger.info("agentmain target classes {}", targetClassNames);
        Set<Class<?>> targetClasses =
                inputClassNames.stream().map(ClazzUtils::getImplementClasses).flatMap(Set::stream).collect(Collectors.toSet());

        Map<String, Set<String>> newClass2MethodNames = instrumentRequest.toClass2Methods();

        for (String targetClassName : newClass2MethodNames.keySet()) {
            if (methodNames.containsKey(targetClassName)) {
                Set<String> newMethodNames = newClass2MethodNames.get(targetClassName);
                for (String newMethodName : newMethodNames) {
                    Set<String> currentMethodNames = methodNames.get(targetClassName);
                    if (currentMethodNames.contains(newMethodName)) {
                        newClass2MethodNames.get(targetClassName).remove(newMethodName);
                    } else {
                        currentMethodNames.add(newMethodName);
                    }
                }
            } else {
                methodNames.put(targetClassName, newClass2MethodNames.get(targetClassName));
            }
        }
        if (newClass2MethodNames.isEmpty() || newClass2MethodNames.values().isEmpty()) {
            return;
        }
        boolean isEmpty = true;
        for (String className : newClass2MethodNames.keySet()) {
            if (!newClass2MethodNames.get(className).isEmpty()) {
                isEmpty = false;
            }
        }
        if (isEmpty) {
            return;
        }
        ClassPool.getDefault().insertClassPath(new ClassClassPath(HotSwapper.class));
        ClassFileTransformer classFileTransformer = new ByteTransformer(targetClassNames, newClass2MethodNames);
        TransformerManager.getInstance(instrumentation).addTransformer(classFileTransformer);
        instrumentation.addTransformer(classFileTransformer, true);
        instrumentation.retransformClasses(targetClasses.toArray(new Class[0]));
    }
```
重构后
```java
public void handleInstrument(InstrumentRequest instrumentRequest) throws Exception {
    Set<String> targetClassNames = extractTargetClassNames(instrumentRequest);
    logger.info("agentmain target classes {}", targetClassNames);

    Map<String, Set<String>> newClass2MethodNames = instrumentRequest.toClass2Methods();
    updateMethodNames(newClass2MethodNames);

    if (newClass2MethodNames.isEmpty() || isAllSetsEmpty(newClass2MethodNames)) {
        return;
    }

    setupClassFileTransformer(instrumentRequest, targetClassNames, newClass2MethodNames);
}

private Set<String> extractTargetClassNames(InstrumentRequest instrumentRequest) {
    Set<String> inputClassNames = instrumentRequest.toClassNames();
    return inputClassNames.stream()
            .map(ClazzUtils::getImplementClassNames)
            .flatMap(Set::stream)
            .collect(Collectors.toSet());
}

private void updateMethodNames(Map<String, Set<String>> newClass2MethodNames) {
    for (Map.Entry<String, Set<String>> entry : newClass2MethodNames.entrySet()) {
        String targetClassName = entry.getKey();
        Set<String> newMethodNames = entry.getValue();

        Set<String> currentMethodNames = methodNames.computeIfAbsent(targetClassName, k -> new HashSet<>());
        currentMethodNames.removeAll(newMethodNames);
        currentMethodNames.addAll(newMethodNames);
    }
}

private boolean isAllSetsEmpty(Map<String, Set<String>> map) {
    return map.values().stream().allMatch(Set::isEmpty);
}

private void setupClassFileTransformer(InstrumentRequest instrumentRequest,
                                       Set<String> targetClassNames,
                                       Map<String, Set<String>> newClass2MethodNames) throws Exception {
    ClassPool.getDefault().insertClassPath(new ClassClassPath(HotSwapper.class));
    ClassFileTransformer classFileTransformer = new ByteTransformer(targetClassNames, newClass2MethodNames);
    TransformerManager.getInstance(instrumentation).addTransformer(classFileTransformer);
    instrumentation.addTransformer(classFileTransformer, true);
    instrumentation.retransformClasses(targetClassNames.stream()
            .map(instrumentRequest::toClass)
            .toArray(Class[]::new));
}
```

### 过长的参数列表

#### 查询取代参数

#### 某几个入参是一个对象的部分字段

#### 某几个参数有关联

#### 函数逻辑都是针对某个入参属性的加工

#### 某些入参属于标记，用于控制业务逻辑


### 重复的 switch

```java
public class Employee {
    public static final int ENGINEER = 0;
    public static final int SALESMAN = 1;
    public static final int MANAGER = 2;

    private int type;

    public Employee(int type) {
        this.type = type;
    }

    public int getType() {
        return type;
    }

    public int calculatePay() {
        switch (type) {
            case ENGINEER:
                return 5000;
            case SALESMAN:
                return 4000;
            case MANAGER:
                return 6000;
            default:
                throw new IllegalArgumentException("Incorrect Employee Type");
        }
    }

    public int getBonus() {
        switch (type) {
            case ENGINEER:
                return 1000;
            case SALESMAN:
                return 1500;
            case MANAGER:
                return 2000;
            default:
                throw new IllegalArgumentException("Incorrect Employee Type");
        }
    }
}

```

重构后的代码
```java
// 定义员工接口
interface Employee {
    int calculatePay();
    int getBonus();
}

// 工程师类
class Engineer implements Employee {
    @Override
    public int calculatePay() {
        return 5000;
    }

    @Override
    public int getBonus() {
        return 1000;
    }
}

// 销售人员类
class Salesman implements Employee {
    @Override
    public int calculatePay() {
        return 4000;
    }

    @Override
    public int getBonus() {
        return 1500;
    }
}

// 经理类
class Manager implements Employee {
    @Override
    public int calculatePay() {
        return 6000;
    }

    @Override
    public int getBonus() {
        return 2000;
    }
}
```



### 参考
https://github.com/RefactoringGuru/refactoring-examples/blob/main/interactive/java/replace-parameter-with-method-call.md