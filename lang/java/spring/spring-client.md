spring client



RequestFactoryCustomizer

设置三个属性：connectTimeout，readTimeout，bufferRequestBody

HttpMessageConverter

消息转换

ClientHttpRequestInterceptor：请求拦截器

ClientHttpRequestFactory：创建 ClientHttpRequest

ResponseErrorHandler：响应错误处理

RestTemplateCustomizer：对 RestTemplate 的自定义扩展点，主要是在创建 RestTemplate 时使用。

RestTemplateRequestCustomizer：对RestTemplateRequest的自定义扩展

UriTemplateHandler：url 模板，处理 url 中有变量的情况



```
ClientHttpRequestFactory -> 
```

```java
protected <T> T doExecute(URI url, @Nullable HttpMethod method, @Nullable RequestCallback requestCallback,
      @Nullable ResponseExtractor<T> responseExtractor) throws RestClientException {

   Assert.notNull(url, "URI is required");
   Assert.notNull(method, "HttpMethod is required");
   ClientHttpResponse response = null;
   try {
      ClientHttpRequest request = createRequest(url, method);
      if (requestCallback != null) {
         requestCallback.doWithRequest(request);
      }
      response = request.execute();
      handleResponse(url, method, response);
      return (responseExtractor != null ? responseExtractor.extractData(response) : null);
   }
   catch (IOException ex) {
      String resource = url.toString();
      String query = url.getRawQuery();
      resource = (query != null ? resource.substring(0, resource.indexOf('?')) : resource);
      throw new ResourceAccessException("I/O error on " + method.name() +
            " request for \"" + resource + "\": " + ex.getMessage(), ex);
   }
   finally {
      if (response != null) {
         response.close();
      }
   }
}
```