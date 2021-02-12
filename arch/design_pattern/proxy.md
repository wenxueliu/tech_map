

代理类





Java动态代理，CGLIB，Javassist，ASM库等



#### Java动态代理的优缺点

优点：Java动态代理可以避免静态代理带来的代码冗余的问题。

缺点：Java动态代理只能针对接口创建代理，不能针对类创建代理。

#### 例子

LoginService.java

```java
package com.wenxueliu.test;

public interface LoginService {
    String login(String userName);
}
```



LoginServiceImpl.java

```java
package com.wenxueliu.test;

public class LoginServiceImpl implements LoginService {

    public String login(String userName){
        System.out.println("正在登录");
        return "success";
    }
}
```



DynamicProxy.java

```java
package com.wenxueliu.test;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;

public class DynamicProxy implements InvocationHandler {

    private Object target;

    public DynamicProxy(Object target){
        this.target = target;
    }

    public <T> T getProxy(){
        return (T) Proxy.newProxyInstance(
                target.getClass().getClassLoader(),
                target.getClass().getInterfaces(),
                this);
    }

    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        long startTime = System.currentTimeMillis();
        Object result = method.invoke(target,args);
        long endTime = System.currentTimeMillis();
        System.out.println("花费了 " + (endTime - startTime) + " ms");
        return result;
    }
}

```



```java
package com.wenxueliu.test;

public class Main {

    public static void main(String[] args) {
        LoginService loginService = new LoginServiceImpl();
        LoginService proxy = new DynamicProxy(loginService).getProxy();
        proxy.login("test");
    }
}
```



